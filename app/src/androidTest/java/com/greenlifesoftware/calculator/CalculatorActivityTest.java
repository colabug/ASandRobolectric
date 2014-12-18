package com.greenlifesoftware.calculator;

import android.app.Activity;

import com.greenlifesoftware.calculator.support.RobojavaTestRunner;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@Config(emulateSdk = 18)
@RunWith(RobojavaTestRunner.class)

public class CalculatorActivityTest {
    @Test
    public void shouldNotBeNull() throws Exception {
        Activity activity = Robolectric.buildActivity(CalculatorActivity.class).create().get();
        assertNotNull(activity);
    }
}