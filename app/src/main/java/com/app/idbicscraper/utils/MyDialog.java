package com.app.idbicscraper.utils;

import android.content.Context;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class MyDialog {

    public interface DialogClickListener {
        void onPositiveButtonClick();

        void onNegativeButtonClick();
    }

    public static void showDialog(Context context, String title, String message, final DialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onPositiveButtonClick();
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onNegativeButtonClick();
                        }
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
