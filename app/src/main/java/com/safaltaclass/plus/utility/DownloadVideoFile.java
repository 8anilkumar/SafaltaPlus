package com.safaltaclass.plus.utility;

import android.content.Context;
import android.os.AsyncTask;

import com.safaltaclass.plus.Interface.FileDownLoadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadVideoFile extends AsyncTask<Boolean,Void,Boolean> {

    private String fileUrl;
    private String fileName;
    private Cipher fileCipher;
    private FileDownLoadListener fileListener;
    private int position;
    private Long range;
    private File filePath;


    public DownloadVideoFile() {
    }



    public DownloadVideoFile(Context context, String url, String fileTitle, Cipher cipher, Integer pos, long byteRange, File file, FileDownLoadListener listener) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("You need to supply a url to a clear MP4 file to download and encrypt, or modify the code to use a local encrypted mp4");
        }
        fileUrl = url;
        fileName = fileTitle;
        fileCipher = cipher;
        fileListener = listener;
        position = pos;
        range = byteRange;
        filePath = file;

    }


    @Override
    protected Boolean doInBackground(Boolean... booleans) {
        Long lengthOfFile = 0L;
        int status = 0;

        OkHttpClient okHttpClient = new OkHttpClient();

        try {

            Call call = okHttpClient.newCall(new Request.Builder().url(fileUrl).addHeader("Range", "bytes=" + String.valueOf(range) + "-").build());
            Response response = call.execute();
            if (response.code() == 200 || response.code() == 201 || response.code() == 206) {
                InputStream inputStream = null;
                try {
                    byte[] buff = new byte[1024 * 4];
                    long downloaded = 0;
                    lengthOfFile = response.body().contentLength();
                    inputStream = response.body().byteStream();
                    FileOutputStream fileOutputStream;
                    CipherOutputStream cipherOutputStream;
                    if (range > 0L) {
                        fileOutputStream = new FileOutputStream(filePath, true);
                        cipherOutputStream = new CipherOutputStream(fileOutputStream, fileCipher);
                    } else {
                        fileOutputStream = new FileOutputStream(filePath, false);
                        cipherOutputStream = new CipherOutputStream(fileOutputStream, fileCipher);
                    }

                    while (true) {
                        int readed = inputStream.read(buff);
                        if (readed == -1) {
                            break;
                        }

                        //write buff
                        downloaded += readed;
                        status = (int) (((range + downloaded) * 100) / (lengthOfFile + range));
                        fileListener.onDownloadProgress(status);
                        cipherOutputStream.write(buff, 0, readed);
                        SafaltaPlusPreferences.getInstance().addVideoDownloadStatus(fileName);
                        if (isCancelled()) {
                            break;
                        }
                    }
                    cipherOutputStream.flush();
                    cipherOutputStream.close();
                    inputStream.close();
                    return downloaded == lengthOfFile;
                } catch (IOException ignore) {
                    return false;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                }
            } else {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        if (aBoolean) {
            SafaltaPlusPreferences.getInstance().addDownloaded(fileName);
            SafaltaPlusPreferences.getInstance().removeDownloading();
            SafaltaPlusPreferences.getInstance().removeVideoDownloadStaus();
            fileListener.onDownloadComplete(true, position);
        } else {
            SafaltaPlusPreferences.getInstance().removeVideoDownloadStaus();
            fileListener.onDownloadComplete(false, position);

        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

    }
}

