/**  

* Licensed to SAICMotor,Inc. under the terms of the SAICMotor 
* Software License version 1.0.

* See the NOTICE file distributed with this work for additional 
* information regarding copyright ownership.  
* ----------------------------------------------------------------------------
* Date           Author      Version        Comments
* 2017年6月2日        nwcjl       1.0            Initial Version

*/  

package com.jimmy.common.util;  

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 *
 */
public class GsonUtil {
    private static Type type = new TypeToken<Map<String,String>>(){}.getType();
    private static Gson gson = new Gson();

    public static Map<String,String> json2map(String data) throws Exception {
        if(data == null) {
           // throw new ApiException(PkErrorCode.PK_REQ_API_PARAM_ERROR_CODE,PkErrorCode.PK_REQ_API_PARAM_ERROR_DESC);
        }
        Map<String,String> dataMap = gson.fromJson(data, type);
        return dataMap;
    }
    
    public static void main(String[] args) throws Exception {
		String data = "{'name':'jimmy','age':'33','childMap':'jimmy_yesterday'}";
		Map<String,String> dataMap = json2map(data);
		System.out.println(dataMap);
	}
}
  
