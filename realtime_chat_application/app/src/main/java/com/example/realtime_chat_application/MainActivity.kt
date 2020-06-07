package com.example.realtime_chat_application

import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.realtime_chat_application.ui.home.HomeFragment
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.net.URL

class MainActivity : AppCompatActivity(),HomeFragment.onFragmentListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var display_picture:ImageView
    private lateinit var profile_name:TextView
    private lateinit var profile_email:TextView

//    private var currentUser:FirebaseUser? = null
//    private var mAuth:FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

//        mAuth =FirebaseAuth.getInstance()
//        currentUser = mAuth!!.currentUser


//        val fab: Button = findViewById(R.id.send_message)
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_home, R.id.nav_gallery), drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

//    override fun profileInitializer(name: String, email: String,photoURL:Uri) {
//        val navView: NavigationView = findViewById(R.id.nav_view)
//        val headerView = navView.getHeaderView(0)
//        display_picture = headerView.findViewById(R.id.profile_photo)
//        profile_name = headerView.findViewById(R.id.profile_name)
//        profile_email= headerView.findViewById(R.id.profile_email)
//
//     profile_name.text = name
//     profile_email.text = email
//     Glide.with(this).load(photoURL).into(display_picture)
//
//    }

    override fun profileInitializer(name: String, email: String, photo: Uri?) {
        val navView: NavigationView = findViewById(R.id.nav_view)
        val headerView = navView.getHeaderView(0)
        display_picture = headerView.findViewById(R.id.profile_photo)
        profile_name = headerView.findViewById(R.id.profile_name)
        profile_email= headerView.findViewById(R.id.profile_email)
             profile_name.text = name
             profile_email.text = email
             Glide.with(this).load(photo).into(display_picture)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.sign_out -> { AuthUI.getInstance().signOut(this)
                return true}
            else -> return super.onOptionsItemSelected(item!!)
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

//    override fun setUserProfile(name:String,email:String){
//        profile_name.text = name
//        profile_email.text = email
//    }
}