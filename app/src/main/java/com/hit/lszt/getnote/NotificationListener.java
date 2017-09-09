package com.hit.lszt.getnote;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by lszt on 2017/8/30.
 */

public class NotificationListener extends NotificationListenerService {
    final static String TAG = "NotificationListener";
    final static String TAGbd = "BDTTS";
    private static Context mcontext;
    /* *
    * 是否发送通知
    * */
    private static boolean isPost = true;
    private CommandReceiver mCommandReceiver=new CommandReceiver();

    private static void SetPostStatus(boolean status){
        isPost=status;
        Toast.makeText(mcontext,String.valueOf(status),Toast.LENGTH_LONG).show();
//        Log.i(TAG, "Receive COMMAND:" + String.valueOf(status));
    }

    private void toggleNotificationListenerService() {
        Log.i(TAG, "toggleNotificationListenerService");
        PackageManager pm = getPackageManager();
        pm.setComponentEnabledSetting(new ComponentName(this, NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(this, NotificationListener.class),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        mcontext=getApplicationContext();
        SetPostStatus(true);
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.lszt.getnote.command");
        registerReceiver(mCommandReceiver,filter);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.i(TAG, "onListenerConnected");
    }

    @Override
    public void onListenerDisconnected() {
        Log.i(TAG, "onListenerDisconnected");
        super.onListenerDisconnected();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
//        toggleNotificationListenerService();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");
        // initialEnv();
        //initialTts();
        return super.onBind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        isPost=false;
        unregisterReceiver(mCommandReceiver);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.i(TAG, "onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        Bundle bundle = sbn.getNotification().extras;
        String notificationPkg = sbn.getPackageName();
        String title=bundle.getString(Notification.EXTRA_TITLE);
        String text= bundle.getString(Notification.EXTRA_TEXT);
        Log.i(TAG, title + text);
        if(isPost){
            Intent i= new Intent("com.example.lszt.getnote.receive");
            i.putExtra("POSTED",true);
            i.putExtra("PACKAGENAME",notificationPkg);
            i.putExtra("TITLE",title);
            i.putExtra("TEXT",text);
            sendBroadcast(i);
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);
        Bundle bundle = sbn.getNotification().extras;
        String notificationPkg = sbn.getPackageName();
        String title=bundle.getString(Notification.EXTRA_TITLE);
        String text= bundle.getString(Notification.EXTRA_TEXT);
        Log.i(TAG, title);
        if(isPost){
            Intent i= new Intent("com.example.lszt.getnote.receive");
            i.putExtra("POSTED",false);
            i.putExtra("PACKAGENAME",notificationPkg);
            i.putExtra("TITLE",title);
            i.putExtra("TEXT",text);
            sendBroadcast(i);
        }
    }

    private  void test(){
        Log.i(TAG,"Testssssss");
    }

    private class CommandReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG,"onReceive");
            String command=intent.getStringExtra("COMMAND");
            test();
            if (command.equals("stop")){
                NotificationListener.SetPostStatus(false);
            }
            else if(command.equals("restart")){
                NotificationListener.SetPostStatus(true);
               // requestRebind(new ComponentName(getBaseContext(),NotificationListener.class));
            }
        }
    }
}
