package kr.co.ecommtech.epsi.ui.dialog;

import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomListDialog extends Dialog {
    public CustomListDialog(@NonNull Context context) {
        super(context);
    }

    public CustomListDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomListDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }



}
