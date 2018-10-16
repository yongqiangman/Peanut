/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yqman.peanut.library.android.permission

import com.yqman.peanut.BaseActivity
import com.yqman.monitor.LogHelper

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