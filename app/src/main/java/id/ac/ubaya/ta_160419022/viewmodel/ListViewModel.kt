package id.ac.ubaya.ta_160419022.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import id.ac.ubaya.ta_160419022.model.ApiResponseNutrition
import id.ac.ubaya.ta_160419022.model.History
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


class ListViewModel:ViewModel(){
    val fruitLD = MutableLiveData<History>()
    val fruitLoadErrorLD = MutableLiveData<Boolean>()
    val loadingLD = MutableLiveData<Boolean>()
    val jsonArray = JSONArray()
    val fileList:MutableList<String> = mutableListOf()
    val nutritionLists: MutableList<ApiResponseNutrition> = mutableListOf()
    val arrayTemp:MutableList<String> = mutableListOf()

    fun refresh() {
        // Load all json file data
        val folderPath = "/data/user/0/id.ac.ubaya.ta_160419022/files" // replace with the actual folder path
        val folder = File(folderPath)
        Log.d("test-load2",folder.toString())
        val txtFiles = folder.listFiles { file -> file.isFile && file.name.endsWith(".json") }
        if(txtFiles != null)
        {
            txtFiles?.forEach { file ->
                // Do something with each TXT file
                val content = file.readText()
                val fileName = file.name.replace(".json","")
                Log.d("test-txtFiles",file.name.replace(".json",""))
                Log.d("test-load",content)

                val jsonObject = JSONObject(content)

                if(fileName !in arrayTemp){
                    // insert the JSON object into the JSON array
                    jsonArray.put(jsonObject)
                    fileList.add(file.toString())
                    arrayTemp.add(fileName)
                }
            }
            nutritionLists.clear()
            Log.d("test-jsonArrayList",jsonArray.length().toString())

            val jsonArray = JSONArray(jsonArray.toString())

            for (i in 0 until jsonArray.length()){
                val jsonObject = jsonArray.getJSONObject(i)
                val nutritionList:ApiResponseNutrition = Gson().fromJson(jsonObject.toString(),ApiResponseNutrition::class.java)
                Log.d("test-nutritionlist",nutritionList.toString())
                nutritionLists.add(nutritionList)
            }
            val history = History(data = nutritionLists)
            Log.d("test-fileList",history.toString())

            fruitLD.value = history
            fruitLoadErrorLD.value = false
            loadingLD.value = false
        }
        else
        {
            Log.d("test-history","EMPTY")
            fruitLoadErrorLD.value = true
            loadingLD.value = false
        }
    }
}