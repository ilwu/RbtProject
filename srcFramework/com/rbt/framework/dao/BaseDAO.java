/*
 * @(#)BaseDAO.java
 *
 * Copyright (c) 2007 HiTRUST Incorporated. All rights reserved.
 *
 * Interface that specifies a basic set of DateBase operations
 *
 * Modify History:
 *  v1.00, 2007-4-28, Royal Shen
 *   1) First release
 */
package com.rbt.framework.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;

import com.rbt.framework.model.Command;

/**
 * <tt>BaseDAO類為DAO層基礎實現。</tt>主要封裝基本的CRUD數據庫操作；基礎查詢方法(包含分頁查詢的實現)。
 * @author Royal
 */
public interface BaseDAO{

	/**
	 * 功能：批次 Insert
	 * @param commandList
	 */
	public void bulkSave(List<Command> commandList);

	/**
	 * 功能：批次 save or update
	 * @param commandList
	 */
	public void bulkSaveOrUpdate(List<Command> commandList);

	/**
	 * 功能：將一個對象新增到對應的數據庫table。
	 * @param command
	 */
	public void save(Command command);

	/**
	 * 功能：更新一個對象到對應的數據庫table。
	 * @param command
	 */
	public void update(Command command);

	/**
	 * 功能：將一個對象新增或者更新到對應的數據庫table。
	 * <p>如果該對象已經存在執行更新動作；
	 * 如果該對象不存在執行新增動作
	 * @param command
	 */
	public void saveOrUpdate(Command command);

	/**
	 * 功能：批量執行數據庫操作。
	 * <p>
	 * 執行類似這樣的操作：<br>
	 * update Command c where c.id='xxxx' <br> <b>or</b> <br>
	 * delete Command c where c.id='xxxx'
	 * @param String hql 標準hibernate HQL語句
	 * @return int 批量操作成功的記錄數
	 */
	//int bulkUpdate(String hql);

	/**
	 * 功能：刪除一個對象。
	 * @param Command command
	 */
	public void delete(Command command);

	/**
	 * 功能：Hibernate Criteria示例查询實現。
	 * @param command Example实例
	 * @return List
	 * @see org.springframework.orm.hibernate3.HibernateTemplate.findByExample(command)
	 */
	public List queryByCommand(Command command);

	/**
	 * 功能：根據持久化對象的主鍵查詢對象。
	 * Return the persistent instance of the given entity class with the given identifier
	 * @param Class pojoClass
	 * @param Serializable id
	 * @return Object
	 */
	public Object queryById(final Class pojoClass, final Serializable id);

	/**
	 * 功能：查詢指定類別的所有對象。
	 * @param Class pojoClass
	 * @return List
	 */
	public List queryAll(final Class pojoClass);

	/**
	 * 功能：根據原生sql執行查詢。<p>結果為一個List，List内容為一個Map，
	 * Map中的key/value值對應為 sql 中的每個column/value。
	 * @param String sql
	 * @return List
	 * @see org.springframework.jdbc.core.JdbcTemplate.queryForList()
	 */
	public List<Map<String, Object>> queryNativeSql(String sql);

	/**
	 * 功能：執行SQL by JdbcTemplate
	 */
	public void executeNativeSql(String sql);

	/**
	 * 功能：執行SQL by JdbcTemplate
	 */
	public void executeNativeSql(String sql, Object[] params);

	/**
	 * 功能：根據原生sql執行查詢。<p>結果為一個List，List内容為一個Map，
	 * Map中的key/value值對應為 sql 中的每個column/value。
	 * @param String sql
	 * @param params
	 * @return List
	 * @see org.springframework.jdbc.core.JdbcTemplate.queryForList()
	 */
	public List<Map<String, Object>> queryNativeSql(String sql, Object[] params);

	/**
	 * 功能：實現Hibernate DetachedCriteria查詢。
	 * @param DetachedCriteria detachedCriteria
	 * @return List
	 */
	List query(DetachedCriteria detachedCriteria);

	/**
	 * 功能：實現HqlDetachedCriteria查詢。
	 * @param HqlDetachedCriteria criteria
	 * @return List
	 * @see com.rbt.framework.criterion.HqlDetachedCriteria
	 */
	//List query(HqlDetachedCriteria criteria);

	/**
	 * 功能：根據對象主鍵加載一個持久化對象。<p> 如果不存在throws一個Exception
	 * @param Command command
	 * @param Serializable id
	 * @throws org.springframework.orm.ObjectRetrievalFailureException
	 */
	void load(Command command ,final Serializable id);

	/**
	 * 功能：取得當前session中的JDBC連接
	 * @return Connection
	 */
	//Connection getConnection();

	/**
	 * 功能：實現Hibernate HQL查詢。
	 * @param String hql Hibernate 標準HQL
	 * @return List
	 */
	//List query(String hql);

	/**
	 * 功能：執行getHibernateTemplate().flush();
	 * @see org.springframework.orm.hibernate3.support.HibernateDaoSupport.getHibernateTemplate().flush()
	 */
	void flush();
}
