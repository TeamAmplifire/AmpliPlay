package com.project.amplifire.Utilities;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class AlbumArtView extends AppCompatImageView
{
    public AlbumArtView(Context context) {
        super(context);
    }

    public AlbumArtView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumArtView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        setMeasuredDimension(width, width);

    }
}
