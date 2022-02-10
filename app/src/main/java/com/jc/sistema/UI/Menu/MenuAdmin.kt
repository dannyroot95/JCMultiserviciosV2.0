package com.jc.sistema.UI.Menu

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.jc.sistema.Providers.AuthenticationProvider
import com.jc.sistema.Providers.TokenProvider
import com.jc.sistema.R
import com.jc.sistema.UI.LoginActivity
import com.jc.sistema.Utils.Constants

class MenuAdmin : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var mAuth : AuthenticationProvider = AuthenticationProvider()
    private var token : TokenProvider = TokenProvider()
    private var mEmail : String = ""
    private var mName : String = ""
    private var mTypeUser : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val preferenceEmail : SharedPreferences = getSharedPreferences(Constants.EMAIL,MODE_PRIVATE)
        mEmail = preferenceEmail.getString(Constants.KEY_EMAIL,"").toString()

        val preferenceName : SharedPreferences = getSharedPreferences(Constants.USERNAME,MODE_PRIVATE)
        mName = preferenceName.getString(Constants.USERNAME,"").toString()

        val preferenceUser : SharedPreferences = getSharedPreferences(Constants.TYPE_USER,MODE_PRIVATE)
        mTypeUser = preferenceUser.getString(Constants.KEY_TYPE_USER,"").toString()

        //val fab: FloatingActionButton = findViewById(R.id.fab)
       /* fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }*/

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val nav : Menu = navView.menu
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_products, R.id.nav_users,R.id.nav_orders, R.id.nav_sales
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (mTypeUser == Constants.SUPER_USER){
            nav.findItem(R.id.nav_orders).isVisible = false
        }
        else if (mTypeUser == Constants.VENDOR){
            navController.navigate(R.id.nav_orders)
            nav.findItem(R.id.nav_products).isVisible = false
            nav.findItem(R.id.nav_users).isVisible = false
        }

        val headerView : View = navView.getHeaderView(0)
        val navUsername : TextView = headerView.findViewById(R.id.name)
        val navUserEmail : TextView = headerView.findViewById(R.id.email)

        navUsername.text = mName
        navUserEmail.text = mEmail

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_logout -> {
                token.deleteToken(mAuth.getId())
                mAuth.logout()
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onBackPressed() {
    }

}