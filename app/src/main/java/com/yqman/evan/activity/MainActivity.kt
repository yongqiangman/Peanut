package com.yqman.evan.activity

import android.content.Intent
import android.databinding.DataBindingUtil
import android.view.View
import android.widget.AdapterView
import com.yqman.evan.BaseActivity
import com.yqman.evan.R
import com.yqman.evan.databinding.ActivityMainBinding
import com.yqman.monitor.LogHelper
import com.yqman.evan.presenter.PermissionPresenter
import java.lang.Exception

/**
 * Created by manyongqiang on 2018/7/13.
 * 主页
 */

class MainActivity : BaseActivity(), View.OnClickListener {

    companion object {
        const val TAG = "MainActivity"
    }

    lateinit var mFrowardClassName: String
    lateinit var binding: ActivityMainBinding

    override fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main) as ActivityMainBinding
        binding.forwardActivityName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mFrowardClassName = resources.getStringArray(R.array.className)[position]
            }
        }
        // enable write permission
        PermissionPresenter.checkWritePermission(this)
    }

    private fun forwardActivity() {
        try {
            val zClass = Class.forName(mFrowardClassName)
            val intent = Intent(this, zClass)
            startActivity(intent)
        } catch (e: Exception) {
            LogHelper.e(TAG, "error", e)
        }
    }

    override fun onClick(p0: View?) {
        forwardActivity()
    }
}