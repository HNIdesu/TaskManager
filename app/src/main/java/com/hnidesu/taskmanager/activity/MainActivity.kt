package com.hnidesu.taskmanager.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hnidesu.taskmanager.R


class MainActivity : AppCompatActivity() {
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navController = this.findNavController(R.id.nav_host_fragment_activity_main)

        val appBarConfiguration: AppBarConfiguration =
            AppBarConfiguration.Builder(setOf(
                R.id.navigation_home, R.id.navigation_task_list, R.id.navigation_setting
            )).build()
        this.setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}
