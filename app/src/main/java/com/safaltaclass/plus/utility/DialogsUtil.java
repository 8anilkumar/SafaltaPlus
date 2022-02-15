package com.safaltaclass.plus.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;

import com.safaltaclass.plus.Interface.OnDialogButtonClickListener;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.shashank.sony.fancydialoglib.FancyAlertDialogListener;
import com.shashank.sony.fancydialoglib.Icon;

public class DialogsUtil {

    public static void openAlertDialog(Activity activity, String title, String message, String positiveBtnText, String negativeBtnText,
                                       int drawable,boolean isError,final OnDialogButtonClickListener listener) {
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            new FancyAlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setBackgroundColor(Color.parseColor("#B11016"))
                    .setNegativeBtnText(negativeBtnText)
                    .setPositiveBtnText(positiveBtnText)
                    .setPositiveBtnBackground(Color.parseColor("#B11016"))
                    .setAnimation(Animation.POP)
                    .isCancellable(false)
                    .setIcon(drawable, Icon.Visible)
                    .OnPositiveClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            if (isError)
                                listener.onErrorButtonClicked();
                            else
                                listener.onPositiveButtonClicked();
                        }
                    })
                    .OnNegativeClicked(new FancyAlertDialogListener() {
                        @Override
                        public void OnClick() {
                            if (isError)
                                listener.onErrorButtonClicked();
                            else
                                listener.onNegativeButtonClicked();
                        }
                    })
                    .build();
        }
    }
}

