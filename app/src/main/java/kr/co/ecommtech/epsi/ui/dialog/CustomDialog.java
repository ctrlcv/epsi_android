package kr.co.ecommtech.epsi.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import kr.co.ecommtech.epsi.ui.utils.Utils;


public class CustomDialog extends Dialog {

    private Context context;
    private CustomDialogListener listener;

    public interface CustomDialogListener {
        void onCreate(Dialog dialog);
    }

    public CustomDialog(@NonNull Context context, @NonNull CustomDialogListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (listener != null) {
            listener.onCreate(this);
        }

        /**
         * 액티비티 또는 다이얼로그의 contentView 받아오기
         * https://hashcode.co.kr/questions/994/%EC%95%A1%ED%8B%B0%EB%B9%84%ED%8B%B0%EC%9D%98-contentview%EB%8A%94-%EC%96%B4%EB%96%BB%EA%B2%8C-%EB%B0%9B%EC%95%84%EC%98%A4%EB%82%98%EC%9A%94
         */
        ViewGroup contentView = (ViewGroup)findViewById(android.R.id.content);
        if (contentView != null) {
            View view = contentView.getChildAt(0);
            if (view != null) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.width = Utils.getDialogWidth(context);
                view.setLayoutParams(params);
            }
        }
        setCancelable(false);
    }
}
