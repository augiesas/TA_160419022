package id.ac.ubaya.ta_160419022.view

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import id.ac.ubaya.ta_160419022.R
import id.ac.ubaya.ta_160419022.model.ApiResponseNutrition
import id.ac.ubaya.ta_160419022.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import java.io.File
import java.io.FileOutputStream

class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel
    private val nutritionListAdapter = NutritionListAdapter(arrayListOf())
    private var fileUri:String ?= null
    private var fileName:String ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.title = "Nutrition Facts Result"

        txtError.visibility = View.GONE
        btnBack.visibility = View.GONE

        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        fileUri = DetailFragmentArgs.fromBundle(requireArguments()).fileUri
        // load Image
        val bitmap = BitmapFactory.decodeFile(fileUri)
        imgFruitDetailHistory.setImageBitmap(bitmap)

        Log.d("test-file", fileUri!!)

        Log.d("test 1","masuk pls")
        viewModel.sendPhoto(fileUri!!)

        recDetailHistory.layoutManager = LinearLayoutManager(context)
        recDetailHistory.adapter = nutritionListAdapter

        val filePath = fileUri.toString()
        val imageName = filePath.substringAfterLast("/")
        fileName = imageName?.substringBeforeLast(".jpg")

        observeViewModel()

        btnBack.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            fragmentManager.popBackStack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun SaveArray(fruit: ApiResponseNutrition){
        // Save the array
        val fileName = fileName+".json"
        val json = Gson().toJson(fruit)
        val file = File(requireContext().filesDir, fileName)
        FileOutputStream(file).use {
            it.write(json.toByteArray())
        }
        Log.d("test-save","$fileName saved successfully in ${context?.filesDir?.absolutePath}!")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun update(nutritionDetail: ApiResponseNutrition){
        Log.d("ajax1", nutritionDetail.toString())

        val fruit = nutritionDetail
        Log.d("test-fruit",fruit.toString())
        Log.d("test-fruit",fruit.data.toString())
        txtFruitName.text = fruit.data[0].value.toString()
        txtLink.text = fruit.data[27].value.toString()

        SaveArray(fruit)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewModel.nutritionLiveData.observe(viewLifecycleOwner, Observer {
            update(it)
        })
        viewModel.nutritionLiveData.observe(viewLifecycleOwner, Observer {
            nutritionListAdapter.updateNutritionList(it)
        })
        viewModel.nutritionLoadErrorLiveData.observe(viewLifecycleOwner){
            if(it){
                Log.d("test-LiveData", "Masuk1")
                txtError.visibility = View.VISIBLE
                btnBack.visibility = View.VISIBLE
                progressLoadDetail.visibility = View.GONE
            }
            else{
                Log.d("test-LiveData", "Masuk2")
                txtError.visibility = View.GONE
                btnBack.visibility = View.GONE

            }
        }
        viewModel.loadingLiveData.observe(viewLifecycleOwner){
            Log.d("test-it", it.toString())

            if(it){
                Log.d("test-LiveData", "Masuk3")
                recDetailHistory.visibility = View.GONE
                progressLoadDetail.visibility = View.VISIBLE
            }
            else{
                Log.d("test-LiveData", "Masuk4")
                recDetailHistory.visibility = View.VISIBLE
                progressLoadDetail.visibility = View.GONE
                cardLoad.visibility = View.GONE
            }
        }
    }

}