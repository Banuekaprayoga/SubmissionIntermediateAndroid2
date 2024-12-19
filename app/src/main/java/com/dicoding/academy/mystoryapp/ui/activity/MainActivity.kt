package com.dicoding.academy.mystoryapp.ui.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.academy.mystoryapp.R
import com.dicoding.academy.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.academy.mystoryapp.ui.viewmodel.ViewModelFactory
import com.dicoding.academy.mystoryapp.ui.adapter.LoadingStateAdapter
import com.dicoding.academy.mystoryapp.ui.adapter.StoryAdapter
import com.dicoding.academy.mystoryapp.ui.viewmodel.MainViewModel
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var adapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                Log.d(TAG, "onCreate: Login Failed")
            }
        }
        setupView()
        setupRecyclerView()
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
        }
    }

private fun setupRecyclerView() {
    adapter = StoryAdapter()
    binding.rvStory.layoutManager = LinearLayoutManager(this)
    binding.rvStory.adapter = adapter.withLoadStateFooter(
        footer = LoadingStateAdapter {
            adapter.retry()
        }
    )
    val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
    binding.rvStory.addItemDecoration(itemDecoration)
    viewModel.story.observe(this) { result ->
        adapter.submitData(lifecycle, result)
    }
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logoutMenu -> { viewModel.logout() }
            R.id.mapsMenu -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}