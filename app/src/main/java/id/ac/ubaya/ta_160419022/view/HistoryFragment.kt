package id.ac.ubaya.ta_160419022.view

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import id.ac.ubaya.ta_160419022.R
import id.ac.ubaya.ta_160419022.model.History
import id.ac.ubaya.ta_160419022.viewmodel.ListViewModel
import kotlinx.android.synthetic.main.fragment_history.*
import java.io.File

class HistoryFragment : Fragment() {
    private lateinit var viewModel: ListViewModel
    private var fruitListAdapter:HistoryListAdapter ?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.title = "History"

        fruitListAdapter = HistoryListAdapter(requireContext(), arrayListOf())

        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        viewModel.refresh()

        recHistory.layoutManager = LinearLayoutManager(context)
        recHistory.adapter = fruitListAdapter

        observeViewModel()
        Log.d("test-load-history","masuk gak?")

        btnRemove.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext()).setTitle("Alert").setMessage("All history data will be erased. Do you want to continue?")
                .setPositiveButton("Continue",object :DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val directory = File("/data/user/0/id.ac.ubaya.ta_160419022/files")
                        val jsonFiles = directory.listFiles { _, name -> name.endsWith(".json") }

                        jsonFiles.forEach {
                            it.delete()
                            History(emptyList())
                        }
                        recHistory.adapter?.notifyDataSetChanged()
                        fruitListAdapter!!.notifyDataSetChanged()

                        val fragmentManager = requireActivity().supportFragmentManager
                        val fragmentTransaction = fragmentManager.beginTransaction()

                        val currentFragment = requireParentFragment()
                        fragmentTransaction.replace(currentFragment.id, currentFragment)

                        fragmentTransaction.commit()
                        Toast.makeText(context,"History is deleted",Toast.LENGTH_SHORT).show()
                    }
                })
                .setNegativeButton("No",object :DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        Toast.makeText(context,"History is not deleted",Toast.LENGTH_SHORT).show()
                    }
                })
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.fruitLD.observe(viewLifecycleOwner, Observer {
            fruitListAdapter?.updateHistoryList(it)
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