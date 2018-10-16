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

package com.yqman.peanut.test

import android.databinding.DataBindingUtil
import android.databinding.ObservableField
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yqman.monitor.LogHelper
import com.yqman.peanut.R
import com.yqman.peanut.databinding.FragmentZiroomBinding
import java.lang.Exception

const val TAG  = "ZiRoomFragment"
class ZiRoomFragment: Fragment(), View.OnClickListener {

    private val mResultMsg = ObservableField<String>()
    private lateinit var mBinding: FragmentZiroomBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentZiroomBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_ziroom, container, false)
        binding.listener = this
        binding.displayMsg = mResultMsg
        mBinding = binding
        return binding.root
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