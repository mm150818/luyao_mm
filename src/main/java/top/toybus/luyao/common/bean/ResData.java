package top.toybus.luyao.common.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 * 
 * @author sunxg
 */
public class ResData {
    /** 用户已经存在 */
    public static final int SC_USER_EXISTS = 1000;
    /** 参数错误 */
    public static final int SC_PARAM_ERROR = 1001;
    /** 未登录 */
    public static final int SC_NOT_LOGIN = 1002;

    private int sc = 0; // 状态码，0：OK，1-99：错误状态码说明，1000-：通用错误码
    private String msg = "OK"; // 返回码说明
    private Map<String, Object> data = new HashMap<>(); // 数据

    public ResData() {
    }

    public ResData(int sc) {
	this.sc = sc;
    }

    public static ResData get() {
	return new ResData();
    }

    public int getSc() {
	return sc;
    }

    public ResData setSc(int sc) {
	this.sc = sc;
	return this;
    }

    public String getMsg() {
	return msg;
    }

    public ResData setMsg(String msg) {
	this.msg = msg;
	return this;
    }

    public Map<String, Object> getData() {
	return data;
    }

    /**
     * 向resData.data中存放数据
     * 
     * @param key
     * @param value
     * @return value
     */
    public <V extends Object> Object put(String key, V value) {
	return this.data.put(key, value);
    }

    /**
     * 向resData.data中存放map数据
     * 
     * @param map
     */
    public void putAll(Map<String, ? extends Object> map) {
	this.data.putAll(map);
    }
}
