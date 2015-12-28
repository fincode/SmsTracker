package com.fincode.smstracker.ui;


import android.widget.CheckBox;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.fincode.smstracker.R;
import com.fincode.smstracker.model.entities.Message;
import com.fincode.smstracker.preferences.Preferences;

public class DialogFactory {


    public static MaterialDialog newInstanceDeleteAllDialog(final MainActivity activity) {
        return new MaterialDialog.Builder(activity)
                .content(R.string.delete_all_data)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .positiveColorRes(R.color.primary)
                .negativeColorRes(R.color.primary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        activity.onDeleteData();
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                    }
                }).build();
    }

    public static MaterialDialog newInstanceMessageDialog(final MainActivity activity, Message message) {
        return new MaterialDialog.Builder(activity)
                .title(message.getFormatedDate())
                .content(message.getText())
                .backgroundColorRes(R.color.primary)
                .contentColorRes(R.color.white)
                .titleColorRes(R.color.white)
                .build();
    }

    public static MaterialDialog newInstanceSettingsDialog(MainActivity activity) {
        return new MaterialDialog.Builder(activity)
                .title(R.string.settings_title)
                .customView(R.layout.dialog_settings, true)
                .positiveText(R.string.save)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.primary)
                .negativeColorRes(R.color.primary)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        EditText etFrom = (EditText) dialog
                                .getCustomView().findViewById(R.id.et_settings_from);
                        EditText etServerUrl = (EditText) dialog
                                .getCustomView().findViewById(R.id.et_settings_server_url);
                        EditText etPhoneNumber = (EditText) dialog
                                .getCustomView().findViewById(R.id.et_settings_phone);
                        CheckBox cbEnableSend = (CheckBox) dialog
                                .getCustomView().findViewById(R.id.cb_settings_send_enable);
                        CheckBox cbAbortSms = (CheckBox) dialog
                                .getCustomView().findViewById(R.id.cb_settings_sms_abort);

                        activity.onSettingsChanged(new Preferences()
                                .setPhoneNumber(etPhoneNumber.getText().toString())
                                .setServerUrl(etServerUrl.getText().toString())
                                .setSendEnabled(cbEnableSend.isChecked())
                                .setAbortSms(cbAbortSms.isChecked())
                                .setFrom(etFrom.getText().toString())
                        );

                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                    }
                }).build();
    }

    public interface DialogListener {
        void onDeleteData();

        void onSettingsChanged(Preferences preferences);
    }

}
