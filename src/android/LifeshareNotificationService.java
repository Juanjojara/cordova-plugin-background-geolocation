import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.util.Log;

public class LifeshareNotificationService extends Service {
	/*
	@Override
	public void onCreate() {
	    super.onCreate();

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    Log.i(TAG, "Received start id " + startId + ": " + intent);

	    return START_NOT_STICKY;
	 }


	@Override
	public IBinder onBind(Intent intent) {
	    // TODO Auto-generated method stub
	    return null;
	}
*/
	public static String ACTION_SWITCH_NOTIFICATIONS =     "com.jakewharton.notificationcompat2.SWITCH_NOTIFICATIONS";
    public static String SWITCH_NOTIFICATION_ARG_ID =           "NOTIF_ID";
    public static String SWITCH_NOTIFICATION_ARG_NOTIFICATION = "NOTIF_NOTIFICATION";
 
    public NotificationReplaceService() {
        super("NotificationReplaceService");
    }
 
    @Override
    protected void onHandleIntent(Intent intent) {
        if (ACTION_SWITCH_NOTIFICATIONS.equals(intent.getAction())) {
 
            int notifiId = intent.getIntExtra(SWITCH_NOTIFICATION_ARG_ID, -1);
            Notification notification = intent.getParcelableExtra(SWITCH_NOTIFICATION_ARG_NOTIFICATION);
 
            // Creating the new notification based on the data came from the intent
            NotificationManager mgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mgr.notify(notifiId, notification);
        }
    }
}