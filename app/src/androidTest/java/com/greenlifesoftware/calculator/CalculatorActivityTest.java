package com.greenlifesoftware.calculator;

import com.greenlifesoftware.calculator.support.RobolectricGradleTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@Config(emulateSdk = 18)
@RunWith(RobolectricGradleTestRunner.class)

public class CalculatorActivityTest {

    private CalculatorActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(CalculatorActivity.class).create().get();
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void shouldHaveWelcomeText() throws Exception {
        assertNotNull(activity.findViewById(R.id.welcome_text));

    }
}
