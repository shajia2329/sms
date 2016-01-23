package com.wxtb.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
/**
 * 短信信息表
 * @author shaka
 *
 */
@Entity
@Table(name="message_info")
public class MessageInfo extends BaseModel implements Serializable{
	
	private Integer id;
	/**
	 * 收信人
	 */
	private String sendTo;
	/**
	 * 电话号码
	 */
	private String phoneNumber;
	/**
	 * 短信内容
	 */
	private String messageContent;
	/**
	 * 短信内容的json串
	 */
	private String messageContentJson;
	/**
	 * 发送部门
	 */
	private String department;
	/**
	 * 发送人
	 */
	private String sendPerson;
	/**
	 * 发送状态
	 */
	private State state;
	/**
	 * 批次号 用于判断发送进度
	 */
	private String batchNumber;
	
	/**
	 * 模板关联
	 */
	private SmsTemplate smsTemplate;
	
	/**
	 * 错误代码
	 */
	private Integer err_code;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(name="send_to")
	public String getSendTo() {
		return sendTo;
	}
	public void setSendTo(String sendTo) {
		this.sendTo = sendTo;
	}
	
	@Column(name="phone_number")
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	@Column(name="message_content")
	public String getMessageContent() {
		return messageContent;
	}
	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent;
	}
	
	@Column(name="department")
	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	
	@Column(name="send_person")
	public String getSendPerson() {
		return sendPerson;
	}
	public void setSendPerson(String sendPerson) {
		this.sendPerson = sendPerson;
	}
	
	@ManyToOne
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	
	@Column(name="batch_number")
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	
	@Column(name="message_content_json")
	public String getMessageContentJson() {
		return messageContentJson;
	}
	public void setMessageContentJson(String messageContentJson) {
		this.messageContentJson = messageContentJson;
	}
	
	@ManyToOne
	public SmsTemplate getSmsTemplate() {
		return smsTemplate;
	}
	public void setSmsTemplate(SmsTemplate smsTemplate) {
		this.smsTemplate = smsTemplate;
	}
	
	public Integer getErr_code() {
		return err_code;
	}
	public void setErr_code(Integer err_code) {
		this.err_code = err_code;
	}
}
