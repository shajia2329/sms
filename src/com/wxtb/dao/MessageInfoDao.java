package com.wxtb.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.wxtb.entity.MessageInfo;
import com.wxtb.entity.State;

public interface MessageInfoDao extends GeneralHibernateDao<MessageInfo, Serializable>{
	
	/**
	 * 获取message数据
	 * @return
	 */
	public List<MessageInfo> getMessageInfos(HttpServletRequest request);
	
	/**
	 * 获取发送成功的短信数量
	 * @param batchNumber 批次号
	 * @return
	 */
	public int getSendSuccessNumber(String batchNumber);
	
	/**
	 * 获取发送失败的短信数量
	 * @param batchNumber 批次号
	 * @return
	 */
	public int getSendFailNumber(String batchNumber);
	
	/**
	 * 根据时间段和发送状态获取短信数量
	 * @param startDateTime 开始时间
	 * @param endDateTime 结束时间
	 * @param state 状态对象
	 * @return
	 */
	public Integer getSendNumberByDateAndState(Date startDateTime, Date endDateTime, State state);
	
	/**
	 * 根据时间段获取发送总数
	 * @param startDateTime 开始时间
	 * @param endDateTime 结束时间
	 * @return 发送总数
	 */
	public Integer getSendSumByDate(Date startDateTime, Date endDateTime);
	
	/**
	 * 获取该批次所有的短信条数
	 * @param batchNumber
	 * @return
	 */
	public int getSumNumber(String batchNumber);
	
	/**
	 * 根据开始时间和结束时间和部门获取当天发送短信的总数
	 * @param startDate 开始时间
	 * @param endDate 结束时间
	 * @param department 部门
	 * @return
	 */
	public int getSumByDateAndDepartment(String startTime, String endTime, String department);
	
	public List<String> getDistinctDepartment();
	
	/**
	 * 根据批次号获取所有待发送的短信
	 * @param BatchNumber 批次号
	 * @return
	 */
	public List<MessageInfo> getWaitSendMessageByBatchNumber(String batchNumber);
}
