package com.yqman.peanut

import android.content.Intent
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import com.yqman.monitor.LogHelper
import com.yqman.peanut.home.HomeFragment
import com.yqman.peanut.test.*
import com.yqman.peanut.cloudfile.DatabaseFragment
import com.yqman.peanut.test.storage.StorageFragment
import com.yqman.wdiget.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.title_bar_normal.*

/**
 * Created by manyongqiang on 2018/7/13.
 * 主页
 */

class MainActivity : BaseActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private var mCurrentSelectedItemId: Int = -1
    private var mCurrentFragment: Fragment? = null
    private var mSwitchFragmentCount: Int = 0
    private lateinit var mSwitchFragmentCountDisplayView: TextView

    override fun initView() {
        setContentView(R.layout.activity_main)
        // enable write permission
        // PermissionPresenter.checkWritePermission(this)
        initNavigation()
        title_bar_normal_tv.text = resources.getString(R.string.app_name)
        title_bar_normal_left_img.visibility = View.VISIBLE
        title_bar_normal_left_img.setImageResource(R.drawable.icon_navigation_menu)
        title_bar_normal_left_img.setOnClickListener {
            drawer_layout.openDrawer(GravityCompat.START)
        }
        switchFragment(HomeFragment(), "${R.id.drawer_menu_home}")
    }

    private fun initNavigation() {
        drawer_navigation.setNavigationItemSelectedListener {
            it.isChecked = true
            title_bar_normal_tv.text = it.title
            handleItemClicked(it.itemId)
            drawer_layout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }
        val menuItem = drawer_navigation.menu.findItem(R.id.drawer_menu_contact)
        menuItem.actionView.apply {
            if (this is TextView) {
                mSwitchFragmentCountDisplayView = this
            }
        }
        val headView = drawer_navigation.getHeaderView(0)
        headView.setBackgroundResource(R.drawable.img_navigation_head)
        val circleImageView = headView.findViewById(R.id.drawer_navigation_header_imageView) as CircleImageView
        val textView = headView.findViewById(R.id.drawer_navigation_header_textView) as TextView
        circleImageView.setImageResource(R.drawable.img_profile_head)
        textView.text = resources.getString(R.string.app_name)
    }

    private fun handleItemClicked(itemId: Int) {
        val tag = "$itemId"
        title_bar_normal.visibility = View.VISIBLE
        when(itemId) {
            R.id.drawer_menu_home -> {
                val fragment = supportFragmentManager.findFragmentByTag(tag)
                if (fragment != null) {
                    switchFragment(fragment, tag)
                } else {
                    switchFragment(HomeFragment(), tag)
                }
            }
            R.id.drawer_menu_test_ziRoom -> {
                val fragment = supportFragmentManager.findFragmentByTag(tag)
                if (fragment != null) {
                    switchFragment(fragment, tag)
                } else {
                    switchFragment(ZiRoomFragment(), tag)
                }
            }
            R.id.drawer_menu_test_storage -> {
                val fragment = supportFragmentManager.findFragmentByTag(tag)
                if (fragment != null) {
                    switchFragment(fragment, tag)
                } else {
                    switchFragment(StorageFragment(), tag)
                }
            }
            R.id.drawer_menu_test_database -> {
                val fragment = supportFragmentManager.findFragmentByTag(tag)
                if (fragment != null) {
                    switchFragment(fragment, tag)
                } else {
                    switchFragment(DatabaseFragment(), tag)
                }
            }
            R.id.drawer_menu_test_clock -> {
                val fragment = supportFragmentManager.findFragmentByTag(tag)
                if (fragment != null) {
                    switchFragment(fragment, tag)
                } else {
                    switchFragment(ClockFragment(), tag)
                }
            }
            R.id.drawer_menu_test_draw -> {
                title_bar_normal.visibility = View.GONE
                val fragment = supportFragmentManager.findFragmentByTag(tag)
                if (fragment != null) {
                    switchFragment(fragment, tag)
                } else {
                    switchFragment(DrawFragment(), tag)
                }
            }
            R.id.drawer_menu_test_webView -> {
                title_bar_normal.visibility = View.GONE
                val fragment = supportFragmentManager.findFragmentByTag(tag)
                if (fragment != null) {
                    switchFragment(fragment, tag)
                } else {
                    switchFragment(WebViewFragment(), tag)
                }
            }
            R.id.drawer_menu_share -> {
                shareApp()
            }
            R.id.drawer_menu_contact -> {
                contactUs()
            }
            else -> {
                LogHelper.d(TAG, "can not handle id $itemId")
            }
        }
        mSwitchFragmentCount++
        mSwitchFragmentCountDisplayView.text = "$mSwitchFragmentCount"
        mCurrentSelectedItemId = itemId
    }

    /**
     * 共享一个URL
     */
    private fun shareApp() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Share Url")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://blog.csdn.net/evan_man")
        //从屏幕底部弹出一个选择菜单，标题为Share
        startActivity(Intent.createChooser(shareIntent, "Share"))
    }

    private fun contactUs() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "message/rfc822"
        shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("evanman0618@gmaill.com"))
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "subject of email")
        shareIntent.putExtra(Intent.EXTRA_TEXT, "body of email")
        //从屏幕底部弹出一个选择菜单，标题为Share
        startActivity(Intent.createChooser(shareIntent, "Contact"))
    }

    private fun switchFragment(targetFragment: Fragment, tag: String) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        mCurrentFragment?.apply {
            fragmentTransaction.hide(this)
        }
        if (targetFragment.isAdded) {
            fragmentTransaction.show(targetFragment).commit()
        } else {
            fragmentTransaction.add(R.id.drawer_container, targetFragment, tag).commit()
        }
        mCurrentFragment = targetFragment
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val fragment = mCurrentFragment
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fragment is StorageFragment) {
                if (fragment.onKeyBack()) {
                    return true
                }
            } else if (fragment is WebViewFragment) {
                if (fragment.onKeyBack()) {
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}