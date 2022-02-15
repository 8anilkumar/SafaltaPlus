package com.safaltaclass.plus.Interface;

public interface FileDownLoadListener {

    void onDownloadComplete(Boolean download,int pos);

    void onDownloadProgress(int status);
}
