package com.wxtb.service;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface MessageInfoService {
	/**
	 * 获取短信列表
	 * @param request
	 * @return
	 */
	public JSONArray getMessageList(HttpServletRequest request);
	
	/**
	 * 导入短信模板
	 * @param request
	 * @return
	 */
	public JSONObject importMessageExcel(HttpServletRequest request);
	
	/**
	 * 获取所有发送失败的短信
	 * @return
	 */
	public JSONObject sendFailMessage(HttpServletRequest request);
	
	/**
	 * 获取短信发送及时率信息
	 * @param request
	 * @return
	 */
	public JSONArray getChatData(HttpServletRequest request);
	
	/**
	 * 获取统计查询数据
	 * @param request
	 * @return
	 */
	public JSONArray getTongJiData(HttpServletRequest request);
}
