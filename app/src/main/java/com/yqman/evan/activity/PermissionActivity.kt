package com.yqman.evan.activity

import com.yqman.evan.BaseActivity
import com.yqman.monitor.LogHelper
import com.yqman.evan.presenter.PermissionPresenter

/**
 * Created by manyongqiang on 2018/7/13.
 * 权限申请界面
 */
class PermissionActivity : BaseActivity() {

    override fun initView() {
        val mPermissionPresenter = PermissionPresenter()
        if (!mPermissionPresenter.hasAcquireWritePermission(this)) {
            mPermissionPresenter.requestWritePermission(this, 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (index in permissions.indices) {
            LogHelper.d("PermissionActivity",
                    "申请的权限为: ${permissions[index]},申请结果：${grantResults[index]}")
        }
        finish()
    }
}