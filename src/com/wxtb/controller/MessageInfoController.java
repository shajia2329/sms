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
import com.wxtb.service.MessageInfoService;
import com.wxtb.tools.Tools;

@Controller
@RequestMapping("/MessageInfoController")
public class MessageInfoController {
	
	@Autowired
	private MessageInfoService messageInfoService;
	
	/**
	 * 获取短信列表
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/getMessageList",method=RequestMethod.POST)
	@ResponseBody
	public JSONArray getMessageList(HttpServletRequest request, HttpServletResponse response) {
		return messageInfoService.getMessageList(request);
	}
	
	@RequestMapping(value="/getUUID",method=RequestMethod.POST)
	@ResponseBody
	public String getUUID(HttpServletRequest request, HttpServletResponse response) {
		return Tools.getUUID();
	}
	
	@RequestMapping(value="/importMessageExcel",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject importMessageExcel(HttpServletRequest request, HttpServletResponse response) {
		return messageInfoService.importMessageExcel(request);
	}
	
	/**
	 * 获取发送失败的短信
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/sendFailMessage",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject sendFailMessage(HttpServletRequest request, HttpServletResponse response) {
		return messageInfoService.sendFailMessage(request);
	}
	
	/**
	 * 获取短信发送及时率信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/getChatData",method=RequestMethod.POST)
	@ResponseBody
	public JSONArray getChatData(HttpServletRequest request, HttpServletResponse response) {
		return messageInfoService.getChatData(request);
	}
	
	/**
	 * 获取统计数据
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/getTongJiData",method=RequestMethod.POST)
	@ResponseBody
	public JSONArray getTongJiData(HttpServletRequest request, HttpServletResponse response) {
		return messageInfoService.getTongJiData(request);
	}
}
