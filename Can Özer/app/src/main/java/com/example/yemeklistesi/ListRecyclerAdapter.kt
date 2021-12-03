package com.example.yemeklistesi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListRecyclerAdapter(val FoodList : ArrayList<String>, val IDList : ArrayList<Int>) : RecyclerView.Adapter<ListRecyclerAdapter.YemekHolder>() {
    class YemekHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): YemekHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.recycler_row,parent,false)
        return YemekHolder(view)
    }

    override fun onBindViewHolder(holder: YemekHolder, position: Int) {
        holder.itemView.recycler_row_text.text = FoodList[position]
        holder.itemView.setOnClickListener {
            val action = FoodListDirections.actionYemekListToYemekTarif("recyclerdangeldim",IDList[position])
            Navigation.findNavController(it).navigate(action)
        }
    }

    override fun getItemCount(): Int {
        return FoodList.size
    }
}