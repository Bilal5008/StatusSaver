package com.craftingapps.status.saver.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

import java.util.ArrayList


class ViewPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    private val fragmentList = ArrayList<Fragment>()
    private val fragmentTitleList = ArrayList<String>()


    //********** Returns the Fragment associated with a specified Position *********//

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }


    //********** Returns the number of Views available *********//

    override fun getCount(): Int {
        return fragmentList.size
    }


    //********** Called by the ViewPager to obtain the Title of specified Page *********//

    override fun getPageTitle(position: Int): CharSequence? {
        return fragmentTitleList[position]
    }


    //********** Adds the new Fragment to the ViewPager *********//

    fun addFragment(fragment: Fragment, title: String) {
        // Add the given Fragment to FragmentList
        fragmentList.add(fragment)

        // Add the Title of a given Fragment to FragmentTitleList
        fragmentTitleList.add(title)
    }

}

