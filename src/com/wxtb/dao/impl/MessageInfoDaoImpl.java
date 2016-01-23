package com.wxtb.dao.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import com.wxtb.dao.MessageInfoDao;
import com.wxtb.entity.MessageInfo;
import com.wxtb.entity.State;

@Repository
public class MessageInfoDaoImpl extends GeneralHibernateDaoImpl<MessageInfo, Serializable> implements MessageInfoDao {

	@Override
	public List<MessageInfo> getMessageInfos(HttpServletRequest request) {
		int pageNum = Integer.parseInt(request.getParameter("page"));
		int rows = Integer.parseInt(request.getParameter("rows"));
		int startPos = (pageNum == 1)? 0 : ((pageNum - 1) * rows);
		Criteria crit = getSession().createCriteria(MessageInfo.class);
		crit.setFirstResult(startPos);
		crit.setMaxResults(rows);
		crit.addOrder(Order.desc("createdAt"));
		return crit.list();
	}

	@Override
	public int getSendSuccessNumber(String batchNumber) {
		Query query = getSession().createQuery("select count(*) from MessageInfo where state=2 and batchNumber=?");
		query.setString(0, batchNumber);
		return ((Long)query.uniqueResult()).intValue();
	}

	@Override
	public int getSendFailNumber(String batchNumber) {
		Query query = getSession().createQuery("select count(*) from MessageInfo where state=3 and batchNumber=?");
		query.setString(0, batchNumber);
		return ((Long)query.uniqueResult()).intValue();
	}

	@Override
	public int getSumNumber(String batchNumber) {
		Query query = getSession().createQuery("select count(*) from MessageInfo where batchNumber=?");
		query.setString(0, batchNumber);
		return ((Long)query.uniqueResult()).intValue();
	}

	@Override
	public int getSumByDateAndDepartment(String startTime, String endTime,
			String department) {
		Query query = getSession().createQuery("select count(*) from MessageInfo where createdAt between ? and ? and department=?");
		query.setString(0, startTime);
		query.setString(1, endTime);
		query.setString(2, department);
		return ((Long)query.uniqueResult()).intValue();
	}

	@Override
	public List<String> getDistinctDepartment() {
		Query query = getSession().createQuery("select distinct(department) from MessageInfo");
		return query.list();
	}

	@Override
	public List<MessageInfo> getWaitSendMessageByBatchNumber(String batchNumber) {
		Query query = getSession().createQuery("from MessageInfo where batch_number=?");
		query.setString(0, batchNumber);
		return query.list();
	}

	@Override
	public Integer getSendNumberByDateAndState(Date startDateTime, Date endDateTime, State state) {
		Criteria crit = getSession().createCriteria(MessageInfo.class);
		crit.add(Restrictions.between("updatedAt", startDateTime, endDateTime));
		crit.add(Restrictions.eq("state", state));
		return crit.list().size();
	}

	@Override
	public Integer getSendSumByDate(Date startDateTime, Date endDateTime) {
		Criteria crit = getSession().createCriteria(MessageInfo.class);
		crit.add(Restrictions.between("updatedAt", startDateTime, endDateTime));
		return crit.list().size();
	}
	
}
