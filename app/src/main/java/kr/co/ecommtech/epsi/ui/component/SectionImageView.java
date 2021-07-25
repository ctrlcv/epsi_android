package kr.co.ecommtech.epsi.ui.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class SectionImageView extends ImageView {
    String mRightText;
    String mCenterText;

    public SectionImageView(Context context) {
        super(context);
    }

    public SectionImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SectionImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(String center, String right) {
        mRightText = right;
        mCenterText = center;
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int textSize;
        if (canvas.getWidth() > 1000) {
            textSize = 40;
        } else {
            textSize = 36;
        }

        if (!TextUtils.isEmpty(mRightText)) {
            Paint rightTextPaint = new Paint();
            rightTextPaint.setColor(Color.RED);
            rightTextPaint.setTextSize(textSize);

            float posX = (float)(float)Math.floor(canvas.getWidth() / 1.46) - ((textSize / 2) * mRightText.length()) / 2;
            float posY = (float)Math.floor(canvas.getHeight() / 2.20);

            canvas.drawText(mRightText, posX, posY, rightTextPaint);
        }

        if (!TextUtils.isEmpty(mCenterText)) {
            Paint centerTextPaint = new Paint();
            centerTextPaint.setColor(Color.RED);
            centerTextPaint.setTextSize(textSize);

            float posX = (float)Math.floor((canvas.getWidth() / 2.05) - ((textSize / 2) * mCenterText.length()) / 2);
            float posY = (float)Math.floor(canvas.getHeight() / 1.45);

            canvas.drawText(mCenterText, posX, posY, centerTextPaint);
        }
    }
}
