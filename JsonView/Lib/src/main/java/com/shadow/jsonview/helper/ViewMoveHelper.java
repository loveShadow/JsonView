package com.shadow.jsonview.helper;

import android.graphics.Rect;
import android.view.MotionEvent;


/**
 * 页面移动帮助类
 *
 * @author zhaoli
 */

public class ViewMoveHelper {

    /**
     * 是否可以移动X/Y轴：默认打开
     * moveEnabled 表示整体移动打开，包含X、Y
     */
    private boolean moveXEnabled = true, moveYEnabled = true;

    /**
     * 移动的边界：默认没有边界
     */
    private Rect borderRect = new Rect(Integer.MIN_VALUE, Integer.MIN_VALUE, 0, 0);

    /**
     * 移动之前的X/Y坐标
     */
    private int moveStartX = 0, moveStartY = 0;

    /**
     * 点击按下时间
     */
    private long clickedDownTime = 0;

    /**
     * 总共移动X/Y坐标多少
     */
    private int moveX = 0, moveY = 0;

    /**
     * 收到ACTION_UP 的X/Y坐标
     */
    private int upX = 0, upY = 0;

    private JsonViewOnClickListener onClickListener = null;

    /**
     * Json 点击事件
     */
    public interface JsonViewOnClickListener {
        /**
         * 点击位置
         * @param x
         * @param y
         */
        void onClicked(int x, int y);
    }

    public ViewMoveHelper() {
    }

    public ViewMoveHelper(Rect borderRect) {
        this.borderRect = borderRect;
    }

    public void setOnClickListener(JsonViewOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /**
     * 当View 接收到onTouchEvent消息时调用
     * @param event
     * @return 是否需要重绘
     */
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveStartX = (int) event.getX();
                moveStartY = (int) event.getY();
                clickedDownTime = System.currentTimeMillis();
                return false;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                int moveEndX = upX = (int) event.getX();
                int moveEndY = upY = (int) event.getY();
                //判断是不是单击事件
                if (event.getAction() == MotionEvent.ACTION_UP &&
                        Math.abs(moveEndX - moveStartX) < 10 &&
                        Math.abs(moveEndY - moveEndY) < 10 &&
                        System.currentTimeMillis() - clickedDownTime < 500) {
                    //算点击
                    if (null != onClickListener) {
                        onClickListener.onClicked(moveEndX, moveEndY);
                        return false;
                    }
                }
                if (isMoveXEnabled()) {
                    moveX += moveEndX - moveStartX;
                    //处理边界
                    if (moveX < borderRect.left) {
                        moveX = borderRect.left;
                    } else if (moveX > borderRect.right) {
                        moveX = borderRect.right;
                    }
                }
                if (isMoveYEnabled()) {
                    moveY += moveEndY - moveStartY;
                    if (moveY < borderRect.top) {
                        moveY = borderRect.top;
                    } else if (moveY > borderRect.bottom) {
                        moveY = borderRect.bottom;
                    }
                }
                moveStartX = moveEndX;
                moveStartY = moveEndY;
                return needRedraw();
            default:
                return false;
        }
    }

    /**
     * 是否需要重绘
     * @return true 需要
     */
    private boolean needRedraw() {
        return moveXEnabled || moveYEnabled;
    }

    /**
     * 获取X轴移动坐标
     * @return
     */
    public int getMoveX() {
        if (moveXEnabled) {
            return moveX;
        }
        return 0;
    }

    /**
     * 获取Y轴移动坐标
     * @return
     */
    public int getMoveY() {
        if (moveYEnabled) {
            return moveY;
        }
        return 0;
    }

    public int getUpX() {
        return upX;
    }

    public int getUpY() {
        return upY;
    }

    public boolean isMoveXEnabled() {
        return moveXEnabled;
    }

    public void setMoveXEnabled(boolean moveXEnabled) {
        this.moveXEnabled = moveXEnabled;
    }

    public boolean isMoveYEnabled() {
        return moveYEnabled;
    }

    public void setMoveYEnabled(boolean moveYEnabled) {
        this.moveYEnabled = moveYEnabled;
    }

    public void setBorderRect(Rect borderRect) {
        this.borderRect = borderRect;
    }
}
