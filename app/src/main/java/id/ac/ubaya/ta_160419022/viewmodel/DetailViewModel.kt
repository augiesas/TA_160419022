package id.ac.ubaya.ta_160419022.viewmodel

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import id.ac.ubaya.ta_160419022.model.ApiResponse
import id.ac.ubaya.ta_160419022.model.ApiResponseNutrition
import id.ac.ubaya.ta_160419022.view.HomeFragment
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.file.Files

class DetailViewModel (application: Application) : AndroidViewModel(application) {
    val nutritionLiveData = MutableLiveData<ApiResponseNutrition>()
    val nutritionLoadErrorLiveData = MutableLiveData<Boolean>()
    val loadingLiveData = MutableLiveData<Boolean>()

    fun sendPhoto(fileUri: String) {
        val client = OkHttpClient()
        Log.d("test 1","masuk")

        val file = File(fileUri)
        Log.d("test 2","masuk")

        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name, RequestBody.create("image/jpeg".toMediaTypeOrNull(), file))
            .build()
        Log.d("test 3","masuk")

        val request = Request.Builder()
            .url("http://192.168.1.9:8000/predict")
            .post(requestBody)
            .build()
        Log.d("test 4","masuk")

        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                Log.d("test-berhasil 1",response.toString())
                Log.d("test-berhasil 2",call.toString())

                val json = response.body?.string()
                Log.d("test-json",json.toString())

//                val nutritionListType = object : TypeToken<ApiResponse>() {}.type
//                val nutritionList: ApiResponse = Gson().fromJson(json, nutritionListType)

                val result: ApiResponseNutrition = Gson().fromJson(json, ApiResponseNutrition::class.java)
                Log.d("test-result",result.toString())

//                nutritionLiveData.value = nutritionList
                nutritionLiveData.postValue(result)

//                loadingLiveData.value = false
                loadingLiveData.postValue(false)
            }

            override fun onFailure(call: Call, e: IOException) {
                nutritionLoadErrorLiveData.postValue(true)
//                nutritionLoadErrorLiveData.value = true
                loadingLiveData.postValue(false)

//                loadingLiveData.value = false
            }
        })
    }
}