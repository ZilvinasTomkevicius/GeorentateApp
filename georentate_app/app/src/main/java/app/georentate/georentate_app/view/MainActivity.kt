package app.georentate.georentate_app.view

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import app.georentate.georentate_app.R
import app.georentate.georentate_app.view.fragments.CheckpointListFragment
import app.georentate.georentate_app.view.fragments.MapFragment
import app.georentate.georentate_app.view.fragments.SettingsFragment
import app.georentate.georentate_app.view.fragments.UserInfoFragment

class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when(item.itemId) {
            R.id.action_map -> {
                val mapFragment = MapFragment.newInstance()
                openFragment(mapFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_userInfo -> {
                val userInfoFragment = UserInfoFragment.newInstance()
                openFragment(userInfoFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_settings -> {
                val settingsFragment = SettingsFragment.newInstance()
                openFragment(settingsFragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_checkpointList -> {
                val checkpointListFragment = CheckpointListFragment.newInstance()
                openFragment(checkpointListFragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
}
