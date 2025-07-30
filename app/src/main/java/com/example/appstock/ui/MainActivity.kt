package com.example.appstock.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appstock.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import com.example.appstock.ui.dashboard.DashboardFragment
import com.example.appstock.ui.products.ProductListFragment
import com.example.appstock.ui.sales.SalesFragment
import com.example.appstock.ui.purchases.PurchasesFragment
import com.example.appstock.ui.more.MoreFragment
import androidx.compose.material3.Typography

/**
 * MainActivity hosts the bottom navigation bar and displays the appropriate fragment
 * when a navigation item is selected.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    openFragment(DashboardFragment())
                    true
                }
                R.id.navigation_products -> {
                    openFragment(ProductListFragment())
                    true
                }
                R.id.navigation_sales -> {
                    openFragment(SalesFragment())
                    true
                }
                R.id.navigation_purchases -> {
                    openFragment(PurchasesFragment())
                    true
                }
                R.id.navigation_more -> {
                    openFragment(MoreFragment())
                    true
                }
                else -> false
            }
        }

        // Set default fragment on launch
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.navigation_dashboard
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}