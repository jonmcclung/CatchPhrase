package com.lerenard.catchphrase.helper;

import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

/**
 * originally taken from http://stackoverflow.com/a/7875656/4714742,
 * modified to serve as a field in FontFitEditText and FontFitTextView so that it
 * is more easily inheritable.
 */

class FontFit {

    static final float
            defaultMinTextSize = 12,
            defaultMaxTextSize = 64;
    private static final String TAG = "FontFit";
    private final TextView textView;
    private float
            minTextSize,
            maxTextSize;
    private int
            widthMeasureSpec,
            heightMeasureSpec;
    private Paint testPaint;

    FontFit(TextView textView, float minTextSize, float maxTextSize) {
        this.minTextSize = minTextSize;
        this.maxTextSize = maxTextSize;
        this.textView = textView;
        testPaint = new Paint();
        testPaint.set(textView.getPaint());
    }

    int getWidthMeasureSpec() {
        return widthMeasureSpec;
    }

    int getHeightMeasureSpec() {
        return heightMeasureSpec;
    }

    void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = textView.getMeasuredHeight();
        refitText(textView.getText().toString(), parentWidth);
        this.widthMeasureSpec = parentWidth;
        this.heightMeasureSpec = height;
    }

    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText(String text, int textWidth) {
        if (textWidth <= 0) {
            return;
        }
        if (StringUtils.indexOf(text, '\n', textView.getMaxLines()) != -1) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, minTextSize);
            return;
        }
        int targetWidth = textWidth - textView.getPaddingLeft() - textView.getPaddingRight();
        float hi = maxTextSize;
        float lo = minTextSize;
        final float threshold = 0.5f; // How close we have to be

        testPaint.set(textView.getPaint());

        while ((hi - lo) > threshold) {
            float size = (hi + lo) / 2;
            testPaint.setTextSize(size);
            if (testPaint.measureText(text) >= targetWidth) {
                hi = size; // too big
            }
            else {
                lo = size; // too small
            }
        }
        // Use lo so that we undershoot rather than overshoot
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }

    void onTextChanged(final CharSequence text) {
        refitText(text.toString(), textView.getWidth());
    }

    void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) {
            refitText(textView.getText().toString(), w);
        }
    }
}