package com.example.yemeklistesi

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_yemek_list.*

class FoodList : Fragment() {
    var FoodNameList = ArrayList<String>()
    var FoodIDList = ArrayList<Int>()
    private lateinit var listAdapter: ListRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_yemek_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter = ListRecyclerAdapter(FoodNameList,FoodIDList)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = listAdapter

        sqlVeriAlma()
    }
    fun sqlVeriAlma(){
        try {
            activity?.let {
                val database = it.openOrCreateDatabase("Foods",Context.MODE_PRIVATE,null)
                val cursor = database.rawQuery("SELECT * FROM yemekler",null)
                val FoodNameIndex = cursor.getColumnIndex("FoodName")
                val FoodIdIndex = cursor.getColumnIndex("id")

                FoodNameList.clear()
                FoodIDList.clear()

                while (cursor.moveToNext()){

                   FoodNameList.add(cursor.getString(FoodNameIndex))
                   FoodIDList.add(cursor.getInt(FoodIdIndex))


                }
                listAdapter.notifyDataSetChanged()
                cursor.close()

            }

        } catch (e: Exception){

        }

    }
    }
