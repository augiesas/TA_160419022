package id.ac.ubaya.ta_160419022.view

//import com.google.gson.GsonBuilder
//import com.google.gson.JsonParser

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import com.google.common.util.concurrent.ListenableFuture
import id.ac.ubaya.ta_160419022.R
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.cardview.widget.CardView


class HomeFragment : Fragment() {

    private var preview: Preview ?= null
    private var imageCapture: ImageCapture?= null
    private var camera: androidx.camera.core.Camera?= null
    private var client: OkHttpClient ?= null
    private var fileuri: String ?= null
    private var file:File ?= null
    private var isCardVisible = 0

    private lateinit var outputDirectory: File
    private lateinit var sharedPreferences: SharedPreferences

    companion object{
        private const val TAG = "camera"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val REQUEST_CODE_PERMISSIONS_CAMERA = 20
        private const val IMAGE_PICK_CODE = 100
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "Fruit Nutrition Facts"
        // For tutorial
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val isFirstTime = sharedPreferences.getBoolean("firstTime", true)
        Log.d("test-FirstTime", isFirstTime.toString())
        if (!isFirstTime) {
            cardTutorial.visibility = View.GONE
        } else {
            cardTutorial.visibility = View.VISIBLE
            imgTutorialCamera.visibility = View.VISIBLE
            imgTutorialGallery.visibility = View.INVISIBLE
        }

        btnNext.setOnClickListener {
            isCardVisible += 1
            if (isCardVisible == 1) {
                // First click: toggle image visibility
                imgTutorialCamera.visibility = View.INVISIBLE
                imgTutorialGallery.visibility = View.VISIBLE
            }
            if (isCardVisible == 2){
                Log.d("test-card","MASUK SINI")
                // Subsequent clicks: hide the card view and its contents
                cardTutorial.visibility = View.GONE
                val editor = sharedPreferences.edit()
                editor.putBoolean("firstTime", false)
                editor.apply()
            }
        }

        // When choose from gallery
        galleryButton.setOnClickListener {
            checkPermissionsAndPickImage()
        }
        // When Captured
        captureButton.setOnClickListener {
            capture()
        }

        outputDirectory = getOutputDirectory()
    }

    // ===============================================================================================================================================================
    // CAMERA SECTION
    // To start camera after the permission allowed
    override fun onResume() {
        super.onResume()
        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        val cameraPermission = Manifest.permission.CAMERA

        if (ContextCompat.checkSelfPermission(requireContext(), cameraPermission) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissions(arrayOf(cameraPermission), REQUEST_CODE_PERMISSIONS_CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> = ProcessCameraProvider.getInstance(
            requireContext()
        )
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider:ProcessCameraProvider = cameraProviderFuture.get()
            val preview = androidx.camera.core.Preview.Builder()
                .setTargetResolution(Size(640, 640))
                .build()

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetResolution(Size(640, 640))
                .build()
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()
                Log.d("Test","harusnya masuk sini")

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                preview?.setSurfaceProvider(camView.createSurfaceProvider(camera?.cameraInfo))
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, context?.let { ContextCompat.getMainExecutor(it) })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun capture() {
        // Create file
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val current = LocalDateTime.now().format(formatter)
        file = File(outputDirectory,current+".jpg")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file!!).build()

        imageCapture?.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), object: ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d("test start","==============================================================================================================")
                Log.d("test 1",file.toString())
                val savedUri = Uri.fromFile(file)
                Log.d("test 2",savedUri.toString())

                // Refresh the gallery to make the image visible
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    mediaScanIntent.data = savedUri
                    context?.sendBroadcast(mediaScanIntent)
                }

                fileuri = savedUri.path.toString()

                Log.d("fileuri",fileuri.toString())
                Log.d("filename", file!!.name)
                val action = HomeFragmentDirections.actionDetailFragment(fileuri!!)
                if (action != null) {
                    Navigation.findNavController(view!!).navigate(action)
                }

                Log.d("test end","==============================================================================================================")

            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Failed to take a photo", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
            }

        })
    }

    // This is to get where the captured image should be saved
    // This function will get the android - data - picture - folder
    private fun getOutputDirectory(): File {
        val mediaDir = context?.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)?.firstOrNull()
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }
    // ===============================================================================================================================================================

    // GALLERY SECTION
    // to run the gallery intent
    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    // This function is used to get the file path of the picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK)
        {
            val selectedImage: Uri? = data?.data
            val imagePath: String? = selectedImage?.let { getImageFilePath(it) }
            Log.d("test-folder", imagePath.toString())
            val action = HomeFragmentDirections.actionDetailFragment(imagePath!!)
            if (action != null) {
                Navigation.findNavController(requireView()).navigate(action)
            }
        }
    }

    // This function used to convert uri file into path file
    private fun getImageFilePath(uri: Uri): String? {
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    //This function used to organize the permission for read and write external storage inside android
    private fun checkPermissionsAndPickImage() {
        val readPermission = Manifest.permission.READ_EXTERNAL_STORAGE
        val writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

        val hasReadPermission = ContextCompat.checkSelfPermission(requireContext(), readPermission) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(requireContext(), writePermission) == PackageManager.PERMISSION_GRANTED

        if (hasReadPermission && hasWritePermission) {
            pickFromGallery()
        } else {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(readPermission, writePermission), REQUEST_CODE_PERMISSIONS)
        }
    }
    // ===============================================================================================================================================================

    // PERMISSION SECTION
    // This function used to organize the permission for camera and write and read folder
    // like what will happen if the permission is granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS_CAMERA){
            Log.d("test-permission",requestCode.toString())
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }){
                startCamera()
            } else {
                Log.d("test-permission","masuk sini")
                Toast.makeText(context, "This application need permission to access camera", Toast.LENGTH_LONG).show()
                requireActivity().finish()
            }
        }

        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                pickFromGallery()
            } else {
                Toast.makeText(context, "This application need permission to access folder", Toast.LENGTH_LONG).show()
            }
        }
    }
}