package id.ac.ubaya.ta_160419022.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import id.ac.ubaya.ta_160419022.R
import id.ac.ubaya.ta_160419022.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_history.*
import java.io.File

class HistoryFragment : Fragment() {
    private lateinit var viewModel: ListViewModel
    private val fruitListAdapter  = HistoryListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.refresh()

        recHistory.layoutManager = LinearLayoutManager(context)
        recHistory.adapter = fruitListAdapter

        observeViewModel()
        Log.d("test-load","masuk gak?")

        // Load all json file data
        // you need to move this to view model
        val folderPath = "/data/user/0/id.ac.ubaya.ta_160419022/files" // replace with the actual folder path
        val folder = File(folderPath)
        Log.d("test-load2",folder.toString())
        val txtFiles = folder.listFiles { file -> file.isFile && file.name.endsWith(".json") }
        txtFiles?.forEach { file ->
            // Do something with each TXT file
            val content = file.readText()
            Log.d("test-load",content)
        }
    }

    private fun observeViewModel() {
//        viewModel.fruitLD.observe(viewLifecycleOwner, Observer {
//            fruitListAdapter.updateHistoryList(it)
//        })
//        viewModel.fruitLoadErrorLD.observe(viewLifecycleOwner, Observer {
//            if(it == true){
//                txtErrorHistory.visibility = View.VISIBLE
//            } else {
//                txtErrorHistory.visibility = View.GONE
//            }
//        })
//        viewModel.loadingLD.observe(viewLifecycleOwner, Observer {
//            if(it == true){
//                recHistory.visibility = View.GONE
//                progressLoadHistory.visibility = View.VISIBLE
//            } else {
//                recHistory.visibility = View.VISIBLE
//                progressLoadHistory.visibility = View.GONE
//            }
//        })
    }

}