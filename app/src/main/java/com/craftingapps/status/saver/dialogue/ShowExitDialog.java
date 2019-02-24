package com.craftingapps.status.saver.dialogue;

import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.craftingapps.status.saver.R;
import com.craftingapps.status.saver.databinding.ExitDailogAdBinding;
import com.craftingapps.status.saver.listeners.CloseAppListener;
import com.craftingapps.status.saver.utils.Utils;

public class ShowExitDialog implements View.OnClickListener {

    private Dialog dialog;
    private Context context;
    private ExitDailogAdBinding exitDialogAdBinding;
    //private Ads exitDialogAdBinding;
    boolean isExist = true;
    CloseAppListener closeAppListener;

    public ShowExitDialog(Context context, boolean isExist) {
        this.context = context;
        this.isExist = isExist;
        closeAppListener = (CloseAppListener) context;
        createDialog();
    }

    private void createDialog() {
        LayoutInflater myInflator = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        exitDialogAdBinding = DataBindingUtil.inflate(myInflator, R.layout.exit_dailog_ad, null, false);


        if (Build.VERSION.SDK_INT >= 21) {
            dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        } else {
            dialog = new Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        }

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(exitDialogAdBinding.getRoot());


        if (isExist) {
            exitDialogAdBinding.exitMain.setVisibility(View.VISIBLE);
//            AdsManager.getInstance().showAdMobLargeBanner(exitDialogAdBinding.adView);
            exitDialogAdBinding.dialogRateUs.setOnClickListener(this);
            exitDialogAdBinding.dialogNo.setOnClickListener(this);
            exitDialogAdBinding.dialogYes.setOnClickListener(this);
        } else {
//            exitDialogAdBinding.nativeAd.setVisibility(View.VISIBLE);
        }
    }

    public void showDialog() {

        dialog.show();
    }

    public void hideDialog() {
        dialog.hide();
    }

    public void setCancelable(boolean isCancelable) {
        dialog.setCancelable(isCancelable);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dialog_rate_us:
                dialog.dismiss();
                context.startActivity(Utils.getLikeUsIntent());

                break;
            case R.id.dialog_no:
                dialog.dismiss();
                break;
            case R.id.dialog_yes:

                if (closeAppListener != null) {
                    dialog.dismiss();
                    dialog.cancel();
                    closeAppListener.closeApp();
                } else
                    dialog.dismiss();
                break;
        }
    }
}