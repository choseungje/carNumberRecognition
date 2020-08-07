package com.example.carnumberrecognition

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Callback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val GALLERY = 0
    lateinit var currentPhotoPath : String
    lateinit var currentImagePath: String

    private var httpConn: HttpConnection = HttpConnection.getInstance();


    private val Url: String = "http://203.232.193.176:3000/post"

    object VolleyService {

        val a: String = "aaa"

        fun testVolley(context: Context, Url: String, imagestring: String, success: (Boolean) -> Unit) {
//            val myJson = JSONObject()
//            val requestBody = myJson.toString()
//            /* myJson에 아무 데이터도 put 하지 않았기 때문에 requestBody는 "{}" 이다 */
            val requestQ = Volley.newRequestQueue(context)

            val testRequest = object : StringRequest(Method.POST, "$Url/img" , Response.Listener { response ->
                println("서버 Response 수신: $response")
                success(true)
            }, Response.ErrorListener { error ->
                Log.d("ERROR", "서버 Response 가져오기 실패: $error")
                success(false)
            }) {
                override fun getParams(): Map<String, String> {
                    val params: MutableMap<String,String> = HashMap()
                    params["img"] = imagestring
                    return params
                }
            }
            testRequest.setShouldCache(false)
            requestQ.add(testRequest)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settingPermission() // 권한체크 시작

        btn_picture.setOnClickListener {
            startCapture()
        }
        openGallery.setOnClickListener { openGallery() }

        submit.setOnClickListener {
            sendData()

//            VolleyService.testVolley(this, Url, imagestring) { testSuccess ->
//                if (testSuccess) {
//                    Toast.makeText(this, "통신 성공!", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this, "통신 실패...!", Toast.LENGTH_LONG).show()
//                }
//            }
            // imageview에서 이미지 가져옴
            val drawable = img_picture.drawable
            // 형변환
            val bitmapDrawable = drawable as BitmapDrawable
            // bitmap 객체로 변환
            val bitmap = bitmapDrawable.bitmap

            val tempFile: File? = null

//            imagestring = BitMapToString(bitmap)
//            Log.d("Tq", imagestring)

            // 비트맵을 파일로 변환
//            FileUploadUtils.goSend(tempFile)
//            val c: Cursor? = getContentResolver().query(Uri.parse(currentImagePath.toString()), null,null,null,null);
//            c?.moveToNext();
//            val absolutePath: String  = c!!.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
            //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
//            DoFileUpload(Url, imagepath);


        }

    }
    /** 웹 서버로 데이터 전송 */
    private fun sendData() {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        object : Thread() {
            override fun run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                httpConn.requestWebServer("데이터1","데이터2", callback);
            }
        }.start()
    }
    val callback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("Tq", "콜백오류:" + e.message)
        }

        @Throws(IOException::class)
        override fun onResponse(call: Call, response: okhttp3.Response) {
            val body = response.body!!.string()
            Log.d("wpqkf", "서버에서 응답한 Body:$body")
        }
    }
//    fun DoFileUpload(apiUrl: String , absolutePath: String ) {
//        HttpFileUpload(apiUrl, "", absolutePath);
//    }
//
//    val lineEnd: String  = "\r\n";
//    val twoHyphens: String  = "--";
//    val boundary: String = "*****";
//
//    fun HttpFileUpload(urlString: String , params: String , fileName: String) {
//
//        val mFileInputStream = FileInputStream(fileName);
//        val connectUrl = URL(urlString);
//        Log.d("Test", "mFileInputStream  is $mFileInputStream");
//
//        // HttpURLConnection 통신
//        val conn = connectUrl.openConnection() as HttpURLConnection
//        conn.doInput = true
//        conn.doOutput = true
//        conn.useCaches = false
//        conn.requestMethod = "POST"
//        conn.setRequestProperty("Connection", "Keep-Alive")
//        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary)
//
//        // write data
//        val dos = DataOutputStream(conn.outputStream)
//        dos.writeBytes(twoHyphens + boundary + lineEnd)
//        dos.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"$fileName\"$lineEnd")
//        dos.writeBytes(lineEnd);
//
//        var bytesAvailable: Int = mFileInputStream.available()
//        val maxBufferSize: Int = 1024
//        var bufferSize: Int = bytesAvailable.coerceAtMost(maxBufferSize);
//
//        val buffer = ByteArray(bufferSize)
//        var bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//
//        Log.d("Test", "image byte is $bytesRead");
//
//        // read image
//        while (bytesRead > 0) {
//            dos.write(buffer, 0, bufferSize);
//            bytesAvailable = mFileInputStream.available();
//            bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//        }
//
//        dos.writeBytes(lineEnd);
//        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
//
//        // close streams
//        Log.e("Test", "File is written");
//        mFileInputStream.close();
//        dos.flush();
//        // finish upload...
//
//        // get response
//        val iss: InputStream  = conn.inputStream
//        Log.d("wpqkf",iss.toString())
//
//        val b = StringBuffer();
//        while (true) {
//            val line = iss.read()
//            if (line == null) break
//            b.append(line)
//        }
//        iss.close();
////            Log.e("Test", b.toString());
//
//
//    } // end of HttpFileUpload()

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
//    fun BitMapToString(bitmap: Bitmap): String {
//        val baos = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos)
//        val b: ByteArray = baos.toByteArray()
//        return Base64.getEncoder().encodeToString(b)
//    }

    private fun saveBitmapAsFile(bitmap: Bitmap, filepath: String) {
        val file = File(filepath)
        val os: OutputStream?
        try {
            file.createNewFile()
            os = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openGallery(){
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, GALLERY)
    }

    // 사진 찍고 이미지를 파일로 저장하는 함수
//   @Throws(IOException::class)
    private fun createImageFile() : File {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply{
            currentPhotoPath = absolutePath
        }
    }

    // Intent를 이용해 카메라를 호출하는 함수, 사진 촬영 버튼을 누를 때 실행된다.
    fun startCapture(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try{
                    createImageFile()
                }catch(ex:IOException){
                    null
                }
                photoFile?.also{
                    val photoURI : Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.carnumberrecognition.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    // 이미지뷰에 내가 찍은 사진을 표시한다
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)

        var bitmap: Bitmap? = null

        // 사진 촬영시 실행
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val file = File(currentPhotoPath)
            Log.d("ab", currentPhotoPath)

            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media
                    .getBitmap(contentResolver, Uri.fromFile(file))
                // imageView set
                img_picture.setImageBitmap(rotateImage(bitmap, rotationData()))
            }
            else{
                val decode = ImageDecoder.createSource(this.contentResolver,
                    Uri.fromFile(file))
                bitmap = ImageDecoder.decodeBitmap(decode)
                // imageView set
                img_picture.setImageBitmap(rotateImage(bitmap, rotationData()))
            }
        }
        // 갤러리에서 불러옴
       else if (requestCode == GALLERY && resultCode == Activity.RESULT_OK){

            val currentImageUrl: Uri? = data?.data
//            imagepath = currentImageUrl?.let { getImagePathToUri(it) }.toString()

            Log.d("bb", currentImageUrl.toString())
            currentImagePath = currentImageUrl.toString()

            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, currentImageUrl)
            Log.d("aa", bitmap.toString())

            img_picture.setImageBitmap(rotateImage(bitmap, 90))
        }

//        val date: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
//        Log.d("zzz", Environment.getExternalStorageState().toString())
//        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
//            Log.d("g", "zzz")
//        }
//        else {
//            Log.d("tq", "tq")
//        }
//
//        val tempSelectFile = File(Environment.getExternalStorageState(E)+ "/DCIM//Camera/",
//            "temp_$date.jpeg"
//        )
//        val out: OutputStream = FileOutputStream(tempSelectFile)
//        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, out)

    }

//    fun getImagePathToUri(data: Uri): String? {
//        //사용자가 선택한 이미지의 정보를 받아옴
//        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
//        val c: Cursor? = contentResolver.query(data, proj, null, null, null)
//        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//        c?.moveToFirst()
//
//        //이미지의 경로 값
//        val imgPath: String? = index?.let { c.getString(it) };
//        Log.d("test", imgPath);
//
//        //이미지의 이름 값
//        val imgName = imgPath?.substring(imgPath.lastIndexOf("/") + 1);
//        Toast.makeText(this, "이미지 이름 : " + imgName, Toast.LENGTH_SHORT).show();
//        if (imgName != null) {
//            imageName = imgName
//        };
//
//        //DoFileUpload("http://192.168.0.37:8080/WebTest/GetImageData.jsp", imgPath);  //해당 함수를 통해 이미지 전송.
//
//        return imgPath
//    }


    fun rotationData(): Int {
        // ExifInterface란 이미지가 갖고 있는 정보의 집합 클래스
        val exif = ExifInterface(currentPhotoPath)
        // ExifInterface.TAG_ORIENTATION = 이미지가 회전한 각도
        val exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        // 회전 각도 리턴
        val exifDegree = exifOrientationToDegrees(exifOrientation)
        return exifDegree
    }

    // 이미지 회전값 리턴
    fun exifOrientationToDegrees(exifOrientation: Int): Int {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270
        }
        return 0
    }

    // 리턴받은 회전값을 기준으로 이미지 회전
    fun rotateImage(source: Bitmap, angle: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    // 권한 체크
    fun settingPermission(){
        val permis = object  : PermissionListener {
            //            어떠한 형식을 상속받는 익명 클래스의 객체를 생성하기 위해 다음과 같이 작성
            override fun onPermissionGranted() {
                Toast.makeText(this@MainActivity, "권한 허가", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "권한 거부", Toast.LENGTH_SHORT)
                    .show()
                ActivityCompat.finishAffinity(this@MainActivity) // 권한 거부시 앱 종료
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permis)
            .setRationaleMessage("카메라 사진 권한 필요")
            .setDeniedMessage("카메라 권한 요청 거부")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .check()
    }
}
// 참고
// https://kangmin1012.tistory.com/22
// https://m.blog.naver.com/PostView.nhn?blogId=whdals0&logNo=221409327416&proxyReferer=https:%2F%2Fwww.google.com%2F
// https://blog.yena.io/studynote/2017/12/12/Android-Kotlin-Volley.html