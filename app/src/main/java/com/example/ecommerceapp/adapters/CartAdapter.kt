package com.example.ecommerceapp.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.ecommerceapp.R
import com.example.ecommerceapp.data.local.entities.ProductModel
import com.example.ecommerceapp.data.local.viewmodel.ProductViewModel
import com.example.ecommerceapp.databinding.ItemCartLayoutBinding
import com.example.ecommerceapp.ui.activities.details.ProductDetailsActivity
import com.example.ecommerceapp.utils.Constants.Companion.PRODUCT_ID
import com.example.ecommerceapp.utils.Constants.Companion.TOTAL_COST
import com.example.ecommerceapp.utils.Constants.Companion.TOTAL_COST_VALUE


class CartAdapter(
    val context: Context,
    val list: List<ProductModel>,
    private val productViewModel: ProductViewModel
) :
    RecyclerView.Adapter<CartAdapter.СartViewHolder>() {

    class СartViewHolder(val binding: ItemCartLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): СartViewHolder {
        return СartViewHolder(
            ItemCartLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: СartViewHolder, position: Int) {
        val currentItem = list[position]
        val intent = Intent(context, ProductDetailsActivity::class.java)

        holder.binding.productImageView.load(currentItem.productImage) {
            crossfade(600)
            error(R.drawable.ic_error_place_holder)
        }

        val product = ProductModel(
            currentItem.productId,
            currentItem.productName,
            currentItem.productImage,
            currentItem.productPrice
        )
        holder.binding.apply {
            productNameTextView.text = currentItem.productName
            productPriceTextView.text = "${currentItem.productPrice} Azn"
        }

        holder.itemView.setOnClickListener {
            intent.putExtra(PRODUCT_ID, currentItem.productId)
            context.startActivity(intent)
        }

        holder.binding.cancelImageView.setOnClickListener {

            val costPreference = context.getSharedPreferences(TOTAL_COST, AppCompatActivity.MODE_PRIVATE)
            val currentPrice = currentItem.productPrice!!.toInt()
            val costEditor = costPreference.edit()
            val totalCost = costPreference.getInt(TOTAL_COST_VALUE, 0)
            costEditor.putInt(TOTAL_COST_VALUE, (totalCost - currentPrice))
            costEditor.apply()
            productViewModel.deleteData(product)

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}