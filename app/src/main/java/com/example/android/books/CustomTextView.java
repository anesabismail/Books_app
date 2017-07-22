package com.example.android.books;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

/**
 * A custom TextView class
 */

public class CustomTextView extends android.support.v7.widget.AppCompatTextView {


    public CustomTextView(Context context) {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-Regular.ttf"));
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-Regular.ttf"));
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-Regular.ttf"));
    }
}
