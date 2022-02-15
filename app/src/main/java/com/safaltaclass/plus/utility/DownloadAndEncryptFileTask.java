package com.safaltaclass.plus.utility;

import android.app.NotificationManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

public class DownloadAndEncryptFileTask extends AsyncTask<Void, Integer, Void> {

    private String mUrl;
    private File mFile;
    private Cipher mCipher;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int id = 1;
    private int i=0;


    public DownloadAndEncryptFileTask(String url, File file, Cipher cipher, NotificationManager manager, NotificationCompat.Builder builder) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("You need to supply a url to a clear MP4 file to download and encrypt, or modify the code to use a local encrypted mp4");
        }
        mUrl = url;
        mFile = file;
        mCipher = cipher;
        mNotifyManager = manager;
        mBuilder = builder;
    }

    private void downloadAndEncrypt() throws Exception {

        URL url = new URL("https://aldine.edu.in/temp/__sample_video/City.mp4");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.connect();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("server error: " + connection.getResponseCode() + ", " + connection.getResponseMessage());
        }

        InputStream inputStream = connection.getInputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(mFile);
        CipherOutputStream cipherOutputStream = new CipherOutputStream(fileOutputStream, mCipher);

        byte buffer[] = new byte[1024 * 1024];
        int bytesRead;
        int totalbytes = 0;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            Log.d(getClass().getCanonicalName(), "reading from http...");
      /*totalbytes = totalbytes + bytesRead/(1024*1024);
      publishProgress(totalbytes);*/
            cipherOutputStream.write(buffer, 0, bytesRead);
        }

        inputStream.close();
        cipherOutputStream.close();
        connection.disconnect();
    }

    @Override
    protected void onPreExecute(){
       // mBuilder.setProgress(100, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        // Update progress
        // mBuilder.setProgress(100, values[0], false);
        // mNotifyManager.notify(id, mBuilder.build());
        super.onProgressUpdate(values);
    }


    @Override
    protected Void doInBackground(Void... params) {
        try {
            downloadAndEncrypt();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {

        Log.d(getClass().getCanonicalName(), "done");
        mBuilder.setContentText("Download complete");
        // Removes the progress bar
      //  mBuilder.setProgress(0, 0, false);
        mNotifyManager.notify(id, mBuilder.build());
    }
}
