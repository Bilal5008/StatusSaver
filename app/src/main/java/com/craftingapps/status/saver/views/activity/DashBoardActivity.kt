package com.craftingapps.status.saver.views.activity

import android.Manifest
import android.annotation.TargetApi
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.craftingapps.status.saver.R
import com.craftingapps.status.saver.adapter.ViewPagerAdapter
import com.craftingapps.status.saver.databinding.ActivityDashBoardBinding
import com.craftingapps.status.saver.dialogue.ShowExitDialog
import com.craftingapps.status.saver.listeners.CloseAppListener
import com.craftingapps.status.saver.views.fragment.FavoritesFragment
import com.craftingapps.status.saver.views.fragment.ImagesFragment
import com.craftingapps.status.saver.views.fragment.VideosFragment

class DashBoardActivity() : AppCompatActivity(), CloseAppListener {
    override fun closeApp() {
      finish()
    }

    override fun onBackPressed() {
        showExitDialog!!.showDialog()
    }

    var binding: ActivityDashBoardBinding? = null
    var viewPagerAdapter: ViewPagerAdapter? = null
    var showExitDialog: ShowExitDialog? = null

    private val REQUEST_EXTERNAL_STORAGE = 123


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dash_board)
        binding!!.activityDashBoard

        showExitDialog = ShowExitDialog(this, true)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        }
        setupViewPager()
        setupCustomTabs()
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
        binding!!.viewpager.setAdapter(viewPagerAdapter)
        binding!!.viewpager.setOffscreenPageLimit(3)

        // Add corresponding ViewPagers to TabLayouts

        binding!!.myTabs.setupWithViewPager(binding!!.viewpager)
    }

    private fun setupCustomTabs() {

        // Initialize New View for custom Tabs
        val tabImages = LayoutInflater.from(this@DashBoardActivity).inflate(R.layout.layout_tabs, null) as View
        val tabVideos = LayoutInflater.from(this@DashBoardActivity).inflate(R.layout.layout_tabs, null) as View
        val tabFavorites = LayoutInflater.from(this@DashBoardActivity).inflate(R.layout.layout_tabs, null) as View

        // Set Icon of custom Tab
        (tabImages.findViewById(R.id.iv_tab_icon) as ImageView).setImageResource(R.mipmap.photos)
        (tabVideos.findViewById(R.id.iv_tab_icon) as ImageView).setImageResource(R.mipmap.video)
        (tabFavorites.findViewById(R.id.iv_tab_icon) as ImageView).setImageResource(R.mipmap.love)

        // Add tabs to TabLayout at their relevant index
        binding!!.myTabs.getTabAt(0)!!.setCustomView(tabImages)
        binding!!.myTabs.getTabAt(1)!!.setCustomView(tabVideos)
        binding!!.myTabs.getTabAt(2)!!.setCustomView(tabFavorites)

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





}
