package com.example.carnumberrecognition;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpConnection {
//    File srcFile = new File("/storage/emulated/0/DCIM/Camera/20200809_192247(0).jpg");
    MediaType mediaType = MediaType.parse("image/jpeg");

    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }


    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(String imageTitle, String srcFile, Callback callback) {
        File src = new File(srcFile);
        // MultipartBody 설정
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(imageTitle, imageTitle+".jpg", RequestBody.create(src, mediaType))
                .build();
        // Request 설정
        Request request = new Request.Builder()
                .url("http://203.232.193.176:3000/post/img")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
