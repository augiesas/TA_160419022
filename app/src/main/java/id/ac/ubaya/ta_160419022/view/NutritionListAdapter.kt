package id.ac.ubaya.ta_160419022.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.ac.ubaya.ta_160419022.R
import id.ac.ubaya.ta_160419022.model.ApiResponse
import id.ac.ubaya.ta_160419022.model.ApiResponseNutrition
import id.ac.ubaya.ta_160419022.model.FruitNutritionFacts
import id.ac.ubaya.ta_160419022.model.NutritionList
import kotlinx.android.synthetic.main.detail_list_item.view.*

class NutritionListAdapter (val nutritionList:ArrayList<NutritionList>): RecyclerView.Adapter<NutritionListAdapter.NutritionViewHolder>(){
    class NutritionViewHolder(var view: View) : RecyclerView.ViewHolder(view)
    val kategori:ArrayList<String> = arrayListOf("air", "energi", "protein", "lemak", "karbohidrat", "gula_total", "serat", "kalsium", "fosfor", "besi", "natrium", "kalium", "tembaga",
                                                "seng", "magnesium", "beta_karoten", "karoten_total", "vitamin_a", "vitaminB1", "vitaminB2", "niacin", "vitamin_b6", "vitaminC",
                                                "vitamin_e", "vitamin_d", "vitamin_k")
    val limit:Int = kategori.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.detail_list_item, parent, false)
        return NutritionViewHolder(view)
    }

    override fun onBindViewHolder(holder: NutritionViewHolder, position: Int) {
        val api:ArrayList<ApiResponseNutrition> = arrayListOf(ApiResponseNutrition(nutritionList))

        val nutrition = api[0].data[position+1]
        with(holder.view){
            txtNutritionCategory.text = nutrition.kategori
            txtCategoryValue.text = nutrition.value
        }
    }

    override fun getItemCount(): Int {
        return Math.min(nutritionList.size, limit)
    }

    fun updateNutritionList(newNutritionList: ApiResponseNutrition){
        nutritionList.clear()
        nutritionList.addAll(newNutritionList.data)
        notifyDataSetChanged()
    }
}