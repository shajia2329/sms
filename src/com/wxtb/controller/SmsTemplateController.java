package com.wxtb.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wxtb.service.SmsTemplateService;

@Controller
@RequestMapping("/SmsTemplateController")
public class SmsTemplateController {
	
	@Autowired
	private SmsTemplateService smsTemplateService;
	/**
	 * 添加模板
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/addTemplate",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject addTemplate(HttpServletRequest request, HttpServletResponse response) {
		return smsTemplateService.addTemplate(request); 
	}
	
	/**
	 * 获取模板列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/getTemplateList",method=RequestMethod.POST)
	@ResponseBody
	public JSONArray getTemplateList(HttpServletRequest request, HttpServletResponse response) {
		return smsTemplateService.getTempList(request);
	}
	
	/**
	 * 设置模板禁用和启用
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/setEnableOrDisenable",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject setEnableOrDisenable(HttpServletRequest request, HttpServletResponse response) {
		return smsTemplateService.setEnableOrDisenable(Integer.parseInt(request.getParameter("id")));
	}
	
	/**
	 * 下载模板
	 * @param request
	 * @param response
	 */
	@RequestMapping(value="/downloadTemplate",method=RequestMethod.POST)
	@ResponseBody
	public void downloadTemplate(HttpServletRequest request, HttpServletResponse response) {
		smsTemplateService.downloadTemplate(request, response);
	}
}
