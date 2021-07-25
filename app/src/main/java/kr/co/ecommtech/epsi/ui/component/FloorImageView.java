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
public class FloorImageView extends ImageView {
    private static String TAG = "FloorImageView";

    String mLeftText;
    String mRightText;
    String mCenterText;

    public FloorImageView(Context context) {
        super(context);
    }

    public FloorImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FloorImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setText(String left, String center, String right) {
        mLeftText = left;
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

        if (!TextUtils.isEmpty(mLeftText)) {
            Paint leftTextPaint = new Paint();
            leftTextPaint.setColor(Color.RED);
            leftTextPaint.setTextSize(textSize);

            float posX = (float)Math.floor(canvas.getWidth() / 2.75) - ((textSize / 2) * mLeftText.length()) / 2;
            float posY = (float)Math.floor(canvas.getHeight() / 4.28);

            canvas.drawText(mLeftText, posX, posY, leftTextPaint);
        }

        if (!TextUtils.isEmpty(mRightText)) {
            Paint rightTextPaint = new Paint();
            rightTextPaint.setColor(Color.RED);
            rightTextPaint.setTextSize(textSize);

            float posX = (float)(float)Math.floor(canvas.getWidth() / 1.62) - ((textSize / 2) * mRightText.length()) / 2;
            float posY = (float)Math.floor(canvas.getHeight() / 4.28);

            canvas.drawText(mRightText, posX, posY, rightTextPaint);
        }

        if (!TextUtils.isEmpty(mCenterText)) {
            Paint centerTextPaint = new Paint();
            centerTextPaint.setColor(Color.RED);
            centerTextPaint.setTextSize(textSize);

            float posX = (float)Math.floor((canvas.getWidth() / 2.05) - ((textSize / 2) * mCenterText.length()) / 2);
            float posY = (float)Math.floor(canvas.getHeight() / 2.5);

            canvas.drawText(mCenterText, posX, posY, centerTextPaint);
        }
    }
}
