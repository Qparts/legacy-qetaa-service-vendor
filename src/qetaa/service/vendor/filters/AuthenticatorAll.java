package qetaa.service.vendor.filters;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import qetaa.service.vendor.dao.DAO;
import qetaa.service.vendor.helpers.AccessMap;
import qetaa.service.vendor.helpers.AppConstants;
import qetaa.service.vendor.model.security.AccessToken;
import qetaa.service.vendor.model.security.WebApp;



@Secured
@Provider 
@Priority(Priorities.AUTHENTICATION)
public class AuthenticatorAll implements ContainerRequestFilter {

	@EJB
	private DAO dao; 

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// GET http autherization header from the request
		String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
		// check if http authorization header is present and formatted correctly
		if (authHeader == null || !authHeader.startsWith("Bearer") || authHeader.startsWith("Bearer null")) {
			requestContext.abortWith(Response.status(401).build());
		} else {
			String[] values = authHeader.split("&&");
			String tokenPart = values[0];
			String token = tokenPart.substring("Bearer".length()).trim();
			String username = values[1].trim();
			String appSecret = values[2].trim();
			String type = values[3].trim();//C customer, U = user
			try {
				// Validate the token
				matchToken(token, username, appSecret, type, authHeader);
			} catch (NotAuthorizedException e) {
				requestContext.abortWith(Response.status(401).build());
			} catch (Exception e) {
				requestContext.abortWith(Response.status(401).build());
			}
		}
	}
	
	public void matchToken(String token, String username, String appSecret, String type, String authHeader) throws NotAuthorizedException, Exception{
		if(type.equals("U")){
			AccessMap map = new AccessMap(username, appSecret, token, "");
			Response r = this.postSecuredRequest(AppConstants.USER_MATCH_TOKEN, map, authHeader);
			if(r.getStatus() != 200){
				throw new NotAuthorizedException("Request authorization failed");
			}
		}
		else if(type.equals("V")){
			WebApp webApp = getWebAppFromSecret(appSecret);
			List<AccessToken> l = dao.getFourConditionsAndDateBefore(AccessToken.class, "userId", "webApp.appCode",
					"status", "token", "expire", Integer.parseInt(username), webApp.getAppCode(), 'A', token, new Date());
			if (l.isEmpty()) {
				throw new NotAuthorizedException("Request authorization failed");
			}
			
		}
		else if(type.equals("C")){
			AccessMap map = new AccessMap(username, appSecret, token, "");
			Response r = this.postSecuredRequest(AppConstants.CUSTOMER_MATCH_TOKEN, map, authHeader);
			if(r.getStatus() != 200){
				throw new NotAuthorizedException("Request authorization failed");
			}
		}
	}

	
	public <T> Response postSecuredRequest(String link, T t, String authHeader) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, authHeader);
		Response r = b.post(Entity.entity(t, "application/json"));// not secured
		return r;
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
