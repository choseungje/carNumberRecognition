package com.example.carnumberrecognition;

import java.io.File;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpConnection {
    File srcFile = new File("storage/emulated/0/Pictures/B612/B612_20190519_104827_454.jpg");

    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }


    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(String parameter, String imageTitle, Callback callback) {
        RequestBody body = new MultipartBody.Builder()
//                .addFormDataPart("parameter", parameter)
                .addFormDataPart(parameter, imageTitle+".jpg",RequestBody.create(MediaType.parse("image/jpg"), srcFile))
                .build();
        Request request = new Request.Builder()
                .url("http://203.232.193.176:3000/post")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
    }


}
