package com.example.s205358lykkehjulet.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.s205358lykkehjulet.databinding.CategoryLayoutBinding
import com.example.s205358lykkehjulet.model.Category

class CategoryListAdapter(private val onItemClicked: (Category) -> Unit) :
    ListAdapter<Category, CategoryListAdapter.CategoryViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            CategoryLayoutBinding.inflate(
                LayoutInflater.from(
                    parent.context
                )
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val current = getItem(position)
        holder.itemView.setOnClickListener {
            onItemClicked(current)
        }
        holder.bind(current)
    }

    class CategoryViewHolder(private var binding: CategoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.categoryName.setText(category.name)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldCategory: Category, newCategory: Category): Boolean {
                return oldCategory === newCategory
            }

            override fun areContentsTheSame(oldCategory: Category, newCategory: Category): Boolean {
                return oldCategory.name == newCategory.name
            }
        }
    }
}