package com.shadow.jsonview.helper;

/**
 * @author zhaoli
 * Json值对象
 */

public class JsonValueBean {
    /**
     * Value值
     */
    private Object value;
    /**
     * 所在行数
     */
    private int lineIndex = 0;

    public JsonValueBean(Object value, int lineIndex) {
        this.value = value;
        this.lineIndex = lineIndex;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }
}
