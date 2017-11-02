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
 *   Class Name: com.zebra.carcloud.trip.utils.StringUtil
 *
 *   General Description:
 *
 *   Revision History:
 *                            Modification
 *    Author                Date(MM/DD/YYYY)   JiraID           Description of Changes
 * **************************************************************************************
 *    lvchuntian            2017-02-13
 *
 * **************************************************************************************
 */

package com.zebra.carcloud.trip.utils;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtilsBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.zebra.carcloud.trip.vo.travel.TravelDayResInfo;

/**
 *
 */
public class StringUtil {
	private static Gson gson = new Gson();
    
    public static String null2Str(Object origin) {
        return ((origin == null || "null".equals(origin)) ? "" : origin.toString().trim());
    }

    public static Map<String, Object> beanToMap(Object obj) {
        Map<String, Object> params = new HashMap<String, Object>(0);
        try {
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
            for (int i = 0; i < descriptors.length; i++) {
                String name = descriptors[i].getName();
                if (!"class".equals(name)) {
                    params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }
    private static ObjectMapper mapper = new ObjectMapper();
    
    public static String getJsonByObj(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "null";
        }
        
    }
    
    public static <T> T transJsonToObject(String objJson,Class<T> t){
        try {
            return gson.fromJson(objJson, t);
        } catch (Exception e) {
        	e.printStackTrace();
            return null;
        }
        
    }
    
    
    public static void main(String[] args) {
    	TravelDayResInfo resInfo = new TravelDayResInfo();
    	resInfo.setCoverUrl("aaa");
    	resInfo.setId("111");
    	resInfo.setResUrl("asdfasd");
    	String json = getJsonByObj(resInfo);
    	System.out.println(json);
    	TravelDayResInfo resInfo11 = transJsonToObject(json,TravelDayResInfo.class);
    	System.out.println(resInfo11.getResUrl());
	}
    
}
