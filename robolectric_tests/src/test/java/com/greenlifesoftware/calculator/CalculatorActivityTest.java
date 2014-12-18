package com.greenlifesoftware.calculator;

import com.greenlifesoftware.support.RobolectricGradleTestRunner;
import com.greenlifesoftware.support.ShadowSupportMenuInflater;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(emulateSdk = 18, shadows = {ShadowSupportMenuInflater.class})

//@Config(emulateSdk = 18, reportSdk = 18)
public class CalculatorActivityTest {

    @Test
    public void shouldNotBeNull() throws Exception {
        CalculatorActivity calculatorActivity = Robolectric.buildActivity(CalculatorActivity.class).create().resume().get();
        assertNotNull(calculatorActivity);
    }
}
