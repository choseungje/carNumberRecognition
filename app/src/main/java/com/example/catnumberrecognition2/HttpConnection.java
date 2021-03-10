package com.example.catnumberrecognition2;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpConnection {
    MediaType mediaType = MediaType.parse("image/jpg");

    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS) //서버로부터의 응답까지의 시간
            .readTimeout(30, TimeUnit.SECONDS) //얼마나 빨리 서버에 바이트를 보낼 수 있는지 확인
            .build(); }

    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(String imageTitle, File sendFile, Callback callback) throws IOException {
//        File src = new File(srcFile);
        if(sendFile.exists()){
            if(sendFile.isFile()){
                // MultipartBody 설정
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("img", imageTitle+".jpg", RequestBody.create(sendFile, mediaType))
                        .build();
                // Request 설정
                Request request = new Request.Builder()
                        .url("http://192.168.0.2:3000/post/post")
                        .post(body)
                        .build();
                client.newCall(request).enqueue(callback);
            }
        }
    }
}
