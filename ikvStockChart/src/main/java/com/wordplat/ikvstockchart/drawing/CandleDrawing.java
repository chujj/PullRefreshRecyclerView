/*
 * Copyright (C) 2017 WordPlat Open Source Project
 *
 *      https://wordplat.com/InteractiveKLineView/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wordplat.ikvstockchart.drawing;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.wordplat.ikvstockchart.compat.ViewUtils;
import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.entry.SizeColor;
import com.wordplat.ikvstockchart.render.AbstractRender;

import java.text.DecimalFormat;

/**
 * <p>CandleDrawing</p>
 * <p>Date: 2017/3/9</p>
 *
 * @author afon
 */

public class CandleDrawing implements IDrawing {
    private static final String TAG = "CandleDrawing";
    private static final boolean DEBUG = false;

    private Paint candlePaint; // 蜡烛图画笔
    private Paint extremumPaint; // 当前可见区域内的极值画笔
    private final DecimalFormat decimalFormatter = new DecimalFormat("0.00");

    private final RectF kLineRect = new RectF(); // K 线图显示区域
    private AbstractRender render;

    private float candleSpace = 0.1f; // entry 与 entry 之间的间隙，默认 0.1f (10%)
    private float extremumToRight;
    private float[] candleLineBuffer = new float[8]; // 计算 2 根线坐标用的
    private float[] candleRectBuffer = new float[4]; // 计算 1 个矩形坐标用的

    @Override
    public void onInit(RectF contentRect, AbstractRender render) {
        this.render = render;
        final SizeColor sizeColor = render.getSizeColor();

        if (candlePaint == null) {
            candlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            candlePaint.setStyle(Paint.Style.FILL);
            candlePaint.setStrokeWidth(sizeColor.getCandleBorderSize());
        }

        if (extremumPaint == null) {
            extremumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        extremumPaint.setTextSize(sizeColor.getCandleExtremumLabelSize());
        extremumPaint.setColor(sizeColor.getCandleExtremumLableColor());

        kLineRect.set(contentRect);

        extremumToRight = kLineRect.right - 150;

        _temp_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _temp_paint.setPathEffect(new DashPathEffect(new float[] {4,4}, 0));
        _temp_paint.setColor(0xFFEBAD33);
        _temp_paint.setStyle(Paint.Style.STROKE);
        _temp_paint.setStrokeWidth(sizeColor.getGridSize());


        _temp_text_paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        _temp_text_paint.setTextSize(sizeColor.getYLabelSize());
        _temp_text_paint.setColor(0xFFEBAD33);
        _temp_text_paint.getFontMetrics(fontMetrics);


    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {

    }

    private Paint _temp_text_paint = new Paint();
    private final Paint.FontMetrics fontMetrics = new Paint.FontMetrics(); // 用于 labelPaint 计算文字位置
    private Paint _temp_paint= new Paint();
    float[] _temp  = new float[4];
    private String _temp_str;

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        final EntrySet entrySet = render.getEntrySet();
        final SizeColor sizeColor = render.getSizeColor();

        canvas.save();
        canvas.clipRect(kLineRect);

//        if (DEBUG) {
//            Log.i(TAG, "##d onComputeOver: minIndex = " + minIndex + ", maxIndex = " + maxIndex
//                    + ", minYIndex = " + entrySet.getMinYIndex() + ", maxYIndex = " + entrySet.getMaxYIndex());
//        }

        for (int i = minIndex + 4; i < maxIndex; i++) {
            Entry entry = ViewUtils.setUpCandlePaint(candlePaint, entrySet, i, sizeColor);

            boolean drawLine = false;

            if (i == (maxIndex - 1)) {
                _temp_str = Float.toString(entry.getClose());
                drawLine = true;
                _temp[0] = minIndex;
                _temp[1] = entry.getClose();
                _temp[2] = i;
                _temp[3] = entry.getClose();
            }
            // 绘制 影线
            candleLineBuffer[0] = i + 0.5f;
            candleLineBuffer[2] = i + 0.5f;
            candleLineBuffer[4] = i + 0.5f;
            candleLineBuffer[6] = i + 0.5f;
            if (entry.getOpen() > entry.getClose()) {
                candleLineBuffer[1] = entry.getHigh();
                candleLineBuffer[3] = entry.getOpen();
                candleLineBuffer[5] = entry.getClose();
                candleLineBuffer[7] = entry.getLow();
            } else {
                candleLineBuffer[1] = entry.getHigh();
                candleLineBuffer[3] = entry.getClose();
                candleLineBuffer[5] = entry.getOpen();
                candleLineBuffer[7] = entry.getLow();
            }
            render.mapPoints(candleLineBuffer);
            canvas.drawLines(candleLineBuffer, candlePaint);

            // 绘制 当前显示区域的"最小"与"最大"两个值
            if (i == entrySet.getMinYIndex()) {
                if (candleLineBuffer[6] > extremumToRight) {
                    extremumPaint.setTextAlign(Paint.Align.RIGHT);

                    canvas.drawText(decimalFormatter.format(entry.getLow()) + " →",
                            candleLineBuffer[6],
                            candleLineBuffer[7] + 20,
                            extremumPaint);
                } else {
                    extremumPaint.setTextAlign(Paint.Align.LEFT);

                    canvas.drawText("← " + decimalFormatter.format(entry.getLow()),
                            candleLineBuffer[6],
                            candleLineBuffer[7] + 20,
                            extremumPaint);
                }
            }
            if (i == entrySet.getMaxYIndex()) {
                if (candleLineBuffer[0] > extremumToRight) {
                    extremumPaint.setTextAlign(Paint.Align.RIGHT);

                    canvas.drawText(decimalFormatter.format(entry.getHigh()) + " →",
                            candleLineBuffer[0],
                            candleLineBuffer[1] - 5,
                            extremumPaint);
                } else {
                    extremumPaint.setTextAlign(Paint.Align.LEFT);

                    canvas.drawText("← " + decimalFormatter.format(entry.getHigh()),
                            candleLineBuffer[0],
                            candleLineBuffer[1] - 5,
                            extremumPaint);
                }
            }

            // 绘制 蜡烛图的矩形
            candleRectBuffer[0] = i + candleSpace;
            candleRectBuffer[2] = i + 1 - candleSpace;

            if (entry.getOpen() > entry.getClose()) {
                candleRectBuffer[1] = entry.getOpen();
                candleRectBuffer[3] = entry.getClose();
            } else {
                candleRectBuffer[1] = entry.getClose();
                candleRectBuffer[3] = entry.getOpen();
            }
            render.mapPoints(candleRectBuffer);

            if (DEBUG) {
                if (i == minIndex || i == maxIndex - 1) {
                    Log.i(TAG, "##d onComputeOver: i = " + i + ", candleRectBuffer = " + candleRectBuffer[0] + " - " + candleRectBuffer[2]);
                }
            }

            if (Math.abs(candleRectBuffer[1] - candleRectBuffer[3]) < 1.f) { // 涨停、跌停、或不涨不跌的一字板
                canvas.drawRect(candleRectBuffer[0], candleRectBuffer[1], candleRectBuffer[2], candleRectBuffer[3] + 2, candlePaint);
            } else {
                canvas.drawRect(candleRectBuffer[0], candleRectBuffer[1], candleRectBuffer[2], candleRectBuffer[3], candlePaint);
            }

            // 计算高亮坐标
            if (render.isHighlight()) {
                final float[] highlightPoint = render.getHighlightPoint();

                if (candleRectBuffer[0] <= highlightPoint[0] && highlightPoint[0] <= candleRectBuffer[2]) {
                    highlightPoint[0] = candleLineBuffer[0];
//                    highlightPoint[1] = (candleRectBuffer[1] + candleRectBuffer[3]) / 2;
                    entrySet.setHighlightIndex(i);
                }
            }

            if (drawLine) {
                render.mapPoints(_temp);
                _temp[0] = kLineRect.left;
                _temp[2] = kLineRect.right;
                canvas.drawLines(_temp, _temp_paint);

                canvas.drawText(_temp_str, _temp[0], _temp[1] - fontMetrics.bottom, _temp_text_paint);
            }
        }

        if (render.isHighlight()) {
            final float[] highlightPoint = render.getHighlightPoint();
            pointBuffer[0] = highlightPoint[0];
            render.invertMapPoints(pointBuffer);
            final int highlightIndex = pointBuffer[0] < 0 ? 0 : (int) pointBuffer[0];
            try {
                Entry entry = entrySet.getEntryList().get(highlightIndex);
                drawHighLinePoint(canvas, highlightPoint, entry);
            } catch (Exception e ) {

            }

        }

        canvas.restore();
    }


    private Paint mBgStrokePaint;
    private Paint mBgSolidPaint;
    private Rect mBgRect;
    private Paint mTextPaint;
    private void drawHighLinePoint(Canvas canvas, float[] pointBuffer, Entry entry) {
        if (mBgStrokePaint == null) {
            mBgStrokePaint = new Paint();
            mBgStrokePaint.setStyle(Paint.Style.STROKE);
            mBgStrokePaint.setStrokeWidth(5);
            mBgStrokePaint.setColor(0xaaff0000);
        }

        if (mBgSolidPaint == null) {
            mBgSolidPaint = new Paint();
            mBgSolidPaint.setStyle(Paint.Style.FILL);
            mBgSolidPaint.setColor(0xccffffff);
        }

        if (mTextPaint == null) {
            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setTextSize(render.getSizeColor().getYLabelSize());
            mTextPaint.setColor(0xff000000);
        }

        if (mBgRect == null ) {
            mBgRect = new Rect();
        }

        int width = (int) mTextPaint.measureText("开盘价:" + Float.toString(entry.getOpen()) + "    ");
        int singleHeight = (int) render.getSizeColor().getYLabelSize();
        int height = (int) (singleHeight * 7);



        int left = (int) pointBuffer[0];

        if (left > (kLineRect.left + kLineRect.width() / 2)) {
            left = left - width - 50;
        } else {
            left += 50;
        }

        int top = (int ) (kLineRect.top + kLineRect.height() / 2 - height / 2);
        mBgRect.set(left, top,left + width, top + height);

        canvas.drawRect(mBgRect, mBgSolidPaint);
        canvas.drawRect(mBgRect, mBgStrokePaint);


        float gap =  (height -  (render.getSizeColor().getYLabelSize() * 5)) / 6;
        float startY = top + singleHeight + gap;
        String[] strs = new String [] {
                entry.getXLabel(),
                "开盘价:" + entry.getOpen(),
                "最高价:" + entry.getHigh(),
                "最低价:" + entry.getLow(),
                "收盘价:" + entry.getClose(),
        };

        for (int i = 0; i < strs.length; i++) {
            drawCenterText(canvas, strs[i], left, startY, width, mTextPaint);
            startY += gap + singleHeight;
        }

    }


    private void drawCenterText(Canvas canves ,String text, float left, float top, int width, Paint paint) {
        canves.drawText(text, left + ((width - paint.measureText(text)) / 2) , top, paint);
    }

    private float[] pointBuffer = new float[2];

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
