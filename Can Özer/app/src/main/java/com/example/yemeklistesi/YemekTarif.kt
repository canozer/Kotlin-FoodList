package com.example.yemeklistesi

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_yemek_tarif.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream


class Recipe : Fragment() {
    var Selectedİmage : Uri? = null
    var SelectedBitmap : Bitmap? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yemek_tarif, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        button.setOnClickListener {
            Save(it)
        }
        imageView.setOnClickListener {
            ChoosePicture(it)
        }
        arguments?.let {
            var İncomingİnformation = RecipeArgs.fromBundle(it).bilgi
            if (İncomingİnformation.equals("menudengeldim")){
                FoodNameText.setText("")
                FoodMaterialText.setText("")
                button.visibility = View.VISIBLE

                val ChooseABackgroundImage = BitmapFactory.decodeResource(context?.resources,R.drawable.resim_ekle)
                imageView.setImageBitmap(ChooseABackgroundImage)
            } else{
                button.visibility = View.INVISIBLE

                val SelectedId = RecipeArgs.fromBundle(it).id

                context?.let{
                    try {
                        val db = it.openOrCreateDatabase("Yemekler",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM yemekler WHERE id = ?", arrayOf(SelectedId.toString()))

                        val FoodNameIndex = cursor.getColumnIndex("FoodName")
                        val FoodMaterialIndex = cursor.getColumnIndex("FoodMaterial")
                        val FoodImage = cursor.getColumnIndex("Image")

                        while (cursor.moveToNext()){
                            FoodNameText.setText(cursor.getString(FoodNameIndex))
                            FoodMaterialText.setText(cursor.getString(FoodMaterialIndex))

                            val byteDizisi = cursor.getBlob(FoodImage)
                            val bitmap = BitmapFactory.decodeByteArray(byteDizisi,0,byteDizisi.size)
                            imageView.setImageBitmap(bitmap)
                        }

                        cursor.close()

                    } catch (e: Exception){
                        e.printStackTrace()
                    }
                }

            }

        }
    }
    fun Save (view: View) {
        val FoodName = FoodNameText.text.toString()
        val FoodMaterial = FoodMaterialText.text.toString()
        if (SelectedBitmap != null){
            val kucukBitmap = kucukBitmapOlustur(SelectedBitmap!!,300)

            val outputStream = ByteArrayOutputStream()
            kucukBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteDizisi = outputStream.toByteArray()

            try {
                context?.let {
                    val database = it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS Food (id INTEGER PRIMARY KEY, FoodName VARCHAR, FoodMaterial VARCHAR, Image BLOB)")
                    val sqlString = "INSERT INTO Food (FoodName, FoodMaterial, Image) VALUES (?, ?, ?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1,FoodName)
                    statement.bindString(2,FoodMaterial)
                    statement.bindBlob(3,byteDizisi)
                    statement.execute()
                }



            } catch (e: Exception){
                e.printStackTrace()
            }
            val action = RecipeDirections.actionYemekTarifToYemekList()
            Navigation.findNavController(view).navigate(action)
        }
    }
    fun ChoosePicture(view: View) {
        activity?.let {
            if (ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            } else {
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1) {
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeriIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeriIntent,2)

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {

            Selectedİmage = data.data

            try {
                context?.let {
                    if(Selectedİmage != null){
                        if(Build.VERSION.SDK_INT >=28) {
                            val source = ImageDecoder.createSource(it.contentResolver,Selectedİmage!!)
                            SelectedBitmap = ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(SelectedBitmap)
                        } else {
                            SelectedBitmap = MediaStore.Images.Media.getBitmap(it.contentResolver,Selectedİmage)
                            imageView.setImageBitmap(SelectedBitmap)

                        }


                    }
                }


            } catch (e: Exception){
                e.printStackTrace()
            }

        }



        super.onActivityResult(requestCode, resultCode, data)
    }
    fun kucukBitmapOlustur(UserSelectedBitmap: Bitmap, maximumBoyut: Int) : Bitmap {
        var width  = UserSelectedBitmap.width
        var height = UserSelectedBitmap.height
        val bitmapOrani : Double = width.toDouble() / height.toDouble()
        if (bitmapOrani > 1) {
            width = maximumBoyut
            val kisaltilmisHeight = width / bitmapOrani
            height = kisaltilmisHeight.toInt()
        } else {
            height = maximumBoyut
            val kisaltilmisWidth = height * bitmapOrani
            width = kisaltilmisWidth.toInt()
        }
        return Bitmap.createScaledBitmap(UserSelectedBitmap,width,height,true)
    }


}