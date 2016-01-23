package com.wxtb.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxtb.dao.MessageInfoDao;
import com.wxtb.dao.SmsTemplateDao;
import com.wxtb.dao.StateDao;
import com.wxtb.entity.ExcelColumn;
import com.wxtb.entity.MessageInfo;
import com.wxtb.entity.SmsTemplate;
import com.wxtb.entity.State;
import com.wxtb.service.MessageInfoService;
import com.wxtb.tools.DateUtil;
import com.wxtb.tools.SendMessage;
import com.wxtb.tools.Tools;

@Service
@Transactional
public class MessageInfoServiceImpl implements MessageInfoService{
	
	@Autowired
	private MessageInfoDao messageInfoDao;
	
	@Autowired
	private SmsTemplateDao smsTemplateDao;
	
	@Autowired
	private StateDao stateDao;

	@Override
	public JSONArray getMessageList(HttpServletRequest request) {
		List<MessageInfo> list = messageInfoDao.getMessageInfos(request);
		JSONArray jsonArr = new JSONArray();
		for(int i=0; i<list.size(); i++){
			JSONObject json = new JSONObject();
			json.put("id", list.get(i).getId());
			json.put("send_to", list.get(i).getSendTo());
			json.put("phone_number", list.get(i).getPhoneNumber());
			json.put("message_content", list.get(i).getMessageContent());
			json.put("department", list.get(i).getDepartment());
			json.put("send_person", list.get(i).getSendPerson());
			json.put("send_time", DateUtil.dateFormat(list.get(i).getUpdatedAt(), "yyyy-MM-dd HH:mm:ss"));
			json.put("state", list.get(i).getState().getName());
			jsonArr.add(json);
		}
		return jsonArr;
	}

	@Override
	public JSONObject importMessageExcel(HttpServletRequest request) {
		String batchNumber = request.getParameter("batchNumber");//发送批次
		JSONObject returnJson = new JSONObject();//用于存储返回信息
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile multipartFile = multipartRequest.getFile("file1");
		Workbook wb = null;
		InputStream is = null;
		try {
			is = multipartFile.getInputStream();
			wb = WorkbookFactory.create(is);
		} catch (IOException e) {
			e.printStackTrace();
			returnJson.put("state", "error");
			returnJson.put("msg", "系统内部错误！");
			return returnJson;
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			returnJson.put("state", "error");
			returnJson.put("msg", "系统内部错误！");
			return returnJson;
		}
		Sheet sheet = wb.getSheetAt(0);
		int rows = sheet.getLastRowNum() + 1 - sheet.getFirstRowNum();
		Row row=sheet.getRow(1);
		String templateCode = Tools.getValue(row.getCell(0));
		//验证模板code是否存在
		if (templateCode != null && templateCode != "") {
			SmsTemplate smsTemplate = smsTemplateDao.findUniqueBy("templateCode", templateCode);
			if(smsTemplate != null) {
				//验证模板表头是否正确
				row = sheet.getRow(3);
				List<ExcelColumn> columnList = smsTemplateDao.getColumnListByTemplateId(smsTemplate.getId()); //smsTemplate.getExcelColumns();
				int columnNumber = 0;
				for(int i=0; i<columnList.size(); i++){
					columnNumber = Tools.columnToIndex(columnList.get(i).getExcelColumnNumber());
					if(!columnList.get(i).getExcelColumnName().equals(Tools.getValue(row.getCell(columnNumber - 1)).trim())) {
						returnJson.put("state", "error");
						returnJson.put("msg", "模板格式错误！");
						return returnJson;
					}
				}
				//验证电话号码是否正确
				StringBuffer cellErr = new StringBuffer();
				for(int i=4; i<rows; i++) { //遍历行
					row = sheet.getRow(i);
					for(int j=0; j<columnList.size(); j++) { //遍历单元格
						if (1 == j) {
							if (11 != Tools.getValue(row.getCell(1)).trim().length()) {
								cellErr.append("第" + (i+1) + "行电话号码格式错误！" + "\n");
								continue;
							}
						}
						System.out.println(Tools.getValue(row.getCell(j)));
						if (Tools.getValue(row.getCell(j)) == "" || Tools.getValue(row.getCell(j)) == null) {
							System.out.println("---------------------------");
							cellErr.append("第" + (i+1) + "行\"" + columnList.get(j).getExcelColumnName() + "\"列" + "不存在数据" + "\n");
						}
					}
				}
				if (cellErr.length() > 0) {
					returnJson.put("state", "error");
					returnJson.put("msg", cellErr.toString());
					return returnJson;
				}
				
				//验证完毕，开始逐条保存到数据库
				State waitToSend = stateDao.findUniqueBy("name", "待发送");//获取待发送的状态对象
				//把短信存到数据库中
				for (int i=4; i<rows; i++) {
					row = sheet.getRow(i);
					//如果电话号码不存在直接跳过
					if (Tools.getValue(row.getCell(1))== "" || Tools.getValue(row.getCell(1))== null) {
						continue;
					}
					MessageInfo message = new MessageInfo();
					message.setBatchNumber(batchNumber);//批次号
					message.setDepartment(Tools.getValue(row.getCell(2)));//部门
					message.setMessageContent("【同步电子】" + Tools.proccessMessageContent(smsTemplate, row));//短信内容
					JSONObject messageContentJson = Tools.proccessMessageContentToJson(smsTemplate.getTemplateContent(), row);
					message.setMessageContentJson(messageContentJson.toJSONString());//短信内容的json
					message.setPhoneNumber(Tools.getValue(row.getCell(1)));//电话号码
					message.setSendPerson(Tools.getValue(row.getCell(3)));//发送人（信息管理员）
					message.setSendTo(Tools.getValue(row.getCell(0)));//客户名称
					message.setSmsTemplate(smsTemplate);
					message.setState(waitToSend);
					messageInfoDao.save(message);
					/*if (messageContentJson != null) {
						//发送短信
						JSONObject sendResponse = sendMessage.send(templateCode, Tools.getValue(row.getCell(1)), messageContentJson.toJSONString());
						if (sendResponse.getBooleanValue("result")) {
							message.setState(successState);
						} else {
							message.setState(failState);
						}
					} else {
						message.setState(failState);
					}*/
//					messageInfoDao.save(message);
				}
System.out.println("短信保存成功！");
				//发送短信
				JSONObject sendResponse = null;
				MessageInfo tempMessageInfo = null;
				List<MessageInfo> waitToSendList = messageInfoDao.getWaitSendMessageByBatchNumber(batchNumber);//根据批次号获取所有待发送的信息
				SendMessage sendMessage = new SendMessage();//创建发送短信对象
//				State successState = stateDao.findUniqueBy("name", "发送成功");//获取成功的状态对象
				State failState = stateDao.findUniqueBy("name", "发送失败");//获取失败的状态对象
				State waitReceipt = stateDao.findUniqueBy("name", "等待回执");//获取等待回执的状态对象
				for(int i=0; i<waitToSendList.size(); i++) {
					tempMessageInfo = waitToSendList.get(i);
					sendResponse = sendMessage.send(tempMessageInfo.getId().toString(), templateCode, tempMessageInfo.getPhoneNumber(), tempMessageInfo.getMessageContentJson());
					if (sendResponse.getBooleanValue("result")) {
						tempMessageInfo.setState(waitReceipt);
						messageInfoDao.save(tempMessageInfo);
					} else {
						tempMessageInfo.setState(failState);
					}
					messageInfoDao.saveOrUpdate(tempMessageInfo);
				}
//				int successNumber = messageInfoDao.getSendSuccessNumber(batchNumber);//成功发送条数
//				int failNumber = messageInfoDao.getSendFailNumber(batchNumber);//发送失败的条数
//				int sumNumber = messageInfoDao.getSumNumber(batchNumber);
				returnJson.put("state", "success");
				returnJson.put("msg", "一共发送了 " + waitToSendList.size() + " 条短信，稍后请查看短信回执！");
//				returnJson.put("msg", "发送总数：" + sumNumber + ";成功条数：" + successNumber + ",失败条数：" + failNumber);
			} else {
				returnJson.put("state", "error");
				returnJson.put("msg", "模板编号不存在！");
				return returnJson;
			}
		} else {
			returnJson.put("state", "error");
			returnJson.put("msg", "Excel中未找到模板编号！");
			return returnJson;
		}
		return returnJson;
	}
	/**
	 * 失败重新发送
	 */
	@Override
	public JSONObject sendFailMessage(HttpServletRequest request) {
		JSONObject returnJson = new JSONObject();
		String[] ids = request.getParameterValues("ids");
//		State successState = stateDao.findUniqueBy("name", "发送成功");
		State failState = stateDao.findUniqueBy("name", "发送失败");
		State waitReceiptState = stateDao.findUniqueBy("name", "等待回执");
		MessageInfo messageInfo = null;
		SendMessage sendMessage = new SendMessage();
		int successNum = 0;//记录待回执条数
		int failNum = 0;//记录失败条数
		JSONObject json = new JSONObject();
		for(int i=0; i<ids.length; i++) {
			messageInfo = messageInfoDao.findUniqueBy("id", Integer.parseInt(ids[i]));
			if ("发送失败".equals(messageInfo.getState().getName())) {
				json = sendMessage.send(ids[i], messageInfo.getSmsTemplate().getTemplateCode(), messageInfo.getPhoneNumber(), messageInfo.getMessageContentJson());
				if (json.getBoolean("result")) {
					messageInfo.setState(waitReceiptState);
					successNum++;
				} else {
					messageInfo.setState(failState);
					failNum++;
				}
				messageInfo.setUpdatedAt(new Date());
				messageInfoDao.saveOrUpdate(messageInfo);
			}
		}
		returnJson.put("result", true);
		returnJson.put("msg", "发送总数：" + ids.length + ",待回执：" + successNum + ",失败条数：" + failNum);
		return returnJson;
	}

	@Override
	public JSONArray getChatData(HttpServletRequest request) {
		JSONArray jsonArr = new JSONArray();
		List<String> distinctDep = messageInfoDao.getDistinctDepartment();//查询所有不重复的部门
		for(int i=0; i<distinctDep.size(); i++ ) {
			for(int j=1; j<=7; j++) {
				String beforeDate = DateUtil.getFormatDateAdd(new Date(), 0-j, "yyyy-MM-dd");
				JSONObject json = new JSONObject();
				json.put("department", distinctDep.get(i));
				json.put("date", beforeDate);
				json.put("send_sum", messageInfoDao.getSumByDateAndDepartment(beforeDate + " 00:00:00", beforeDate + " 23:59:59", distinctDep.get(i)));
				json.put("qualified_sum", messageInfoDao.getSumByDateAndDepartment(beforeDate + " 00:00:00", beforeDate + " 09:30:00", distinctDep.get(i)));
				jsonArr.add(json);
			}
		}
		return jsonArr;
	}

	@Override
	public JSONArray getTongJiData(HttpServletRequest request) {
		String clickDate = request.getParameter("click_date");
		if (null == clickDate || "" == clickDate) {
			clickDate = DateUtil.dateFormat(new Date(), "yyyy-MM-dd");
		}
		Date startClickDate = DateUtil.getDateFromString(clickDate + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
		Date endClickDate = DateUtil.getDateFromString(clickDate + " 23:59:59", "yyyy-MM-dd HH:mm:ss");
		JSONArray jsonArr = new JSONArray();
		State successState = stateDao.findUniqueBy("name", "发送成功");
		State failState = stateDao.findUniqueBy("name", "发送失败");
		State waitReceiptState = stateDao.findUniqueBy("name", "等待回执");
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("wait_receipt_count", messageInfoDao.getSendNumberByDateAndState(startClickDate, endClickDate, waitReceiptState));
		jsonObj.put("success_count", messageInfoDao.getSendNumberByDateAndState(startClickDate, endClickDate, successState));
		jsonObj.put("fail_count", messageInfoDao.getSendNumberByDateAndState(startClickDate, endClickDate, failState));
		jsonObj.put("sum_count", messageInfoDao.getSendSumByDate(startClickDate, endClickDate));
		jsonArr.add(jsonObj);
		return jsonArr;
	}
	
}
