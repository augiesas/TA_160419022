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
        val fruit1 = Fruit(16055,"Nonie","http://dummyimage.com/75x100.jpg/cc0000/ffffff","5718444778","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada")
        val fruit2 = Fruit(13312,"Rich","http://dummyimage.com/75x100.jpg/5fa2dd/ffffff","3925444073","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada")
        val fruit3 = Fruit(11204,"Dinny","http://dummyimage.com/75x100.jpg/5fa2dd/ffffff1","6827808747","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada","ada")

        val fruitList:ArrayList<Fruit> = arrayListOf<Fruit>(fruit1, fruit2, fruit3)
        fruitLD.value = fruitList
        fruitLoadErrorLD.value = false
        loadingLD.value = false
    }

}