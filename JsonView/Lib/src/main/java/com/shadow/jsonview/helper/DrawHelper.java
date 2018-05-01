package com.shadow.jsonview.helper;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 绘制帮助类
 *
 * @author zhaoli
 */

public class DrawHelper implements IDrawConstant {

    /**
     * 每行文本的高度
     */
    private int textHeight = 0;

    /**
     * 伸缩图标的偏移量（textHeight * 10%）
     */
    private int shrinkIconOffset = 0;

    /**
     * 最大行数的数据
     */
    private Rect maxLineCountData = new Rect();

    /**
     * 符号的宽度
     */
    private static HashMap<String, Integer> symbolWidth = new HashMap<>();

    /**
     * JsonView的宽和高
     */
    private int jsonViewWidth = 0, jsonViewHeight = 0;

    /**
     * 设置JsonView的宽和高
     *
     * @param width  宽
     * @param height 高
     */
    public void setViewWidthAndHeight(int width, int height) {
        jsonViewWidth = width;
        jsonViewHeight = height;
    }

    /**
     * 复原绘制数据
     *
     * @param paint     画笔
     * @param lineCount 行数
     */
    public void resetDrawData(Paint paint, int lineCount) {
        paint.setTextSize(TEXT_SIZE);
        paint.setColor(LINE_COUNT_COLOR);
        paint.setTextAlign(Paint.Align.LEFT);
        maxLineCountData.setEmpty();
        //计算行数文本的最大宽度，以及每行的文本高度
        paint.getTextBounds(String.valueOf(lineCount), 0,
                String.valueOf(lineCount).length(), maxLineCountData);
        currentY = -maxLineCountData.top + TEXT_LINE_SPACE;
        //计算文本高度
        textHeight = maxLineCountData.height();
        shrinkIconOffset = (int) (0.1 * textHeight);
    }

    /**
     * 绘制Json对象
     *
     * @param canvas   画布
     * @param paint    画笔
     * @param jsonBean Json对象
     */
    public void drawJsonBean(Canvas canvas, Paint paint, BaseJsonBean jsonBean) {
        //step 1. 绘制内容
        //起始X坐标需要跳过数字和竖线
        int currentX = LINE_COUNT_LEFT_BOUND + maxLineCountData.width() + LINE_COUNT_RIGHT_BOUND
                + LINE_COUNT_LINE_WIDTH + TEXT_LEFT_BOUND;
        if (jsonBean instanceof JsonMapBean) {
            //JsonMap对象
            drawJsonMapBean(canvas, paint, (JsonMapBean) jsonBean, currentX, currentX, false);
        } else if (jsonBean instanceof JsonListBean) {
            //JsonList对象
            drawJsonListBean(canvas, paint, (JsonListBean) jsonBean, currentX, currentX, false);
        } else {
            Log.e("JsonView", "drawJsonBean error : " + jsonBean);
        }
        //step 2. 绘制线条
        canvas.drawLine(currentX - LINE_COUNT_LINE_WIDTH - TEXT_LEFT_BOUND,
                0, currentX - LINE_COUNT_LINE_WIDTH - TEXT_LEFT_BOUND,
                currentY + textHeight, paint);
    }

    /**
     * 当前Y坐标
     */
    private int currentY = 0;

    /**
     * 绘制JsonMap对象
     *
     * @param canvas      画布
     * @param paint       画笔
     * @param jsonMapBean JsonMap对象
     * @param currentX    当前X坐标（用于绘制节点内部内容）
     * @param nodeX       缩进的X坐标（用于绘制Key之后的括号和缩进图标）
     * @param nDC         是否需要绘制,
     */
    private void drawJsonMapBean(Canvas canvas, Paint paint, JsonMapBean jsonMapBean,
                                 int currentX, int nodeX, boolean nDC) {
        int startTempX = currentX;
        Rect rect = new Rect();
        //step 1. 绘制数字
        if (currentX == nodeX) {
            //相等，说明之前没有绘制过
            drawLineText(canvas, paint, jsonMapBean.getStartLineIndex(), currentY);
        }
        //step 2. 绘制缩进图标
        int x = drawFoldIcon(canvas, paint, nodeX, currentY, jsonMapBean);
        //X坐标偏移
        nodeX += x;
        //step 3. 判断是否缩进
        if (jsonMapBean.isFold()) {
            String foldString = "Object{...}";
            canvas.drawText(foldString, nodeX, currentY, paint);
            //X坐标偏移
            paint.getTextBounds(foldString, 0, foldString.length(), rect);
            return;
        }
        //step 4. 绘制括号
        canvas.drawText(Symbol.Big_Parantheses_Left, nodeX, currentY, paint);
        //X、Y坐标偏移
        currentX += TEXT_TAB;
        currentY += textHeight + TEXT_LINE_SPACE;
        //step 5. 绘制内容
        HashMap<String, JsonValueBean> map = jsonMapBean.getData();
        Set<String> keys = map.keySet();
        //将Key进行按照行数排序
        int index = 0;
        for (String key : keys) {
            JsonValueBean value = map.get(key);

            index++;
            //是否需要绘制 ,
            boolean needDrawComma = (index != map.size());
            //临时X坐标
            int tempX = currentX;
            //绘制key
            //绘制数字
            drawLineText(canvas, paint, value.getLineIndex(), currentY);
            String keyText = Symbol.Double_Quotes + key + Symbol.Double_Quotes;
            int drawWidth = drawValueBean(canvas, paint, new JsonValueBean(keyText, -1),
                    tempX, COLOR_KEY, false);
            //X坐标偏移
            tempX += drawWidth;

            //绘制 :
            tempX += SYMBOL_SPACE;
            canvas.drawText(Symbol.Colon, tempX, currentY, paint);
            //X坐标偏移
            tempX += getSymbolWidth(Symbol.Colon, paint) + SYMBOL_SPACE;

            //绘制value
            if (value.getValue() instanceof JsonMapBean) {
                //绘制Map
                drawJsonMapBean(canvas, paint, (JsonMapBean) value.getValue(),
                        startTempX + TEXT_TAB, tempX, needDrawComma);
            } else if (value.getValue() instanceof JsonListBean) {
                //绘制List
                drawJsonListBean(canvas, paint, (JsonListBean) value.getValue(),
                        startTempX + TEXT_TAB, tempX, needDrawComma);
            } else {
                //绘制值
                drawValueBean(canvas, paint, value, tempX, -1, needDrawComma);
            }
            //Y坐标偏移
            currentY += textHeight + TEXT_LINE_SPACE;
        }
        //step 6. 绘制括号
        //绘制数字
        drawLineText(canvas, paint, jsonMapBean.getEndLineIndex(), currentY);
        canvas.drawText(Symbol.Big_Parantheses_Right, startTempX, currentY, paint);
        //step 7. 绘制,
        if (nDC) {
            //绘制 , 注意：增加符号的空间
            canvas.drawText(Symbol.Comma,
                    startTempX + getSymbolWidth(Symbol.Big_Parantheses_Right, paint) + SYMBOL_SPACE,
                    currentY, paint);
        }
    }

    /**
     * 绘制JsonList对象
     *
     * @param canvas       画布
     * @param paint        画笔
     * @param jsonListBean JsonMap对象
     * @param currentX     当前X坐标
     * @param nodeX        缩进的X坐标（用于绘制Key之后的括号和缩进图标）
     * @param nDC          是否需要绘制,
     */
    private void drawJsonListBean(Canvas canvas, Paint paint, JsonListBean jsonListBean,
                                  int currentX, int nodeX, boolean nDC) {
        int startTempX = currentX;
        Rect rect = new Rect();
        //step 1. 绘制数字
        drawLineText(canvas, paint, jsonListBean.getStartLineIndex(), currentY);
        //step 2. 绘制缩进图标
        int x = drawFoldIcon(canvas, paint, nodeX, currentY, jsonListBean);
        //X坐标偏移
        nodeX += x;
        //step 3. 判断是否缩进
        if (jsonListBean.isFold()) {
            String foldString = "Array[" + jsonListBean.getData().size() + "]";
            canvas.drawText(foldString, nodeX, currentY, paint);
            //X坐标偏移
            paint.getTextBounds(foldString, 0, foldString.length(), rect);
            return;
        }
        //step 4. 绘制括号
        canvas.drawText(Symbol.Brackets_Left, nodeX, currentY, paint);
        //X、Y坐标偏移
        currentX += TEXT_TAB;
        currentY += textHeight + TEXT_LINE_SPACE;
        //step 5. 绘制内容
        List<JsonValueBean> list = jsonListBean.getData();
        int index = 0;
        for (int i = 0; i < list.size(); i++) {
            index++;
            //是否需要绘制 ,
            boolean needDrawComma = (index != list.size());
            //临时X坐标
            int tempX = currentX;

            //绘制value
            JsonValueBean value = list.get(i);
            if (value.getValue() instanceof JsonMapBean) {
                //绘制Map
                drawJsonMapBean(canvas, paint, (JsonMapBean) value.getValue(),
                        startTempX + TEXT_TAB, tempX, needDrawComma);
            } else if (value.getValue() instanceof JsonListBean) {
                //绘制List
                drawJsonListBean(canvas, paint, (JsonListBean) value.getValue(),
                        startTempX + TEXT_TAB, tempX, needDrawComma);
            } else {
                //绘制数字
                drawLineText(canvas, paint, value.getLineIndex(), currentY);
                //绘制值
                drawValueBean(canvas, paint, value, tempX, -1, needDrawComma);
            }
            //Y坐标偏移
            currentY += textHeight + TEXT_LINE_SPACE;
        }
        //step 6. 绘制括号
        //绘制数字
        drawLineText(canvas, paint, jsonListBean.getEndLineIndex(), currentY);
        canvas.drawText(Symbol.Brackets_Right, startTempX, currentY, paint);
        //step 7. 绘制,
        if (nDC) {
            //绘制 , 注意：增加符号的空间
            canvas.drawText(Symbol.Comma,
                    startTempX + getSymbolWidth(Symbol.Brackets_Right, paint) + SYMBOL_SPACE,
                    currentY, paint);
        }
    }

    private void drawLineText(Canvas canvas, Paint paint, int line, int y) {
        //step 1. 绘制数字
        //从右边绘制
        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setColor(COLOR_NORMAL);
        canvas.drawText(String.valueOf(line),
                LINE_COUNT_LEFT_BOUND + maxLineCountData.width(), y, paint);
        //恢复
        paint.setTextAlign(Paint.Align.LEFT);
    }

    /**
     * 绘制值
     *
     * @param canvas        画布
     * @param paint         画笔
     * @param valueBean     值
     * @param startX        开始X坐标
     * @param defaultColor  默认颜色
     * @param needDrawComma 是否需要绘制,
     * @return X 坐标偏移
     */
    private int drawValueBean(Canvas canvas, Paint paint, JsonValueBean valueBean,
                              int startX, int defaultColor, boolean needDrawComma) {
        int tempStartX = startX;
        Rect rect = new Rect();
        //设置画笔颜色
        int color;
        String drawValue = String.valueOf(valueBean.getValue());
        if (-1 == defaultColor) {
            color = COLOR_NORMAL;
            if (valueBean.getValue() instanceof String) {
                color = COLOR_VALUE_STRING;
                drawValue = Symbol.Double_Quotes + drawValue + Symbol.Double_Quotes;
            } else if (valueBean.getValue() instanceof Number) {
                color = COLOR_VALUE_NUMBER;
            } else if (valueBean.getValue() instanceof Boolean) {
                color = COLOR_VALUE_BOOLEAN;
            }
        } else {
            color = defaultColor;
        }
        paint.setColor(color);
        //绘制
        paint.getTextBounds(drawValue, 0, drawValue.length(), rect);
        if (startX + rect.width() > jsonViewWidth) {
            //一行绘制不下
            TextPaint textPaint = new TextPaint(paint);
            /**
             * 文本内容、画笔、最大宽度、对齐方式、行间距倍数、行间距增值、文本顶部和底部是否留白
             */
            StaticLayout sl = new StaticLayout(drawValue, textPaint, jsonViewWidth - startX,
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            int lineCount = sl.getLineCount();
            int drawTextIndex = 0;
            String drawLineText = "";
            for (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
                drawLineText = drawValue.substring(drawTextIndex, sl.getLineEnd(lineIndex));
                canvas.drawText(drawLineText, startX, currentY, paint);
                currentY += textHeight + TEXT_LINE_SPACE;
                drawTextIndex += drawLineText.length();
            }
            //最后一行不用加
            currentY -= (textHeight + TEXT_LINE_SPACE);
            //最后一行的宽度
            paint.getTextBounds(drawLineText, 0, drawLineText.length(), rect);
        } else {
            canvas.drawText(drawValue, startX, currentY, paint);
        }
        startX += rect.width();
        //绘制,
        if (needDrawComma) {
            startX += SYMBOL_SPACE;
            //绘制 , 注意：增加符号的空间
            canvas.drawText(Symbol.Comma, startX, currentY, paint);
            startX += getSymbolWidth(Symbol.Comma, paint);
        }
        paint.setColor(COLOR_NORMAL);
        return startX - tempStartX;
    }

    /**
     * 绘制伸缩图标
     *
     * @param canvas   画布
     * @param paint    画笔
     * @param x        起始X
     * @param y        起始Y
     * @param jsonBean Json对象
     * @return 绘制的长度
     */
    private int drawFoldIcon(Canvas canvas, Paint paint, int x, int y, BaseJsonBean jsonBean) {
        //计算绘制的Y坐标中心位置
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int offset = (fontMetricsInt.descent - fontMetricsInt.ascent) / 2;
        //中心Y
        int centerY = y + fontMetricsInt.ascent + offset;
        //绘制长度的一半
        int lengthHalf = (textHeight - 2 * shrinkIconOffset) / 2;
        //设置缩进图标的区域
        jsonBean.setFoldIconRect(new Rect(x, centerY - lengthHalf - shrinkIconOffset,
                x + textHeight, centerY + lengthHalf + shrinkIconOffset));
        //绘制图标
        Paint.Style style = paint.getStyle();
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(new Rect(x + shrinkIconOffset, centerY - lengthHalf,
                        x + textHeight - shrinkIconOffset, centerY + lengthHalf),
                paint);
        canvas.drawLine(x + shrinkIconOffset, centerY,
                x + textHeight - shrinkIconOffset, centerY, paint);
        if (jsonBean.isFold()) {
            canvas.drawLine(x + textHeight / 2, centerY - lengthHalf,
                    x + textHeight / 2, centerY + lengthHalf, paint);
        }
        //恢复画笔样式
        paint.setStyle(style);
        //返回绘制的长度
        return textHeight + SYMBOL_SPACE;
    }

    /**
     * 获取符号的宽度，用于改变X轴坐标
     *
     * @param symbol 符号
     * @param paint  画笔
     * @return 宽度
     */
    private int getSymbolWidth(String symbol, Paint paint) {
        if (symbolWidth.containsKey(symbol)) {
            return symbolWidth.get(symbol);
        }
        Rect rect = new Rect();
        paint.getTextBounds(symbol, 0, symbol.length(), rect);
        symbolWidth.put(symbol, rect.width());
        return rect.width();
    }

    /**
     * 获取排序后的Keys
     * @param map
     */
//    private List<String> getSortKeys(HashMap<String, JsonValueBean> map) {
//        List<String> keys = new ArrayList<>();
//        Set<String> oldKeys = map.keySet();
//        int minKeyLineIndex = 0;
//        for (String key : oldKeys) {
//            int keyLineIndex = map.get(key).getLineIndex();
//
//        }
//    }
}
