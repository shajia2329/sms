package com.wxtb.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


public interface SmsTemplateService {
	/**
	 * 添加模板
	 * @param request
	 * @return
	 */
	public JSONObject addTemplate(HttpServletRequest request);
	
	/**
	 * 获取模板列表
	 * @param request
	 * @return
	 */
	public JSONArray getTempList(HttpServletRequest request);
	
	/**
	 * 设置模板是否可用
	 * @param id
	 * @return
	 */
	public JSONObject setEnableOrDisenable(int id);
	
	/**
	 * 下载模板
	 * @param request
	 * @param response
	 */
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response);
}
