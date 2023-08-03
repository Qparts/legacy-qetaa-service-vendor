package qvm.service.vendor.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="qvm_vendor_makes")
@IdClass(QvmVendorMake.QvmVendorMakePK.class)
public class QvmVendorMake implements Serializable{
	
	private static final long serialVersionUID = 1L;
	@Id
	@JoinColumn(name="vendor_id", updatable=false, insertable=false)
	@ManyToOne(cascade= {CascadeType.REMOVE})
	private QvmVendor qvmVendor;
	@Id
	@Column(name="make_id")
	private int makeId;
	@Column(name="created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;
	@Column(name="created_by")
	private int createdBy;
	@Column(name="genuine")
	private boolean genuine;
	@Column(name="none_genuine")
	private boolean noneGenuine;
	
	
	public boolean isGenuine() {
		return genuine;
	}
	public void setGenuine(boolean genuine) {
		this.genuine = genuine;
	}
	public boolean isNoneGenuine() {
		return noneGenuine;
	}
	public void setNoneGenuine(boolean noneGenuine) {
		this.noneGenuine = noneGenuine;
	}
	public QvmVendor getQvmVendor() {
		return qvmVendor;
	}
	public void setQvmVendor(QvmVendor qvmVendor) {
		this.qvmVendor = qvmVendor;
	}
	public int getMakeId() {
		return makeId;
	}
	public void setMakeId(int makeId) {
		this.makeId = makeId;
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
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + createdBy;
		result = prime * result + makeId;
		result = prime * result + ((qvmVendor == null) ? 0 : qvmVendor.hashCode());
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
		QvmVendorMake other = (QvmVendorMake) obj;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (createdBy != other.createdBy)
			return false;
		if (makeId != other.makeId)
			return false;
		if (qvmVendor == null) {
			if (other.qvmVendor != null)
				return false;
		} else if (!qvmVendor.equals(other.qvmVendor))
			return false;
		return true;
	}



	public static class QvmVendorMakePK implements Serializable{
		private static final long serialVersionUID = 1L;
		protected int qvmVendor;
		protected int makeId;
		public QvmVendorMakePK() {}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + qvmVendor;
			result = prime * result + makeId;
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
			QvmVendorMakePK other = (QvmVendorMakePK) obj;
			if (qvmVendor != other.qvmVendor)
				return false;
			if (makeId != other.makeId)
				return false;
			return true;
		}
		
		
		
	}
	
}
