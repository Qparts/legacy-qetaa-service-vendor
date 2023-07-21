package qetaa.service.vendor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="vnd_user")
public class VendorUser implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "vnd_user_id_seq_gen", sequenceName = "vnd_user_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "vnd_user_id_seq_gen")
	@Column(name="id")
	private int id;
	@JoinColumn(name="vendor_id")
	@ManyToOne
	private Vendor vendor;
	@Column(name="username")
	private String username;
	@Column(name="first_name")
	private String firstName;
	@Column(name="last_name")
	private String lastNAme;
	@Column(name="password")
	private String password;
	@Column(name="created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Column(name="created_by")
	private int createdBy;
	@Column(name="contact_number")
	private String contactNumber;
	@Column(name="email")
	private String email;
	@Column(name="status")
	private char status;
	public int getId() {
		return id;
	}
	public Vendor getVendor() {
		return vendor;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastNAme() {
		return lastNAme;
	}
	public String getUsername() {
		return username;
	}
	public String getPassword() {
		return password;
	}
	public Date getCreated() {
		return created;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public String getEmail() {
		return email;
	}
	public char getStatus() {
		return status;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setLastNAme(String lastNAme) {
		this.lastNAme = lastNAme;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	
	
	public int getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VendorUser other = (VendorUser) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	
}
