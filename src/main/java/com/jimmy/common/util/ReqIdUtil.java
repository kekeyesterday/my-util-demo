/***************************************************************************************
 *
 *  Project:        ZXQ
 *
 *  Copyright ©     2014-2017 Banma Technologies Co.,Ltd
 *                  All rights reserved.
 *
 *  This software is supplied only under the terms of a license agreement,
 *  nondisclosure agreement or other written agreement with Banma Technologies
 *  Co.,Ltd. Use, redistribution or other disclosure of any parts of this
 *  software is prohibited except in accordance with the terms of such written
 *  agreement with Banma Technologies Co.,Ltd. This software is confidential
 *  and proprietary information of Banma Technologies Co.,Ltd.
 *
 ***************************************************************************************
 *
 *  Header Name: Banma.h
 *
 *  General Description: Copyright and file header.
 *
 *  Revision History:
 *                           Modification
 *   Author                Date(MM/DD/YYYY)   JiraID           Description of Changes
 *   ---------------------   ------------    ----------     -----------------------------
 *   lvchuntian            2017年3月1日
 *
 ****************************************************************************************/

package com.jimmy.common.util;  

/**
 *
 */
public class ReqIdUtil {
	public static final String REQ_ID_TAG_SERVICE = "SERVICE";
	public static final String REQ_ID_TAG_CONTROLLER = "Controller";
	public static final String CONCAT_STR = "_";
	
    private static final ThreadLocal<String> reqId = new ThreadLocal<String>();
    
    public static String getReqId(){
        return reqId.get();
    }
    public static void setReqId(String id){
        reqId.set(id);
    }
    public static void removeReqId(){
        reqId.remove();
    }
    
	public static String getReqIdForService(){
		return new StringBuffer(REQ_ID_TAG_SERVICE).append(CONCAT_STR)
				.append(System.currentTimeMillis()).append(CONCAT_STR)
				.append((int) (Math.random() * 10000)).toString();
	}
	public static String getReqIdForController(){
		return new StringBuffer(REQ_ID_TAG_CONTROLLER).append(CONCAT_STR)
				.append(System.currentTimeMillis()).append(CONCAT_STR)
				.append((int) (Math.random() * 10000)).toString();
	}
}
  
