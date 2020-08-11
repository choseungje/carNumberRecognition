package com.example.carnumberrecognition;

import java.io.File;
import java.io.IOException;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class HttpConnection {
<<<<<<< HEAD
    MediaType mediaType = MediaType.parse("image/jpg");
=======
//    File srcFile = new File("/storage/emulated/0/DCIM/Camera/20200731_183149.jpg");

    MediaType mediaType = MediaType.parse("image/jpeg");
>>>>>>> 03aa7497cb8ea3038499ff944f4b029956645258

    private OkHttpClient client;
    private static HttpConnection instance = new HttpConnection();
    public static HttpConnection getInstance() {
        return instance;
    }

    private HttpConnection(){ this.client = new OkHttpClient(); }

    /** 웹 서버로 요청을 한다. */
    public void requestWebServer(String imageTitle, String srcFile, Callback callback) throws IOException {
        File src = new File(srcFile);

        // MultipartBody 설정
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(imageTitle, imageTitle+".jpg", RequestBody.create(src, mediaType))
                .build();
        // Request 설정
        Request request = new Request.Builder()
                .url("http://203.232.193.176:3000/post")
                .post(body)
                .build();
        client.newCall(request).enqueue(callback);
        Response response = client.newCall(request).execute();
        ResponseBody responseBody = response.body();
        assert responseBody != null;
        String res = responseBody.toString();
        System.out.print(res);
<<<<<<< HEAD
=======

>>>>>>> 03aa7497cb8ea3038499ff944f4b029956645258
    }
}
