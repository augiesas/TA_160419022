package id.ac.ubaya.ta_160419022.util

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.squareup.picasso.Callback
import java.lang.Exception
import com.squareup.picasso.Picasso
import id.ac.ubaya.ta_160419022.R
import kotlinx.android.synthetic.main.fragment_detail.view.*

fun ImageView.loadImage(url: String?, progressBar: ProgressBar){
    Picasso.get()
        .load(url)
        .resize(170,170)
        .centerCrop()
        .error(R.drawable.ic_baseline_error_24)
        .into(this, object: Callback {
            override fun onSuccess() {
                progressBar.visibility = View.GONE
            }

            override fun onError(e: Exception?) {
                Log.e("ERROR",e.toString())
            }

        })
}