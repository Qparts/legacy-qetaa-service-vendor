package qetaa.service.vendor.model.test;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="tst_workshop_promocode")
public class TestWorkshopPromotion implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "tst_workshop_promocode_id_seq_gen", sequenceName = "tst_workshop_promocode_id_seq", initialValue=1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "tst_workshop_promocode_id_seq_gen")
	@Column(name="id")
	private long id;
	
	@Column(name="workshop_id")
	private int workshopId;
	
	
	@Column(name="city_id")
	private int cityId;
	
	@Column(name="customer_id")
	private long customerId;
	
	@Column(name="cart_id")
	private long cartId;

	@Column(name="code")
	private String promoCode;
	
	@Column(name="created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	
	@Column(name="labor_amount")
	private Double laborAmount;
	
	@Column(name="used")
	@Temporal(TemporalType.TIMESTAMP)
	private Date used;
	
	@Column(name="checked")
	@Temporal(TemporalType.TIMESTAMP)
	private Date checked;
	
	@Column(name="expire")
	@Temporal(TemporalType.TIMESTAMP)
	private Date expire;
	
	@Column(name="status")
	private char status;//N = new , C = checked, U = Used
	
	

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getCityId() {
		return cityId;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public long getCartId() {
		return cartId;
	}

	public void setCartId(long cartId) {
		this.cartId = cartId;
	}

	public String getPromoCode() {
		return promoCode;
	}

	public void setPromoCode(String promoCode) {
		this.promoCode = promoCode;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUsed() {
		return used;
	}

	public void setUsed(Date used) {
		this.used = used;
	}

	public Date getExpire() {
		return expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	public int getWorkshopId() {
		return workshopId;
	}

	public void setWorkshopId(int workshopId) {
		this.workshopId = workshopId;
	}

	public Double getLaborAmount() {
		return laborAmount;
	}

	public void setLaborAmount(Double laborAmount) {
		this.laborAmount = laborAmount;
	}

	public Date getChecked() {
		return checked;
	}

	public void setChecked(Date checked) {
		this.checked = checked;
	}
	
	
}
