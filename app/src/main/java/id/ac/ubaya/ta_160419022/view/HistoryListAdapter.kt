package id.ac.ubaya.ta_160419022.view

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import id.ac.ubaya.ta_160419022.R
import id.ac.ubaya.ta_160419022.model.Fruit
import id.ac.ubaya.ta_160419022.model.FruitNutritionFacts
import kotlinx.android.synthetic.main.history_list_item.view.*

class HistoryListAdapter(val historyList: ArrayList<NutritionListAdapter>):RecyclerView.Adapter<HistoryListAdapter.FruitViewHolder>(){
    class FruitViewHolder(var view:View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FruitViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.history_list_item, parent, false)

        return FruitViewHolder(view)
    }

    override fun onBindViewHolder(holder: FruitViewHolder, position: Int) {
        holder.view.txtJudulBuah.text = historyList[position].kategori.toString()
        holder.view.txtReadMore.setOnClickListener {
            val action = HistoryFragmentDirections.actionHistoryDetail(null.toString())
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    fun updateHistoryList(newHistoryList: List<NutritionListAdapter>){
        historyList.clear()
        historyList.addAll(newHistoryList)
        notifyDataSetChanged()
    }
}