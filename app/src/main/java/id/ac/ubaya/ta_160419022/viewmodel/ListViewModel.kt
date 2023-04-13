package id.ac.ubaya.ta_160419022.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import id.ac.ubaya.ta_160419022.model.ApiResponse
import id.ac.ubaya.ta_160419022.model.Fruit

class ListViewModel:ViewModel(){
    val fruitLD = MutableLiveData<List<Fruit>>()
    val fruitLoadErrorLD = MutableLiveData<Boolean>()
    val loadingLD = MutableLiveData<Boolean>()

    fun refresh() {
        val fruit1 = Fruit(16055,"Nonie")
        val fruit2 = Fruit(13312,"Rich")
        val fruit3 = Fruit(11204,"Dinny")

        val fruitList:ArrayList<Fruit> = arrayListOf<Fruit>(fruit1, fruit2, fruit3)
        fruitLD.value = fruitList
        fruitLoadErrorLD.value = false
        loadingLD.value = false
    }

    fun detail() {
        val fruit1 = Fruit(16055,"Nonie")

        val fruitList:ArrayList<Fruit> = arrayListOf<Fruit>(fruit1)
        fruitLD.value = fruitList
        fruitLoadErrorLD.value = false
        loadingLD.value = false
    }

}