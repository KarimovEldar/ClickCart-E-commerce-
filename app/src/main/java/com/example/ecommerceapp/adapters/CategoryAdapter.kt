package com.example.ecommerceapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.ItemCategoryLayoutBinding
import com.example.ecommerceapp.model.CategoryModel
import com.example.ecommerceapp.ui.activities.categoryproduct.CategoryProductActivity
import com.example.ecommerceapp.utils.Constants.Companion.CATEGORY_PRODUCT

class CategoryAdapter(private val context: Context, private val list: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ItemCategoryLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategoryLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = list[position]
        holder.binding.apply {
            itemCategoryTextView.text = currentItem.category.toString()
            itemCategoryImageView.load(currentItem.image) {
                crossfade(600)
                placeholder(R.drawable.progress_circle)
                error(R.drawable.ic_error_place_holder)
            }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CategoryProductActivity::class.java)
            intent.putExtra(CATEGORY_PRODUCT, currentItem.category)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

}