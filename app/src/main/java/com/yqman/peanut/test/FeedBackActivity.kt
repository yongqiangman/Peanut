package com.yqman.peanut.test

import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.view.View
import com.yqman.peanut.BaseActivity
import com.yqman.peanut.R
import com.yqman.peanut.databinding.ActivityFeedbackBinding

/**
 * Created by manyongqiang on 2018/7/13.
 * 用户反馈，统计项
 */
class FeedBackActivity : BaseActivity(), View.OnClickListener {
    private val mResult = ObservableField<String>()
    private lateinit var mBinding: ActivityFeedbackBinding

    override fun initView() {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_feedback)
        mBinding.result = mResult
        title = "FeedBack"
    }

    override fun onClick(v: View?) {
        val sumCount = Integer.valueOf(mBinding.etSumCount.editableText.toString())
        val sumPeople = Integer.valueOf(mBinding.etSumPerson.editableText.toString())
        val feedBack = UserFeedBack(sumCount)
        feedBack.setPersonCount(sumPeople)
        mResult.set(feedBack.dump())
    }
}