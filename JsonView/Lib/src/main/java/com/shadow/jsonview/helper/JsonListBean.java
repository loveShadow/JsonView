package com.shadow.jsonview.helper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaoli
 */

public class JsonListBean extends BaseJsonBean {
    /**
     * Json数据
     * Object包含：
     * 1. 标准类型：int、String等
     * 2. JsonBean类型
     * 3. List类型
     */
    private List<JsonValueBean> data = new ArrayList<>();

    public List<JsonValueBean> getData() {
        return data;
    }

    public void setData(List<JsonValueBean> data) {
        this.data = data;
    }
}
