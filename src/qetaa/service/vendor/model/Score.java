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
@Table(name = "vnd_score")
public class Score implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@SequenceGenerator(name = "vnd_score_id_seq_gen", sequenceName = "vnd_score_id_seq", initialValue = 1, allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "vnd_score_id_seq_gen")
	@Column(name = "id")
	private long id;
	@JoinColumn(name = "vendor_id")
	@ManyToOne
	private Vendor vendor;
	@Column(name = "make_id")
	private int makeId;
	@Column(name = "score")
	private int score;
	@Column(name = "score_source")
	private String source;// Newly Joined, Best Quotation
	@Column(name = "score_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;
	@Column(name = "cart_id")
	private long cartId;// Cart ID


	public Score() {

	}

	public Score(Vendor vendor, int makeId, int score, String source, long cartId) {
		this.vendor = vendor;
		this.makeId = makeId;
		this.score = score;
		this.source = source;
		this.cartId = cartId;
		this.date = new Date();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Vendor getVendor() {
		return vendor;
	}

	public void setVendor(Vendor vendor) {
		this.vendor = vendor;
	}

	public int getMakeId() {
		return makeId;
	}

	public void setMakeId(int makeId) {
		this.makeId = makeId;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getCartId() {
		return cartId;
	}

	public void setCartId(long cartId) {
		this.cartId = cartId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + (int) (cartId ^ (cartId >>> 32));
		result = prime * result + makeId;
		result = prime * result + score;
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((vendor == null) ? 0 : vendor.hashCode());
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
		Score other = (Score) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (id != other.id)
			return false;
		if (cartId != other.cartId)
			return false;
		if (makeId != other.makeId)
			return false;
		if (score != other.score)
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (vendor == null) {
			if (other.vendor != null)
				return false;
		} else if (!vendor.equals(other.vendor))
			return false;
		return true;
	}


	
	
}
