package com.dicoding.academy.mystoryapp.ui.activity

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.academy.mystoryapp.R
import com.dicoding.academy.mystoryapp.data.Result
import com.dicoding.academy.mystoryapp.databinding.ActivityDetailBinding
import com.dicoding.academy.mystoryapp.ui.viewmodel.MainViewModel
import com.dicoding.academy.mystoryapp.ui.viewmodel.ViewModelFactory
import com.dicoding.academy.mystoryapp.utils.DateFormatter

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val storyId = intent.getStringExtra(DETAIL_STORY)
        if (storyId != null) {
            observeViewModel(storyId)
        } else {
            showToast(getString(R.string.id_not_found))
        }
        setupView()
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
    private fun observeViewModel(id: String) {
        viewModel.getDetailStory(id).observe(this) { result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> showLoading(true)
                    is Result.Success -> {
                        showLoading(false)
                        val detailData = result.data
                        binding.apply {
                            textNameView.text = detailData.name
                            textDescView.text = detailData.description
                            dateTextView.text = DateFormatter.formatDate(detailData.createdAt)
                            Log.d(TAG, "${dateTextView.text}")
                        }
                        Glide.with(this)
                            .load(detailData.photoUrl)
                            .apply(RequestOptions.placeholderOf(R.drawable.ic_loading).error(R.drawable.ic_error))
                            .into(binding.imgDetail)
                    }
                    is Result.Error -> {
                        showLoading(false)
                        showToast(result.error)
                    }
                }
            }
        }
    }
    private fun showToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading)  View.VISIBLE else View.GONE
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val DETAIL_STORY = "detail_story"
        private const val TAG = "DetailActivity"
    }
}