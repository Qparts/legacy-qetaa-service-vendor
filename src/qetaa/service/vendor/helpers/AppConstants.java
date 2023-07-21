package qetaa.service.vendor.helpers;

public class AppConstants {
	private static final String CUSTOMER_SERVICE = "http://localhost:8080/service-qetaa-customer/rest/";
	private static final String USER_SERVICE = "http://localhost:8080/service-qetaa-user/rest/";
	private static final String VEHICLE_SERVICE = "http://localhost:8080/service-qetaa-vehicle/rest/";
	
	
	public static final String CUSTOMER_MATCH_TOKEN = CUSTOMER_SERVICE + "match-token";
	public static final String USER_MATCH_TOKEN = USER_SERVICE + "match-token";
	public static final String OTHER_ACTIVE_MAKES = VEHICLE_SERVICE + "all-other-active-makes";
	

}
