package qetaa.service.vendor.model;

import java.io.Serializable;

public class VendorUserHolder implements Serializable{

	private static final long serialVersionUID = 1L;
	private VendorUser vendorUser;
	private String token;
	
	public VendorUser getVendorUser() {
		return vendorUser;
	}
	public String getToken() {
		return token;
	}
	public void setVendorUser(VendorUser vendorUser) {
		this.vendorUser = vendorUser;
	}
	public void setToken(String token) {
		this.token = token;
	}
	
}
