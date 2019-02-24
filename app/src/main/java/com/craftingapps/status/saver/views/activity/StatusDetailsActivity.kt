package com.craftingapps.status.saver.views.activity

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide

import com.craftingapps.status.saver.R
import com.craftingapps.status.saver.databinding.ActivityStatusDetailsBinding
import com.craftingapps.status.saver.managers.AdsManager
import com.craftingapps.status.saver.models.StatusModel
import com.craftingapps.status.saver.models.UpdateEvent
import com.craftingapps.status.saver.utils.Utils

import org.greenrobot.eventbus.EventBus


class StatusDetailsActivity : AppCompatActivity() {
    internal var binding: ActivityStatusDetailsBinding? = null

    internal var statusDetails: StatusModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_status_details)
        binding!!.activity = this

        statusDetails = intent.getParcelableExtra("statusInfo")

        binding!!.ivPlayBtn.visibility = if (statusDetails!!.isVideo) View.VISIBLE else View.GONE

        Glide.with(this@StatusDetailsActivity)
                .load(statusDetails!!.uri)
                .into(binding!!.ivStatusCover)


        binding!!.fabMenu.setOnMenuToggleListener { }

        binding!!.fabMenu.setClosedOnTouchOutside(true)
        AdsManager.getInstance().showAdMobLargeBanner(binding!!.detailsAdds)
        binding!!.fabMenu.isIconAnimated = true

    }


    fun onClick(view: View) {
        when (view.id) {

            R.id.iv_play_btn -> {
                Utils.getInstance().playVideo(this, statusDetails!!.path)
            }


            R.id.fab_save -> {
                Utils.getInstance().saveMedia(this, statusDetails!!.filename, statusDetails!!.path)
                EventBus.getDefault().post(UpdateEvent(true))
                AdsManager.getInstance().showInterstitialAd("Save Activity ads")
            }

            R.id.fab_share ->
                // FirebaseAnalyticsLogs.getInstance().logEvent(StatusDetailsActivity.this, 17, "shareStatusBtn")
                if (statusDetails!!.isVideo) {
                    Utils.getInstance().shareVideo(this, statusDetails!!.path, statusDetails!!.filename)
                    AdsManager.getInstance().showInterstitialAd("Share Activity ads")
                } else {
                    Utils.getInstance().shareImage(this, statusDetails!!.path, statusDetails!!.filename)
                    AdsManager.getInstance().showInterstitialAd("Share Activity ads")
                }


            R.id.fab_delete -> {
                //    FirebaseAnalyticsLogs.getInstance().logEvent(StatusDetailsActivity.this, 18, "deleteStatusBtn");
                Utils.getInstance().deleteMedia(this, statusDetails!!.path)
                EventBus.getDefault().post(UpdateEvent(true))
                // AdsManager.getInstance(StatusDetailsActivity.this).showInterstitial();
                this.finish()
            }
        }

    }

    companion object {

        private val TAG = StatusDetailsActivity::class.java.javaClass.simpleName
    }

}

