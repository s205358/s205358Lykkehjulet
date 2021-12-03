package com.example.s205358lykkehjulet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.s205358lykkehjulet.databinding.LetterLayoutBinding

class LetterListAdapter(
    private val values: List<Char>
) : RecyclerView.Adapter<LetterListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LetterLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.bind(item.toString())
    }

    class ViewHolder(private var binding: LetterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(letter: String) {
            binding.letter.text = letter
        }
    }

    override fun getItemCount(): Int = values.size
}