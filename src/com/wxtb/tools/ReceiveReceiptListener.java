package com.wxtb.tools;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.view.tiles2.SpringTilesApplicationContextFactory;

import com.alibaba.fastjson.JSONObject;
import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.internal.tmc.TmcClient;
import com.taobao.top.link.LinkException;
import com.wxtb.dao.MessageInfoDao;
import com.wxtb.dao.StateDao;
import com.wxtb.entity.MessageInfo;
import com.wxtb.entity.State;

/**
 * 此类用于监听阿里大鱼短信的发送回执
 * @author shaka
 *
 */

public class ReceiveReceiptListener implements ServletContextListener{
	
	TmcClient client = null;
	private String appkey; //请求的appkey
	private String secret; //请求的secret
	private String receipt_url;//接收回执请求的url
	private WebApplicationContext springContext;
	private State successState;
	private State failState;
//	@Autowired
	private MessageInfoDao messageInfoDao;
//	
//	@Autowired
	private StateDao stateDao;
	
	public ReceiveReceiptListener() {
		String[] keyArr = {"app_key", "app_Secret", "receipt_url"};
		JSONObject jsonObj = Tools.getPropertiesValues(keyArr);
		appkey = jsonObj.getString("app_key");
		secret = jsonObj.getString("app_Secret");
		receipt_url = jsonObj.getString("receipt_url");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
System.out.println("----------------关闭短信回执监听服务------------------");
		if (null != client) {
			client.close();
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		/*springContext = WebApplicationContextUtils.getWebApplicationContext(arg0.getServletContext());
		messageInfoDao = (MessageInfoDao)springContext.getBean("messageInfoDaoImpl");//通过springcontext寻找bean对象
		stateDao = (StateDao)springContext.getBean("stateDaoImpl");
		successState = stateDao.findUniqueBy("name", "发送成功");//获取成功的状态对象
		failState = stateDao.findUniqueBy("name", "发送失败");//获取失败的状态对象
System.out.println("----------------正在启动短信回执监听服务------------------");
		client = new TmcClient(appkey, secret, "default");
		
		client.setMessageHandler(new MessageHandler() {
			@Override
			public void onMessage(Message message, MessageStatus status) throws Exception {
				try {
System.out.println(message.getContent());
System.out.println(status.getReason());
					JSONObject returnJson = JSONObject.parseObject(message.getContent());
					if (returnJson.getString("extend") != null) {
						MessageInfo msg = messageInfoDao.findUniqueBy("id", Integer.parseInt(returnJson.getString("extend")));
						if (msg == null) {
							return;
						}
						if ("1".equals(returnJson.getString("state"))) {
							msg.setState(successState);
						} else if ("2".equals(returnJson.getString("state"))) {
							msg.setState(failState);
							try {
								msg.setErr_code(Integer.parseInt(returnJson.getString("err_code")));
							} catch (NumberFormatException e){
								msg.setErr_code(1);
							}
						} else {
							msg.setState(failState);
							try {
								msg.setErr_code(Integer.parseInt(returnJson.getString("err_code")));
							} catch (NumberFormatException e){
								msg.setErr_code(1);
							}
						}
						messageInfoDao.saveOrUpdate(msg);
					}
					System.out.println(status.getReason());
				} catch (Exception e) {
					e.printStackTrace();
					status.fail();
				}
			}
		});
		try {
			client.connect(receipt_url);
System.out.println("-------------------监听服务启动成功！-----------------------");
		} catch (LinkException e) {
			e.printStackTrace();
			
		}*/
	}
	
}
