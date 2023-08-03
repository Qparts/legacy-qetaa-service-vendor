package qetaa.service.vendor.restful;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import qetaa.service.vendor.dao.DAO;
import qetaa.service.vendor.filters.ValidApp;
import qetaa.service.vendor.helpers.Helper;
import qetaa.service.vendor.model.security.AccessToken;
import qetaa.service.vendor.model.security.WebApp;
import qvm.service.vendor.model.QvmBranch;
import qvm.service.vendor.model.QvmUser;
import qvm.service.vendor.model.QvmVendor;
import qvm.service.vendor.model.QvmVendorMake;
import qvm.service.vendor.model.QvmVendorVerification;
import qvm.service.vendor.model.contract.SignUpContract;

@Path("/qvm/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class QvmService {

	
	@EJB
	private DAO dao;
	
	@EJB
	private QvmAsyncService async;

	@ValidApp
	@Path("signup")
	@POST
	public Response signup(@HeaderParam("Authorization") String authHeader, SignUpContract suc) {
		try {
			WebApp webApp = this.getWebAppFromAuthHeader(authHeader);
			//check if user is created
			List<QvmUser> list = dao.getCondition(QvmUser.class, "email", suc.getEmail().toLowerCase());
			if(!list.isEmpty()) {
				return Response.status(409).build();
			}
			QvmVendor qvmVendor = this.createCompany(suc);
			QvmUser qvmUser = this.createMainUser(suc, qvmVendor);
			this.createVerification(suc.getEmail(), qvmVendor);
			Map<String,Object> map = getLoginObject(qvmUser, webApp);
			return Response.status(200).entity(map).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	@ValidApp
	@Path("activate-main")
	@POST
	public Response activateAccount(@HeaderParam("Authorization") String authHeader, Map<String,Object> activateMap) {
		try {
			WebApp webApp = this.getWebAppFromAuthHeader(authHeader);
			String code = (String) activateMap.get("code");
			Integer companyId = ((Number) activateMap.get("seq")).intValue();
			
			String jpql = "select b from QvmVendorVerification b where b.code = :value0 "
					+ " and b.companyId = :value1 and b.expire > :value2";
			QvmVendorVerification ver = dao.findJPQLParams(QvmVendorVerification.class, jpql, code, companyId, new Date());
			if(ver == null) {
				QvmVendorVerification ver2 = dao.findTwoConditions(QvmVendorVerification.class, "code", "companyId", code, companyId);
				if(ver2 != null) {
					dao.delete(ver2);
					return Response.status(410).build();
				}
				//not found
				return Response.status(404).build();
			}
			//delete verification
			dao.delete(ver);
			//activate company
			QvmUser qvmUser = this.activateQvm(companyId);
			Map<String,Object> map = this.getLoginObject(qvmUser, webApp);
			return Response.status(200).entity(map).build();
			
		}catch(Exception ex){
			return Response.status(500).build();
		}		
	}
	
	private QvmUser activateQvm(int companyId) {
		//activate company
		QvmVendor qvmVendor = dao.find(QvmVendor.class, companyId);
		qvmVendor.setStatus('V');
		dao.update(qvmVendor);
		//activate main user
		QvmUser qvmUser = dao.findTwoConditions(QvmUser.class, "qvmVendor.id", "mainUser", companyId, true);
		qvmUser.setStatus('A');
		dao.update(qvmUser);
		return qvmUser;
	}

	@ValidApp
	@Path("login")
	@POST
	public Response login(@HeaderParam("Authorization") String authHeader, Map<String,String> loginMap) {
		try {
			WebApp webApp = this.getWebAppFromAuthHeader(authHeader);
			String email = loginMap.get("email").trim().toLowerCase();
			String password = Helper.cypher(loginMap.get("password").trim());
			String jpql = "select b from QvmUser b where b.email = :value0 and b.password = :value1 and b.status in (:value2, :value3)";
			QvmUser qvmUser = dao.findJPQLParams(QvmUser.class, jpql, email, password, 'A', 'N');
			if(qvmUser == null) {
				return Response.status(404).build();
			}
			Map<String,Object> map = getLoginObject(qvmUser, webApp);
			return Response.status(200).entity(map).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	

	private Map<String, Object> getLoginObject(QvmUser qvmUser, WebApp webApp) {
		//get branches
		List<QvmBranch> branches = dao.getCondition(QvmBranch.class, "vendorId", qvmUser.getQvmVendor().getId());
		qvmUser.getQvmVendor().setBranches(branches);
		List<QvmVendorMake> vendorMakes = dao.getCondition(QvmVendorMake.class, "qvmVendor.id", qvmUser.getQvmVendor().getId());		
		qvmUser.getQvmVendor().setVendorMakes(vendorMakes);
		AccessToken token = this.issueToken(qvmUser, webApp, 60);// 60 minutes
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("token", token.getToken());
		map.put("tokenExpire", token.getExpire().getTime());
		map.put("user", qvmUser);
		return map;
	}
	
	private void deactivateOldTokens(QvmUser qvmUser, WebApp webApp) {
		List<AccessToken> tokens = dao.getThreeConditions(AccessToken.class, "userId", "status", "webApp", qvmUser.getId(), 'A', webApp);
		for (AccessToken t : tokens) {
			t.setStatus('K');// kill old token
			dao.update(t);
		}
	}
	
	private AccessToken issueToken(QvmUser qvmUser, WebApp webApp, int expireMinutes) {
		deactivateOldTokens(qvmUser, webApp);
		Date tokenTime = new Date();
		AccessToken accessToken = new AccessToken(qvmUser.getId(), tokenTime, webApp);
		accessToken.setWebApp(webApp);
		accessToken.setExpire(Helper.addMinutes(tokenTime, expireMinutes));
		accessToken.setStatus('A');
		accessToken.setToken(Helper.getSecuredRandom());
		dao.persist(accessToken);
		return accessToken;
	}
	
	private QvmVendor createCompany(SignUpContract suc) {
		QvmVendor qvmVendor = new QvmVendor();
		qvmVendor.setCreated(new Date());
		qvmVendor.setCreatedBy(suc.getCreatedBy());
		qvmVendor.setDelivery(false);
		qvmVendor.setName(suc.getCompanyName());
		qvmVendor.setStatus('N');
		dao.persist(qvmVendor);
		return qvmVendor;
	}
	
	private void createVerification(String email, QvmVendor qvmVendor) {
		String activationCode = Helper.getRandomSaltString(20);
		QvmVendorVerification qvv = new QvmVendorVerification();
		qvv.setCode(activationCode);
		qvv.setCompanyId(qvmVendor.getId());
		qvv.setCreated(new Date());
		qvv.setExpire(Helper.addMinutes(qvv.getCreated(), 60*24*7));
		dao.persist(qvv);
		String html = Helper.prepareHtmlActivationEmail(activationCode, qvmVendor.getName(), qvmVendor.getId());
		async.sendHtmlEmail(email, "Activate your Account", html);
	}
	
	private QvmUser createMainUser(SignUpContract suc, QvmVendor qvmVendor) throws Exception {
		QvmUser user = new QvmUser();
		user.setCreated(new Date());
		user.setCreatedBy(suc.getCreatedBy());
		user.setEmail(suc.getEmail().toLowerCase().trim());
		user.setFirstName(suc.getFirstName().trim());
		user.setLastName(suc.getLastName().trim());
		user.setMainUser(true);
		user.setMobile(suc.getMobile());
		user.setPassword(Helper.cypher(suc.getPassword()));
		user.setQvmVendor(qvmVendor);
		user.setStatus('N');
		dao.persist(user);
		return user;
	}
	

	private WebApp getWebAppFromAuthHeader(String authHeader) {
		try {
			String[] values = authHeader.split("&&");
			String appSecret = values[2].trim();
			// Validate app secret
			return getWebAppFromSecret(appSecret);
		} catch (Exception ex) {
			return null;
		}
	}
	

	// retrieves app object from app secret
	private WebApp getWebAppFromSecret(String secret) throws Exception {
		// verify web app secret
		WebApp webApp = dao.findTwoConditions(WebApp.class, "appSecret", "active", secret, true);
		if (webApp == null) {
			throw new Exception();
		}
		return webApp;
	}

	
	
}
