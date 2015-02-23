package com.saintsrobotics.saintspush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.arellomobile.android.push.BasePushMessageReceiver;
import com.arellomobile.android.push.PushManager;
import com.arellomobile.android.push.utils.RegisterBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    BroadcastReceiver registrationReceiver = new RegisterBroadcastReceiver() {
        @Override
        public void onRegisterActionReceive(Context context, Intent intent) {
            checkMessage(intent);
        }
    };

    private BroadcastReceiver pushMessageReceiver = new BasePushMessageReceiver() {
        @Override
        protected void onMessageReceive(Intent intent) {
            showMessage(intent.getExtras().getString(JSON_DATA_KEY));
        }
    };

    MessageAdapter messageAdapter;

    //Registration of the receivers
    public void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".action.PUSH_MESSAGE_RECEIVE");
        registerReceiver(pushMessageReceiver, intentFilter, getPackageName() + ".permission.C2D_MESSAGE", null);
        registerReceiver(registrationReceiver, new IntentFilter(getPackageName() + "." + PushManager.REGISTER_BROAD_CAST_ACTION));
    }

    public void unregisterReceivers() {
        unregisterReceiver(pushMessageReceiver);
        unregisterReceiver(registrationReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageAdapter = new MessageAdapter(this);
        ListView messages = (ListView) this.findViewById(R.id.messages);
        messages.setAdapter(messageAdapter);

        registerReceivers();
        PushManager pushManager = PushManager.getInstance(this);
        try {
            pushManager.onStartup(this);
        } catch (Exception e) {
            Log.e("Saints Push", "Unable to start");
        }

        pushManager.registerForPushNotifications();
        checkMessage(getIntent());
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceivers();
    }

    private void checkMessage(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
                showMessage(intent.getExtras().getString(PushManager.PUSH_RECEIVE_EVENT));
            } else if (intent.hasExtra(PushManager.REGISTER_EVENT)) {
                Log.d("Saints Push", "register");
            } else if (intent.hasExtra(PushManager.UNREGISTER_EVENT)) {
                Log.d("Saints Push", "unregister");
            } else if (intent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
                Log.d("Saints Push", "register error");
            } else if (intent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
                Log.d("Saints Push", "unregister error");
            }

            resetIntentValues();
        }
    }

    /**
     * Will check main Activity intent and if it contains any PushWoosh data, will clear it
     */
    private void resetIntentValues() {
        Intent mainAppIntent = getIntent();

        if (mainAppIntent.hasExtra(PushManager.PUSH_RECEIVE_EVENT)) {
            mainAppIntent.removeExtra(PushManager.PUSH_RECEIVE_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.REGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.REGISTER_ERROR_EVENT);
        } else if (mainAppIntent.hasExtra(PushManager.UNREGISTER_ERROR_EVENT)) {
            mainAppIntent.removeExtra(PushManager.UNREGISTER_ERROR_EVENT);
        }

        setIntent(mainAppIntent);
    }

    private void showMessage(String message) {
        String text = "APPLICATION ERROR";
        try {
            JSONObject jsonMessage = new JSONObject(message);
            text = jsonMessage.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageAdapter.add(text);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        checkMessage(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
