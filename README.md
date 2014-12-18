# Robolectric with Android Studio 1.0

Using these [instructions](http://blog.blundell-apps.com/android-gradle-app-with-robolectric-junit-tests/) to manually configure the a new application (created with the wizard).

## Create module

> Note: The module view changed, screen shot captured.

**TODO: Insert pictures**

Module created `src/main` instead of `src/test`. Android Studio shows `tests` - so weird!

```
FAILURE: Build failed with an exception.

* What went wrong:
Could not resolve all dependencies for configuration ':robolectric_tests:testCompile'.
> Could not find com.android.support:support-v4:19.0.1.
  Searched in the following locations:
      https://jcenter.bintray.com/com/android/support/support-v4/19.0.1/support-v4-19.0.1.pom
      https://jcenter.bintray.com/com/android/support/support-v4/19.0.1/support-v4-19.0.1.jar
  Required by:
      Calculator:robolectric_tests:unspecified > org.robolectric:robolectric:2.3
```

## Update Dependencies

Need to update the dependencies to exclude this library:

```java
    testCompile('org.robolectric:robolectric:2.3') {
        exclude module: 'support-v4'
    }
```

## Error: `"superClassName is empty"`

Another failure!

```
com.greenlifesoftware.calculator.CalculatorActivityTest > shouldNotBeNull FAILED
    java.lang.VerifyError at CalculatorActivityTest.java:16

1 test completed, 1 failed
:robolectric_tests:test FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':robolectric_tests:test'.
> superClassName is empty!
```

Fix:

```java
// Prevents the "superClassName is empty" error for classes not annotated as tests
tasks.withType(Test) {
    scanForTestClasses = false
    include "**/*Test.class"
}
```

## Error: `Expecting a stackmap frame`

More errors!

```
java.lang.VerifyError: Expecting a stackmap frame at branch target 47
Exception Details:
  Location:
    android/support/v7/app/ActionBarActivity.<init>()V @19: ifnull
  Reason:
    Expected stackmap frame at this location.
  Bytecode:
    0000000: 2ab7 018a 2ab6 018d 1301 8f03 1202 b801
    0000010: 954c 2bc6 001c 2b2a 2ab6 0199 03bd 019b
    0000020: b901 a104 0057 a700 144d 2cb8 01a5 bf2a
    0000030: b701 85b1 4d2c b801 a5bf b1            
  Exception Handler Table:
    bci [22, 38] => handler: 41
    bci [47, 51] => handler: 52

    at java.lang.Class.getDeclaredConstructors0(Native Method)
    at java.lang.Class.privateGetDeclaredConstructors(Class.java:2532)
    at java.lang.Class.getConstructor0(Class.java:2842)
    at java.lang.Class.getDeclaredConstructor(Class.java:2053)
    at org.fest.reflect.constructor.Invoker.constructor(Invoker.java:54)
    ...
```

Found possible [solution](https://github.com/robolectric/robolectric/issues/1332). Gives me [new error](https://github.com/robolectric/robolectric/issues/979):

```
tasks.withType(Test) {
    scanForTestClasses = false
    include "**/*Test.class"

    test {
        // set JVM arguments for the test JVM(s)
        jvmArgs '-XX:-UseSplitVerifier'
    }
}
```

## Latest Robolectric

Updgraded to 2.4 and aded maven central to the build repository options.

New error!

```
java.lang.RuntimeException: Could not find any resource  from reference ResName{com.greenlifesoftware.calculator:style/Theme_AppCompat_Light_DarkActionBar} from style StyleData{name='AppTheme', parent='Theme_AppCompat_Light_DarkActionBar'} with theme null
    at org.robolectric.shadows.ShadowAssetManager$StyleResolver.getParent(ShadowAssetManager.java:456)
    at org.robolectric.shadows.ShadowAssetManager$StyleResolver.getAttrValue(ShadowAssetManager.java:394)
    at org.robolectric.shadows.ShadowResources.getOverlayedThemeValue(ShadowResources.java:294)
    at org.robolectric.shadows.ShadowResources.findAttributeValue(ShadowResources.java:283)
    at org.robolectric.shadows.ShadowResources.attrsToTypedArray(ShadowResources.java:186)
    at org.robolectric.shadows.ShadowResources.access$000(ShadowResources.java:44)
    at org.robolectric.shadows.ShadowResources$ShadowTheme.obtainStyledAttributes(ShadowResources.java:491)
    at org.robolectric.shadows.ShadowResources$ShadowTheme.obtainStyledAttributes(ShadowResources.java:486)
    at org.robolectric.shadows.ShadowResources$ShadowTheme.obtainStyledAttributes(ShadowResources.java:481)
    at android.content.res.Resources$Theme.obtainStyledAttributes(Resources.java)
    at android.content.Context.obtainStyledAttributes(Context.java:380)
    at android.support.v7.app.ActionBarActivityDelegate.onCreate(ActionBarActivityDelegate.java:147)
    at android.support.v7.app.ActionBarActivityDelegateBase.onCreate(ActionBarActivityDelegateBase.java:138)
    at android.support.v7.app.ActionBarActivity.onCreate(ActionBarActivity.java:123)
    at com.greenlifesoftware.calculator.CalculatorActivity.onCreate(CalculatorActivity.java:13)
    at android.app.Activity.performCreate(Activity.java:5133)
```

Back to this [article](https://github.com/robolectric/robolectric/issues/1332)

## Max SDK 18

Added emulation:

```java
@RunWith(RobolectricGradleTestRunner.class)
@Config(emulateSdk = 18, reportsSdk = 18)
public class CalculatorActivityTest {

    @Test
    public void shouldNotBeNull() throws Exception {
        CalculatorActivity calculatorActivity = Robolectric.buildActivity(CalculatorActivity.class).create().resume().get();
        assertNotNull(calculatorActivity);
    }
}
```

Got this error:

```
cannot find symbol
@Config(emulateSdk = 18, reportsSdk = 18)
                         ^
  symbol:   method reportsSdk()
  location: @interface Config
```

Turns out there was a typo in the recommendation that I copied. Should be:

```java
@Config(emulateSdk = 18, reportSdk = 18)
```

Same error!

Changed target SDK version to 18.

```
defaultConfig {
    applicationId "com.greenlifesoftware.calculator"
    minSdkVersion 12
    targetSdkVersion 18
    versionCode 1
    versionName "1.0"
}
```

## `ShadowMenuInflater`

Trying this workaround suggestion:

Use a shadow.

```java
  @RunWith(RobolectricTestRunner.class)
  @Config(emulateSdk = 18, shadows = {ShadowSupportMenuInflater.class})
  public class MainActivityTest {…}
```

`SupportMenuInflater.java`

```java
  import org.robolectric.annotation.Implementation;
  import org.robolectric.annotation.Implements;

  import android.support.v7.internal.view.SupportMenuInflater;
  import android.view.Menu;
  import org.robolectric.shadows.ShadowMenuInflater;

  @Implements(SupportMenuInflater.class)
  public class ShadowSupportMenuInflater extends ShadowMenuInflater {
      @Implementation
      public void inflate(int menuRes, Menu menu) {
          super.inflate(menuRes, menu);
      }
  }
```

Didn't work.

## Hacks

Removed the theme in the `styles.xml` file and got more issues. Take a nap.

## Test Runer

Added a basic robolectric test runner.

```
package com.greenlifesoftware.support;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;


public class RobolectricGradleTestRunner extends RobolectricTestRunner {
    private static final int MAX_SDK_SUPPORTED_BY_ROBOLECTRIC = 18;

    public RobolectricGradleTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config) {
        String manifestProperty = "../app/src/main/AndroidManifest.xml";
        String resProperty = "../app/src/main/res";
        return new AndroidManifest(Fs.fileFromPath(manifestProperty), Fs.fileFromPath(resProperty)) {
            @Override
            public int getTargetSdkVersion() {
                return MAX_SDK_SUPPORTED_BY_ROBOLECTRIC;
            }
        };
    }
}
```

Used in my test class:

```java
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
```

## Plugin Test Runner

Used test runner from the plugin instead:

```
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.res.Fs;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.IOException;

/**
 * Custom test runner which is needed if your tests need resources etc.
 */
public class RobojavaTestRunner extends RobolectricTestRunner {

    private static final String PROJECT_DIR = getProjectDirectory();
    private static final String MANIFEST_PROPERTY = PROJECT_DIR + "src/main/AndroidManifest.xml";
    private static final String RES_PROPERTY = PROJECT_DIR + "build/intermediates/res/debug/";
    private static final int TARGET_SDK_VERSION = 18;
    private static final AndroidManifest sAndroidManifest = getAndroidManifest();

    public RobojavaTestRunner(final Class<?> testClass) throws Exception {
        super(testClass);
    }

    @Override
    public AndroidManifest getAppManifest(Config config) {
        return sAndroidManifest;
    }

    private static AndroidManifest getAndroidManifest() {
        return new AndroidManifest(Fs.fileFromPath(MANIFEST_PROPERTY), Fs.fileFromPath(RES_PROPERTY)) {
            @Override
            public int getTargetSdkVersion() {
                return TARGET_SDK_VERSION;
            }
        };
    }

    /**
     * Unfortunately this step is required so that tests can run both from ide and the commandline.
     * Robolectric has difficulty recognizing the manifest file from relative paths.
     *
     * @return The working directory from which tests are run.
     */
    private static String getProjectDirectory() {
        String path = "";
        try {
            File file = new File(".");
            path = file.getCanonicalPath();
            path = path.replace("tdd", ""); //name of stub project
            path = path + "/app/"; //name of android project
        } catch (IOException ignored) {
        }
        return path;
    }
}
```

Gives different error:

```
java.lang.RuntimeException: java.lang.RuntimeException: /Users/colabug/AndroidStudioProjects/Calculator/robolectric_tests/app/src/main/AndroidManifest.xml not found or not a file; it should point to your project's AndroidManifest.xml
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:226)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
...
```

This runner may need to be hacked to change the code paths since the layout is different.

I tried hacking the string myself. I debugged the check task and added a break point here (changed to my project specifics):

```java
File file = new File(".");
path = file.getCanonicalPath();
path = path.replace("robolectric_test", ""); //name of stub project
path = path + "/app/"; //name of android project
```

Turns out there was a bug with the test runner, so I integrated the updated version.

File:

```
package com.greenlifesoftware.support;

import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

import java.io.File;
import java.io.IOException;

/**
 * Custom test runner which is needed if your tests need resources etc.
 */
public class RobolectricGradleTestRunner extends RobolectricTestRunner {

    private static final String PROJECT_DIR = getProjectDirectory();
    private static final String MANIFEST_PROPERTY = PROJECT_DIR + "src/main/AndroidManifest.xml";
    private static final String RES_PROPERTY = PROJECT_DIR + "build/intermediates/res/debug/";
    private static final int TARGET_SDK_VERSION = 18;
    private static final AndroidManifest sAndroidManifest = getAndroidManifest();

    public RobolectricGradleTestRunner(final Class<?> testClass) throws Exception {
        super(testClass);
    }

    @Override
    public AndroidManifest getAppManifest(Config config) {
        return sAndroidManifest;
    }

    private static AndroidManifest getAndroidManifest() {
        return new AndroidManifest(Fs.fileFromPath(MANIFEST_PROPERTY), Fs.fileFromPath(RES_PROPERTY)) {
            @Override
            public int getTargetSdkVersion() {
                return TARGET_SDK_VERSION;
            }
        };
    }

    /**
     * Unfortunately this step is required so that tests can run both from ide and the commandline.
     * Robolectric has difficulty recognizing the manifest file from relative paths.
     *
     * @return The working directory from which tests are run.
     */
    private static String getProjectDirectory() {
        String path = "";
        try {
            File file = new File(".");
            path = file.getCanonicalPath();
            path = path.replace("robolectric_test", ""); //name of stub project
            path = path.replace("app", "");
            path = path + "/app/"; //name of android project
        } catch (IOException ignored) {
        }
        return path;
    }
}
```

## Clean Up

Tests weren't started, complained about the application test not being found. I'm not using it, so it dies: `/app/src/androidTest/java/com/greenlifesoftware/calculator/ApplicationTest.java`

## Revert Hacks

### Restore Theme

Test started, got this error:

```
WARNING: no system properties value for ro.build.date.utc
DEBUG: Loading resources for com.greenlifesoftware.calculator from /Users/colabug/AndroidStudioProjects/Calculator/app/build/intermediates/res/debug...
DEBUG: Loading resources for android from jar:/Users/colabug/.m2/repository/org/robolectric/android-all/4.3_r2-robolectric-0/android-all-4.3_r2-robolectric-0.jar!/res...

java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.
    at android.support.v7.app.ActionBarActivityDelegate.onCreate(ActionBarActivityDelegate.java:151)
    at android.support.v7.app.ActionBarActivityDelegateBase.onCreate(ActionBarActivityDelegateBase.java:138)
    at android.support.v7.app.ActionBarActivity.onCreate(ActionBarActivity.java:123)
    at com.greenlifesoftware.calculator.CalculatorActivity.onCreate(CalculatorActivity.java:13)
    at android.app.Activity.performCreate(Activity.java:5133)
    at org.robolectric.internal.ReflectionHelpers$3.run(ReflectionHelpers.java:64)
    ...
Process finished with exit code 255
```

### Remove `ShadowMenuInflator`

## TESTS PASS!

Testing passing on the command line and in Android Studio.

TODO: Make notes about how to configure the run configuration. Turn this into an easier to read saga/set of steps to help a newb get started with Robolectric. Reference my IntelliJ article as well.

## Remove Emulation

Take back emulation since the new test runner handles it.

## Warnings

These warnings remain:

```
WARNING: no system properties value for ro.build.date.utc
DEBUG: Loading resources for com.greenlifesoftware.calculator from /Users/colabug/AndroidStudioProjects/Calculator/app/build/intermediates/res/debug...
DEBUG: Loading resources for android from jar:/Users/colabug/.m2/repository/org/robolectric/android-all/4.3_r2-robolectric-0/android-all-4.3_r2-robolectric-0.jar!/res...
Couldn't find ResName{com.greenlifesoftware.calculator:attr/colorControlNormal} in ResName{android:style/Theme_DeviceDefault}
Couldn't find ResName{com.greenlifesoftware.calculator:attr/colorControlActivated} in ResName{android:style/Theme_DeviceDefault}
Couldn't find ResName{com.greenlifesoftware.calculator:attr/colorControlNormal} in ResName{android:style/Theme_DeviceDefault}
```
