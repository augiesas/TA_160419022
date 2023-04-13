package id.ac.ubaya.ta_160419022.view

import android.content.ContentValues
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import id.ac.ubaya.ta_160419022.R
import id.ac.ubaya.ta_160419022.model.ApiResponse
import id.ac.ubaya.ta_160419022.model.ApiResponseNutrition
import id.ac.ubaya.ta_160419022.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.history_list_item.*
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel
    private val nutritionListAdapter = NutritionListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        viewModel =ViewModelProvider(this).get(DetailViewModel::class.java)
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        val fileUri = DetailFragmentArgs.fromBundle(requireArguments()).fileUri
        Log.d("test-file",fileUri)

        Log.d("test 1","masuk pls")
        viewModel.sendPhoto(fileUri)

        recDetail.layoutManager = LinearLayoutManager(context)
        recDetail.adapter = nutritionListAdapter

        observeViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(nutritionDetail: ApiResponseNutrition){
//        val api:ArrayList<ApiResponse> = arrayListOf(ApiResponse(standDetail))
        Log.d("ajax1", nutritionDetail.toString())

        val fruit = nutritionDetail
        Log.d("test-fruit",fruit.toString())
        Log.d("test-fruit",fruit.data.toString())
        txtFruitName.text = fruit.data[0].value.toString()
        txtLink.text = fruit.data[27].value.toString()

        // Save the array
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        val current = LocalDateTime.now().format(formatter)
        val fileName = current+".json"
        val json = Gson().toJson(fruit)
        val file = File(requireContext().filesDir, fileName)
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
        Log.d("test-save","$fileName saved successfully in ${context?.filesDir?.absolutePath}!")

//        val resolver = requireContext().contentResolver
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, file)
//            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/myapp")
//        }
//        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
//        uri?.let {
//            resolver.openOutputStream(uri)?.use { outputStream ->
//                outputStream.write(fruit.data.joinToString(", ").toByteArray())
//            }
//        }

//        imgDetail.loadImage(stand.url_img, progressBarDetail)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewModel.nutritionLiveData.observe(viewLifecycleOwner, Observer {
            update(it)
        })
        viewModel.nutritionLiveData.observe(viewLifecycleOwner, Observer {
            nutritionListAdapter.updateNutritionList(it)
            if(it == null){
                recDetail.visibility= View.GONE
                progressLoadDetail.visibility= View.VISIBLE
            }else{
                recDetail.visibility= View.VISIBLE
                progressLoadDetail.visibility= View.GONE
            }
        })
    }

}