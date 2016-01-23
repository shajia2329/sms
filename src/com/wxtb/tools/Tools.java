package com.wxtb.tools;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.alibaba.fastjson.JSONObject;
import com.wxtb.entity.ExcelColumn;
import com.wxtb.entity.SmsTemplate;
/**
 * 工具类
 * @author shajia
 *
 */
public class Tools {
	
	/**
	 * 获取UUID
	 * @return
	 */
	public static String getUUID(){
		return UUID.randomUUID().toString().toUpperCase();
	}
	
	/**
	 * 用于验证短信模板和模板参数是否符合要求
	 * @param templateMsg 短信模板内容
	 * @param templateArgs 短信参数list集合
	 * @return true or false
	 */
	public static boolean validateTemplate(String templateMsg, List<String> templateArgs) {
		String startSep= "{";//前置字符串
		String endSep = "}";//结尾字符串
		int startPoint = 0;//开始位置指针
		int endPoint = 0;//结束位置指针
		List<String> params = new ArrayList<String>();//用于存储从模板信息中提取的变量名
		boolean flag = true;
		while(flag) {
			startPoint = templateMsg.indexOf(startSep, startPoint);
			if(startPoint != -1) {
				endPoint = templateMsg.indexOf(endSep, startPoint);
				if(endPoint != -1) {
					params.add(templateMsg.substring(startPoint+1, endPoint));
					startPoint = endPoint;
				} else {
					return false;
				}
			} else {
				flag = false;
			}
		}
		if(templateArgs.size() == params.size()) {
			if(templateArgs.containsAll(params)) {
				return true;
			}else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * 处理模板中的变量，使之全部变成大写
	 * @param content 需要处理的模板内容
	 * @return 处理后的模板内容
	 */
	public static String upperCaseContent(String content) {
		String startSep= "{";//前置字符串
		String endSep = "}";//结尾字符串
		int startPoint = 0;//开始位置指针
		int endPoint = 0;//结束位置指针
		StringBuffer sb = new StringBuffer(content);
		boolean flag = true;
		while(flag) {
			startPoint = content.indexOf(startSep, startPoint);
			if(startPoint != -1) {
				endPoint = content.indexOf(endSep, startPoint);
				if(endPoint != -1) {
					sb.replace(startPoint+1, endPoint, content.substring(startPoint+1, endPoint).toUpperCase());
					startPoint = endPoint;
				} else {
					return "";
				}
			} else {
				flag = false;
			}
		}
		return sb.toString();
	}
	
	/**
	 * 获取模板公共的列
	 * @return
	 */
	public static Map<String,String> getPublicColumn() {
		Map<String,String> columnMap = new HashMap<String, String>();
		columnMap.put("A", "客户姓名");
		columnMap.put("B", "手机号码");
		columnMap.put("C", "发送部门");
		columnMap.put("D", "信息管理员");
		return columnMap;
	}
	
	/**
	 * 把短信模板和短信内容进行合并
	 * @param smsTemplate 短信模板
	 * @param row excel的row
	 * @return
	 */
	public static String proccessMessageContent(SmsTemplate smsTemplate, Row row) {
		List<ExcelColumn> columnList = smsTemplate.getExcelColumns();
		StringBuffer sb = new StringBuffer(smsTemplate.getTemplateContent());//用于保存需要处理的字符串
		int indexStart;
		int indexEnd;
		for(int i=0; i<columnList.size(); i++) {
			indexStart = indexEnd = 0;
			String needReplace = "${" + columnList.get(i).getExcelColumnNumber() + "}";
			indexStart = sb.indexOf(needReplace, indexStart);
			if (indexStart != -1) {
				indexEnd = indexStart + needReplace.length();
				sb.replace(indexStart, indexEnd, getValue(row.getCell(columnToIndex(columnList.get(i).getExcelColumnNumber())-1)));
			}
		}
		return sb.toString();
	}
	
	/**
	 * 将短信内容和模板拼成发送短信所需要的json格式
	 * @param smsTemplate 短信模板
	 * @param row excel的 row
	 * @return
	 */
	public static JSONObject proccessMessageContentToJson(String smsTemplate, Row row) {
		JSONObject json = new JSONObject();
		String startSep= "${";//前置字符串
		String endSep = "}";//结尾字符串
		int startPoint = 0;//开始位置指针
		int endPoint = 0;//结束位置指针
		boolean flag = true;
		String jsonKey;
		while(flag) {
			startPoint = smsTemplate.indexOf(startSep, startPoint);
			if(startPoint != -1) {
				endPoint = smsTemplate.indexOf(endSep, startPoint);
				if(endPoint != -1) {
					jsonKey = smsTemplate.substring(startPoint+2, endPoint);
					json.put(jsonKey, getValue(row.getCell(columnToIndex(jsonKey)-1)));
					startPoint = endPoint;
				} else {
					return null;
				}
			} else {
				flag = false;
			}
		}
		return json;
	}
	
	/**
	 * 根据模板内容生成验证短信
	 * @param templateContent 模板内容
	 * @return
	 */
	public static JSONObject proccessMessageContentToJson(String templateContent) {
		StringBuffer sb = new StringBuffer(templateContent);
		JSONObject json = new JSONObject();
		String startSep= "${";//前置字符串
		String endSep = "}";//结尾字符串
		int startPoint = 0;//开始位置指针
		int endPoint = 0;//结束位置指针
		int fillNum = 0;//用于填充短信内容
		boolean flag = true;
		String jsonKey;
		while(flag) {
			startPoint = sb.indexOf(startSep, startPoint);
			if(startPoint != -1) {
				endPoint = sb.indexOf(endSep, startPoint);
				if(endPoint != -1) {
					jsonKey = sb.substring(startPoint+2, endPoint);
					json.put(jsonKey, "" + (fillNum++));
					startPoint = endPoint;
				} else {
					return null;
				}
			} else {
				flag = false;
			}
		}
		return json;
	}
	
	/**
	 * 用于将Excel表格中列号字母转成列索引
	 * @param column 列号
	 * @return 列索引
	 */
	public static int columnToIndex(String column) {
		if (!column.matches("[A-Z]+")) {
			try {
				throw new Exception("Invalid parameter");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		int index = 0;
		char[] chars = column.toUpperCase().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			index += ((int) chars[i] - (int) 'A' + 1) * (int) Math.pow(26, chars.length - i - 1);
		}
		return index;
	}
	
	/**
	* 用于将excel表格中列索引转成列号字母，从A对应1开始
	* 
	* @param index
	*            列索引
	* @return 列号
	*/
	public static String indexToColumn(int index) {
		if (index <= 0) {
			try {
				throw new Exception("Invalid parameter");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		index--;
		String column = "";
		do {
			if (column.length() > 0) {
				index--;
			}
			column = ((char) (index % 26 + (int) 'A')) + column;
			index = (int) ((index - index % 26) / 26);
		} while (index > 0);
		return column;
	}
	
	/**
	 * 读取单元格内容
	 * @param cell
	 * @return
	 */
	public static String getValue(Cell cell){
		if(cell == null) {
			return "";
		}
		if(cell.getCellType()==Cell.CELL_TYPE_BOOLEAN){
			if(cell.getBooleanCellValue()){
				return "true";
			}else{
				return "false";
			}
		}else if(cell.getCellType()==Cell.CELL_TYPE_NUMERIC){
			if(HSSFDateUtil.isCellDateFormatted(cell)){
				Date date=cell.getDateCellValue();
				SimpleDateFormat formattor=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				return formattor.format(date);
			}
		}else if(cell.getCellType()==Cell.CELL_TYPE_BLANK){
			return "";
		}else if(cell.getCellType()==Cell.CELL_TYPE_STRING){
			return cell.getStringCellValue();
		}
		cell.setCellType(Cell.CELL_TYPE_STRING);
		return cell.getStringCellValue();
	}
	
	/**
	 * 根据key数组从properties文件中获取内容
	 * @param  keys数组
	 * @return json对象
	 */
	public static JSONObject getPropertiesValues(String[] keys) {
		InputStream fis = null;
		JSONObject jsonObj = new JSONObject();
		try {
			Properties prop = new Properties();
			fis = Tools.class.getClassLoader().getResourceAsStream("/config/properties/params.properties");
			prop.load(fis);
			for(String key : keys) {
				jsonObj.put(key, prop.getProperty(key));
			}
			return jsonObj;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (null != fis) {
					fis.close();
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	/*public static void main(String[] args) {
		String value = Tools.getPropertiesValueByKey("url");
		System.out.println(value);
		
	}*/
}
