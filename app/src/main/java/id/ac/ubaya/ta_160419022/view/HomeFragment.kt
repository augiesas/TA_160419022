package id.ac.ubaya.ta_160419022.view

//import com.google.gson.GsonBuilder
//import com.google.gson.JsonParser

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
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
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.common.util.concurrent.ListenableFuture
import id.ac.ubaya.ta_160419022.R
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import java.io.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class HomeFragment : Fragment() {

    private var preview: Preview ?= null
    private var imageCapture: ImageCapture?= null
    private var camera: androidx.camera.core.Camera?= null
    private var client: OkHttpClient ?= null
    private var fileuri: String ?= null
    private var file:File ?= null

    private lateinit var outputDirectory: File

    companion object{
        private const val TAG = "camera"
        private const val FILENAME_FORMAT = "yyy-MM-dd-HH-mm-ss"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val IMAGE_PICK_CODE = 100
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
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
        // When choose from gallery
        galleryButton.setOnClickListener {
            pickFromGallery()
        }
        // When Captured
        captureButton.setOnClickListener {
            capture()
        }
        if(permissionGranted()){
            startCamera()
        }
        else {
            ActivityCompat.requestPermissions(context as Activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
        outputDirectory = getOutputDirectory()
    }

    private fun pickFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"

        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = context?.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)?.firstOrNull()
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else requireContext().filesDir
    }


    private fun permissionGranted() = REQUIRED_PERMISSIONS.all{
        ContextCompat.checkSelfPermission(
            requireActivity().baseContext, it) == PackageManager.PERMISSION_GRANTED
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

    private fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun capture() {
        // Create file
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            Log.d("test-permission",requestCode.toString())
            if (permissionGranted()){
                startCamera()
            } else {
                Log.d("test-permission","masuk sini")
                Toast.makeText(context, "This application need permission to access camera", Toast.LENGTH_LONG).show()
                requireActivity().finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK)
        {
            val action = HomeFragmentDirections.actionDetailFragment(fileuri!!)
            Navigation.findNavController(this.requireView()).navigate(action)
        }
    }
}