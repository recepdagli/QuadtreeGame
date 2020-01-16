package com.recepdagli.quadtreegame

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.graphics.Bitmap
import android.preference.PreferenceManager
import java.util.*

class oyun4(context : Context) : View(context)
{
    val paint2: Paint//paintler
    val paint : Paint

    var click_x : Float = 0f//tıklanan yerin x uzunluğu
    var click_y : Float = 0f//tıklanan yerin y uzunluğu

    var entity_count = getdata("entity_count",0) //oluşturulacak nesnelerin sayısı (örn: değer 7 ise; 7 dikdörtgen 7 daire)

    var circle_dot_count = 5//daire etrafında oluşturulacak nokta sayısı

    //nesnelerin görünümünü sağlayan(bitmap) diziler
    var rect_bitmap: Array<Bitmap> = Array<Bitmap>(entity_count){ Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources,R.mipmap.rect),10,10,true) }
    var circle_bitmap: Array<Bitmap> = Array<Bitmap>(entity_count){ Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources,R.mipmap.rect),10,10,true) }
    var red_rect_bitmap: Array<Bitmap> = Array<Bitmap>(entity_count){ Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources,R.mipmap.rect),10,10,true) }
    var red_circle_bitmap: Array<Bitmap> = Array<Bitmap>(entity_count){ Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources,R.mipmap.rect),10,10,true) }

    var olcek = 0f//ekran ölçüsüne göre referans ölçek

    var circle_dots =  Array<Array<Array<Int>>>(entity_count){
        Array<Array<Int>>(circle_dot_count){
            Array<Int>(5){0}
        }
    }//dairenin etrafındaki noktaların quadtree algoritmasından sonraki değerini barındıran 3 boyutlu dizi

    var rect_dots = Array<Array<Array<Int>>>(entity_count){
        Array<Array<Int>>(27){
            Array<Int>(5){0}
        }
    }//dikdörtgen etrafındaki noktaların quadtree algoritmasından sonraki değerini barındıran 3 boyutlu dizi

    var rect_points = Array<Int>(entity_count){5}//dikdörtgenlerin puanlarının tutlduğu dizi
    var circle_points= Array<Int>(entity_count){5}//dairelerin puanlarının tutlduğu dizi

    var rect_visible = Array<Boolean>(entity_count){true}//dikdörtgenlerin görünürlüklerinin tutulduğu dizi
    var circle_visible= Array<Boolean>(entity_count){true}//dairelerin görünürlüklerinin tutulduğu dizi

    var rect_x = Array<Float>(entity_count){0f}//dikdörtgenlerin x posizyonunun tutulduğu dizi
    var rect_y = Array<Float>(entity_count){0f}//dikdörtgenlerin y posizyonunun tutulduğu dizi
    var rect_w = Array<Float>(entity_count){0f}//dikdörtgenlerin en uzunluğunun tutulduğu dizi
    var rect_h = Array<Float>(entity_count){0f}//dikdörtgenlerin boy uzunluğunun tutulduğu dizi
    var rect_move_x = Array<Float>(entity_count){0f}//dikdörtgenlerin x doğrusundaki hareketinin ilerleyiş değerinin tutulduğu dizi(örn -1.2,0.7)
    var rect_move_y = Array<Float>(entity_count){0f}//dikdörtgenlerin y doğrusundaki hareketinin ilerleyiş değerinin tutulduğu dizi(örn -1.2,0.7)
    var rect_rnd = Array<Float>(entity_count){0f}//dikdörtgenlerin boyutlarının random olması için çarpılan değerin tutulduğu dizi
    var rect_dot_x = Array<Array<Float>>(entity_count){
        Array<Float>(30){0f}
    }//dikdörtgenlerin etrafında bulunan noktaların x doğrusundaki değerinin tutulduğu dizi
    var rect_dot_y = Array<Array<Float>>(entity_count){
        Array<Float>(30){0f}
    }//dikdörtgenlerin etrafında bulunan noktaların y doğrusundaki değerinin tutulduğu dizi

    var circle_x = Array<Float>(entity_count){0f}//dairelerin x posizyonunun tutulduğu dizi
    var circle_y = Array<Float>(entity_count){0f}//dairelerin y posizyonunun tutulduğu dizi
    var circle_w = Array<Float>(entity_count){0f}//dairelerin en uzunluğunun tutulduğu dizi
    var circle_h = Array<Float>(entity_count){0f}//dairelerin boy uzunluğunun tutulduğu dizi
    var circle_move_x = Array<Float>(entity_count){0f}//dairelerin x doğrusundaki hareketinin ilerleyiş değerinin tutulduğu dizi(örn -1.2,0.7)
    var circle_move_y = Array<Float>(entity_count){0f}//dairelerin y doğrusundaki hareketinin ilerleyiş değerinin tutulduğu dizi(örn -1.2,0.7)
    var circle_rnd = Array<Float>(entity_count){0f}//dairelerin boyutlarının random olması için çarpılan değerin tutulduğu dizi
    var circle_dot_x =Array<Array<Float>>(entity_count){
        Array<Float>(circle_dot_count){0f}
    }//dairelerin etrafında bulunan noktaların x doğrusundaki değerinin tutulduğu dizi
    var circle_dot_y = Array<Array<Float>>(entity_count){
        Array<Float>(circle_dot_count){0f}
    }//dairelerin etrafında bulunan noktaların y doğrusundaki değerinin tutulduğu dizi

    var w:Int//ekran en uzunluğu
    var h:Int//ekran boy uzunluğu

    fun init_rect()//dikdörtgenlerin özelliklerinin tanımlandığı fonksyon
    {
        for(i in 0..entity_count-1)//tüm dikdörtgenleri tanımlamak için i tanımlayıp nesne sayısı kadar dönecek döngü oluşturuyoruz ve dizilere erişiyoruz
        {
            rect_rnd[i] = rand_float(0.2f,2.0f)//random değer atıyoruz
            rect_w[i] = (olcek *2)*rect_rnd[i]//dikdörtgen olması için önce ölçeği 2 ile çarpıp, boyutunu random değer ile çarpıp dikdörtgenin en değerine atıyoruz
            rect_h[i] = olcek *rect_rnd[i]//boyunu random değer ile çarpıp boy/en oranı 1/2 olacak şekilde ayarlıyoruz
            rect_move_x[i] = rand_float(-2f,2f)//dikdörtgenin x doğrusunda random hareketini belirliyoruz
            rect_move_y[i] = rand_float(-2f,2f)//dikdörtgenin y doğrusunda random hareketini belirliyoruz
            rect_x[i] = rand_float(0f,w.toFloat()-rect_w[i])//dikdörtgenin x doğrusunda başlangıç pozisyonunu belirliyoruz
            rect_y[i] = rand_float(0f,h.toFloat()-rect_h[i])//dikdörtgenin y doğrusunda başlangıç pozisyonunu belirliyoruz
            //enini, boyunu, posizyonunu ayarladığımız dikdörtgenin bitmap'ını oluşturuyoruz
            rect_bitmap[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources,R.mipmap.rect),rect_w[i].toInt(),rect_h[i].toInt(),true)
            red_rect_bitmap[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources,R.mipmap.red_rect),rect_w[i].toInt(),rect_h[i].toInt(),true)
        }
    }

    fun init_circle()//dairelerin özelliklerinin tanımlandığı fonksyon
    {
        for(i in 0..entity_count-1)//tüm daireleri tanımlamak için i tanımlayıp nesne sayısı kadar dönecek döngü oluşturuyoruz ve dizilere erişiyoruz
        {
            circle_rnd[i] = rand_float(0.2f, 2.0f)//random değer atıyoruz
            circle_w[i] = olcek * circle_rnd[i]//dairenin enini ölçek ve random değer ile çarpıp belirliyoruz
            circle_h[i] = olcek * circle_rnd[i]//dairenin boyunu ölçek ve random değer ile çarpıp belirliyoruz
            circle_move_x[i] = rand_float(-2f, 2f)//dairenin x doğrusunda random hareketini belirliyoruz
            circle_move_y[i] = rand_float(-2f, 2f)//dairenin y doğrusunda random hareketini belirliyoruz
            circle_x[i] = rand_float(0f, w.toFloat() - circle_w[i])//dairenin x doğrusunda başlangıç pozisyonunu belirliyoruz
            circle_y[i] = rand_float(0f, h.toFloat() - circle_h[i])//dairenin y doğrusunda başlangıç pozisyonunu belirliyoruz
            //enini, boyunu, posizyonunu ayarladığımız dairenin bitmap'ını oluşturuyoruz
            circle_bitmap[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.mipmap.circle), circle_w[i].toInt(), circle_h[i].toInt(), true)
            red_circle_bitmap[i] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.mipmap.red_circle), circle_w[i].toInt(), circle_h[i].toInt(), true)
        }

    }
    fun set_rect_dots()//dikdörtgenin etrafındaki noktaların x,y pozisyonlarını ayarlıyoruz
    {
        for(j in 0..entity_count-1)//tüm dikdörtgenlere işlem yapmak için nesne sayısı kadar dönecek bir döngü oluşturuyoruz ve değerleri atıyoruz
        {
            var top = rect_w[j] / 6  //dikdörtgenin uzun kenarlarındakı noktaların aralarındaki boşluk için
            var left = rect_h[j] / 3 //dikdörtgenin kısa kenarlarındakı noktaların aralarındaki boşluk için

            var toplam = 0f //toplam değerini burda tutuyoruz
            for (i in 0..5) {//uzun kenarlarda bulunan noktaların x pozisyonunun atandığı döngü

                rect_dot_x[j][i] = rect_x[j] + toplam//rect_dot_x[j] de bulunan dizinin elemanlarına döngü ile sırasıyla toplamdaki değeri atıyoruz
                rect_dot_x[j][i + 5] = rect_x[j] + toplam//aynı işlemi diğer uzun kenara da yapıyoruz (+5 ile)
                toplam = toplam + top//toplamı artırıyoruz
            }
            for (i in 0..5) {//uzun kenarlarda bulunan noktaların y pozisyonunun atandığı döngü
                rect_dot_y[j][i] = rect_y[j]//rect_dot_y[j] de bulunan dizinin elemanlarına döngü ile sırasıyla toplamdaki değeri atıyoruz
                rect_dot_y[j][i + 5] = rect_y[j] + rect_h[j]//aynı işlemi diğer uzun kenara da yapıyoruz (+5 ile)
                //burada rect_h[j] eklememizin sebebi y doğrusunda diğer uzun kenarın değerlerine bu sayıyı eklediğimizde ulaşabiliyoruz. yani dikdörtgenin alt kısmına.
            }

            toplam = 0f//toplamı sıfırladık
            for (i in 11..14) {//kısa kenarlarda bulunan noktaların x pozisyonunun atandığı döngü

                rect_dot_y[j][i] = rect_y[j] + toplam//rect_dot_y[j] de bulunan dizinin elemanlarına döngü ile sırasıyla toplamdaki değeri atıyoruz
                rect_dot_y[j][i + 3] = rect_y[j] + toplam//aynı işlemi diğer kısa kenara da yapıyoruz (+3 ile)
                toplam = toplam + left//toplamı artırıyoruz
            }
            for (i in 11..14) {//kısa kenarlarda bulunan noktaların y pozisyonunun atandığı döngü
                rect_dot_x[j][i] = rect_x[j]//rect_dot_x[j] de bulunan dizinin elemanlarına döngü ile sırasıyla toplamdaki değeri atıyoruz
                rect_dot_x[j][i + 3] = rect_x[j] + rect_w[j]//aynı işlemi diğer kısa kenara da yapıyoruz (+3 ile)
                //burada rect_w[j] eklememizin sebebi x doğrusunda diğer kısa kenarın değerlerine bu sayıyı eklediğimizde ulaşabiliyoruz. yani dikdörtgenin sağ kısmına.
            }
        }

    }
    fun set_circle_dots()//dairelerin etrafındaki noktaların x,y pozisyonlarını ayarlıyoruz
    {
        for(j in 0..entity_count-1)//tüm dairelere işlem yapmak için nesne sayısı kadar dönecek bir döngü oluşturuyoruz ve değerleri atıyoruz
        {
            var _angle = 360/circle_dot_count//bütün açıyı nesne sayısı kadar bölüyoruz
            var toplam = 0f//toplam değişkeni
            for (i in 0..circle_dot_count-1)//sıradan bütün noktaların pozisyonunu almak için daire nokta sayısı kadar dönecek bir döngü oluşturuyoruz
            {
                val (x,y) = circle(toplam,circle_x[j],circle_y[j],circle_w[j]/2)//toplam,dairenin x ve y pozisyonları ve yarıçapı circle fonk. geçirip
                //x ve y değerlerini alıyoruz.
                circle_dot_x[j][i]=x//noktanın x değerini atıyoruz
                circle_dot_y[j][i]=y//noktanın y değerini atıyoruz
                toplam = toplam+_angle//toplamı hesapladığımız açı kadar ekleyerek toplamı artırıyoruz
            }
        }

    }
    //açıya ve yarıçapa göre dairenin etrafındaki noktaların x ve y değerlerini hesapladığımız fonksyon
    fun circle(angle:Float,ref_x: Float,ref_y: Float,yaricap:Float) : Pair<Float,Float>
    {
        var x = yaricap*(Math.cos(Math.toRadians(angle.toDouble())))+ref_x//x değeri için açıyı radians a çevirdim, ardından cos fonk.undan geçirdim
        var y = yaricap*(Math.sin(Math.toRadians(angle.toDouble())))+ref_y+yaricap//x değeri için açıyı radians a çevirdim, ardından cos fonk.undan geçirdim
        // yarıçap ekledim çünkü cos direkt merkez alırken sin 0 dan alıyor(sebebini araştırıyorum)

        if(angle >= 0 && angle <= 90)
        {
            x += yaricap//açı 0 dan büyük 90 dan küçük ise x e yarıçapı ekledim
        }
        else if(angle >= 90 && angle <= 180)
        {
            x = yaricap - (x*-1)//açı 90 dan büyük 180 den küçük ise yarıçapdan x in ters işaretli halini çıkardım
        }
        else if(angle >= 180 && angle <= 270)
        {
            y = yaricap-(y*-1)//açı 180 den büyük 270 den küçük ise yarıçapdan y nin ters işaretli halini çıkardım
            y -= yaricap// ve y den yarıçapı çıkardım
            x = yaricap-(x*-1)//ve yarıçapdan x in ters işaretli halini çıkardım
        }
        else if(angle >= 270 && angle <= 360)
        {
            y = yaricap - (y*-1)//açı 270 den büyük 360 dan küçük ise yarıçapdan y nin ters işaretli halini çıkardım
            x += yaricap//ve x e yarıçap ekledim
            y -= yaricap//ve y den yarıçapı çıkardım
        }
        return Pair(x.toFloat(),y.toFloat())//bulduğum değerleri return ettim
        //tüm bu işlemler aslında x ve y doğrusunda tanımlanan bir daireyi sadece çeyrek kısmından hariç (0-90 derece) tüm daireyi (0-360 derece) tanımlamak için
        //kendimce bulduğum bir yöntem
    }

    init {

        w = Resources.getSystem().getDisplayMetrics().widthPixels//ekran en uzunluğunu atadım
        h = Resources.getSystem().getDisplayMetrics().heightPixels//ekran boy uzunluğunu atadım

        olcek = w/15f//ölçeği tanımladım

        //paint tanımladım
        paint = Paint()
        paint.textSize = 20f//-->font
        paint.isFilterBitmap = true//-->filterbitmap
        paint.isAntiAlias = true//-->antialias
        paint.color = Color.parseColor("#939292")//-->renk

        paint2 = Paint()
        paint2.textSize = olcek /2//-->font
        paint2.isFilterBitmap = true//-->filterbitmap
        paint2.isAntiAlias = true//-->antialias
        paint2.color = Color.parseColor("#ffffff")//-->renk

        init_rect()//dikdörtgenlerin özelliklerini tanımladım
        init_circle()//dairenin özelliklerini tanımladım



    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        canvas?.drawColor(paint.color)

        //FRAME KISMI

        set_rect_dots()//dikdörtgenlerin noktalaları güncelleniyor
        set_circle_dots()//dairelerin noktalaları güncelleniyor

        for(k in 0..entity_count-1)//dairelerin hesap işlemleri için döngü (referans: k)
        {
            for(i in 0..circle_dot_count-1)//dairelerin noktalarının quadtree algorıtmasından geçmesi için hazırlanan döngü
            {
                //canvas?.drawBitmap(rect_bitmap, circle_dot_x[i],circle_dot_y[i],null)
                circle_dots[k][i] = (get_dot_quadtree(circle_dot_x[k][i],circle_dot_y[k][i]))//dairelerin noktalarının quadtree algorıtmasından geçmesi ve atanması
            }


            for (i in circle_dots[k])//dairelerin çarpışma kontrol döngüsü
            {
                for (l in 0..entity_count-1)//dikdörtgenlerin hesap işlemleri için döngü(referans: l)
                {
                    var temp = 0//geçici değer
                    for(r in 0..17)//dikdörtgenlerin noktalarının quadtree algorıtmasından geçmesi için hazırlanan döngü
                    {
                        if(r!=15)//15. değer sürekli 0 döndürüyor (sebebini araştırıyorum)
                        {
                            rect_dots[l][temp] = get_dot_quadtree(rect_dot_x[l][r],rect_dot_y[l][r])//dikdörtgenlerin noktalarının quadtree algorıtmasından geçmesi ve atanması
                            temp += 1//temp i 1 artırıyoruz
                        }
                    }
                    for (j in rect_dots[l])//dikdörtgenlerin çarpışma kontrol döngüsü
                    {
                        if(Arrays.equals(i,j))//daire ve dikdörtgenlerin quadtree algoritmasından geçtiği değerleri kontrol ediyoruz, eşit ise
                        {
                            rect_move_x[l] = rect_move_x[l]*-1//dikdörtgenin x doğrusundaki hareketini tersine çeviriyoruz
                            rect_move_y[l] = rect_move_y[l]*-1//dikdörtgenin y doğrusundaki hareketini tersine çeviriyoruz
                            circle_move_x[k] = circle_move_x[k]*-1//dairenin x doğrusundaki hareketini tersine çeviriyoruz
                            circle_move_y[k] = circle_move_y[k]*-1//dairenin y doğrusundaki hareketini tersine çeviriyoruz

                            if(rand(1,0)==0)//rasgele kazanan nesneyi belirliyoruz,ilki doğru ise dikdörtgen kaybediyor
                            {
                                if(rect_visible[l])//görünürlük aktif ise
                                {
                                    rect_points[l] -= 1//dikdörtgenin puanı düşüyor
                                    rect_x[l] = rand_float(0f,w.toFloat()-rect_w[l])//dikdörtgenin x pozisyonu random atanıyor
                                    rect_y[l] = rand_float(0f,h.toFloat()-rect_h[l])//dikdörtgenin x pozisyonu random atanıyor
                                }
                                if(rect_points[l] == 0)//eğer puan 0 olmuş ise
                                {
                                    rect_visible[l]=false//görünürlüğü false yapıyoruz
                                }
                            }
                            else//değil ise
                            {
                                if(circle_visible[k])//görünürlük aktif ise
                                {
                                    circle_points[k] -= 1//dairenin puanı düşüyor
                                    circle_x[k] = rand_float(0f, w.toFloat() - circle_w[k])//dairenin x pozisyonu random atanıyor
                                    circle_y[k] = rand_float(0f, h.toFloat() - circle_h[k])//dairenin y pozisyonu random atanıyor
                                }
                                if(circle_points[k] == 0)//eğer puan 0 olmuş ise
                                {
                                    circle_visible[k] = false//görünürlüğü false yapıyoruz
                                }
                            }
                        }
                    }

                }
            }

            if(rect_visible[k])//görünürlük aktif ise
            {
                val (r_x,r_y) = move(rect_x[k],rect_y[k],rect_move_x[k],rect_move_y[k])//hareketi gerçekleştiren fonksyon çağırılıyor
                rect_x[k] = r_x//dönen x değeri x'e,
                rect_y[k] = r_y//y değeri y'ye atanıyor
                if(rect_x[k]<0 || rect_x[k]>w-rect_w[k])//ekranın üst veya alt kısmına vurmuşsa
                {
                    rect_move_x[k] = rect_move_x[k]*-1//yönü değişiyor
                }
                if(rect_y[k]<0 || rect_y[k]>h-rect_h[k])//ekranın yan kısımlarına vurmuşsa
                {
                    rect_move_y[k] = rect_move_y[k]*-1//yönü değişiyor
                }
                canvas?.drawBitmap(rect_bitmap[k], rect_x[k], rect_y[k], null)//ekrana çiziliyor
                canvas?.drawText(rect_points[k].toString(), rect_x[k], rect_y[k], paint2)//puan ekrana çiziliyor
            }
            else
            {
                canvas?.drawBitmap(red_rect_bitmap[k], rect_x[k], rect_y[k], null)//ekrana çiziliyor
                canvas?.drawText(rect_points[k].toString(), rect_x[k], rect_y[k], paint2)//puan ekrana çiziliyor
                dead_anim(true,k)//ölme animasyonu çağırılıyor
            }

            if(circle_visible[k])//görünürlük aktif ise
            {
                val (c_x,c_y) = move(circle_x[k],circle_y[k],circle_move_x[k],circle_move_y[k])//hareketi gerçekleştiren fonksyon çağırılıyor
                circle_x[k] = c_x//dönen x değeri x'e,
                circle_y[k] = c_y//y değeri y'ye atanıyor
                if(circle_x[k]<0 || circle_x[k]>w-circle_w[k])//ekranın üst veya alt kısmına vurmuşsa
                {
                    circle_move_x[k] = circle_move_x[k]*-1//yönü değişiyor
                }
                if(circle_y[k]<0 || circle_y[k]>h-circle_h[k])//ekranın yan kısımlarına vurmuşsa
                {
                    circle_move_y[k] = circle_move_y[k]*-1//yönü değişiyor
                }
                canvas?.drawBitmap(circle_bitmap[k], circle_x[k], circle_y[k], null)//ekrana çiziliyor
                canvas?.drawText(circle_points[k].toString(), circle_x[k], circle_y[k], paint2)//puan ekrana çiziliyor
            }
            else
            {
                canvas?.drawBitmap(red_circle_bitmap[k], circle_x[k], circle_y[k], null)//ekrana çiziliyor
                canvas?.drawText(circle_points[k].toString(), circle_x[k], circle_y[k], paint2)//puan ekrana çiziliyor
                dead_anim(false,k)//ölme animasyonu çağırılıyor
            }

        }
        circle_dots =  Array<Array<Array<Int>>>(entity_count){
            Array<Array<Int>>(circle_dot_count){
                Array<Int>(5){0}
            }
        }//dairenin noktaları sıfırlanıyor
        rect_dots = Array<Array<Array<Int>>>(entity_count){
            Array<Array<Int>>(17){
                Array<Int>(5){0}
            }
        }//dikdörtgenin noktaları sıfırlanıyor

        invalidate()//olan biten işleniyor
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean
    {
        click_x = event!!.x//ekranda kullanıcının tıkladığı x pozisyonu
        click_y = event!!.y//ekranda kullanıcının tıkladığı y posizyonu

        return true//return true
    }


    fun get_dot_quadtree(dot_x: Float,dot_y: Float):Array<Int>//noktanın quadtree dizisini bulup döndüren method
    {
        /*
        *                               _____
        *                              |1 | 2|
        *  quadtree blok index tanımı; |--|--|
        *                              |3 | 4|
         *                             |--|--|
        *
         */
        val quadtree = Array<Int>(5) { 0 }//quadtree dizisi tanımlıyoruz
        var quad_x = 0f//quadtreenin x doğrusunda başlangıç değerini tanımlıyoruz
        var quad_y = 0f//quadtreenin y doğrusunda başlangıç değerini tanımlıyoruz
        var quad_w = w.toFloat()//quadtreenin başlangıç en uzunluğu değerini tanımlıyoruz
        var quad_h = h.toFloat()//quadtreenin başlangıç boy uzunluğu değerini tanımlıyoruz
        for (q in 0..4)//kaç sıra quad kaydedeceksek o kadar dönecek döngü hazırlıyoruz
        {
            quadtree[q] = quadtree_detect(quad_x,quad_y,quad_w,quad_h,dot_x,dot_y)//quadtree detect fonksyonuna gönderip bir integer değer alıyoruz
            if(quadtree[q]==1)//tanıma göre 1. blokda ise
            {
                quad_w = quad_w/2f//eni 2 ye bölüyoruz
                quad_h = quad_h/2f//boyu 2 ye bölüyoruz
                quad_x = quad_x//pozisyon aynı kalıyor
                quad_y = quad_y//pozisyon aynı kalıyor
            }
            if(quadtree[q]==2)//tanıma göre 2. blokda ise
            {
                quad_w = quad_w/2f//eni 2 ye bölüyoruz
                quad_h = quad_h/2f//boyu 2 ye bölüyoruz
                quad_x = quad_x+quad_w //x pozisyonuna önceki enin yarısı kadar ekliyoruz
                quad_y = quad_y//y pozisyonu aynı kalıyor
            }
            if(quadtree[q]==3)//tanıma göre 3. blokda ise
            {
                quad_w = quad_w/2f//eni 2 ye bölüyoruz
                quad_h = quad_h/2f//boyu 2 ye bölüyoruz
                quad_x = quad_x//x pozisyonu aynı kalıyor
                quad_y = quad_y+quad_h//y pozisyonuna önceki boyun yarısı kadar ekliyoruz
            }
            if(quadtree[q]==4)//tanıma göre 4. blokda ise
            {
                quad_w = quad_w/2f//eni 2 ye bölüyoruz
                quad_h = quad_h/2f//boyu 2 ye bölüyoruz
                quad_x = quad_x+quad_w//x pozisyonuna önceki enin yarısı kadar ekliyoruz
                quad_y = quad_y+quad_h//y pozisyonuna önceki boyun yarısı kadar ekliyoruz
            }
        }
        return quadtree // geri döndürüyoruz
    }
    fun quadtree_detect(quad_x:Float,quad_y: Float,quad_w: Float,quad_h:Float,dot_x:Float,dot_y: Float):Int//noktanın hangi blokta olduğunu öğrenmek için kullanılan fonksyon
    {
        val block_x = quad_w/2//gelen eni 2'ye bölüyoruz
        val block_y = quad_h/2//gelen boyu 2'ye bölüyoruz
        var ret = 0//döndürülecek değer
        if(dot_x > 0+quad_x && dot_x < block_x+quad_x && dot_y > 0+quad_y && dot_y < block_y+quad_y)//1.bloğun şartları
        {
            ret = 1
        }
        else if(dot_x > block_x+quad_x && dot_x < quad_w+quad_x && dot_y > 0+quad_y && dot_y < block_y+quad_y)//2.bloğun şartları
        {
            ret = 2
        }
        else if(dot_x > 0+quad_x && dot_x < block_x+quad_x && dot_y > block_y+quad_y && dot_y < quad_h+quad_y)//3.bloğun şartları
        {
            ret = 3
        }
        else if(dot_x > block_x+quad_x && dot_x < quad_w+quad_x && dot_y > block_y+quad_y && dot_y < quad_h+quad_y)//4.bloğun şartları
        {
            ret = 4
        }
        return ret//geri döndürüyoruz
    }

    fun dead_anim(r_or_c:Boolean,index:Int)//ölme animasyonu
    {
        val animator_ = ValueAnimator.ofFloat(0f,1f )//0 dan 1 e sürecek animasyon tanıtıyoruz
        animator_.setDuration((1000f).toLong())//saniye değeri
        animator_.addUpdateListener(object: ValueAnimator.AnimatorUpdateListener {//listener
            override fun onAnimationUpdate(animation: ValueAnimator) {//güncelleme fonksyonu
                val animationValue = ((animation.getAnimatedValue()) as Float).toFloat()//animasyon değeri

                invalidate()//olan biten işleniyor
                if (animationValue >= 1f)//sona gelindiğinde
                {
                    if(r_or_c)//ölen animasyon dikdörtgen ise
                    {
                        rect_visible[index]=true//görünürlüğü true yapıyor(konumu da random atanıyor)
                        rect_points[index] = 5//canı 5'e yükseliyor
                    }
                    else
                    {
                        circle_visible[index]=true//görünürlüğü true yapıyor(konumu da random atanıyor)
                        circle_points[index] = 5//canı 5'e yükseliyor
                    }
                }
            }
        })
        animator_.start()//animasyon başlatılıyor
    }
    fun rand(max:Int, min: Int) : Int//rasgele integer sayı atayan fonksyon
    {
        val range = max - min + 1
        val random = (Math.random() * range) + min
        return random.toInt()
    }
    fun rand_float(max:Float, min: Float) : Float//rasgele float sayı atayan fonksyon
    {
        val range = max - min + 1
        val random = (Math.random() * range) + min
        return random.toFloat()
    }
    fun editdata(id:String,data:Int)//shared preferences ile veri editleyen fonksyon
    {
        val prefences = PreferenceManager.getDefaultSharedPreferences(getContext())
        val editor = prefences.edit()
        editor.putInt(id,data)
        editor.apply()
    }
    fun getdata(id:String,data: Int):Int//shared preferences ile veri çeken fonksyon
    {
        val prefences = PreferenceManager.getDefaultSharedPreferences(getContext())
        val data = prefences.getInt(id,data)
        return data
    }
    fun move(x:Float, y:Float, way_x:Float, way_y:Float) : Pair<Float,Float>// gelen x ve y düzlemindeki pozisyonun hareket işlemini gerçekleştiren fonksyon
    {
        return Pair(x + way_x,y + way_y)
    }
}


