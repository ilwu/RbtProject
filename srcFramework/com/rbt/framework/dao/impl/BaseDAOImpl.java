package com.rbt.framework.dao.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.rbt.framework.dao.BaseDAO;
import com.rbt.framework.model.Command;
import com.rbt.util.BeanUtil;

/**
 * Base class for BaseDAO
 * @see BaseDAO
 */
public class BaseDAOImpl extends HibernateDaoSupport implements BaseDAO {
	protected Log LOG = LogFactory.getLog(getClass().getName());
	private DataSource dataSource;

	/**
	 *
	 */
	public BaseDAOImpl() {
	}


	/* (non-Javadoc)
	 * @see com.rbt.framework.dao.BaseDAO#bulkSave(java.util.List)
	 */
	public void bulkSave(List<Command> commandList) {
		this.logger.info("bulkSave size: " + commandList.size());
		long start_time = System.currentTimeMillis();
		long save_time = 0;

		Session session = this.getSessionFactory().openSession();
		Transaction tx = session.beginTransaction();
		//
		int i = 0;
		for (Command command : commandList) {
			session.saveOrUpdate(command);
			if ( i % 20 == 0 ) { //20, same as the JDBC batch size
				//flush a batch of inserts and release memory:
				session.flush();
				session.clear();
			}
		}
		tx.commit();
		save_time = System.currentTimeMillis();
		this.logger.info("Total time: " + (save_time - start_time));
	}


	/* (non-Javadoc)
	 * @see com.rbt.framework.dao.BaseDAO#bulkSaveOrUpdate(java.util.List)
	 */
	@Override
	public void bulkSaveOrUpdate(List<Command> commandList) {
		this.logger.info("bulkSaveOrUpdate size: " + commandList.size());
		long start_time = System.currentTimeMillis();
		long save_time = 0;
		Session session = this.getSessionFactory().openSession();
		session.setCacheMode(CacheMode.IGNORE);
		Transaction tx = session.beginTransaction();
		int i = 0;

		for (Command command : commandList) {
			session.saveOrUpdate(command);
			if ( i % 20 == 0 ) { //20, same as the JDBC batch size
				//flush a batch of inserts and release memory:
				session.flush();
				session.evict(command);
			}
		}
		tx.commit();
		save_time = System.currentTimeMillis();
		this.logger.info("Total Time: " + (save_time - start_time));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#save(com.rbt.framework.model.Command)
	 */
	@Override
	public void save(Command command) {
		this.getSessionFactory().getCurrentSession().save(command);
		// getHibernateTemplate().save(command);
		// getHibernateTemplate().flush();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#update(com.rbt.framework.model.Command)
	 */
	@Override
	public void update(Command command) {
		Session session = this.getSessionFactory().getCurrentSession();
		session.merge(command);
		session.flush();
		// getHibernateTemplate().flush();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#saveOrUpdate(com.rbt.framework.model.Command)
	 */
	@Override
	public void saveOrUpdate(Command command) {
		this.getSessionFactory().getCurrentSession().saveOrUpdate(command);
		// getHibernateTemplate().flush();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#delete(com.rbt.framework.model.Command)
	 */
	@Override
	public void delete(Command command) {
		this.getSessionFactory().getCurrentSession().delete(command);
		this.getSessionFactory().getCurrentSession().flush();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#queryByCommand(com.rbt.framework.model.Command)
	 */
	@Override
	public List queryByCommand(Command command) {
		return this.getSessionFactory().getCurrentSession().createCriteria(command.getClass()).add(Example.create(command)).list();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#query(com.rbt.framework.criterion.HqlDetachedCriteria)
	 */
	// public List query(HqlDetachedCriteria criteria) {
	// return getHibernateTemplate().find(criteria.getHql(), criteria.getValues());
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#bulkUpdate(java.lang.String)
	 */
	// public int bulkUpdate(String hql) {
	// return getHibernateTemplate().bulkUpdate(hql);
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#queryById(java.lang.Class, java.io.Serializable)
	 */
	@Override
	public Object queryById(final Class pojoClass, final Serializable id) {
		return this.getSessionFactory().getCurrentSession().get(pojoClass, id);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#query(org.hibernate.criterion.DetachedCriteria)
	 */
	@Override
	public List query(DetachedCriteria detachedCriteria) {
		return detachedCriteria.getExecutableCriteria(this.getSessionFactory().getCurrentSession()).list();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#queryAll(java.lang.Class)
	 */
	@Override
	public List queryAll(final Class pojoClass) {
		return query(DetachedCriteria.forClass(pojoClass));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#queryNativeSql(java.lang.String)
	 */
	@Override
	public List<Map<String, Object>> queryNativeSql(String sql) {
		try {
			JdbcTemplate jt = new JdbcTemplate(this.dataSource);
			List<Map<String, Object>> list = jt.queryForList(sql);
			this.LOG.debug("result size:[" + list.size() + "]");
			return list;
		} catch (SQLGrammarException e) {
			this.LOG.error("SQL:[" + sql + "]");
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#queryNativeSql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<Map<String, Object>> queryNativeSql(String sql, Object[] params) {
		//try {
			//this.LOG.debug("params:\n" + new BeanUtil().showContent(params));
			JdbcTemplate jt = new JdbcTemplate(this.dataSource);
			List<Map<String, Object>> list = jt.queryForList(sql, params);
			this.LOG.debug("result size:[" + list.size() + "]");
			return list;
//		} catch (Exception e) {
//			this.LOG.error("SQL:[" + sql + "]");
//			throw e;
//		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#executeNativeSql(java.lang.String)
	 */
	@Override
	public void executeNativeSql(String sql) {
		try {
			JdbcTemplate jt = new JdbcTemplate(this.dataSource);
			jt.execute(sql);
		} catch (SQLGrammarException e) {
			this.LOG.error("SQL:[" + sql + "]");
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#executeNativeSql(java.lang.String, java.lang.Object[])
	 */
	@Override
	public void executeNativeSql(String sql, Object[] params) {
		JdbcTemplate jt = new JdbcTemplate(this.dataSource);
		final String fSql = sql;
		final Object[] fParams = params;

		jt.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(fSql);

				for (int i = 0; i < fParams.length; i++) {
					// System.out.println(fParams[i].getClass() + ":[" + fParams[i] + "]");
					ps.setObject(i + 1, fParams[i]);
				}
				return ps;
			}

		});
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#load(com.rbt.framework.model.Command, java.io.Serializable)
	 */
	@Override
	public void load(Command command, final Serializable id) {
		this.getSessionFactory().getCurrentSession().load(command, id);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#query(java.lang.String)
	 */
	// public List query(String hql) {
	// return getHibernateTemplate().find(hql);
	// }

	/**
	 * @param detachedCriteria
	 * @param page
	 * @return
	 */
//	private PageQuery findByDetachedCriteria(final DetachedCriteria detachedCriteria, final Page page) {
//		this.logger.debug("get PageQuery by : [ DetachedCriteria ]");
//		return (PageQuery) getHibernateTemplate().execute(new HibernateCallback() {
//			public Object doInHibernate(Session session) throws HibernateException {
//				DetachedCriteria clone = SerializationUtils.clone(detachedCriteria);
//				Criteria criteria = clone.getExecutableCriteria(session);
//				// remove orderby condition
//				try {
//					Field field = CriteriaImpl.class.getDeclaredField("orderEntries");
//					boolean accessible = field.isAccessible();
//					field.setAccessible(true);
//					field.set(criteria, new ArrayList());
//					field.setAccessible(accessible);
//				} catch (IllegalAccessException e) {
//					BaseDAOImpl.this.logger.error("IllegalAccessException", e);
//					e.printStackTrace();
//				} catch (NoSuchFieldException e) {
//					BaseDAOImpl.this.logger.error("NoSuchFieldException", e);
//					e.printStackTrace();
//				}
//				criteria.setFirstResult(0);
//				criteria.setMaxResults(1);
//				// get total count
//				int total = ((Integer) criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue();
//				// query result
//				List result = getHibernateTemplate().findByCriteria(detachedCriteria, page.getStartNo(), page.getItemsPerPage());
//				if ((result == null || result.size() == 0) && total != 0) {
//					page.setPageNo(page.getPageNo() - 1);
//					result = getHibernateTemplate().findByCriteria(detachedCriteria, page.getStartNo(), page.getItemsPerPage());
//				}
//				PageQuery queryResult = PageQuery.setResult(page.getPageNo(), total, result);
//				BaseDAOImpl.this.logger.debug("[ " + queryResult.getPage().toString() + "]");
//				return queryResult;
//			}
//		});
//
//	}

//	/**
//	 * @param criteria
//	 * @param page
//	 * @return
//	 * @throws DataAccessException
//	 */
//	private PageQuery findByMultiCriteria(final HqlDetachedCriteria criteria, final Page page) throws DataAccessException {
//		this.logger.debug("get PageQuery by : [ MultiDetachedCriteria ]");
//		return (PageQuery) getHibernateTemplate().execute(new HibernateCallback() {
//			public Object doInHibernate(Session session) throws HibernateException {
//				int total = 0;
//				List result = null;
//				PageQuery queryResult = null;
//				int strartNo = page.getStartNo();
//				int intemsPerPage = page.getItemsPerPage();
//				// int pageNo = page.getPageNo();
//				// get the string for result query
//				String hql = criteria.getHql();
//				// get the string for total count query
//				String countStr = criteria.getPageHql();
//				Object[] values = criteria.getValues();
//				BaseDAOImpl.this.logger.info("query HQL is : [ " + hql + " ]");
//				// get result
//				Query queryObject = session.createQuery(hql);
//				if (values != null) {
//					for (int i = 0; i < values.length; i++) {
//						queryObject.setParameter(i, values[i]);
//					}
//				}
//				queryObject.setFirstResult(strartNo);
//				queryObject.setMaxResults(intemsPerPage);
//				result = queryObject.list();
//				// define total count var
//				Integer amount = new Integer(0);
//				Query query = session.createQuery(countStr);
//				// set query where conditions
//				if (values != null) {
//					for (int i = 0; i < values.length; i++) {
//						query.setParameter(i, values[i]);
//					}
//				}
//				// get total count
//				if (!query.list().isEmpty()) {
//					amount = (Integer) query.list().get(0);
//				}
//				total = amount.intValue();
//				if ((result == null || result.size() == 0) && total != 0) {
//					page.setPageNo(page.getPageNo() - 1);
//					queryObject.setFirstResult(page.getStartNo());
//					result = queryObject.list();
//				}
//				queryResult = PageQuery.setResult(page.getPageNo(), total, result);
//				BaseDAOImpl.this.logger.debug("[ " + queryResult.getPage().toString() + "]");
//				return queryResult;
//			}
//		});
//	}

//	/**
//	 * @param hql
//	 * @param page
//	 * @return
//	 */
//	private PageQuery findByHql(final String hql, final Page page) {
//		this.logger.debug("get PageQuery by : [ Hql ]");
//		return (PageQuery) getHibernateTemplate().execute(new HibernateCallback() {
//			public Object doInHibernate(Session session) throws HibernateException {
//				int total = 0;
//				List result = null;
//				PageQuery queryResult = null;
//				int strartNo = page.getStartNo();
//				int intemsPerPage = page.getItemsPerPage();
//				// int pageNo = page.getPageNo();
//				// get the string for result query
//
//				// get the string for total count query
//				String countStr = "select count(*) " + hql.substring(hql.indexOf("from"));
//
//				BaseDAOImpl.this.logger.info("query HQL is : [ " + hql + " ]");
//				// get result
//				Query queryObject = session.createQuery(hql);
//
//				queryObject.setFirstResult(strartNo);
//				queryObject.setMaxResults(intemsPerPage);
//				result = queryObject.list();
//				// define total count var
//				Integer amount = new Integer(0);
//				Query query = session.createQuery(countStr);
//				// set query where conditions
//
//				// get total count
//				Iterator it = query.iterate();
//				if (it.hasNext()) {
//					amount = (Integer) it.next();
//				}
//				total = amount.intValue();
//				if ((result == null || result.size() == 0) && total != 0) {
//					page.setPageNo(page.getPageNo() - 1);
//					queryObject.setFirstResult(page.getStartNo());
//					result = queryObject.list();
//				}
//				queryResult = PageQuery.setResult(page.getPageNo(), total, result);
//				BaseDAOImpl.this.logger.debug("[ " + queryResult.getPage().toString() + "]");
//				return queryResult;
//			}
//		});
//	}

	/**
	 * set the DataSource for Native Sql query
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

//	/**
//	 * @param itemsPerPage
//	 */
//	public void setItemsPerPage(int itemsPerPage) {
//		this.itemsPerPage = itemsPerPage;
//		Page.setDefaultItemsPerPage(this.itemsPerPage);
//	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.rbt.framework.dao.BaseDAO#flush()
	 */
	@Override
	public void flush() {
		this.getSessionFactory().getCurrentSession().flush();
	}

}
