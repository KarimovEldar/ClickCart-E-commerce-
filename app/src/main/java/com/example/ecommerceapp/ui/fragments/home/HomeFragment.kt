package com.example.ecommerceapp.ui.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.ecommerceapp.R
import com.example.ecommerceapp.adapters.CategoryAdapter
import com.example.ecommerceapp.adapters.ProductAdapter
import com.example.ecommerceapp.data.network.FirebaseDb
import com.example.ecommerceapp.databinding.FragmentHomeBinding
import com.example.ecommerceapp.utils.Constants.Companion.INFO
import com.example.ecommerceapp.utils.Constants.Companion.IS_CART
import com.example.ecommerceapp.utils.Constants.Companion.USER_PHONE_NUMBER_KEY
import com.example.ecommerceapp.utils.Constants.Companion.USER_PREFERENCES_NAME
import com.example.ecommerceapp.utils.NetworkListener
import com.example.ecommerceapp.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var firebaseDb: FirebaseDb
    private var userPhoneNumber: String? = ""

    private lateinit var networkListener: NetworkListener
    private var networkStatus: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        firebaseDb = FirebaseDb(requireContext())
        val preference = requireContext().getSharedPreferences(INFO, AppCompatActivity.MODE_PRIVATE)
        val preferenceUser =
            requireContext().getSharedPreferences(
                USER_PREFERENCES_NAME,
                AppCompatActivity.MODE_PRIVATE
            )
        userPhoneNumber = preferenceUser.getString(USER_PHONE_NUMBER_KEY, "")
        networkListener = NetworkListener()

        lifecycleScope.launchWhenStarted {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext())
                .collect { status ->
                    if (status) {
                        requireContext().showToast("Back online")
                        getUser()
                        getCategory()
                        getSliderImg()
                        getProducts()
                    }
                    else{
                        requireContext().showToast("No internet connection!")
                    }
                    networkStatus = status
                }
        }

        if (preference.getBoolean(IS_CART, false)) {
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        }

        if(networkStatus){
            binding.categorySeeAllTextView.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_categoryActivity)
            }

            binding.productSeeAllTextView.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_productActivity)
            }
        }

        return binding.root
    }

    private fun getUser() {

        if (userPhoneNumber != null && userPhoneNumber!!.isNotEmpty()) {
            firebaseDb.getUser(userPhoneNumber!!) { userName ->
                if (userName != null) {
                    binding.userNameTextViewInHomeFragment.text = userName
                } else {
                    binding.userNameTextViewInHomeFragment.text = "User Name"
                }
            }
        }

    }

    private fun getSliderImg() {

        firebaseDb.getSliderImages { sliderImages ->
            val slideList = ArrayList<SlideModel>()
            for (data in sliderImages) {
                slideList.add(SlideModel(data, ScaleTypes.FIT))
            }
            binding.sliderImageView.setImageList(slideList)
        }

    }

    private fun getProducts() {

        firebaseDb.getProducts { productsList ->
            val productArrayList = ArrayList(productsList)
            binding.recyclerProduct.adapter = ProductAdapter(requireContext(), productArrayList)
        }

    }

    private fun getCategory() {

        firebaseDb.getCategory { categoryList ->
            val categoryArrayList = ArrayList(categoryList)
            binding.recyclerCategory.adapter = CategoryAdapter(requireContext(), categoryArrayList)
        }

    }

    /*

    private fun showShimmerEffect(){
        binding.shimmerLayout.startShimmer()
        binding.shimmerLayout.visibility = View.VISIBLE
    }

    private fun hideShimmerEffect(){
        binding.shimmerLayout.stopShimmer()
        binding.shimmerLayout.visibility = View.INVISIBLE
    }

     */

}