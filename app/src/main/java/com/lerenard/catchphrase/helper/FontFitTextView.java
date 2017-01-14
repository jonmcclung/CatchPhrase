package com.lerenard.catchphrase.helper;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lerenard.catchphrase.R;

public class FontFitTextView extends TextView {
    protected final FontFit fontFit;
    private final String TAG = "FontFitTextView";

    public FontFitTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.FontFit,
                0, 0);
        float minTextSize, maxTextSize;
        try {
            minTextSize = typedArray.getDimension(
                    R.styleable.FontFit_minTextSize,
                    FontFit.defaultMinTextSize);
            maxTextSize = typedArray.getDimension(
                    R.styleable.FontFit_maxTextSize,
                    FontFit.defaultMaxTextSize);
        } finally {
            typedArray.recycle();
        }
        fontFit = new FontFit(this, minTextSize, maxTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        fontFit.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(fontFit.getWidthMeasureSpec(), fontFit.getHeightMeasureSpec());
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (fontFit == null) {
            super.onTextChanged(text, start, lengthBefore, lengthAfter);
        }
        else {
            fontFit.onTextChanged(text);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        fontFit.onSizeChanged(w, h, oldw, oldh);
    }
}