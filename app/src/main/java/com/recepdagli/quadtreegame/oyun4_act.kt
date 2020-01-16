package com.recepdagli.quadtreegame

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.RelativeLayout


class oyun4_act : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = RelativeLayout(this)//relative layout oluşturuyoruz
        layout.setLayoutParams(RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT))
        //özellikleri tanımlıyoruz
        val myView = oyun4(this)//view oluşturup oyun sayfamızı çağırıyoruz
        layout.addView(myView)//oluşturduğumuz layouta viewi ekliyoruz
        setContentView(layout)//content view'e layoutu ekliyoruz


    }
    override fun onBackPressed() {//geri basıldığında çağırılan fonksyon
        super.onBackPressed()
        this.finish()//sayfayı kapatıyoruz
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)//geçiş animasyonunu çağırıyoruz
    }
}