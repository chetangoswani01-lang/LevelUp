package com.example.levelup

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigation =
            findViewById(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            openFragment(HomeFragment())
        }

        bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_home -> {
                    openFragment(HomeFragment())
                    true
                }

                R.id.nav_stats -> {
                    openFragment(StatsFragment())
                    true
                }

                R.id.nav_achievements -> {
                    openFragment(AchievementsFragment())
                    true
                }

                R.id.nav_profile -> {
                    openFragment(ProfileFragment())
                    true
                }

                else -> false
            }
        }

        bottomNavigation.selectedItemId =
            R.id.nav_home
    }

    private fun openFragment(
        fragment: Fragment
    ) {

        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                fragment
            )
            .commit()
    }
}