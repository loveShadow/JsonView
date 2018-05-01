package com.shadow.jsonview.helper;

import java.util.HashMap;

/**
 * @author zhaoli
 * Json对象
 */

public class JsonMapBean extends BaseJsonBean {
    /**
     * Json数据
     * Object包含：
     * 1. 标准类型：int、String等
     * 2. JsonBean类型
     * 3. List类型
     */
    private HashMap<String, JsonValueBean> data = new HashMap<>();

    public HashMap<String, JsonValueBean> getData() {
        return data;
    }

    public void setData(HashMap<String, JsonValueBean> data) {
        this.data = data;
    }
}
