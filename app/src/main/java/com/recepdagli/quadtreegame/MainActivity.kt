package com.recepdagli.quadtreegame

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.security.AccessController.getContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button)//buttonu çağırıyoruz
        val entity_count = findViewById<EditText>(R.id.editText)//nesne sayısını gireceğimiz textbox'ı çağırıyoruz
        button.setOnClickListener {//onclick listener ile butona tıklandığında çalışacak methodu çağırıyoruz
            try {//deniyoruz
                if (entity_count.text.toString().toInt()>1) {
                    editdata("entity_count", entity_count.text.toString().toInt())//oyundan ulaşmak için değeri "entity_count" id si ile tanımlıyoruz
                    val intent = Intent(this, oyun4_act::class.java)//oyunu aktifleştirmek için intent oluşturuyoruz
                    startActivity(intent)//oyunu başlatıyoruz
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)//geçiş animasyonu
                }
                else
                {
                    //kullanıcıyı uygun değerleri girmesi için uyarıyoruz
                    Toast.makeText(applicationContext,"1'den büyük değer giriniz", Toast.LENGTH_SHORT).show()
                }
            }
            catch (e:Exception)//hata ile karşılaşırsak
            {
                //kullanıcıyı uygun değerleri girmesi için uyarıyoruz
                Toast.makeText(applicationContext,"1'den büyük değer giriniz", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun editdata(id:String,data:Int)//shared preferences ile veri editleyen fonksyon
    {
        val prefences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = prefences.edit()
        editor.putInt(id,data)
        editor.apply()
    }
}
