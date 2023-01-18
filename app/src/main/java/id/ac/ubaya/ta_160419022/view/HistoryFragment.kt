package id.ac.ubaya.ta_160419022.view

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.common.util.concurrent.ListenableFuture
import id.ac.ubaya.ta_160419022.R
import id.ac.ubaya.ta_160419022.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_home.*

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
    }

    private fun observeViewModel() {
        viewModel.fruitLD.observe(viewLifecycleOwner, Observer {
            fruitListAdapter.updateHistoryList(it)
        })
        viewModel.fruitLoadErrorLD.observe(viewLifecycleOwner, Observer {
            if(it == true){
                txtErrorHistory.visibility = View.VISIBLE
            } else {
                txtErrorHistory.visibility = View.GONE
            }
        })
        viewModel.loadingLD.observe(viewLifecycleOwner, Observer {
            if(it == true){
                recHistory.visibility = View.GONE
                progressLoadHistory.visibility = View.VISIBLE
            } else {
                recHistory.visibility = View.VISIBLE
                progressLoadHistory.visibility = View.GONE
            }
        })
    }

}