package com.wxtb.service.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxtb.dao.SmsTemplateDao;
import com.wxtb.entity.ExcelColumn;
import com.wxtb.entity.SmsTemplate;
import com.wxtb.service.SmsTemplateService;
import com.wxtb.tools.DateUtil;
import com.wxtb.tools.SendMessage;
import com.wxtb.tools.Tools;

@Service
@Transactional
public class SmsTemplateServiceImpl implements SmsTemplateService{
	
	@Autowired
	private SmsTemplateDao smsTemplateDao;
	
	@Override
	public JSONObject addTemplate(HttpServletRequest request) {
		//验证码${code}，您正在尝试修改${product}登录密码，请妥善保管账户信息
		String templateCode = request.getParameter("template_code");//excel的列号
		String templateName = request.getParameter("template_name");//excel列名
		String templateContent = request.getParameter("template_content");//模板内容
		String phone_num = request.getParameter("phone_num");//电话号码
		JSONObject json = new JSONObject();
		final String[] publicColumnNumber = {"A","B","C","D"};
		if (smsTemplateDao.findBy("templateCode", templateCode).size() <=0) {
			templateContent = Tools.upperCaseContent(templateContent);
			if (templateContent != "") {
				JSONArray jsonArr = JSONArray.parseArray(request.getParameter("datagridDatas"));
				List<ExcelColumn> list = new ArrayList<ExcelColumn>();//用于保存excel列号和名字的集合
				List<String> args = new ArrayList<String>();//用于保存列序号
				
				Map<String,String> columnMap = Tools.getPublicColumn(); //获取模板公共的列
				for(Map.Entry<String, String> entry : columnMap.entrySet()) {
					ExcelColumn ec = new ExcelColumn();
					ec.setEnable(true);
					ec.setExcelColumnNumber(entry.getKey());
					ec.setExcelColumnName(entry.getValue());
					list.add(ec);
				}
				//获取前台传过来的excel列号和名字对应的集合
				for(int i=0; i<jsonArr.size(); i++){
					JSONObject tempJson = (JSONObject)jsonArr.get(i);
					for(int j=0; j<publicColumnNumber.length; j++) {
						if (tempJson.getString("excel_column_number").toUpperCase().equals(publicColumnNumber[j])) {
							json.put("state", "error");
							json.put("msg", "excel列号应从字母E开始");
							return json;
						}
					}
					ExcelColumn ec = new ExcelColumn();
					ec.setEnable(true);
					ec.setExcelColumnNumber(tempJson.getString("excel_column_number").toUpperCase());
					ec.setExcelColumnName(tempJson.getString("excel_column_name"));
					list.add(ec);
					args.add(tempJson.getString("excel_column_number").toUpperCase());
				}
				if (Tools.validateTemplate(templateContent, args)){//判断列的需要是否符合要求
					SendMessage sm = new SendMessage();
					JSONObject jsonContent = Tools.proccessMessageContentToJson(templateContent);
					JSONObject sendResult = sm.send(null, templateCode, phone_num, jsonContent.toJSONString());
					if (sendResult.getBoolean("result")) {
						SmsTemplate smsTemplate = new SmsTemplate();
						smsTemplate.setEnable(true);
						smsTemplate.setExcelColumns(list);
						smsTemplate.setName(templateName);
						smsTemplate.setTemplateCode(templateCode);
						smsTemplate.setTemplateContent(templateContent);
						smsTemplateDao.save(smsTemplate);
						json.put("state", "success");
						json.put("msg", "保存成功！");
					} else {
						json.put("state", "error");
						json.put("msg", "保存失败！验证短信发送失败！");
					}
				} else {
					json.put("state", "error");
					json.put("msg", "保存失败,模板配置错误！");
				}
			} else {
				json.put("state", "error");
				json.put("msg", "保存失败,模板配置错误！");
			}
		} else {
			json.put("state", "error");
			json.put("msg", "保存失败,模板编号重复！");
		}
		return json;
	}

	@Override
	public JSONArray getTempList(HttpServletRequest request) {
		JSONArray jsonArr = new JSONArray();
		List<SmsTemplate> list = smsTemplateDao.findAll();
		for (int i=0; i<list.size(); i++) {
			JSONObject json = new JSONObject();
			if (list.get(i).isEnable()) {
				json.put("id", list.get(i).getId());
				json.put("template_code", list.get(i).getTemplateCode());
				json.put("template_name", list.get(i).getName());
				json.put("create_at", DateUtil.dateFormat(list.get(i).getCreatedAt(), "yyyy-MM-dd HH:mm:ss"));
				json.put("update_at", DateUtil.dateFormat(list.get(i).getUpdatedAt(), "yyyy-MM-dd HH:mm:ss"));
//				json.put("enable_or_disenable", list.get(i).isEnable());
				json.put("template_content", list.get(i).getTemplateContent());
				jsonArr.add(json);
			}
		}
		return jsonArr;
	}

	@Override
	public JSONObject setEnableOrDisenable(int id) {
		JSONObject json = new JSONObject();
		SmsTemplate smsTemplate = smsTemplateDao.findUniqueBy("id", id);
		if (smsTemplate.isEnable()){
			smsTemplate.setEnable(false);
		} else {
			smsTemplate.setEnable(true);
		}
		smsTemplateDao.saveOrUpdate(smsTemplate);
		json.put("state", "success");
		json.put("msg", "修改成功！");
		return json;
	}

	@Override
	public void downloadTemplate(HttpServletRequest request,
			HttpServletResponse response) {
		int id = Integer.parseInt(request.getParameter("id"));
		SmsTemplate smsTemplate = smsTemplateDao.findUniqueBy("id", id);
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("短信模板");
		//设置样式
		sheet.setDefaultColumnWidth((short) 30);
		//主标题样式
		XSSFCellStyle masterStyle = workbook.createCellStyle();
		masterStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		XSSFFont masterFont = workbook.createFont();
		masterFont.setFontHeightInPoints((short)20);
		masterFont.setBold(true);
		masterStyle.setFont(masterFont);
		//标题样式
		XSSFCellStyle titleStyle = workbook.createCellStyle();
		titleStyle.setAlignment(XSSFCellStyle.ALIGN_CENTER);
		XSSFFont titleFont = workbook.createFont();
		titleFont.setFontHeightInPoints((short)13);
		titleFont.setBold(true);
		titleStyle.setFont(titleFont);
		//设置主标题
		CellRangeAddress masterTitle=new CellRangeAddress(0, 0, 0, 15);//合并标题单元格
		sheet.addMergedRegion(masterTitle);
		Row masterRow = sheet.createRow(0);
		Cell masterTitleCell = masterRow.createCell(0);
		masterTitleCell.setCellStyle(masterStyle);
		masterTitleCell.setCellValue(smsTemplate.getName());
		
		//设置模板编号
		CellRangeAddress templateCode=new CellRangeAddress(1, 1, 0, 20);
		sheet.addMergedRegion(templateCode);
		Row templateCodeRow = sheet.createRow(1);
		Cell templateCodeCell = templateCodeRow.createCell(0);
		templateCodeCell.setCellValue(smsTemplate.getTemplateCode());
		//设置模板内容
		CellRangeAddress templateContent=new CellRangeAddress(2, 2, 0, 20);
		sheet.addMergedRegion(templateContent);
		Row templateContentRow = sheet.createRow(2);
		Cell templateContentCell = templateContentRow.createCell(0);
		templateContentCell.setCellValue(smsTemplate.getTemplateContent());
		//设置excel表头
		List<ExcelColumn> excelColumns = smsTemplate.getExcelColumns();
		ExcelColumn tempExcelColumn;
		Row titleRow = sheet.createRow(3);
		Cell titleCell;
		for(int i=0; i<excelColumns.size(); i++) {
			tempExcelColumn = excelColumns.get(i);
			titleCell = titleRow.createCell(Tools.columnToIndex(tempExcelColumn.getExcelColumnNumber())-1);
			titleCell.setCellValue(tempExcelColumn.getExcelColumnName());
			titleCell.setCellStyle(titleStyle);
		}
		//下载内容
		response.reset();
		String downFileName = "短信模板-" + smsTemplate.getName() + ".xlsx";
		try {
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(downFileName.getBytes("utf-8"),"iso8859-1"));
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel");
			workbook.write(toClient);
			toClient.flush();
			toClient.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
