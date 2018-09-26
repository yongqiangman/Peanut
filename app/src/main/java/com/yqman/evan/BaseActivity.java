package com.yqman.evan;

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
