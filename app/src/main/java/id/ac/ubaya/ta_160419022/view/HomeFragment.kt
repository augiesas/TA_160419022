package id.ac.ubaya.ta_160419022.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
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
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.common.util.concurrent.ListenableFuture
//import com.google.gson.GsonBuilder
//import com.google.gson.JsonParser
import id.ac.ubaya.ta_160419022.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.Executors


class HomeFragment : Fragment() {

    private var preview: Preview ?= null
    private var imageCapture: ImageCapture?= null
    private var camera: androidx.camera.core.Camera?= null
    private var client: OkHttpClient ?= null

    private lateinit var outputDirectory: File

    companion object{
        private const val TAG = "camera"
        private const val FILENAME_FORMAT = "yyy-MM-dd-HH-mm-ss"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val IMAGE_PICK_CODE = 100
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
//            pickFromGallery()
            testAPI()
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
        // Saat capture ini masih save
        // harus dirubah biar foto yang di dapet bukan ke save tapi ke upload
        // ==================================================================================================================================

        // Create file
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        val file = File(outputDirectory,current+".jpg")
//
//        Log.d("Lihat", file.toString())

        val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

        imageCapture?.takePicture(outputOptions, ContextCompat.getMainExecutor(requireContext()), object: ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                Log.d("test start","==============================================================================================================")
                Log.d("test 1",file.toString())
                val savedUri = Uri.fromFile(file)
                Log.d("test 2",savedUri.toString())

                // Check if external storage is available
                if (isExternalStorageAvailable()) {
                    // Save the image to external storage
                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val contentResolver = context?.contentResolver
                    val uri = contentResolver?.insert(externalContentUri, contentValues)
                    val outputStream = uri?.let { contentResolver.openOutputStream(it) }
                    outputStream?.use { file.inputStream().copyTo(it) }
                } else {
                    // Save the image to internal storage
                    val outputInternalFile = File(context?.filesDir, file.name)
                    val outputStream = FileOutputStream(outputInternalFile)
                    outputStream.use { file.inputStream().copyTo(it) }
                }

                // Refresh the gallery to make the image visible
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
                    mediaScanIntent.data = savedUri
                    context?.sendBroadcast(mediaScanIntent)
                }

                sendPhoto(file)

                Log.d("test end","==============================================================================================================")

            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "Failed to take a photo", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
            }

        })
    }

    fun formData(file: File) {
        GlobalScope.launch(Dispatchers.IO) {
            val serverUrl = URL("http://192.168.1.8:8000/predict")
//            val file = File(file)
            val boundary = "-----------------------------12345"

            val connection = serverUrl.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.doInput = true
            connection.setRequestProperty(
                "Content-Type",
                "multipart/form-data; boundary=$boundary"
            )

            val outputStream = connection.outputStream
            val writer = OutputStreamWriter(outputStream)


            // Write the form-data with the filename and image file contents
            writer.append("--$boundary\r\n")
            writer.append("Content-Disposition: form-data; name=\"file\"; filename=\"${file.name}\"\r\n")
            writer.append("Content-Type: image/png\r\n")
            writer.append("\r\n")
            writer.flush()

            FileInputStream(file).use { fileInputStream ->
                fileInputStream.copyTo(outputStream)
            }

            writer.append("\r\n")
            writer.flush()
            writer.append("--$boundary--\r\n")
            writer.flush()

            val responseCode = connection.responseCode
            println("Response Code: $responseCode")

            connection.disconnect()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS){
            if (permissionGranted()){
                startCamera()
            } else {
                Toast.makeText(context, "This application need permission to access camera", Toast.LENGTH_LONG).show()
                requireActivity().finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK)
        {
            val action = HomeFragmentDirections.actionDetailFragment()
            Navigation.findNavController(this.requireView()).navigate(action)
        }
    }

    fun testAPI(){
        // Create JSON using JSONObject
        val jsonObject = JSONObject()
        jsonObject.put("name", "Jack")
        jsonObject.put("salary", "3540")
        jsonObject.put("age", "23")

        // Convert JSONObject to String
        val jsonObjectString = jsonObject.toString()

        GlobalScope.launch(Dispatchers.IO) {
            val url = URL("http://192.168.1.8:8000/items")
            val httpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.requestMethod = "POST"
            httpURLConnection.setRequestProperty("Content-Type", "application/json") // The format of the content we're sending to the server
            httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
            httpURLConnection.doInput = true
            httpURLConnection.doOutput = true

            // Send the JSON we created
            val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
            outputStreamWriter.write(jsonObjectString)
            outputStreamWriter.flush()

            // Check if the connection is successful
            val responseCode = httpURLConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = httpURLConnection.inputStream.bufferedReader()
                    .use { it.readText() }  // defaults to UTF-8
                withContext(Dispatchers.Main) {

                    // Convert raw JSON to pretty JSON using GSON library
//                    val gson = GsonBuilder().setPrettyPrinting().create()
//                    val prettyJson = gson.toJson(JsonParser.parseString(response))
//                    Log.d("Pretty Printed JSON :", prettyJson)

                }
            } else {
                Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
            }
        }
    }

    private fun sendPhoto(file: File) {
        val client = OkHttpClient()

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), file))
            .build()

        val request = Request.Builder()
            .url("http://192.168.1.5:8000/predict")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
//                Toast.makeText(context, "Failed send photo to API", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Photo capture failed: ${e.message}", e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("test-berhasil 1",response.toString())
                Log.d("test-berhasil 2",call.toString())
            }
        })
    }

//    fun Predict(testFeatures:ArrayList<Int>){
//        // Load the SVM model from file
//        val svmModel = joblib.load("E:/Materi Kuliah/TA/Program/Data/Model/model_CNN_SVM.sav")
//
//// Load the test features from file
//        val testFeatures = // Load the test features using your preferred method
//
//// Make predictions on the test data
//        val predictions = svmModel.predict(testFeatures)
//
//// Convert the predictions to a ByteBuffer
//        val buffer = ByteBuffer.allocate(predictions.size * 4)
//        buffer.order(ByteOrder.nativeOrder())
//        for (prediction in predictions) {
//            buffer.putFloat(prediction)
//        }
//        buffer.rewind()
//
//// Load the TensorFlow Lite model from file
//        val model = Interpreter(FileInputStream("svm_model.tflite").channel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size()))
//
//// Allocate input and output tensors
//        val inputTensor = model.getInputTensor(0)
//        val outputTensor = model.getOutputTensor(0)
//
//// Run inference
//        model.run(buffer, inputTensor.buffer)
//        val outputBuffer = ByteBuffer.allocate(outputTensor.shape()[0] * outputTensor.dataType().byteSize())
//        model.outputTensor(0).buffer.rewind().get(outputBuffer)
//
//// Print the predictions
//        val submissionResults = FloatArray(outputTensor.shape()[0])
//        outputBuffer.order(ByteOrder.nativeOrder()).asFloatBuffer().get(submissionResults)
//        for (result in submissionResults) {
//            println(result)
//        }
//    }
}