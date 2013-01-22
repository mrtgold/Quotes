package com.gec.rq;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 1/19/13
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuotesUpdateService extends IntentService {
    private int result = Activity.RESULT_CANCELED;

    public QuotesUpdateService() {
        super("QuotesUpdateService");
    }

    // Will be called asynchronously be Android
    @Override
    protected void onHandleIntent(Intent intent) {
        Uri data = intent.getData();
        String urlPath = intent.getStringExtra("urlpath");
        String fileName = data.getLastPathSegment();
        Log.i(getClass().getName(),"getExternalStorageState=" + Environment.getExternalStorageState());
        Log.i(getClass().getName(),"getDownloadCacheDirectory=" + Environment.getDownloadCacheDirectory().getPath());
        File output = new File(Environment.getDownloadCacheDirectory(), fileName);
        if (output.exists()) {
            output.delete();
        }
        Log.i(getClass().getName(),"output=" + output.getPath());

        InputStream stream = null;
        FileOutputStream fos = null;
        try {

            URL url = new URL(urlPath);
            stream = url.openConnection().getInputStream();
            InputStreamReader reader = new InputStreamReader(stream);
            fos = new FileOutputStream(output.getPath());
            int next = -1;
            while ((next = reader.read()) != -1) {
                fos.write(next);
            }
            // Sucessful finished
            result = Activity.RESULT_OK;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Messenger messenger = (Messenger) extras.get("MESSENGER");
            Message msg = Message.obtain();
            msg.arg1 = result;
            msg.obj = output.getAbsolutePath();
            try {
//                Bundle bundle = new Bundle();
//                bundle.putString();
                messenger.send(msg);
            } catch (android.os.RemoteException e1) {
                Log.w(getClass().getName(), "Exception sending message", e1);
            }

        }
    }
}

