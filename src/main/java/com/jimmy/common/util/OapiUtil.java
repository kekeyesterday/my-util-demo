/*
 * *************************************************************************************
 *
 *   Project:        ZXQ
 *
 *   Copyright ©     2014-2017 Banma Technologies Co.,Ltd
 *                   All rights reserved.
 *
 *   This software is supplied only under the terms of a license agreement,
 *   nondisclosure agreement or other written agreement with Banma Technologies
 *   Co.,Ltd. Use, redistribution or other disclosure of any parts of this
 *   software is prohibited except in accordance with the terms of such written
 *   agreement with Banma Technologies Co.,Ltd. This software is confidential
 *   and proprietary information of Banma Technologies Co.,Ltd.
 *
 * *************************************************************************************
 *
 *   Class Name: com.zebra.carcloud.group.service.impl.util.OapiUtil
 *
 *   General Description:
 *
 *   Revision History:
 *                            Modification
 *    Author                Date(MM/DD/YYYY)   JiraID           Description of Changes
 *    *********************   ************    **********     *****************************
 *    zhangjun                   2017-02-22
 *
 * **************************************************************************************
 */

package com.zebra.carcloud.pushmsg.util;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/**
 * 自驾游服务 OpenAPI调用工具类
 * Created by zhangjun on 2017/2/22.
 */
@Component("oapiUtil")
public class OapiUtil {

	private Logger LOGGER = LoggerFactory.getLogger(OapiUtil.class);
	
	private static final Integer MDS_BEARER=1; 
	private static final Integer ACTIVE_BEARER=2; 

	@Value("${opm-pushmsg.oapi.oapiUrl:defaultValue}")
	private String mdsOapiUrl;
	@Value("${opm-pushmsg.oapiBearer:defaultValue}")
	private String mdsOapiBearer;
//	@Value("${trip-api.oapi.activeOapiUrl}")
//	private String activeOapiUrl;
//	@Value("${trip-api.oapi.activeOapiBearer}")
//	private String activeOapiBearer;
	


	public Object get(String method, Map<String, Object> params) throws Exception {
		StringBuilder url = new StringBuilder(mdsOapiUrl + method + "?");
		for (String key : params.keySet()) {
			url.append(key + "=" + params.get(key) + "&");
		}
		CloseableHttpClient client = HttpClients.createDefault();
		return doGet(client, url.toString(),MDS_BEARER);
	}
	
	public Object doPost(String method, Map<String, Object> params,boolean noNeedResponse) throws Exception {
		StringBuilder url = new StringBuilder(mdsOapiUrl + method + "?");
		for (String key : params.keySet()) {
			url.append(key + "=" + params.get(key) + "&");
		}
		CloseableHttpClient client = HttpClients.createDefault();
		return doPost(client, url.toString(),null,"",noNeedResponse,MDS_BEARER);
	}
	

	public Object post(String method, Map<String, Object> paraMap,String jsonBody, boolean noNeedResponse,Integer bearerType) throws Exception {
		String url = mdsOapiUrl + method;
		CloseableHttpClient client = HttpClients.createDefault();
		return doPost(client, url, paraMap,jsonBody, noNeedResponse,bearerType);
	}


	public Object doGet(CloseableHttpClient client, String url,Integer bearerType) throws Exception {
		return doGet(client, url, Object.class,bearerType);
	}

	public <T> T doGet(CloseableHttpClient client, String url, Class<T> responseClass,Integer bearerType) throws Exception {
		T result = null;
		CloseableHttpResponse response = null;

		try {
			HttpGet e = new HttpGet(url);
			if(MDS_BEARER == bearerType){
				e.setHeader("Authorization", "Bearer " + mdsOapiBearer);
			}else if(ACTIVE_BEARER == bearerType){
				//e.setHeader("Authorization", "Bearer " + activeOapiBearer);
			}
			
			response = client.execute(e);
			if (response.getStatusLine().getStatusCode() == 200) {
				String responseEntity = EntityUtils.toString(response.getEntity());
				result = JSON.parseObject(responseEntity, responseClass);
			} else {
				url = URLDecoder.decode(url, "UTF-8");
				LOGGER.error("get请求提交失败[" + response.getStatusLine().getStatusCode() + "]:" + url);
			}
		} catch (Exception var10) {
			var10.printStackTrace();
			LOGGER.error("get请求提交失败:" + url + "，异常信息：" + var10,var10);
			throw var10;
		} finally {
			response.close();
			client.close();
		}

		return result;
	}

	public Object doPost(CloseableHttpClient client, String url,Map<String, Object> paraMap, String json, boolean noNeedResponse,Integer bearerType)
			throws Exception {
		return doPost(client, url, paraMap,json, noNeedResponse, Object.class,bearerType);
	}

	@SuppressWarnings("deprecation")
	public <T> T doPost(CloseableHttpClient client, String url, Map<String, Object> paraMap,String json, boolean noNeedResponse,
			Class<T> responseClass,Integer bearerType) throws Exception {
		T result = null;
		CloseableHttpResponse response = null;

		try {
			HttpPost e = new HttpPost(url);
			if(MDS_BEARER == bearerType){
				e.setHeader("Authorization", "Bearer " + mdsOapiBearer);
			}else if(ACTIVE_BEARER == bearerType){
				//e.setHeader("Authorization", "Bearer " + activeOapiBearer);
			}
			if (json != null) {
				StringEntity responseEntity = new StringEntity(json, "utf-8");
				responseEntity.setContentEncoding("UTF-8");
				responseEntity.setContentType("application/json");
				e.setEntity(responseEntity);
			}
			if(null != paraMap && !paraMap.isEmpty()){
				List<NameValuePair>list = new ArrayList<NameValuePair>();  
				Iterator<String> keyIt = paraMap.keySet().iterator();
				String tempKey;
				while(keyIt.hasNext()){
					tempKey = keyIt.next();
					list.add(new BasicNameValuePair(tempKey, paraMap.get(tempKey).toString()));  
				}
				e.setEntity(new UrlEncodedFormEntity(list,HTTP.UTF_8)); 
			}

			response = client.execute(e);
			if (response.getStatusLine().getStatusCode() != 200) {
				LOGGER.error("post response error:" + JSON.toJSONString(response));
				return result;
			}

			String responseEntity1 = EntityUtils.toString(response.getEntity());
			if (!noNeedResponse) {
				result = JSON.parseObject(responseEntity1, responseClass);
				return result;
			}
		} catch (Exception var12) {
			var12.printStackTrace();
			url = URLDecoder.decode(url, "UTF-8");
			LOGGER.error("post请求提交失败:" + url + ",code: " + response.getStatusLine().getStatusCode() + "，异常信息：" + var12,var12);
			throw var12;
		} finally {
			response.close();
			client.close();
		}

		return null;
	}
	
	
	public static void main(String[] args) {
		OapiUtil oapiUtil = new OapiUtil();
	    String actName = "lvTest";
	    String actDes = "lvTest";
	    String vin = "lvTest";
	    String start = "2016-05-30 13:37:08";
	    String end = "2016-07-30 13:37:08";
	    String picUrl = "https://210.13.68.150:20011/oss/download/assets/18a7490741154a8d9f605ebb49d97f4d.jpg";
	    String H5Url = "https://210.13.68.150:20011/app-avn/activity/01A1001.jsp";
		String method = "createDynamicActivity";
		Map<String, Object> paraMap = new HashMap<>();
		try {
			paraMap.put("actName", actName);
			paraMap.put("actDes", actDes);
			paraMap.put("vin", vin);
			paraMap.put("start", start);
			paraMap.put("end", end);
			paraMap.put("picUrl", picUrl);
			paraMap.put("H5Url", H5Url);
			paraMap.put("poi_id", "P2100025");
			//oapiUtil.activePost(method,paraMap,true);
			/*
			paraMap.put("tourCardId", "1");
			paraMap.put("eventName", "kkk");
			paraMap.put("eventType", "2");
			paraMap.put("triggerType", "1");
			paraMap.put("radius", "1");
			paraMap.put("tips", "22");
			oapiUtil.activePost("",paraMap,true);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}


