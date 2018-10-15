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

package com.yqman.peanut

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Handler
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_navigation.*

class Navigation : BaseActivity() {

    companion object {
        val HANDLER = Handler()
    }
    private lateinit var mAnimator: ValueAnimator


    override fun initDataBeforeView() {
        super.initDataBeforeView()
        mAnimator = ValueAnimator.ofFloat(0.5f, 1f)
        mAnimator.duration = 2000
        mAnimator.addListener(LocalAnimatorListener())
        mAnimator.addUpdateListener(LocalAnimatorUpdateListener())
    }

    override fun initView() {
        super.initView()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN) // 当前Activity全屏显示
        setContentView(R.layout.activity_navigation)
        navigate_txt.alpha = 0f
        mAnimator.start()
    }


    internal inner class LocalAnimatorListener : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}

        override fun onAnimationEnd(animation: Animator) {
            HANDLER.postDelayed({
                startActivity(Intent(this@Navigation, MainActivity::class.java))
                finish()
            }, 1000)
        }

        override fun onAnimationCancel(animation: Animator) {}

        override fun onAnimationRepeat(animation: Animator) {}
    }

    internal inner class LocalAnimatorUpdateListener : ValueAnimator.AnimatorUpdateListener {
        override fun onAnimationUpdate(animation: ValueAnimator) {
            val currentValue = animation.animatedValue as Float
            if (currentValue > 0.8) {
                val data = ((0.2 - 1 + currentValue) / 0.2).toFloat()
                navigate_txt.alpha = data
            }
            navigate_img.scaleX = 1 + currentValue / 5
            navigate_img.scaleY = 1 + currentValue / 5
        }
    }
}