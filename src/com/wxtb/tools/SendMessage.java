package com.wxtb.tools;

import com.alibaba.fastjson.JSONObject;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.internal.tmc.Message;
import com.taobao.api.internal.tmc.MessageHandler;
import com.taobao.api.internal.tmc.MessageStatus;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

/**
 * 发送短信的类
 * @author shaka
 */
public class SendMessage {
	
	private String url; //请求的url
	private String appkey; //请求的appkey
	private String secret; //请求的secret
	
	public SendMessage() {
		String[] keyArr = {"url", "app_key", "app_Secret"};
		JSONObject jsonObj = Tools.getPropertiesValues(keyArr);
		for(int i=0; i<jsonObj.size(); i++) {
			url = jsonObj.getString("url");
			appkey = jsonObj.getString("app_key");
			secret = jsonObj.getString("app_Secret");
		}
	}
	
	/**
	 * 发送短信的方法
	 * @param templateCode 模板编号
	 * @param phone_num 电话号码
	 * @param content 短信内容
	 * @return
	 */
	public JSONObject send(String msg_id, String templateCode, String phone_num, String content){
		JSONObject returnJson = new JSONObject();
		TaobaoClient client = new DefaultTaobaoClient(url, appkey, secret);
		AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
		if (null != msg_id) {
			req.setExtend(msg_id);
		}
		req.setSmsType("normal");
		req.setSmsFreeSignName("同步电子");
		req.setSmsParamString(content);
		req.setRecNum(phone_num);
		req.setSmsTemplateCode(templateCode);
		AlibabaAliqinFcSmsNumSendResponse rsp = null;
		try {
			rsp = client.execute(req);
		} catch (ApiException e) {
			e.printStackTrace();
			returnJson.put("result", false);
			returnJson.put("sub_code", "0x0001");
			returnJson.put("sub_msg", "无法找到主机！");
			System.out.println("网络不通，或者无法找到主机");
			return returnJson;
		} catch (Exception e) {
			e.printStackTrace();
			returnJson.put("result", false);
			returnJson.put("sub_code", "0x0002");
			returnJson.put("sub_msg", "未知错误！");
			System.out.println("未知错误！");
			return returnJson;
		}
		String responseStr = rsp.getBody();
System.out.println(responseStr);
		JSONObject json = JSONObject.parseObject(responseStr);
		if (null != json.get("alibaba_aliqin_fc_sms_num_send_response")) {
			boolean result = json.getJSONObject("alibaba_aliqin_fc_sms_num_send_response").getJSONObject("result").getBooleanValue("success");
			returnJson.put("result", result);
			return returnJson;
		} else if (null != json.get("error_response")) {
			returnJson.put("result", false);
			returnJson.put("sub_code", json.getJSONObject("error_response").get("sub_code"));
			returnJson.put("sub_msg", json.getJSONObject("error_response").get("sub_msg"));
			return returnJson;
		} else {
			return returnJson;
		}
	}
}
