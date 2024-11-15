package com.hnidesu.taskmanager.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hnidesu.taskmanager.R
import com.hnidesu.taskmanager.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private var mActivityMainBinding: ActivityMainBinding? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater).also {
            mActivityMainBinding = it
        }
        setContentView(binding.root)
        val navController = this.findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration: AppBarConfiguration =
            AppBarConfiguration.Builder(
                setOf(
                    R.id.navigation_task_list, R.id.navigation_setting
                )
            ).build()
        this.setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavigation.setupWithNavController(navController)
    }
}
