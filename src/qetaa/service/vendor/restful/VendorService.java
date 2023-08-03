package qetaa.service.vendor.restful;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import qetaa.service.vendor.dao.DAO;
import qetaa.service.vendor.filters.Secured;
import qetaa.service.vendor.filters.SecuredUser;
import qetaa.service.vendor.filters.SecuredVendor;
import qetaa.service.vendor.filters.ValidApp;
import qetaa.service.vendor.helpers.AccessMap;
import qetaa.service.vendor.helpers.AppConstants;
import qetaa.service.vendor.helpers.Helper;
import qetaa.service.vendor.model.Courier;
import qetaa.service.vendor.model.Score;
import qetaa.service.vendor.model.Vendor;
import qetaa.service.vendor.model.VendorHolder;
import qetaa.service.vendor.model.VendorJoinRequest;
import qetaa.service.vendor.model.VendorMake;
import qetaa.service.vendor.model.VendorRegion;
import qetaa.service.vendor.model.VendorUser;
import qetaa.service.vendor.model.VendorUserHolder;
import qetaa.service.vendor.model.security.AccessToken;
import qetaa.service.vendor.model.security.WebApp;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VendorService {
	@EJB
	private DAO dao;

	@Secured
	@GET
	@Path("/test")
	public void app() {

	}

	@SecuredUser
	@GET
	@Path("couriers")
	public Response getCouriers() {
		try {
			List<Courier> couriers = dao.get(Courier.class);
			return Response.status(200).entity(couriers).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("courier/{param}")
	public Response getCourier(@PathParam(value = "param") int cId) {
		try {
			Courier courier = dao.find(Courier.class, cId);
			return Response.status(200).entity(courier).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("active-couriers/user")
	public Response getActiveCourriersUser() {
		try {
			List<Courier> couriers = dao.getCondition(Courier.class, "internalStatus", 'A');
			return Response.status(200).entity(couriers).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("courier")
	public Response createCourier(Courier courier) {
		try {
			Courier c = dao.findCondition(Courier.class, "name", courier.getName());
			if (c != null) {
				return Response.status(409).build();
			}
			dao.persist(courier);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("active-vendor/make/{param}")
	public Response getActiveVendorsByMake(@PathParam(value = "param") int makeId) {
		try {
			String jpql = "select b.vendor from VendorMake b where makeId = :value0 and b.vendor.id in ("
					+ "select c.id from Vendor c where c.status = :value1)";

			List<Vendor> vendors = dao.getJPQLParams(Vendor.class, jpql, makeId, 'A');

			return Response.status(200).entity(vendors).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("all-vendors/make/{param}")
	public Response getAllVendorsByMake(@PathParam(value = "param") int makeId) {
		try {
			List<Vendor> vendors = dao.getConditionClassColumn(VendorMake.class, Vendor.class, "vendor", "makeId",
					makeId);
			return Response.status(200).entity(vendors).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}

	@Secured
	@GET
	@Path("vendor-user/{param}")
	public Response getVendorUser(@PathParam(value = "param") int vendorUserId) {
		try {
			VendorUser vendorUser = dao.find(VendorUser.class, vendorUserId);
			return Response.status(200).entity(vendorUser).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("vendor/{param}")
	public Response getVendor(@PathParam(value = "param") int vendorId) {
		try {
			Vendor vendor = dao.find(Vendor.class, vendorId);
			return Response.status(200).entity(vendor).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}

	@SecuredUser
	@GET
	@Path("all-vendors")
	public Response getAllVendors() {
		try {
			List<Vendor> vendors = dao.getOrderBy(Vendor.class, "name");
			List<VendorHolder> holders = new ArrayList<>();
			for (Vendor vendor : vendors) {
				VendorHolder holder = new VendorHolder();
				holder.setVendor(vendor);
				List<VendorUser> users = dao.getCondition(VendorUser.class, "vendor", vendor);
				if (!users.isEmpty()) {
					holder.setVendorUsers(users);
				}
				List<VendorMake> vendorMakes = dao.getCondition(VendorMake.class, "vendor", vendor);
				if (!vendorMakes.isEmpty()) {
					holder.setVendorMakes(vendorMakes);
				}
				holders.add(holder);
			}
			return Response.status(200).entity(holders).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@ValidApp
	@POST
	@Path("/login")
	public Response login(AccessMap map) {
		try {
			// verify web app and get it
			WebApp webApp = getWebAppFromSecret(map.getAppSecret());
			// get password
			String hashed = Helper.cypher(map.getCode());
			VendorUser vendorUser = dao.findThreeConditions(VendorUser.class, "status", "username", "password", 'A',
					map.getUsername(), hashed);
			if (vendorUser != null) {
				String token = issueToken(vendorUser, webApp, 500);
				VendorUserHolder holder = new VendorUserHolder();
				holder.setToken(token);
				holder.setVendorUser(vendorUser);
				Response r = Response.status(200).entity(holder).build();
				return r;
			} else {
				throw new Exception();
			}
		} catch (Exception ex) {
			return Response.status(404).build();
		}
	}

	@SecuredUser
	@PUT
	@Path("vendor")
	public Response updateVendor(VendorHolder vendorHolder) {
		try {
			dao.update(vendorHolder.getVendor());
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// idempotant
	@SecuredUser
	@POST
	@Path("vendor")
	public Response createVendor(VendorHolder vendorHolder) {
		try {
			List<Vendor> vendors = dao.getCondition(Vendor.class, "name", vendorHolder.getVendor().getName());
			if (vendors.isEmpty()) {
				vendorHolder.getVendor().setCreated(new Date());
				dao.persist(vendorHolder.getVendor());
				return Response.status(200).build();
			} else {
				return Response.status(409).build();
			}
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("/vendor-score/best-quotation")
	public Response createBestQuotationVendorScore(Map<String, Long> map) {
		try {
			doUpdateScore(map, 10, "Best Quotation");
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("/vendor-score/second-quotation")
	public Response createSecondQuotationVendorScore(Map<String, Long> map) {
		try {
			doUpdateScore(map, 5, "Second Best Quotation");
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("/vendor-score/first-quotation")
	public Response createFirstQuotationVendorScore(Map<String, Long> map) {
		try {
			doUpdateScore(map, 2, "First Arrived Quotation");
			return Response.status(200).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("/vendor-score/incomplete-quotation")
	public Response createIncompleteQuotationVendorScore(Map<String, Long> map) {
		try {
			doUpdateScore(map, -10, "Incomplete Quotation");
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	private void doUpdateScore(Map<String, Long> map, int score, String sourceName) throws Exception {
		int makeId = map.get("makeId").intValue();
		Vendor vendor = dao.getReference(Vendor.class, map.get("vendorId").intValue());
		long entityId = map.get("entityId");
		Score vs = new Score(vendor, makeId, score, sourceName, entityId);
		dao.persist(vs);
	}

	private void createNewVendorScore(int vendorId, int makeId) throws Exception {
		Score score = new Score();
		Vendor vendor = dao.getReference(Vendor.class, vendorId);
		score.setVendor(vendor);
		score.setMakeId(makeId);
		score.setScore(1);
		score.setSource("Newly Joined");
		score.setDate(new Date());
		dao.persist(score);
	}

	// idempotent
	@SecuredUser
	@POST
	@Path("vendor-user")
	public Response createVendorUser(VendorUser vendorUser) {
		try {
			List<VendorUser> users = dao.getTwoConditions(VendorUser.class, "username", "vendor.id",
					vendorUser.getUsername(), vendorUser.getVendor().getId());
			if (users.isEmpty()) {
				vendorUser.setCreated(new Date());
				vendorUser.setPassword(Helper.cypher(vendorUser.getPassword()));
				dao.refreshCache();
				dao.persist(vendorUser);
				return Response.status(200).build();
			} else {
				return Response.status(409).build();
			}
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("/update-vendor-user")
	public Response updateVendorUser(VendorUser vendorUser) {
		try {
			dao.update(vendorUser);
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}

	}
	
	@SecuredUser
	@GET
	@Path("vendors/region/{param}")
	public Response getRegionsVendors(@PathParam(value="param") int regionId) {
		try {
			String jpql = "select b.vendor from VendorRegion b where b.regionId = :value0 order by b.vendor.id";
			List<Vendor> vendors = dao.getJPQLParams(Vendor.class, jpql, regionId);
			return Response.status(200).entity(vendors).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("regions-vendors")
	public Response getRegionsVendors() {
		try {
			List<VendorRegion> rvds = dao.get(VendorRegion.class);
			return Response.status(200).entity(rvds).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("vendor-region")
	public Response addVendorRegion(VendorRegion vr) {
		try {
			List<VendorRegion> vrds = dao.getTwoConditions(VendorRegion.class, "vendor", "regionId",
					vr.getVendor(), vr.getRegionId());
			if (!vrds.isEmpty()) {
				return Response.status(409).build();
			}
			Helper h = new Helper();
			String sql = "insert into vnd_region_vendor_default (vendor_id, region_id, created, created_by)" + "values("
					+ vr.getVendor().getId() + "," + vr.getRegionId() + ",'" + h.getDateFormat(new Date()) + "',"
					+ vr.getCreatedBy() + ")";
			dao.insertNative(sql);
			return Response.status(201).build();
		} catch (Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@POST
	@Path("vendor-make")
	public Response addVendorMake(VendorMake vendorMake) {
		try {
			List<VendorMake> vendorMakes = dao.getTwoConditions(VendorMake.class, "vendor", "makeId",
					vendorMake.getVendor(), vendorMake.getMakeId());
			if (!vendorMakes.isEmpty()) {
				return Response.status(409).build();
			}
			Helper h = new Helper();
			String sql = "insert into vnd_vendor_make (vendor_id, make_id, sales_percentage, created, created_by)"
					+ "values(" + vendorMake.getVendor().getId() + "," + vendorMake.getMakeId() + ","
					+ vendorMake.getPercentage() + ",'" + h.getDateFormat(new Date()) + "'," + vendorMake.getCreatedBy()
					+ ")";
			dao.insertNative(sql);
			createNewVendorScore(vendorMake.getVendor().getId(), vendorMake.getMakeId());
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@Secured
	@GET
	@Path("vendor-percentage/vendor/{param}/make/{param2}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getVendorPercentage(@PathParam(value = "param") int vendorId,
			@PathParam(value = "param2") int makeId) {
		try {
			List<VendorMake> vms = dao.getTwoConditions(VendorMake.class, "vendor.id", "makeId", vendorId, makeId);
			if (vms.isEmpty()) {
				return Response.status(404).build();
			}
			return Response.status(200).entity(vms.get(0).getPercentage()).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@DELETE
	@Path("vendor-make/vendor/{param}/make/{param2}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response removeVendorMake(@PathParam(value = "param") int vendorId,
			@PathParam(value = "param2") int makeId) {
		try {
			String sql = "delete from vnd_vendor_make where vendor_id = " + vendorId + " and make_id = " + makeId;
			dao.updateNative(sql);
			// log the delete operation
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("unselected-makes/vendor/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUnselectedMakes(@HeaderParam("Authorization") String authHeader,
			@PathParam(value = "param") int vendorId) {
		try {
			// get distinct make ids for this vendor
			String sql = "select distinct * from vnd_vendor_make where vendor_id = " + vendorId;
			List<VendorMake> vendorMakes = dao.getNative(VendorMake.class, sql);
			List<Integer> ints = new ArrayList<>();
			for (VendorMake vm : vendorMakes) {
				ints.add(vm.getMakeId());
			}
			Response r = this.postSecuredRequest(AppConstants.OTHER_ACTIVE_MAKES, ints, authHeader);
			if (r.getStatus() == 200) {
				String object = r.readEntity(String.class);
				return Response.status(200).entity(object).build();
			} else {
				return Response.status(200).entity(new ArrayList<>()).build();
			}
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("/get-selected-vendors/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getSelectedVendros(@PathParam(value = "param") int makeId) {
		try {
			// two top vendor with highest score in the past 45 days
			List<Vendor> vlist = getTopVendors(makeId, 45, 2);// two best vendors
			// vendor with least orders
			int vids[] = new int[vlist.size()];
			for (int i = 0; i < vids.length; i++) {
				vids[i] = vlist.get(i).getId();
			}
			Vendor v = getLeastVendor(makeId, 45, vids);
			if (null != v) {
				vlist.add(v);
			}
			return Response.status(200).entity(vlist).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("selected-vendor-ids/{param}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getSelectedVendroIds(@PathParam(value = "param") int makeId) {
		try {
			// two top vendor with highest score in the past 45 days
			List<Vendor> vlist = getTopVendors(makeId, 45, 2);// two best vendors
			// vendor with least orders
			int vids[] = new int[vlist.size()];
			for (int i = 0; i < vids.length; i++) {
				vids[i] = vlist.get(i).getId();
			}
			Vendor v = getLeastVendor(makeId, 45, vids);
			if (null != v) {
				vlist.add(v);
			}
			List<Integer> integerList = new ArrayList<>();
			for (Vendor vendor : vlist) {
				integerList.add(vendor.getId());
			}
			return Response.status(200).entity(integerList).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	// just get randomly another vendor not actually with least oreders
	private Vendor getLeastVendor(int makeid, int days, int[] vids) {
		String conds = "";
		for (int v : vids) {
			conds = conds + "," + v;
		}
		String sql = "select * from vnd_vendor" + " where status = 'A'" + " and id in ("
				+ " select vendor_id from vnd_vendor_make" + " where make_id = " + makeid + ")" + " and id not in (0"
				+ conds + ")" + " order by random() limit 1";
		List<Vendor> leastVendors = dao.getNative(Vendor.class, sql);
		if (null == leastVendors || leastVendors.isEmpty())
			return null;
		else {
			return leastVendors.get(0);
		}
	}

	private List<Vendor> getTopVendors(int makeid, int days, int limit) {
		// get top two
		String sql = "select * from vnd_vendor where status = 'A'" + " and id in ( "
				+ " select z.vendor_id from ( select vendor_id, sum(score) total from vnd_score"
				+ " where score_date > current_date - " + days + " and make_id = " + makeid
				+ " and vendor_id in (select vendor_id from vnd_vendor_make" + " where make_id = " + makeid + ")"
				+ " group by vendor_id" + " order by total desc , random() limit " + limit + ") z )";
		List<Vendor> topVendors = dao.getNative(Vendor.class, sql);
		return topVendors;
	}

	@SecuredUser
	@DELETE
	@Path("vendor-join/{param}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response archiveVendorJoin(@PathParam(value = "param") int vjrId) {
		try {
			VendorJoinRequest vjr = dao.find(VendorJoinRequest.class, vjrId);
			vjr.setArchived(true);
			dao.update(vjr);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@GET
	@Path("active-vendor-join-requests")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getActiveVendorJoinRequests() {
		try {
			String jpql = "select b from VendorJoinRequest b where b.archived = :value0 order by created desc";
			List<VendorJoinRequest> vjrs = dao.getJPQLParams(VendorJoinRequest.class, jpql, false);
			return Response.status(200).entity(vjrs).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@POST
	@ValidApp
	@Path("vendor-join-request")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createVendorJoinRequest(VendorJoinRequest vjr) {
		try {
			vjr.setArchived(false);
			vjr.setCreated(new Date());
			dao.persist(vjr);
			return Response.status(200).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}

	@SecuredVendor
	@POST
	@Path("/match-token")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response matchToken(AccessMap usermap) {
		try {
			WebApp webApp = getWebAppFromSecret(usermap.getAppSecret());
			List<AccessToken> l = dao.getFourConditionsAndDateBefore(AccessToken.class, "userId", "webApp.appCode",
					"status", "token", "expire", Integer.parseInt(usermap.getUsername()), webApp.getAppCode(), 'A',
					usermap.getCode(), new Date());
			if (!l.isEmpty()) {
				return Response.status(200).build();
			} else {
				return Response.status(403).build();// forbidden response
			}
		} catch (Exception e) {
			return Response.status(403).build();// unauthorized
		}
	}

	private String issueToken(VendorUser vendorUser, WebApp webApp, int expireMinutes) {
		deactivateOldTokens(vendorUser);
		Date tokenTime = new Date();
		AccessToken accessToken = new AccessToken(vendorUser.getId(), tokenTime);
		accessToken.setWebApp(webApp);
		accessToken.setExpire(Helper.addMinutes(tokenTime, expireMinutes));
		accessToken.setStatus('A');
		accessToken.setToken(Helper.getSecuredRandom());
		dao.persist(accessToken);
		return accessToken.getToken();
	}

	private void deactivateOldTokens(VendorUser vendorUser) {
		List<AccessToken> tokens = dao.getTwoConditions(AccessToken.class, "userId", "status", vendorUser.getId(), 'A');
		for (AccessToken t : tokens) {
			t.setStatus('K');// kill old token
			dao.update(t);
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

	// qualified
	public <T> Response postSecuredRequest(String link, T t, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.post(Entity.entity(t, "application/json"));// not secured
		return r;
	}

}
