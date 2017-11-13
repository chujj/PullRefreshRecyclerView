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
import android.graphics.Path;
import android.graphics.RectF;

import com.wordplat.ikvstockchart.entry.Entry;
import com.wordplat.ikvstockchart.entry.EntrySet;
import com.wordplat.ikvstockchart.entry.SizeColor;
import com.wordplat.ikvstockchart.render.AbstractRender;

/**
 * <p>TimeLineDrawing</p>
 * <p>Date: 2017/3/9</p>
 *
 * @author afon
 */

public class TimeLineDrawing implements IDrawing {


    public static interface PriceMarkerProvider {
        public boolean needDraw();

        public float[] getLine1Y();
        public float[] getLine2Y();

        public String getLine1Promt();
    }

    private Paint linePaint;
    private Paint shadowPaint;

    private final RectF chartRect = new RectF(); // 分时图显示区域
    private AbstractRender render;
    private final Path path = new Path();

    private float[] lineBuffer = new float[4];
    private float[] pointBuffer = new float[2];


    private Paint mMarkLine1Paint;
    private Paint mMarkLine2Paint;
    private Paint mMarkLine1TextPaint;
    private final Paint.FontMetrics fontMetrics = new Paint.FontMetrics(); // 用于 labelPaint 计算文字位置

    public PriceMarkerProvider mPriceMarkerProvider;

    private Paint mPointInsidePaint, mPointOutsidePaint, mPointBlinkPaint;
    private RectF mPointRectF = new RectF();

    public Runnable drawAfterCb = null;

    @Override
    public void onInit(RectF contentRect, AbstractRender render) {
        this.render = render;
        final SizeColor sizeColor = render.getSizeColor();

        if (linePaint == null) {
            linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            linePaint.setStyle(Paint.Style.FILL);
        }
        linePaint.setStrokeWidth(sizeColor.getTimeLineSize());
        linePaint.setColor(sizeColor.getTimeLineColor());


        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(0x11ffffff);


        mMarkLine1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkLine1Paint.setPathEffect(new DashPathEffect(new float[] {4,4}, 0));
        mMarkLine1Paint.setColor(0xff35a64b);
        mMarkLine1Paint.setStyle(Paint.Style.STROKE);
        mMarkLine1Paint.setStrokeWidth(sizeColor.getGridSize());

        mMarkLine2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkLine2Paint.setPathEffect(new DashPathEffect(new float[] {4,4}, 0));
        mMarkLine2Paint.setColor(0xff4187c2);
        mMarkLine2Paint.setStyle(Paint.Style.STROKE);
        mMarkLine2Paint.setStrokeWidth(sizeColor.getGridSize());

        mMarkLine1TextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mMarkLine1TextPaint.setTextSize(sizeColor.getYLabelSize());
        mMarkLine1TextPaint.setColor(0xff35a64b);
        mMarkLine1TextPaint.getFontMetrics(fontMetrics);

        mPointInsidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointInsidePaint.setStyle(Paint.Style.FILL);
        mPointInsidePaint.setColor(0xffa4a4a4);

        mPointOutsidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointOutsidePaint.setStyle(Paint.Style.FILL);
        mPointOutsidePaint.setColor(0xffd7d7d7);


        mPointBlinkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointBlinkPaint.setStrokeWidth(10);
        mPointBlinkPaint.setStyle(Paint.Style.STROKE);
        mPointBlinkPaint.setColor(0x88d7d7d7);


        chartRect.set(contentRect);
    }

    @Override
    public void computePoint(int minIndex, int maxIndex, int currentIndex) {
        final int count = (maxIndex - minIndex) * 4;

        if (lineBuffer.length < count) {
            lineBuffer = new float[count];
        }

        final EntrySet entrySet = render.getEntrySet();
        final Entry entry = entrySet.getEntryList().get(currentIndex);
        final int i = currentIndex - minIndex;

        if (currentIndex < maxIndex) {
            lineBuffer[i * 4 + 0] = currentIndex;
            lineBuffer[i * 4 + 1] = entry.getClose();
            lineBuffer[i * 4 + 2] = currentIndex + 1;
            lineBuffer[i * 4 + 3] = entrySet.getEntryList().get(currentIndex + 1).getClose();
        }
    }

    @Override
    public void onComputeOver(Canvas canvas, int minIndex, int maxIndex, float minY, float maxY) {
        canvas.save();
        canvas.clipRect(chartRect);

        render.mapPoints(lineBuffer);

        final int count = (maxIndex - minIndex) * 4;

        if (count > 0) {
            canvas.drawLines(lineBuffer, 0, count, linePaint);

            // draw shadow
            path.reset();
            path.moveTo(chartRect.left, chartRect.bottom);
            for (int i = 0; (i +1) < lineBuffer.length ; i+=2) {
                path.lineTo(lineBuffer[i], lineBuffer[i+1]);
            }
            path.lineTo(lineBuffer[lineBuffer.length - 2], chartRect.bottom);
            path.close();
            canvas.drawPath(path, shadowPaint);

            // draw line
            if (mPriceMarkerProvider != null && mPriceMarkerProvider.needDraw()) {

                float[] line1 = mPriceMarkerProvider.getLine1Y();
                render.mapPoints(line1);

                path.reset();
                path.moveTo(chartRect.left, line1[1]);
                path.lineTo(chartRect.right, line1[1]);
                canvas.drawPath(path, mMarkLine1Paint);

                canvas.drawText(mPriceMarkerProvider.getLine1Promt(),
                        lineBuffer[lineBuffer.length -2] - mMarkLine1TextPaint.measureText(mPriceMarkerProvider.getLine1Promt()) - render.getSizeColor().markLine1MarginRight,
                        line1[1] - fontMetrics.bottom,
                        mMarkLine1TextPaint);

                path.reset();
                path.moveTo(lineBuffer[lineBuffer.length -2], lineBuffer[lineBuffer.length -1]);
                path.lineTo(chartRect.left, lineBuffer[lineBuffer.length -1]);
                canvas.drawPath(path, mMarkLine2Paint);
            }

            // draw point
            float x = lineBuffer[lineBuffer.length -2];
            float y = lineBuffer[lineBuffer.length -1];

            float radius = render.getSizeColor().outsizePointRadius;
//            mPointRectF.set(x - radius, y+radius, x+radius, y-radius);
//            canvas.drawOval(mPointRectF, mPointOutsidePaint);


            radius = render.getSizeColor().insidePointRadius;
            mPointRectF.set(x - radius, y+radius, x+radius, y-radius);
            canvas.drawOval(mPointRectF, mPointInsidePaint);


            radius = render.getSizeColor().insidePointRadius;

            long current = System.currentTimeMillis();
            long step = (current / 45) % render.getSizeColor().outsizePointRadius;
//            System.out.println("zjj step: " + step);
            radius += step;
            mPointRectF.set(x - radius, y + radius, x + radius, y - radius);
            canvas.drawOval(mPointRectF, mPointBlinkPaint);

            if (drawAfterCb != null) {
                drawAfterCb.run();
            }

        }

        // 计算高亮坐标
        if (render.isHighlight()) {
            final EntrySet entrySet = render.getEntrySet();
            final int lastEntryIndex = entrySet.getEntryList().size() - 2;
            final float[] highlightPoint = render.getHighlightPoint();
            pointBuffer[0] = highlightPoint[0];
            render.invertMapPoints(pointBuffer);
            final int highlightIndex = pointBuffer[0] < 0 ? 0 : (int) pointBuffer[0];
            final int i = highlightIndex - minIndex;
            highlightPoint[0] = highlightIndex < lastEntryIndex ?
                    lineBuffer[i * 4 + 0] : lineBuffer[lastEntryIndex * 4 + 2];
            highlightPoint[1] = highlightIndex < lastEntryIndex ?
                    lineBuffer[i * 4 + 1] : lineBuffer[lastEntryIndex * 4 + 3];
        }

        canvas.restore();
    }

    @Override
    public void onDrawOver(Canvas canvas) {

    }
}
