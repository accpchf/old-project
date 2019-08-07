package com.ks0100.common;

import java.util.HashMap;
import java.util.Map;

/**
 * 响应数据格式封装
 *
 * 创建日期：2014-12-3
 * @author chengls
 */
public class ResultDataJsonUtils {
	
	/**
	 * 自定义返回格式
	 *
	 * @param isSuccess
	 * @param data
	 * @param errormsg
	 * @return
	 * 创建日期：2014-12-3
	 * 修改说明：
	 * @author chengls
	 */
	public static Map<String, Object> responseResult(boolean isSuccess, Object data, String errormsg, String errorType) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("success", isSuccess);
		map.put("resultData", data);
		map.put("errormsg", errormsg);
		map.put("errorType", errorType);
		return map;
	}
	/**
	 * 自定义返回格式
	 *
	 * @param map
	 * @return
	 * 创建日期：2014-12-3
	 * 修改说明：
	 * @author chengls
	 */
	public static Map<String, Object> responseResult(Map<String, Object> map) {
		return map;
	}
	
	/**
	 * 请求成功快速响应
	 *
	 * @param data
	 * @return
	 * 创建日期：2014-12-3
	 * 修改说明：
	 * @author chengls
	 */
	public static Map<String, Object> successResponseResult(Object data) {
		return responseResult(true, data, "", "");
	}
	/**
	 * 请求成功快速响应
	 *
	 * @param data
	 * @return
	 * 创建日期：2014-12-3
	 * 修改说明：
	 * @author chengls
	 */
	public static Map<String, Object> successResponseResult() {
		return responseResult(true, null, "", "");
	}
	
	/**
	 * 请求失败快速响应
	 *
	 * @param errormsg
	 * @return
	 * 创建日期：2014-12-3
	 * 修改说明：
	 * @author chengls
	 */
	public static Map<String, Object> errorResponseResultData(String errormsg,Object data) {
		return responseResult(false, data, errormsg, "");
	}
	
	/**
	 * 请求失败快速响应
	 *
	 * @param errormsg
	 * @return
	 * 创建日期：2014-12-3
	 * 修改说明：
	 * @author chengls
	 */
	public static Map<String, Object> errorResponseResult(String errormsg) {
		return responseResult(false, null, errormsg, "");
	}
	
	/**
	 * 当错误信息需要分类时，可用此方法， 通过errorType参数分类
	 *
	 * @param errormsg
	 * @param errorType
	 * @return
	 * 创建日期：2014-12-8
	 * 修改说明：
	 * @author chengls
	 */
	public static Map<String, Object> errorResponseResult(String errormsg, String errorType) {
		return responseResult(false, null, errormsg, errorType);
	}
}
