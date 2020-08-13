package com.example.carnumberrecognition

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.Callback
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private val GALLERY = 0
    lateinit var currentPhotoPath : String
    lateinit var currentImagePath: String
    private var imageTitle: String = ""
    private var imagePath: String = ""
    private var sendFile: File? = null

    private var httpConn: HttpConnection = HttpConnection.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        settingPermission() // 권한체크 시작

        val sdcardStat = Environment.getExternalStorageDirectory().absolutePath
        Log.d("zzzzzztq", sdcardStat)

        btn_picture.setOnClickListener {
            startCapture()
        }
        openGallery.setOnClickListener { openGallery() }

        submit.setOnClickListener {
            sendData()
        }
    }

    /** 웹 서버로 데이터 전송 */
    private fun sendData() {
// 네트워크 통신하는 작업은 무조건 작업스레드를 생성해서 호출 해줄 것!!
        object : Thread() {
            override fun run() {
// 파라미터 2개와 미리정의해논 콜백함수를 매개변수로 전달하여 호출
                httpConn.requestWebServer(imageTitle, sendFile, callback)
            }
        }.start()
    }

    val callback: Callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.d("dk", "콜백오류:" + e.message)
        }
        @Throws(IOException::class)
        override fun onResponse(call: Call, response: okhttp3.Response) {
            val body = response.body!!.string()
            Log.d("wpqkf", "서버에서 응답한 Body:$body")
        }
    }

    private fun openGallery(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY)
    }

    // 사진 찍고 이미지를 파일로 저장하는 함수
    private fun createImageFile() : File {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply{
            currentPhotoPath = absolutePath
            sendFile = File(currentPhotoPath)
            imageTitle = "JPEG_${timeStamp}_.jpg"
        }
    }

    // 카메라로 찍었을 때 실행, 핸드폰에 저장
    private fun saveBitmap(bitmap: Bitmap): String {
        var folderPath = Environment.getExternalStorageDirectory().absolutePath + "/path/"
        var fileName = "comment.jpeg"
        var imagePath = folderPath + fileName
        var folder = File(folderPath)
        if (!folder.isDirectory) folder.mkdirs()
        var out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        return imagePath }

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
                        "com.example.carnumberrecognition.fileprovider", it
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

        // 사진 촬영시 실행
        if(requestCode == REQUEST_IMAGE_CAPTURE){
            val file = File(currentPhotoPath)
            Log.d("ab", currentPhotoPath)

            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media
                    .getBitmap(contentResolver, Uri.fromFile(file))
                // imageView set
                saveBitmap(bitmap)
                img_picture.setImageBitmap(rotateImage(bitmap, rotationData()))
            }
            else{
                val decode = ImageDecoder.createSource(this.contentResolver,
                    Uri.fromFile(file))
                val bitmap = ImageDecoder.decodeBitmap(decode)
                // imageView set
                img_picture.setImageBitmap(rotateImage(bitmap, rotationData()))
            }
        }
        // 갤러리에서 불러옴
       else if (requestCode == GALLERY){
            var currentImageUrl: Uri? = data?.data

            currentImageUrl?.let { getImageNameToUri(it) }
            currentImagePath = currentImageUrl.toString()

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, currentImageUrl)
            Log.d("aa", bitmap.toString())

            img_picture.setImageBitmap(rotateImage(bitmap, 90))
        }
    }

    private fun getImageNameToUri(uri: Uri): String? {
        var path: String? = null
        var name: String? = null
        val contentResolver = applicationContext.contentResolver
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            cursor.moveToNext()
            val pathColumnIdx = cursor.getColumnIndex("_data")
            val column_title: Int? =
                cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
            name = cursor.getString(column_title!!)

            if (pathColumnIdx != -1) {
                path = cursor.getString(pathColumnIdx)
            } else {
                val idColumnIdx = cursor.getColumnIndex("document_id")
                if (idColumnIdx != -1) {
                    val documentId = cursor.getString(idColumnIdx)
                    val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val selection = "_id = ?"
                    val selectionArgs = arrayOf(documentId.split(':')[1])
                    contentResolver.query(contentUri, null, selection, selectionArgs, null)
                        ?.use { cursor2 ->
                            cursor2.moveToNext()
                            val pathColumnIdx2 = cursor2.getColumnIndex("_data")
                            if (pathColumnIdx2 != -1)
                                path = cursor2.getString(pathColumnIdx2)
                        }
                }
            }
        }
        Log.d("path", path)
        Log.d("name", name)

        imageTitle = name.toString()
        imagePath = path.toString()
        sendFile = File(imagePath)
        return path
    }

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
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .check()
    }
}
// 참고
// https://kangmin1012.tistory.com/22
// https://m.blog.naver.com/PostView.nhn?blogId=whdals0&logNo=221409327416&proxyReferer=https:%2F%2Fwww.google.com%2F
// https://blog.yena.io/studynote/2017/12/12/Android-Kotlin-Volley.html
