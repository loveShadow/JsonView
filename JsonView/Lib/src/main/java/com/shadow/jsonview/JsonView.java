package com.shadow.jsonview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.shadow.jsonview.helper.BaseJsonBean;
import com.shadow.jsonview.helper.DrawHelper;
import com.shadow.jsonview.helper.JsonListBean;
import com.shadow.jsonview.helper.JsonMapBean;
import com.shadow.jsonview.helper.JsonParseHelper;
import com.shadow.jsonview.helper.JsonValueBean;
import com.shadow.jsonview.helper.ViewMoveHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * 用于展示Json的页面
 *
 * @author zhaoli
 */

public class JsonView extends View {

    /**
     * 画笔：用于绘制行号、Json串
     */
    private Paint paint;
    /**
     * 页面移动帮助类：用于页面无法显示全Json,左右移动布局使用
     */
    private ViewMoveHelper viewMoveHelper;
    /**
     * 页面绘制帮助类
     */
    private DrawHelper drawHelper = new DrawHelper();
    /**
     * Json数据
     */
    private BaseJsonBean jsonBean = null;

    /**
     * 点击回调事件
     */
    private OnClickListener oriClickListener = null;

    public JsonView(Context context) {
        this(context, null);
    }

    public JsonView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public JsonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);

        viewMoveHelper = new ViewMoveHelper();
        viewMoveHelper.setMoveXEnabled(false);
        viewMoveHelper.setOnClickListener(new ViewMoveHelper.JsonViewOnClickListener() {
            @Override
            public void onClicked(int x, int y) {
                //判断点击坐标（最后松开的坐标）
                if (null != jsonBean) {
                    BaseJsonBean bean = findClickedJsonBean(jsonBean, x, y);
                    if (null != bean) {
                        bean.changeFoldState();
                        Log.d("JsonView", "clicked line = " + bean.getStartLineIndex());
                        invalidate();
                    }
                }
                if (null != oriClickListener) {
                    oriClickListener.onClick(JsonView.this);
                }
            }
        });
    }

    /**
     * 设置Json
     * @param json
     */
    public void setJson(String json) {
        jsonBean = JsonParseHelper.parseJson(json);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawHelper.setViewWidthAndHeight(getWidth(), getHeight());
        canvas.translate(viewMoveHelper.getMoveX(), viewMoveHelper.getMoveY());
        if (null != jsonBean) {
            drawHelper.resetDrawData(paint, jsonBean.getTotalLineCount());
            drawHelper.drawJsonBean(canvas, paint, jsonBean);
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (viewMoveHelper.onTouchEvent(event)) {
            invalidate();
        }
        return true;
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        oriClickListener = l;
    }

    private BaseJsonBean findClickedJsonBeanInMap(HashMap<String, JsonValueBean> map, int x, int y) {
        Set<String> keys = map.keySet();
        for (String key : keys) {
            JsonValueBean value = map.get(key);
            if (value.getValue() instanceof BaseJsonBean) {
                BaseJsonBean bean = findClickedJsonBean((BaseJsonBean) value.getValue(), x, y);
                if (null != bean) {
                    return bean;
                }
            }
        }
        return null;
    }

    private BaseJsonBean findClickedJsonBeanInList(List<JsonValueBean> list, int x, int y) {
        for (JsonValueBean value : list) {
            if (value.getValue() instanceof BaseJsonBean) {
                BaseJsonBean bean = findClickedJsonBean((BaseJsonBean) value.getValue(), x, y);
                if (null != bean) {
                    return bean;
                }
            }
        }
        return null;
    }

    private BaseJsonBean findClickedJsonBean(BaseJsonBean jsonBean, int x, int y) {
        if (jsonBean.clickInRect(x, y, 0)) {
            return jsonBean;
        }
        if (jsonBean instanceof JsonListBean) {
            return findClickedJsonBeanInList(((JsonListBean) jsonBean).getData(), x, y);
        } else if (jsonBean instanceof JsonMapBean) {
            return findClickedJsonBeanInMap(((JsonMapBean) jsonBean).getData(), x, y);
        }
        return null;
    }
}
