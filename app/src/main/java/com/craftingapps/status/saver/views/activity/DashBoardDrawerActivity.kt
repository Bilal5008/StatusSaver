package com.craftingapps.status.saver.views.activity

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatRatingBar
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.craftingapps.status.saver.R
import com.craftingapps.status.saver.adapter.ViewPagerAdapter
import com.craftingapps.status.saver.databinding.ActivityDashBoardDrawerBinding
import com.craftingapps.status.saver.dialogue.ShowExitDialog
import com.craftingapps.status.saver.listeners.CloseAppListener
import com.craftingapps.status.saver.managers.ActivityManager
import com.craftingapps.status.saver.managers.AdsManager
import com.craftingapps.status.saver.views.fragment.FavoritesFragment
import com.craftingapps.status.saver.views.fragment.ImagesFragment
import com.craftingapps.status.saver.views.fragment.VideosFragment
import kotlinx.android.synthetic.main.activity_dash_board_drawer.*
import kotlinx.android.synthetic.main.app_bar_dash_board_drawer.*
import kotlinx.android.synthetic.main.content_dash_board_drawer.view.*

class DashBoardDrawerActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CloseAppListener {
    override fun closeApp() {
        finish()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {

            showExitDialog!!.showDialog()
        }
    }

    private val REQUEST_EXTERNAL_STORAGE = 123
    var binding: ActivityDashBoardDrawerBinding? = null
    var viewPagerAdapter: ViewPagerAdapter? = null
    var showExitDialog: ShowExitDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board_drawer)
        binding!!.activityDashBoard

        showExitDialog = ShowExitDialog(this, true)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        }

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        setupViewPager()
        setupCustomTabs()
        AdsManager.getInstance().showInterstitialAd("DashBoard Activity ads")
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    fun requestPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    val alertBuilder = AlertDialog.Builder(this)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!")
                    alertBuilder.setPositiveButton(android.R.string.yes) { dialog, which -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE) }
                    val alert = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE)
                }
                return false
            } else {
                return true
            }
        } else {
            return true
        }
    }

    private fun setupViewPager() {
        // Initialize ViewPagerAdapter with ChildFragmentManager for ViewPager
        viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        // Add the Fragments to the ViewPagerAdapter with TabHeader
        viewPagerAdapter!!.addFragment(ImagesFragment(), getString(R.string.images))
        viewPagerAdapter!!.addFragment(VideosFragment(), getString(R.string.videos))
        viewPagerAdapter!!.addFragment(FavoritesFragment(), getString(R.string.favorites))


        // Attach the ViewPagerAdapter to given ViewPager
        binding!!.drawerLayout.viewpager.setAdapter(viewPagerAdapter)
        binding!!.drawerLayout.viewpager.setOffscreenPageLimit(3)

        // Add corresponding ViewPagers to TabLayouts

        binding!!.drawerLayout.myTabs.setupWithViewPager(binding!!.drawerLayout.viewpager)
    }

    private fun setupCustomTabs() {

        // Initialize New View for custom Tabs
        val tabImages = LayoutInflater.from(this@DashBoardDrawerActivity).inflate(R.layout.layout_tabs, null) as View
        val tabVideos = LayoutInflater.from(this@DashBoardDrawerActivity).inflate(R.layout.layout_tabs, null) as View
        val tabFavorites = LayoutInflater.from(this@DashBoardDrawerActivity).inflate(R.layout.layout_tabs, null) as View

        // Set Icon of custom Tab
        (tabImages.findViewById(R.id.iv_tab_icon) as ImageView).setImageResource(R.mipmap.photos)
        (tabVideos.findViewById(R.id.iv_tab_icon) as ImageView).setImageResource(R.mipmap.video)
        (tabFavorites.findViewById(R.id.iv_tab_icon) as ImageView).setImageResource(R.mipmap.love)

        // Add tabs to TabLayout at their relevant index
        binding!!.drawerLayout.myTabs.getTabAt(0)!!.setCustomView(tabImages)
        binding!!.drawerLayout.myTabs.getTabAt(1)!!.setCustomView(tabVideos)
        binding!!.drawerLayout.myTabs.getTabAt(2)!!.setCustomView(tabFavorites)

    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.dash_board_drawer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.rate_us_layout -> {
                showRatingDialog()
                drawer_layout.closeDrawer(binding!!.navView)
            }
            R.id.feedback_layout -> {
                onFeedbackClicked()
                drawer_layout.closeDrawer(binding!!.navView)

            }
            R.id.private_policy_layout -> {
                showContextPolicy()
                drawer_layout.closeDrawer(binding!!.navView)

            }


        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_EXTERNAL_STORAGE -> if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                requestPermissionAgain()
            } else {
                setupViewPager()
                setupCustomTabs()
            }
        }
    }

    fun requestPermissionAgain() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setCancelable(false)
            alertBuilder.setTitle("Permission necessary")
            alertBuilder.setMessage("Write Storage permission is necessary to Download Images and Videos!!!")
            alertBuilder.setPositiveButton(android.R.string.yes) { dialog, which -> ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE) }
            val alert = alertBuilder.create()
            alert.show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_EXTERNAL_STORAGE)
        }
    }

    fun shareApp(shareText: String, context: Context) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, shareText)
        intent.type = "text/plain"
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(Intent.createChooser(intent, "share"))
    }

    fun onMoreAppsClicked() {
        onBackPressed()
        Handler().postDelayed({
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://developer?id=6320158268438251782")))
            } catch (anfe: android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=6320158268438251782")))
            }
        }, 500)
        //  AnalyticsManager.getInstance().sendAnalytics("More Apps", "Clicked");
    }

    fun onFeedbackClicked() {
        onBackPressed()
        Handler().postDelayed({ ActivityManager.getInstance().openNewActivity(this, FeedbackActivity::class.java, true) }, 500)
    }

    @SuppressLint("InflateParams")
    private fun showRatingDialog() {

        //  AnalyticsManager.getInstance().sendAnalytics(TAG, "Rate us clicked");
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_rating, null)
        val ratingBar = dialogView.findViewById<AppCompatRatingBar>(R.id.ratingBar)

        val dialogBuilder = AlertDialog.Builder(this)
        val alertDialog = dialogBuilder.create()
        alertDialog.setView(dialogView)
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getText(R.string.no_thanks)) { dialog, which ->

        }
        alertDialog.show()
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray))
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).transformationMethod = null

        ratingBar.setOnRatingBarChangeListener({ ratingBar1, rating, fromUser ->
            if (ratingBar1.getRating() >= 4 && ratingBar1.getRating() <= 5) {
                rateUs()
            } else {
                val feedbackDialog = dialogBuilder.create()
                feedbackDialog.setTitle(getString(R.string.uhoh))
                feedbackDialog.setMessage(getString(R.string.how_can_we_improve))
                feedbackDialog.setButton(DialogInterface.BUTTON_POSITIVE, getText(R.string.feedback)) { dialog, which -> ActivityManager.getInstance().openNewActivity(this, FeedbackActivity::class.java, true) }
                feedbackDialog.show()
                feedbackDialog.getButton(DialogInterface.BUTTON_POSITIVE).transformationMethod = null
            }
            alertDialog.dismiss()
        })
    }

    private fun showContextPolicy() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/permalink.php?story_fbid=2139847892991930&id=2139845399658846&__tn__=K-R"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    private fun rateUs() {
        var url: String
        try {
            this.getPackageManager().getPackageInfo("com.craftingapps.status.saver", 0)
            url = "market://details?id=$packageName"
        } catch (e: Exception) {
            url = "https://play.google.com/store/apps/details?id=$packageName"
        }

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET)
        startActivity(intent)
    }

}

