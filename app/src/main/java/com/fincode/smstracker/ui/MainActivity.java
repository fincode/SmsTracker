package com.fincode.smstracker.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.fincode.smstracker.app.App;
import com.fincode.smstracker.R;
import com.fincode.smstracker.Utils;
import com.fincode.smstracker.event.ActivityRefreshEvent;
import com.fincode.smstracker.event.BusProvider;
import com.fincode.smstracker.model.ContentManager;
import com.fincode.smstracker.model.entities.Message;
import com.fincode.smstracker.preferences.Preferences;
import com.fincode.smstracker.preferences.PreferencesHelper;
import com.fincode.smstracker.service.MessageSendService;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.*;

public class MainActivity extends AppCompatActivity implements DialogFactory.DialogListener {

    private static final int IDM_DELETE_MESSAGE_DB = 0;

    private TextView mTxtSentCnt;
    private TextView mTxtUnsentCnt;
    private ListView mLvHistory;
    private LinearLayout mLlLoading;

    private List<Message> mMessages;
    private MessageHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.brandGlowEffect(this, getResources().getColor(R.color.primary));
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primaryDark));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.app_name));

        mTxtSentCnt = (TextView) findViewById(R.id.txt_messages_sent);
        mTxtUnsentCnt = (TextView) findViewById(R.id.txt_messages_unsent);
        mLlLoading = (LinearLayout) findViewById(R.id.ll_message_loading);

        mMessages = new ArrayList<>();
        mAdapter = new MessageHistoryAdapter(this, mMessages);
        mLvHistory = (ListView) findViewById(R.id.lv_history);
        mLvHistory.setOnItemClickListener((adapterView, view, position, l) ->
                DialogFactory.newInstanceMessageDialog(
                        MainActivity.this,
                        mMessages.get(position)
                ).show());
        mLvHistory.setAdapter(mAdapter);
        registerForContextMenu(mLvHistory);

        if (PreferencesHelper.isFirstLaunch(this)) {
            showSettingsDialog();
            PreferencesHelper.setFirstLaunch(this, false);
        }

        scanPhoneSms();
    }

    // Выбор пункта контекстного меню
    public boolean onContextItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item
                .getMenuInfo();
        int pos = acmi.position;
        Message message = mMessages.get(pos);
        switch (itemId) {
            case IDM_DELETE_MESSAGE_DB:
                ContentManager.deleteMessageFromDb(message);
                refreshHistoryList();
                return true;

            default:
                break;
        }

        return false;
    }

    /*private BroadcastReceiver mRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshHistoryList();
        }
    };

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
        super.onPause();
    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver,
                new IntentFilter(MessageSendService.EVENT_REFRESH_GUI));
        refreshHistoryList();

    }*/

    // Создание контекстного меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(Menu.NONE, IDM_DELETE_MESSAGE_DB, Menu.NONE,
                getString(R.string.delete_from_db));
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
        refreshHistoryList();
        MessageSendService service = MessageSendService.getInstance();
        mLlLoading.setVisibility(service != null && service.isSending() ? View.VISIBLE : View.GONE);
    }


    @Subscribe
    public void onActivityRefreshEvent(ActivityRefreshEvent event) {
        if (event.isSentFinished()) {
            refreshHistoryList();
            makeText(this, getString(event.isSuccessSent() ? R.string.sent_success : R.string.sent_fail), LENGTH_SHORT).show();
        }
        mLlLoading.setVisibility(event.isSentFinished() ? View.GONE : View.VISIBLE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    private void refreshHistoryList() {
        List<Message> messages = ContentManager.getMessages();
        int sentCnt = ContentManager.getMessages(true).size();
        int unsentCnt = ContentManager.getMessages(false).size();
        mMessages.clear();
        mMessages.addAll(messages);
        mAdapter.notifyDataSetChanged();

        mTxtSentCnt.setText(String.format("%s: %d ", getString(R.string.sent), sentCnt));
        mTxtUnsentCnt.setText(String.format("%s: %d ", getString(R.string.unsent), unsentCnt));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_send:
                MessageSendService.startSendService(null);
                break;

            case R.id.action_settings:
                showSettingsDialog();
                break;

            case R.id.action_delete:
                MaterialDialog deleteDialog = DialogFactory.newInstanceDeleteAllDialog(this);
                deleteDialog.show();
                break;

            case R.id.action_scan_sms:
                scanPhoneSms();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void scanPhoneSms() {
        List<Message> messagesFromPhone = ContentManager.loadMessageFromPhone(this);
        ContentManager.createOrUpdateMessages(messagesFromPhone, true);
        refreshHistoryList();
    }


    private void showSettingsDialog() {
        MaterialDialog settingsDialog = DialogFactory.newInstanceSettingsDialog(this);

        final View btnDialogPositive = settingsDialog
                .getActionButton(DialogAction.POSITIVE);
        View customView = settingsDialog.getCustomView();
        if (customView == null) {
            return;
        }
        EditText etServerUrl = (EditText) customView.findViewById(R.id.et_settings_server_url);
        EditText etPhoneNumber = (EditText) customView.findViewById(R.id.et_settings_phone);
        EditText etFrom = (EditText) customView.findViewById(R.id.et_settings_from);
        CheckBox cbEnableSend = (CheckBox) customView.findViewById(R.id.cb_settings_send_enable);
        CheckBox cbAbortSms = (CheckBox) customView.findViewById(R.id.cb_settings_sms_abort);

        Preferences preferences = App.inst().getPreferences();
        etFrom.setText(preferences.getFrom());
        etServerUrl.setText(preferences.getServerUrl());
        etPhoneNumber.setText(preferences.getPhoneNumber());
        cbEnableSend.setChecked(preferences.isSendEnabled());
        cbAbortSms.setChecked(preferences.isAbortSms());

        btnDialogPositive.setEnabled(!etServerUrl.toString().trim().isEmpty());
        etServerUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnDialogPositive.setEnabled(!s.toString().trim().isEmpty());
            }
        });


        settingsDialog.show();
    }

    @Override
    public void onDeleteData() {
        boolean success = ContentManager.clearData();
        //PreferencesHelper.clear(this); // Удаление настроек
        App.inst().refreshAppSettings();
        makeText(
                this,
                getString(success ? R.string.delete_success : R.string.delete_err),
                LENGTH_SHORT
        ).show();
        refreshHistoryList();
    }

    @Override
    public void onSettingsChanged(Preferences preferences) {
        PreferencesHelper.savePreferences(this, preferences);
        App.inst().refreshAppSettings();
    }

    private static class MessageHistoryAdapter extends ArrayAdapter<Message> {

        public Activity mContext;

        public MessageHistoryAdapter(Activity context, List<Message> messages) {
            super(context, R.layout.row_message_history, messages);
            mContext = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.row_message_history, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }

            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.cvLayout.setCardBackgroundColor(getContext().getResources().getColor(R.color.primary));
            Message message = getItem(position);
            if (message != null) {
                holder.txtText.setText(message.getText());
                holder.txtDate.setText(message.getFormatedDate());
                holder.imgStatus.setImageResource(
                        message.isSent() ? R.drawable.ic_success : R.drawable.ic_bad);
            }
            return convertView;
        }

        private static class ViewHolder {
            private final TextView txtText;
            private final TextView txtDate;
            private final ImageView imgStatus;
            private final CardView cvLayout;

            public ViewHolder(View view) {
                txtText = ((TextView) view.findViewById(R.id.txt_message_text));
                txtDate = ((TextView) view.findViewById(R.id.txt_message_date));
                imgStatus = ((ImageView) view.findViewById(R.id.img_message_status));
                cvLayout = ((CardView) view.findViewById(R.id.cv_message_content));
            }
        }

    }
}
