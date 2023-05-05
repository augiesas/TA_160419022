package id.ac.ubaya.ta_160419022.view

import android.content.Context
import android.os.Environment
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import id.ac.ubaya.ta_160419022.R
import id.ac.ubaya.ta_160419022.model.ApiResponseNutrition
import id.ac.ubaya.ta_160419022.model.Fruit
import id.ac.ubaya.ta_160419022.model.FruitNutritionFacts
import id.ac.ubaya.ta_160419022.model.History
import kotlinx.android.synthetic.main.history_list_item.view.*
import java.io.File

class HistoryListAdapter(private val context:Context, val historyList: ArrayList<ApiResponseNutrition>):RecyclerView.Adapter<HistoryListAdapter.FruitViewHolder>(){
    class FruitViewHolder(var view:View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FruitViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.history_list_item, parent, false)

        return FruitViewHolder(view)
    }

    override fun onBindViewHolder(holder: FruitViewHolder, position: Int) {
        val api:ArrayList<History> = arrayListOf(History(historyList))

        val history = api[0].data[position]
        val internalStorageDir = context.filesDir

        with(holder.view){
            txtJudulBuah.text = history.data[0].value
            txtReadMore.setOnClickListener {
                val fileuri = context.getExternalFilesDirs(Environment.DIRECTORY_PICTURES)?.firstOrNull().toString()+"/"+history.data[history.data.lastIndex].value.toString()
                Log.d("test-fileURI",fileuri)
                val action = HistoryFragmentDirections.actionHistoryDetail(fileuri)
                Navigation.findNavController(it).navigate(action)
            }
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateHistoryList(newHistoryList: History){
        historyList.clear()
        historyList.addAll(newHistoryList.data)
        notifyDataSetChanged()
    }
}