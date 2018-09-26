package com.yqman.evan.activity

import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.view.View
import com.yqman.evan.BaseActivity
import com.yqman.evan.R
import com.yqman.evan.databinding.ActivityZiroomBinding
import com.yqman.monitor.LogHelper
import java.lang.Exception

/**
 * Created by manyongqiang on 2018/7/13.
 *
 */
class ZiRoomActivity : BaseActivity(), View.OnClickListener {
    companion object {
        const val TAG = "ZiRoomActivity"
    }

    private lateinit var mBinding: ActivityZiroomBinding
    private val mResultMsg = ObservableField<String>()

    override fun initView() {
        title = "ZiRoom"
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_ziroom)
        mBinding.displayMsg = mResultMsg
    }

    override fun onClick(v: View?) {
        try {
            computeNewPrice()
        } catch (e: Exception) {
            LogHelper.e(TAG, "err", e)
        }
    }

    private fun computeNewPrice() {
        val price = Integer.valueOf(mBinding.editText.text.toString())
        val resultBuilder = StringBuilder()
        val tradition = ZiRoom(price, true)
        resultBuilder.append("--------一次付清-----------").append('\n')
        resultBuilder.append("year Price:").append(tradition.getYearPrice()).append('\n')
        resultBuilder.append("year month price:").append(tradition.getYearMonthPrice()).append('\n')
        resultBuilder.append("--------押一付三----------").append('\n')
        resultBuilder.append("first price:").append(tradition.getFirstPrice()).append('\n')
        resultBuilder.append("month price:").append(tradition.getNormalPrice()).append('\n')
        val fashion = ZiRoom(price, false)
        resultBuilder.append("--------分期-----------").append('\n')
        resultBuilder.append("divide first price:").append(fashion.getFirstPrice()).append('\n')
        resultBuilder.append("divide month price:").append(fashion.getNormalPrice()).append('\n')

        tradition.mBigCompany = true
        resultBuilder.append('\n')
        resultBuilder.append('\n')
        resultBuilder.append('\n')
        resultBuilder.append("--------大客户-----------").append('\n')
        resultBuilder.append("--------一次付清-----------").append('\n')
        resultBuilder.append("year Price:").append(tradition.getYearPrice()).append('\n')
        resultBuilder.append("year month price:").append(tradition.getYearMonthPrice()).append('\n')
        resultBuilder.append("--------押一付三-----------").append('\n')
        resultBuilder.append("first price:").append(tradition.getFirstPrice()).append('\n')
        resultBuilder.append("month price:").append(tradition.getNormalPrice()).append('\n')
        fashion.mBigCompany = true
        resultBuilder.append("--------分期----------").append('\n')
        resultBuilder.append("divide first price:").append(fashion.getFirstPrice()).append('\n')
        resultBuilder.append("divide month price:").append(fashion.getNormalPrice()).append('\n')
        mResultMsg.set(resultBuilder.toString())
    }

    class ZiRoom(private val mPrice: Int, private val mIsTraditional: Boolean) {
        var mBigCompany = false

        fun getFirstPrice(): Float {
            return if (mIsTraditional) mPrice * 4 + getServicePrice()
            else mPrice * 2 + getServicePrice() / 12
        }

        fun getNormalPrice(): Float {
            return if (mIsTraditional) mPrice + getServicePrice() / 12
            else (mPrice + getServicePrice() / 12) * (1 + 0.0627f)
        }

        fun getYearPrice(): Float {
            return if (mIsTraditional) mPrice * 12 + mPrice * 1.2f * 0.7f * 0.88f
            else mPrice * 12 + mPrice * 1.2f * 0.7f
        }

        fun getYearMonthPrice() = getYearPrice() / 12

        private fun getServicePrice(): Float {
            val service = if (mIsTraditional) mPrice * 1.2f else mPrice * 1.2f * 0.7f
            if (mBigCompany) service * 0.88f
            return service
        }
    }
}