package com.example.carnumberrecognition

import android.app.Activity
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
import android.util.SparseIntArray
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath : String

//    // 사진 회전값 리턴을 위해 선언
//    companion object
//    {
//        private val ORIENTATIONS = SparseIntArray()
//
//        init {
//            ORIENTATIONS.append(ExifInterface.ORIENTATION_NORMAL, 0)
//            ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_90, 90)
//            ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_180, 180)
//            ORIENTATIONS.append(ExifInterface.ORIENTATION_ROTATE_270, 270)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        settingPermission() // 권한체크 시작

        btn_picture.setOnClickListener {
            startCapture()
        }

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

    // 사진 찍고 이미지를 파일로 저장하는 함수수
   @Throws(IOException::class)
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
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val file = File(currentPhotoPath)

            // ExifInterface란 이미지가 갖고 있는 정보의 집합 클래스
            val exif = ExifInterface(currentPhotoPath)
            val exifOrientation: Int
            val exifDegree: Int

            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media
                    .getBitmap(contentResolver, Uri.fromFile(file))

                // ExifInterface.TAG_ORIENTATION = 이미지가 회전한 각도
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                // 회전 각도 리턴
                exifDegree = exifOrientationToDegrees(exifOrientation)
                // imageView set
                img_picture.setImageBitmap(rotateImage(bitmap, exifDegree))
            }
            else{
                val decode = ImageDecoder.createSource(this.contentResolver,
                    Uri.fromFile(file))
                val bitmap = ImageDecoder.decodeBitmap(decode)

                // ExifInterface.TAG_ORIENTATION = 이미지가 회전한 각도
                exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
                // 회전 각도 리턴
                exifDegree = exifOrientationToDegrees(exifOrientation)
                // imageView set
                img_picture.setImageBitmap(rotateImage(bitmap, exifDegree))
            }
        }
    }

    // 이미지 회전값 리턴
    private fun exifOrientationToDegrees(exifOrientation: Int): Int {
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
}
// 참고
// https://kangmin1012.tistory.com/22
// https://m.blog.naver.com/PostView.nhn?blogId=whdals0&logNo=221409327416&proxyReferer=https:%2F%2Fwww.google.com%2F