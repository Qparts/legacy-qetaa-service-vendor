package qetaa.service.vendor.restful;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import qetaa.service.vendor.dao.DAO;
import qetaa.service.vendor.filters.Secured;
import qetaa.service.vendor.filters.SecuredUser;
import qetaa.service.vendor.helpers.Helper;
import qetaa.service.vendor.model.PromotionCode;
import qetaa.service.vendor.model.PromotionProvider;
import qetaa.service.vendor.model.test.TestWorkshopPromotion;

@Path("/promotion")
public class PromotionService {

	@EJB
	private DAO dao;

	
	@SecuredUser
	@Path("provider/{param}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProvider(@PathParam(value="param") int id) {
		try {
			PromotionProvider p = dao.find(PromotionProvider.class, id);
			if(p == null) {
				return Response.status(404).build();
			}
			else {
				List<PromotionCode> codes = dao.getCondition(PromotionCode.class, "provider", p);
				p.setPromotionCodes(codes);
			}
			return Response.status(200).entity(p).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	@SecuredUser
	@Path("all-providers")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAllProviders() {
		try {
			List<PromotionProvider> ps = dao.get(PromotionProvider.class);
			return Response.status(200).entity(ps).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	@SecuredUser
	@Path("generate-code/bulk")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateCodeBulk(Map<String,Object> map) {
		try {
			Integer providerId = (Integer) map.get("providerId");
			Integer bulkNumber = (Integer) map.get("bulkNumber");
			Long expireLong = (Long) map.get("expire");
			Boolean discountPromo = (Boolean) map.get("discountPromo");
			Boolean reusable = (Boolean) map.get("reusable");
			Double discountPercentage = (Double) map.get("discountPercentage");
			
			Date expire = new Date(expireLong);
			PromotionProvider provider = dao.find(PromotionProvider.class, providerId);
			if(provider == null) {
				return Response.status(404).build();
			}
			//check if already created
			Date previous = Helper.addSeconds(expire, 10);
			String jpql  = "select b from PromotionCode b where b.provider.id = :value0 and created > :value1";
			List<PromotionCode> check = dao.getJPQLParams(PromotionCode.class, jpql, providerId, previous);
			if(!check.isEmpty()) {
				return Response.status(429).build();
			}
			
			for(int i = 0; i < bulkNumber; i++ ) {
				PromotionCode pcode = new PromotionCode();
				String code = "";
				boolean available = false;
				while (!available) {
					code = Helper.getRandomSaltString(6);
					String jpql2 = "select b from PromotionCode b where b.code = :value0 and b.expire > :value1";
					List<PromotionCode> codes = dao.getJPQLParams(PromotionCode.class, jpql2, code, new Date());
					if (codes.isEmpty()) {
						available = true;
					}
				}
				pcode.setReusable(reusable);
				pcode.setDiscountPromo(discountPromo);
				pcode.setDiscountPercentage(discountPercentage);
				pcode.setCode(code);
				pcode.setProvider(provider);
				pcode.setCreated(new Date());
				pcode.setExpire(expire);
				dao.persist(pcode);
			}
			return Response.status(201).build();
			
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	@Path("generate-code")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createCode(Map<String,String> string) {
		try {
			PromotionProvider provider = dao.findCondition(PromotionProvider.class, "secret", string.get("secret"));
			if(provider == null) {
				return Response.status(404).build();
			}
			PromotionCode pcode = new PromotionCode();
			String code = "";
			boolean available = false;
			while (!available) {
				code = Helper.getRandomSaltString(6);
				String jpql = "select b from PromotionCode b where b.code = :value0 and b.expire > :value1";
				List<PromotionCode> codes = dao.getJPQLParams(PromotionCode.class, jpql, code, new Date());
				if (codes.isEmpty()) {
					available = true;
				}
			}
			pcode.setCode(code);
			pcode.setProvider(provider);
			pcode.setCreated(new Date());
			pcode.setExpire(Helper.addMinutes(pcode.getCreated(), 60*24*10));
			dao.persist(pcode);
			Map<String,String> map = new HashMap<String,String>();
			map.put("code", pcode.getCode());
			return Response.status(200).entity(map).build();
		}catch(Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}

	@SecuredUser
	@Path("provider")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createProvider(PromotionProvider provider) {
		try {
			List<PromotionProvider> ps = dao.getCondition(PromotionProvider.class, "name", provider.getName().trim());
			if (!ps.isEmpty()) {
				return Response.status(409).build();
			}
			String secret = "";
			boolean available = false;
			while (!available) {
				secret = Helper.getRandomSaltString(16);
				List<PromotionProvider> provs = dao.getCondition(PromotionProvider.class, "secret", secret);
				if (provs.isEmpty()) {
					available = true;
				}
			}
			provider.setSecret(secret);
			provider.setName(provider.getName().trim());
			provider.setNameAr(provider.getNameAr().trim());
			provider.setCreated(new Date());
			dao.persist(provider);
			return Response.status(201).build();
		} catch (Exception ex) {
			return Response.status(500).build();
		}
	}
	
	@Secured
	@Path("promotion-code/{param}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPromoCode(@PathParam(value="param") int id) {
		try {
			PromotionCode pc = dao.find(PromotionCode.class, id);
			return Response.status(200).entity(pc).build();
		}catch(Exception ex) {
			ex.printStackTrace();
			return Response.status(500).build();
		}
	}
	
	@Secured
	@Path("promotion-codes/discount-only")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getDiscountPromoCodes() {
		try {
			List<PromotionCode> disc =dao.getCondition(PromotionCode.class, "discountPromo", true);
			return Response.status(200).entity(disc).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	@Secured
	@Path("promotion-code/{param}/discount")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPromotionDiscount(@PathParam(value="param") Integer promId) {
		try {
			PromotionCode pcode = dao.find(PromotionCode.class, promId);
			if(pcode.isDiscountPromo()) {
				return Response.status(200).entity(pcode.getDiscountPercentage()).build();
			}
			throw new Exception();
		}catch(Exception ex) {
			return Response.status(200).entity(0D).build();
		}
	}
	
	@Path("use-test-workshop-promo-code")
	@Consumes(MediaType.APPLICATION_JSON)
	@PUT
	public Response updateWorkshopTestPromoCode(TestWorkshopPromotion wPromo) {
		try {
			wPromo.setUsed(new Date());
			wPromo.setStatus('U');
			dao.update(wPromo);
			return Response.status(201).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	@Path("find-test-workshop-promo-code/code/{code}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response  getTestWorkshopPromoCodeFromCode(@PathParam(value="code") String code) {
		try {
			String jpql = "select b from TestWorkshopPromotion b "
					+ " where b.expire > :value0"
					+ " and b.promoCode = :value1"
					+ " and b.status in (:value2, :value3)";
			TestWorkshopPromotion pc = dao.findJPQLParams(TestWorkshopPromotion.class, jpql, new Date(), code, 'N', 'C');
			if(pc != null) {
				if(pc.getStatus() == 'N') {
					pc.setChecked(new Date());
					pc.setStatus('C');
					dao.update(pc);
				}
				return Response.status(200).entity(pc).build();
			}
			else {
				String jpql2 = "select b from TestWorkshopPromotion b "
						+ " where b.expire < :value0"
						+ " and b.promoCode = :value1"
						+ " and b.status in (:value2, :value3)"
						+ " order by b.created desc";
				List<TestWorkshopPromotion> pcs = dao.getJPQLParams(TestWorkshopPromotion.class, jpql2, new Date(), code, 'N', 'C');
				if(!pcs.isEmpty()) {
					TestWorkshopPromotion theone = pcs.get(pcs.size() - 1);
					return Response.status(498).entity(theone).build();//expired code
				}
			}
			return Response.status(404).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	
	@Secured
	@Path("generate-test-workshop-promo-code/cart/{cart}/city/{city}/customer/{customer}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response generateTestWorkshopPromoCode(@PathParam(value="cart") long cartId, @PathParam(value="city") int cityId, @PathParam(value="customer") long customerId) {
		try {
			if(cityId == 2 || cityId == 3 || cityId == 9) {
				String code = "";
				boolean available = false;
				while (!available) {
					code = Helper.getRandomSaltString(6);
					String jpql2 = "select b from TestWorkshopPromotion b where b.promoCode = :value0 and b.expire > :value1";
					List<PromotionCode> codes = dao.getJPQLParams(PromotionCode.class, jpql2, code, new Date());
					if (codes.isEmpty()) {
						available = true;
					}
				}
				
				TestWorkshopPromotion wp = new TestWorkshopPromotion();
				wp.setCartId(cartId);
				wp.setCityId(cityId);
				wp.setCreated(new Date());
				wp.setCustomerId(customerId);
				wp.setExpire(Helper.addMinutes(wp.getCreated(), 60*24*45));
				wp.setWorkshopId(1);
				wp.setPromoCode(code);
				wp.setStatus('N');//new 
				dao.persist(wp);
				return Response.status(200).entity(wp.getPromoCode()).build();

			}else {
				return Response.status(404).build();
			}
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	
	@Secured
	@Path("promotion-code/code/{param}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getPromCode(@PathParam(value="param") String code) {
		try {
			String jpql = "select b from PromotionCode b where b.used is null"
					+ " and b.customerId is null"
					+ " and b.cartId is null"
					+ " and b.expire > :value0"
					+ " and b.code = :value1"
					+ " and b.provider.status = :value2";
			PromotionCode pc = dao.findJPQLParams(PromotionCode.class, jpql, new Date(), code, 'A');
			if(pc != null) {
				return Response.status(200).entity(pc).build();
			}
			else {
				PromotionCode pc2 = dao.findCondition(PromotionCode.class, "code", code);
				if(pc2 != null) {
					if(pc2.getExpire().before(new Date())){
						return Response.status(498).build();//expired
					}
					else if (null != pc2.getUsed()) {
						return Response.status(410).build();//used
					}
				}
			}
			return Response.status(404).build();
		}catch(Exception ex) {
			return Response.status(500).build();
		}
	}
	
	
}
