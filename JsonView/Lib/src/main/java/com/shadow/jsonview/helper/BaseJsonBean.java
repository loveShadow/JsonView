package com.shadow.jsonview.helper;

import android.graphics.Rect;

/**
 * @author zhaoli
 */

public abstract class BaseJsonBean {

    /**
     * 是否折叠
     * true 非折叠、打开
     * false 折叠、关闭
     */
    private boolean isFold = false;

    /**
     * 折叠图标的区域：用于判断点击的是哪个
     */
    private Rect foldIconRect = new Rect();

    /**
     * 该组合里面的总行数：包含 {、[
     * 例如："name" : {
     *     "test1" : "111",
     *     "test2" : "222"
     * }
     * 那么 name节点的总行数为4
     */
    private int totalLineCount = 0;

    /**
     * 该节点所在的行位置
     */
    private int startLineIndex = 0;

    /**
     * 是偶折叠
     * @return true 折叠 false 未折叠
     */
    public boolean isFold() {
        return isFold;
    }

    /**
     * 更改折叠状态
     */
    public void changeFoldState() {
        isFold = !isFold;
    }

    /**
     * 设置折叠图标
     * @param foldIconRect
     */
    public void setFoldIconRect(Rect foldIconRect) {
        this.foldIconRect = foldIconRect;
    }

    /**
     * 点击是否在这个区域
     * @param x
     * @param y
     * @param offset
     * @return
     */
    public boolean clickInRect(int x, int y, int offset) {
        if (x > (foldIconRect.left - 30) && x < (foldIconRect.right + 30) &&
                y > (foldIconRect.top - 30) && y < (foldIconRect.bottom + 30)) {
            return true;
        }
        return false;
    }

    /**
     * 获取该节点的总行数
     * @return 总行数
     */
    public int getTotalLineCount() {
        return totalLineCount;
    }

    /**
     * 设置该节点的总行数
     * @param totalLineCount 总行数
     */
    public void setTotalLineCount(int totalLineCount) {
        this.totalLineCount = totalLineCount;
    }

    /**
     * 获取该节点的起始行索引
     * @return 起始行索引
     */
    public int getStartLineIndex() {
        return startLineIndex;
    }

    /**
     * 获取该节点的结束行索引
     * @return
     */
    public int getEndLineIndex() {
        /**
         * 减一的目的：例如：startLineIndex = 1
         */
        return startLineIndex + totalLineCount - 1;
    }

    /**
     * 设置该节点的起始行索引
     * @param startLineIndex 起始行索引
     */
    public void setStartLineIndex(int startLineIndex) {
        this.startLineIndex = startLineIndex;
    }
}
