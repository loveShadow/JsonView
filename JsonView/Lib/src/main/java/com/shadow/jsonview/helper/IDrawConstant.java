package com.shadow.jsonview.helper;

import android.graphics.Color;

import java.util.HashMap;

/**
 * 绘制的常量
 * @author zhaoli
 */

public interface IDrawConstant {
    /**
     * 行数绘制--左边距离
     */
    int LINE_COUNT_LEFT_BOUND = 10;

    /**
     * 行数绘制--右边距离
     */
    int LINE_COUNT_RIGHT_BOUND = 10;

    /**
     * 行数绘制--竖线宽度
     */
    int LINE_COUNT_LINE_WIDTH = 1;

    /**
     * 行数绘制--颜色
     */
    int LINE_COUNT_COLOR = Color.parseColor("#FFFFFF");

    /**
     * 文本绘制--文字大小
     */
    int TEXT_SIZE = 35;

    /**
     * 文本绘制--行间隔，不包含文本高度
     */
    int TEXT_LINE_SPACE = 20;

    /**
     * 文本绘制--左边距离
     */
    int TEXT_LEFT_BOUND = 10;

    /**
     * 文本绘制--缩进
     */
    int TEXT_TAB = 40;

    /**
     * 符号两边的距离
     */
    int SYMBOL_SPACE = 10;

    /**
     * Key的颜色
     */
    int COLOR_KEY = Color.parseColor("#92278F");

    /**
     * 正常文本颜色
     */
    int COLOR_NORMAL = Color.parseColor("#FFFFFF");

    /**
     * String 值的颜色
     */
    int COLOR_VALUE_STRING = Color.parseColor("#63AF5C");

    /**
     * Number 值的颜色
     */
    int COLOR_VALUE_NUMBER = Color.parseColor("#3BA4E4");

    /**
     * Boolean 值的颜色
     */
    int COLOR_VALUE_BOOLEAN = Color.parseColor("#F8707E");

    interface Symbol {
        String Brackets_Left = "[";
        String Brackets_Right = "]";
        String Big_Parantheses_Left = "{";
        String Big_Parantheses_Right = "}";
        String Comma = ",";
        String Double_Quotes = "\"";
        String Colon = ":";
    }
}
