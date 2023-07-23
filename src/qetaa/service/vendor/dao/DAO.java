package qetaa.service.vendor.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

@Stateless
public class DAO {

	@PersistenceContext(unitName = "QetaaVendorPU")
	private EntityManager em;

	@SuppressWarnings("unchecked")
	public <T> List<T> get(Class<T> klass) {
		return (List<T>) em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b").getResultList();
	}

	public <T> List<T> getOrderBy(Class<T> klass, String orderColumn) {
		return (List<T>) em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b order by " + orderColumn)
				.getResultList();
	}

	public List getNative(Class klass, String sql) {
		return em.createNativeQuery(sql, klass).getResultList();
	}

	public List getNative(String sql) {
		return em.createNativeQuery(sql).getResultList();
	}

	public void updateNative(String sql) {
		em.createNativeQuery(sql).executeUpdate();
	}

	public void createNative(String sql) {
		em.createNativeQuery(sql);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> getJPQLParams(Class<T> klass, String jpql, Object... vals) {
		Query q = em.createQuery(jpql);
		int i = 0;
		for (Object o : vals) {
			this.setParameter(q, "value" + i, o);
			i++;
		}
		return (List<T>) q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public <T> T findJPQLParams(Class<T> klass, String jpql, Object... vals) {
		try {
			Query q = em.createQuery(jpql);
			int i = 0;
			for (Object o : vals) {
				this.setParameter(q, "value" + i, o);
				i++;
			}
			return (T) q.getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public Object getNativeSingle(String sql) {
		Object o;
		try {
			o = em.createNativeQuery(sql).getSingleResult();
		} catch (NoResultException nre) {
			o = null;
		}
		return o;
	}

	public <T> T getNativeSingle(Class<T> klass, String sql) {
		try {
			return (T) em.createNativeQuery(sql).getSingleResult();
		} catch (Exception e) {
			return null;
		}

	}

	public <T> T find(Class<T> klass, Object obj) {
		try {
			return em.find(klass, obj);
		} catch (Exception ex) {
			return null;
		}
	}
	/*
	 * public Object findCondition(Class klass, String columnName, Object val) {
	 * Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() +
	 * " b WHERE b." + columnName + " = :value"); this.setParameter(q, "value",
	 * val); return q.getSingleResult(); }
	 */

	public <T> T findCondition(Class<T> klass, String columnName, Object val) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value");
		this.setParameter(q, "value", val);
		List<T> list = q.getResultList();
		if (!list.isEmpty())
			return (T) list.get(0);
		else
			return null;
	}

	public <T> T findTwoConditions(Class<T> klass, String columnName, String columnName2, Object val, Object val2) {
		try {
			Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
					+ " AND b." + columnName2 + "= :value2");
			this.setParameter(q, "value", val);
			this.setParameter(q, "value2", val2);
			return (T) q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public <T> T findThreeConditions(Class<T> klass, String columnName, String columnName2, String columnName3,
			Object val, Object val2, Object val3) {
		try {
			Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
					+ " AND b." + columnName2 + "= :value2" + " AND b." + columnName3 + "= :value3");
			this.setParameter(q, "value", val);
			this.setParameter(q, "value2", val2);
			this.setParameter(q, "value3", val3);
			return (T) q.getSingleResult();
		} catch (EJBException e) {
			return null;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public <T> T findFourConditions(Class<T> klass, String columnName, String columnName2, String columnName3,
			String columnName4, Object val, Object val2, Object val3, Object val4) {
		try {
			Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
					+ " AND b." + columnName2 + "= :value2" + " AND b." + columnName3 + "= :value3" + " AND b."
					+ columnName4 + " = :value4");
			this.setParameter(q, "value", val);
			this.setParameter(q, "value2", val2);
			this.setParameter(q, "value3", val3);
			this.setParameter(q, "value4", val4);
			return (T) q.getSingleResult();
		} catch (EJBException e) {
			return null;
		} catch (NoResultException e) {
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	public <T> T findFourConditionsAndDateBefore(Class<T> klass, String columnName, String columnName2,
			String columnName3, String columnName4, String dateColumn, Object val, Object val2, Object val3,
			Object val4, Date date) {
		try {
			Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b" + " WHERE b." + columnName
					+ " = :value" + " AND b." + columnName2 + "= :value2" + " AND b." + columnName3 + "= :value3"
					+ " AND b." + columnName4 + "= :value4" + " AND :value5" + " < " + "b." + dateColumn);
			this.setParameter(q, "value", val);
			this.setParameter(q, "value2", val2);
			this.setParameter(q, "value3", val3);
			this.setParameter(q, "value4", val4);
			this.setDateTimeParameter(q, "value5", date);
			return (T) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	public <T> T findFiveConditionsAndDateBefore(Class<T> klass, String columnName, String columnName2,
			String columnName3, String columnName4, String columnName5, String dateColumn, Object val, Object val2,
			Object val3, Object val4, Object val5, Date date) {
		try {
			Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b" + " WHERE b." + columnName
					+ " = :value" + " AND b." + columnName2 + "= :value2" + " AND b." + columnName3 + "= :value3"
					+ " AND b." + columnName4 + "= :value4" + " AND b." + columnName5 + "= :value5" + " AND :value6"
					+ " < " + "b." + dateColumn);
			this.setParameter(q, "value", val);
			this.setParameter(q, "value2", val2);
			this.setParameter(q, "value3", val3);
			this.setParameter(q, "value4", val4);
			this.setParameter(q, "value5", val5);
			this.setDateTimeParameter(q, "value6", date);
			return (T) q.getSingleResult();
		} catch (javax.persistence.NoResultException e) {
			return null;
		}
	}

	/*
	 * public List getCondition(Class klass, String columnName, Object val) { Query
	 * q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." +
	 * columnName + " = :value"); this.setParameter(q, "value", val); return
	 * q.getResultList(); }
	 */

	public <T> List<T> getCondition(Class<T> klass, String columnName, Object val) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value");
		this.setParameter(q, "value", val);
		return q.getResultList();
	}

	public <T, S> List<S> getConditionClassColumn(Class<T> klass, Class<S> selectedClass, String selectedcolumn,
			String columnName, Object val) {
		Query q = em.createQuery("SELECT b." + selectedcolumn + " From " + klass.getSimpleName() + " b " + " WHERE b."
				+ columnName + " = :value");
		this.setParameter(q, "value", val);
		return q.getResultList();
	}

	public <T, S> List<S> getTwoConditionsClassColumn(Class<T> klass, Class<S> selectedClass, String selectedcolumn,
			String columnName, String columnName2, Object val, Object val2) {
		Query q = em.createQuery("SELECT b." + selectedcolumn + " From " + klass.getSimpleName() + " b " + " WHERE b."
				+ columnName + " = :value" + " AND b." + columnName2 + " = :value2");
		this.setParameter(q, "value", val);
		this.setParameter(q, "value2", val2);
		return q.getResultList();
	}

	public <T> List<T> getConditionIsNull(Class<T> klass, String columnName) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " is null");
		return q.getResultList();
	}

	/*
	 * public List getTwoConditions(Class klass, String columnName, String
	 * columnName2, Object val, Object val2) { Query q =
	 * em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." +
	 * columnName + " = :value" + " AND b." + columnName2 + "= :value2");
	 * this.setParameter(q, "value", val); this.setParameter(q, "value2", val2);
	 * return q.getResultList(); }
	 */

	public <T> List<T> getTwoConditions(Class<T> klass, String columnName, String columnName2, Object val,
			Object val2) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
				+ " AND b." + columnName2 + "= :value2");
		this.setParameter(q, "value", val);
		this.setParameter(q, "value2", val2);
		return q.getResultList();
	}

	public <T> List<T> getTwoOrConditions(Class<T> klass, String columnName, String columnName2, Object val,
			Object val2) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
				+ " OR b." + columnName2 + "= :value2");
		this.setParameter(q, "value", val);
		this.setParameter(q, "value2", val2);
		return q.getResultList();
	}

	public <T> List<T> getThreeConditions(Class<T> klass, String columnName, String columnName2, String columnName3,
			Object val, Object val2, Object val3) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
				+ " AND b." + columnName2 + "= :value2" + " AND b." + columnName3 + "= :value3");
		this.setParameter(q, "value", val);
		this.setParameter(q, "value2", val2);
		this.setParameter(q, "value3", val3);
		return q.getResultList();
	}

	public <T> List<T> getThreeConditionsAndDateBefore(Class<T> klass, String columnName, String columnName2,
			String columnName3, String dateColumn, Object val, Object val2, Object val3, Date dateVal) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
				+ " AND b." + columnName2 + "= :value2" + " AND b." + columnName3 + "= :value3" + " AND b." + dateColumn
				+ " > :value4");
		this.setParameter(q, "value", val);
		this.setParameter(q, "value2", val2);
		this.setParameter(q, "value3", val3);
		this.setParameter(q, "value4", dateVal);
		return q.getResultList();
	}

	public <T> List<T> getTwoConditionsAndDateBefore(Class<T> klass, String columnName, String columnName2,
			String dateColumn, Object val, Object val2, Date dateVal) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
				+ " AND b." + columnName2 + "= :value2" + " AND b." + dateColumn + " > :value3");
		this.setParameter(q, "value", val);
		this.setParameter(q, "value2", val2);
		this.setParameter(q, "value3", dateVal);
		return q.getResultList();
	}

	public <T> List<T> getFourConditionsAndDateBefore(Class<T> klass, String columnName, String columnName2,
			String columnName3, String columnName4, String dateColumn, Object val, Object val2, Object val3,
			Object val4, Date dateVal) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
				+ " AND b." + columnName2 + "= :value2" + " AND b." + columnName3 + "= :value3" + " AND b."
				+ columnName4 + "= :value4" + " AND b." + dateColumn + " > :value5");
		this.setParameter(q, "value", val);
		this.setParameter(q, "value2", val2);
		this.setParameter(q, "value3", val3);
		this.setParameter(q, "value4", val4);
		this.setParameter(q, "value5", dateVal);
		return q.getResultList();
	}

	public List getTwoConditionsOrdered(Class klass, String columnName, String columnName2, Object val, Object val2,
			String orderBy, String direction) {
		Query q = em.createQuery("SELECT b FROM " + klass.getSimpleName() + " b WHERE b." + columnName + " = :value"
				+ " AND b." + columnName2 + "= :value2 ORDER BY b." + orderBy + " " + direction);
		this.setParameter(q, "value", val);
		this.setParameter(q, "value2", val2);
		return q.getResultList();
	}

	private void setDateTimeParameter(Query q, String name, Date val) {
		if (val instanceof Date) {
			Date d = (Date) val;
			Calendar c = Calendar.getInstance();
			c.setTime(d);
			q.setParameter(name, c, TemporalType.TIMESTAMP);
		}
	}

	private void setParameter(Query q, String name, Object val) {
		if (val instanceof Date) {
			Date d = (Date) val;
			q.setParameter(name, d, TemporalType.TIMESTAMP);
		} else if (val instanceof Calendar) {
			Calendar c = (Calendar) val;
			q.setParameter(name, c, TemporalType.TIMESTAMP);
		} else {
			q.setParameter(name, val);
		}
	}

	public void refreshCache() {
		em.clear();
		em.getEntityManagerFactory().getCache().evictAll();
	}

	public <T> T persistAndReturn(T t) {
		em.persist(t);
		em.flush();
		return t;
	}

	public <T> void persist(T t) {
		em.persist(t);
	}

	public <T> T getReference(Class<T> klass, Object id) {
		return em.getReference(klass, id);
	}

	public void delete(Object object) {
		object = em.merge(object);
		em.remove(object);
	}

	public void insertNative(String sql) {
		Query q = em.createNativeQuery(sql);
		q.executeUpdate();
	}

	public void update(Object obj) {
		em.merge(obj);
	}

}
