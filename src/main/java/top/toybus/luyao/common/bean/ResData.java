package top.toybus.luyao.common.bean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 返回数据
 * 
 * @author sunxg
 */
public class ResData {
    /** 参数错误 */
    public static final int C_PARAM_ERROR = 1001;
    /** 未登录 */
    public static final int C_NOT_LOGIN = 1002;

    private int code = 0; // 状态码，0：OK，1-99：错误状态码说明，1000-：通用错误码
    private String msg = "OK"; // 返回码说明
    private Map<String, Object> data = new HashMap<>(); // 数据

    public ResData() {
    }

    public ResData(int code) {
        this.code = code;
    }

    public ResData(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @JsonIgnore
    public boolean isOk() {
        return code == 0;
    }

    public static ResData get() {
        return new ResData();
    }

    public int getCode() {
        return code;
    }

    public ResData setCode(int code) {
        this.code = code;
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
     * @return ResData
     */
    public ResData put(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    /**
     * 向resData.data中存放map数据
     * 
     * @param map
     */
    public ResData putAll(Map<String, ? extends Object> dataMap) {
        if (MapUtils.isNotEmpty(dataMap)) {
            this.data.putAll(dataMap);
        }
        return this;
    }
}
