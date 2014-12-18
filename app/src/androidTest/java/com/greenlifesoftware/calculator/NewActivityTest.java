package com.greenlifesoftware.calculator;

import com.greenlifesoftware.calculator.support.RobojavaTestRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.annotation.Config;
import static org.junit.Assert.*;

@Config(emulateSdk = 18)
@RunWith(RobojavaTestRunner.class)
public class NewActivityTest {
    private NewActivity activity;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.buildActivity(NewActivity.class).create().get();
    }

    @Test
    public void shouldNotBeNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void shouldHaveNewText() throws Exception {
        assertNotNull(activity.findViewById(R.id.new_text));

    }
}