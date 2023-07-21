package qetaa.service.vendor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VendorHolder implements Serializable{

	private static final long serialVersionUID = 1L;
	private Vendor vendor;
	private List<VendorMake> vendorMakes;
	private List<VendorUser> vendorUsers;
	
	public VendorHolder(){
		vendor = new Vendor();
		vendorMakes = new ArrayList<>();
		vendorUsers = new ArrayList<>();
	}
	
	
	public Vendor getVendor() {
		return vendor;
	}
	public List<VendorMake> getVendorMakes() {
		return vendorMakes;
	}
	public List<VendorUser> getVendorUsers() {
		return vendorUsers;
	}
	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
	public void setVendorMakes(List<VendorMake> vendorMakes) {
		this.vendorMakes = vendorMakes;
	}
	public void setVendorUsers(List<VendorUser> vendorUsers) {
		this.vendorUsers = vendorUsers;
	}
	
	
}
