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
import id.ac.ubaya.ta_160419022.R
import id.ac.ubaya.ta_160419022.model.ApiResponseNutrition
import id.ac.ubaya.ta_160419022.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.fragment_detail.*
import kotlinx.android.synthetic.main.fragment_detail.imgFruitDetailHistory
import kotlinx.android.synthetic.main.fragment_detail.recDetailHistory
import kotlinx.android.synthetic.main.fragment_detail_history.*

class DetailHistoryFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel
    private val nutritionListAdapter = NutritionListAdapter(arrayListOf())
    private var fileUri:String ?= null
    private var fileName:String ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_history, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as AppCompatActivity).supportActionBar?.title = "Nutrition Facts Result"

        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        fileUri = DetailFragmentArgs.fromBundle(requireArguments()).fileUri
        // load Image
        val bitmap = BitmapFactory.decodeFile(fileUri)
        imgFruitDetailHistory.setImageBitmap(bitmap)

        val filePath = fileUri.toString()
        val imageName = filePath.substringAfterLast("/")
        fileName = imageName?.substringBeforeLast(".jpg")
        var fileJson = "/data/user/0/id.ac.ubaya.ta_160419022/files/"+fileName+".json"
        viewModel.read(fileJson)

        recDetailHistory.layoutManager = LinearLayoutManager(context)
        recDetailHistory.adapter = nutritionListAdapter

        observeViewModel()
    }

    fun update(nutritionDetail: ApiResponseNutrition){
        Log.d("ajax1", nutritionDetail.toString())

        val fruit = nutritionDetail
        Log.d("test-fruit",fruit.toString())
        Log.d("test-fruit",fruit.data.toString())
        txtFruitNameHistory.text = fruit.data[0].value.toString()
        txtLinkHistory.text = fruit.data[27].value.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        viewModel.nutritionLiveData.observe(viewLifecycleOwner, Observer {
            update(it)
        })
        viewModel.nutritionLiveData.observe(viewLifecycleOwner, Observer {
            nutritionListAdapter.updateNutritionList(it)
            if(it == null){
                recDetailHistory.visibility= View.GONE
                progressLoadDetailHistory.visibility= View.VISIBLE
            }else{
                recDetailHistory.visibility= View.VISIBLE
                progressLoadDetailHistory.visibility= View.GONE
            }
        })
    }
}