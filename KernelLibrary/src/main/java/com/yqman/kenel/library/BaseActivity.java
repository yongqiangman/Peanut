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

package com.yqman.kenel.library;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by manyongqiang on 2017/12/3.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static ArrayList<BaseActivity> mActivities = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataBeforeView();
        initView();
        mActivities.add(this);
    }

    protected void initDataBeforeView() {

    }

    protected void initView() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivities.remove(this);
    }

    protected final BaseActivity getTopBaseActivity() {
        if (mActivities != null && !mActivities.isEmpty()) {
            for (int index = mActivities.size() - 1; index >= 0; --index) {
                BaseActivity activity = mActivities.get(index);
                if (!activity.isFinishing()) {
                    return activity;
                }
            }
        }
        return null;
    }
}
