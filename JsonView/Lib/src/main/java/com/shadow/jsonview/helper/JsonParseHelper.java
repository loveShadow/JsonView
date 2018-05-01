package com.shadow.jsonview.helper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Json解析帮助类
 * @author zhaoli
 */

public class JsonParseHelper {

    private final static String TAG_ARRAY = "[";
    private final static String TAG_OBJECT = "{";

    /**
     * 解析Json数据
     * @param json
     * @return
     */
    public static BaseJsonBean parseJson(String json) {
        BaseJsonBean jsonBean = null;
        int startLineIndex = 1;
        try {
            if (json.startsWith(TAG_ARRAY)) {
                jsonBean = parseList(new JSONArray(json), startLineIndex + 1);
            } else if (json.startsWith(TAG_OBJECT)) {
                jsonBean = parseObject(new JSONObject(json), startLineIndex + 1);
            }
            if (null != jsonBean) {
                //设置起始行数为1
                jsonBean.setStartLineIndex(startLineIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonBean;
    }

    private static JsonMapBean parseObject(JSONObject jsonObject, int startLineIndex) {
        if (null == jsonObject) {
            return null;
        }
        try {
            //计算总行数
            int lineCount = 0;
            JsonMapBean jsonMapBean = new JsonMapBean();
            Iterator<String> keys = jsonObject.keys();
            HashMap<String, JsonValueBean> map = new LinkedHashMap<>();
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                //判断值的类型
                if (value instanceof JSONObject) {
                    JsonMapBean mapBean = parseObject((JSONObject) value,
                            startLineIndex + 1);
                    if (null != mapBean) {
                        lineCount += mapBean.getTotalLineCount();
                        mapBean.setStartLineIndex(startLineIndex);
                        map.put(key, new JsonValueBean(mapBean, startLineIndex));
                        //起始行数要加 该节点的总行数
                        startLineIndex += mapBean.getTotalLineCount();
                    }
                } else if (value instanceof JSONArray) {
                    JsonListBean listBean = parseList((JSONArray) value,
                            startLineIndex + 1);
                    if (null != listBean) {
                        lineCount += listBean.getTotalLineCount();
                        listBean.setStartLineIndex(startLineIndex);
                        map.put(key, new JsonValueBean(listBean, startLineIndex));
                        //起始行数要加 该节点的总行数
                        startLineIndex += listBean.getTotalLineCount();
                    }
                } else {
                    lineCount ++;
                    map.put(key, new JsonValueBean(value, startLineIndex));
                    //起始行数要加 1
                    startLineIndex ++;
                }
            }
            jsonMapBean.setData(map);
            //包含 { 、 } 两行
            jsonMapBean.setTotalLineCount(2 + lineCount);
            return jsonMapBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static JsonListBean parseList(JSONArray jsonArray, int startLineIndex) {
        if (null == jsonArray) {
            return null;
        }
        try {
            int lineCount = 0;
            JsonListBean jsonListBean = new JsonListBean();
            List<JsonValueBean> list = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                Object value = jsonArray.get(i);
                if (value instanceof JSONObject) {
                    JsonMapBean mapBean = parseObject((JSONObject) value,
                            startLineIndex + 1);
                    if (null != mapBean) {
                        lineCount += mapBean.getTotalLineCount();
                        mapBean.setStartLineIndex(startLineIndex);
                        list.add(new JsonValueBean(mapBean, startLineIndex));
                        startLineIndex += mapBean.getTotalLineCount();
                    }
                } else if (value instanceof JSONArray) {
                    JsonListBean listBean = parseList((JSONArray) value,
                            startLineIndex + 1);
                    if (null != listBean) {
                        lineCount += listBean.getTotalLineCount();
                        listBean.setStartLineIndex(startLineIndex);
                        list.add(new JsonValueBean(listBean, startLineIndex));
                        startLineIndex += listBean.getTotalLineCount();
                    }
                } else {
                    lineCount ++;
                    list.add(new JsonValueBean(value, startLineIndex));
                    //起始行数要加 1
                    startLineIndex ++;
                }
            }
            jsonListBean.setData(list);
            //包含 [ 、 ] 两行
            jsonListBean.setTotalLineCount(2 + lineCount);
            return jsonListBean;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
