package com.demo.jsonview;

import android.app.Activity;
import android.os.Bundle;

import com.shadow.jsonview.JsonView;

/**
 * 测试Activity
 * @author zhaoli
 */
public class MainActivity extends Activity {
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((JsonView) findViewById(R.id.jsonView)).setJson(
                "{\"service\":\"video\",\"semantic\":{\"type\":\"QUERY\",\"startYear\":\"1993-01-01\",\"endyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyYear\":\"1993-12-31\",\"artist\":\"周星驰|刘德华\",\"artistRole\":\"主演\",\"category\":\"电视你见到是接视你见到是接视你见到是接视你见到是接视你见到是接收剧\",\"test\":[\"111\",\"2222222222222222222222222222222222222\",\"333\"],\"test2\":[{\"name\":\"111\",\"value\":2211111111111112},{\"name\":\"4444444444444444444444444444444444444444444444444444444\",\"value\":false}],\"test3\":[[\"111\", \"333\", \"444\"],[\"222\", \"666\"]]}}");
    }
}
