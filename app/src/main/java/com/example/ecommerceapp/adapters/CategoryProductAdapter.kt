package com.example.ecommerceapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ecommerceapp.R
import com.example.ecommerceapp.databinding.ItemCategoryProductLayoutBinding
import com.example.ecommerceapp.model.AddProductModel
import com.example.ecommerceapp.ui.activities.details.ProductDetailsActivity
import com.example.ecommerceapp.utils.Constants.Companion.PRODUCT_ID

class CategoryProductAdapter(val context: Context, val list: ArrayList<AddProductModel>) :
    RecyclerView.Adapter<CategoryProductAdapter.CategoryProductViewHolder>() {

    class CategoryProductViewHolder(val binding: ItemCategoryProductLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewHolder {
        return CategoryProductViewHolder(
            ItemCategoryProductLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoryProductViewHolder, position: Int) {
        val currentItem = list[position]
        holder.binding.apply {
            categoryImageView.load(currentItem.productCoverImage) {
                crossfade(600)
                placeholder(R.drawable.progress_circle)
                error(R.drawable.ic_error_place_holder)
            }

            categoryProductNameTextView.text = currentItem.productName
            categoryProductPriceTextView.text = "${currentItem.productPrice} Azn"
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ProductDetailsActivity::class.java)
            intent.putExtra(PRODUCT_ID, currentItem.productId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}