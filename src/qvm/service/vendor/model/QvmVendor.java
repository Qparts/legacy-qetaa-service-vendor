package qvm.service.vendor.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="qvm_vendor")
public class QvmVendor implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "qvm_vendor_id_seq_gen", sequenceName = "qvm_vendor_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "qvm_vendor_id_seq_gen")
	@Column(name="id")
	private int id;
	@Column(name="company_name")
	private String name;
	@Column(name="status")
	private char status;//N = new (needs verification), V = Verified, A = Approved, R= Rejected
	@Column(name="created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Column(name="created_by")
	private int createdBy;
	@Column(name="delivery")
	private boolean delivery;
	@Transient
	private List<QvmBranch> branches;
	@Transient
	private List<QvmVendorMake> vendorMakes;
	
	public List<QvmVendorMake> getVendorMakes() {
		return vendorMakes;
	}
	
	public void setVendorMakes(List<QvmVendorMake> vendorMakes) {
		this.vendorMakes = vendorMakes;
	}
	
	public List<QvmBranch> getBranches() {
		return branches;
	}
	public void setBranches(List<QvmBranch> branches) {
		this.branches = branches;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	public boolean isDelivery() {
		return delivery;
	}
	public void setDelivery(boolean delivery) {
		this.delivery = delivery;
	}
	
	
}
