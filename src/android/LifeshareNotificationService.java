import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;

import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.util.Log;

public class LifeshareNotificationService extends Service {
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
}