package com.yqman.evan.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

@RunWith(AndroidJUnit4.class)
public class ThreadInstrTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void finish() {
        ThreadRunTest.getInstance().finish();
        System.out.println("test run finished!!");
    }

    @Test
    public void getTag() {
    }
}