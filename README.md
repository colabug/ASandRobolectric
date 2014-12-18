# Plugin Route

Trying this plugin: https://github.com/kageiit/gradle-robojava-plugin

I am starting from a Blank Activity wizard hello world project - nothing fancy. I stashed by attempts at the manual configuration route that I was pursuing yesterday.

## Added `tdd` module

Used the drop down menu, selected Java at the very bottom.

### `settings.gradle` (top-level)

Confirmed that Android Studio updated with both modules.

`include ':app', ':tdd'`

## `app/build.gradle`

Applied `robolectric` plugin to the module `app`.

`apply plugin: 'robolectric'`

Added dependencies:

```java
dependencies {
    ...
    androidTestCompile 'junit:junit:4.12'
    androidTestCompile('org.robolectric:robolectric:2.4') {
        exclude group: "commons-logging", module: "commons-logging"
        exclude group: "org.apache.httpcomponents", module: "httpclient"
    }
}
```

Configured Robolectric to look for the test files.

```java
robolectric {
    include "com/greenlifesoftware/**/*Test.class"
}
```

Final file:

```java
apply plugin: 'com.android.application'
apply plugin: 'robolectric'

android {
    compileSdkVersion 21
    buildToolsVersion "21.1.2"

    defaultConfig {
        applicationId "com.greenlifesoftware.calculator"
        minSdkVersion 12
        targetSdkVersion 21
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    androidTestCompile 'junit:junit:4.12'
    androidTestCompile('org.robolectric:robolectric:2.4') {
        exclude group: "commons-logging", module: "commons-logging"
        exclude group: "org.apache.httpcomponents", module: "httpclient"
    }
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:21.0.3'
}

robolectric {
    include "com/greenlifesoftware/**/*Test.class"
}
```

## `tdd.gradle`

Replaced the default/template text in the module text with this:

```
evaluationDependsOn(':app')
ext.androidProject = 'app'
apply plugin: 'com.kageiit.robojava'
```

## `build.gradle` (top-level)

Added the plugins to the classpath.

```java
dependencies {
    ...
    classpath 'com.kageiit:robojava-plugin:1.0.0'
    classpath 'org.robolectric:robolectric-gradle-plugin:0.14.+'
}
```

Final state:

```java
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:1.0.0'
        classpath 'com.kageiit:robojava-plugin:1.0.0'
        classpath 'org.robolectric:robolectric-gradle-plugin:0.14.+'
    }
}

allprojects {
    repositories {
        jcenter()
    }
}
```

## Run

> Note: Running what ever the default Android project configuration is as well as the default module creation - except for the changes mentioned above. The new AS project wizard creates an `androidTest` folder when you create a project.

`./gradlew tdd check`

Error, doesn't know where to find Android.

```
java.lang.RuntimeException: Stub!
    at junit.framework.TestSuite.<init>(TestSuite.java:6)
    at org.junit.internal.runners.JUnit38ClassRunner.<init>(JUnit38ClassRunner.java:74)
    at org.junit.internal.builders.JUnit3Builder.runnerForClass(JUnit3Builder.java:11)
    at org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:59)
    at org.junit.internal.builders.AllDefaultPossibilitiesBuilder.runnerForClass(AllDefaultPossibilitiesBuilder.java:26)
    at org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:59)
    at org.junit.internal.requests.ClassRequest.getRunner(ClassRequest.java:33)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:80)
...
```

Guessing since tdd is really a stub.

## Create Test

Created a Robolectric test in addition to the default `ApplicationTest` that comes with the wizard.

Used the shortcut Command Shift T to create a test, selected junit 4.

File location: `.../Calculator/app/src/androidTest/java/com/greenlifesoftware/calculator/CalculatorActivityTest.java`

Test file:

```
package com.greenlifesoftware.calculator;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)

public class CalculatorActivityTest {
    @Test
    public void shouldNotBeNull() throws Exception {
        Activity activity = Robolectric.buildActivity(CalculatorActivity.class).create().get();
        assertNotNull(activity);
    }
}
```

## Run Test: AS

Right clicking on the file name (either the text or the tab at the top) to create a run configuration. When I run this configuration, I get this error:

```
WARNING: No manifest file found at ./AndroidManifest.xml.Falling back to the Android OS resources only.
To remove this warning, annotate your test class with @Config(manifest=Config.NONE).
WARNING: no system properties value for ro.build.date.utc
DEBUG: Loading resources for android from jar:/Users/colabug/.m2/repository/org/robolectric/android-all/4.1.2_r1-robolectric-0/android-all-4.1.2_r1-robolectric-0.jar!/res...

java.lang.IllegalStateException: You need to use a Theme.AppCompat theme (or descendant) with this activity.
    at android.support.v7.app.ActionBarActivityDelegate.onCreate(ActionBarActivityDelegate.java:151)
    at android.support.v7.app.ActionBarActivityDelegateBase.onCreate(ActionBarActivityDelegateBase.java:138)
    at android.support.v7.app.ActionBarActivity.onCreate(ActionBarActivity.java:123)
    at com.greenlifesoftware.calculator.CalculatorActivity.onCreate(CalculatorActivity.java:13)
    at android.app.Activity.performCreate(Activity.java:5008)
    at org.robolectric.internal.ReflectionHelpers$3.run(ReflectionHelpers.java:64)
    at org.robolectric.internal.ReflectionHelpers.traverseClassHierarchy(ReflectionHelpers.java:114)
    at org.robolectric.internal.ReflectionHelpers.callInstanceMethodReflectively(ReflectionHelpers.java:59)
    at org.robolectric.util.ActivityController$1.run(ActivityController.java:115)
    at org.robolectric.shadows.ShadowLooper.runPaused(ShadowLooper.java:268)
    at org.robolectric.util.ActivityController.create(ActivityController.java:111)
    at org.robolectric.util.ActivityController.create(ActivityController.java:122)
    at com.greenlifesoftware.calculator.CalculatorActivityTest.shouldNotBeNull(CalculatorActivityTest.java:17)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:236)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.robolectric.RobolectricTestRunner$1.evaluate(RobolectricTestRunner.java:158)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
    at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)

Process finished with exit code 255
```

Why doesn't AS know where my `AndroidManifest.xml` is located? In other integration versions, we created a test runner to point to it. I don't see that happening in the example project - it uses the default one.

## Run Test: Command Line

When running from the command line with `./gradlew check` I get this error:

```
java.lang.UnsupportedOperationException: Robolectric does not support API level 21, sorry!
    at org.robolectric.SdkConfig.<init>(SdkConfig.java:23)
    at org.robolectric.RobolectricTestRunner.pickSdkVersion(RobolectricTestRunner.java:306)
    at org.robolectric.RobolectricTestRunner.getEnvironment(RobolectricTestRunner.java:282)
    at org.robolectric.RobolectricTestRunner.access$300(RobolectricTestRunner.java:37)
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:183)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.robolectric.RobolectricTestRunner$1.evaluate(RobolectricTestRunner.java:158)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69)
    at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)
    at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
    at com.sun.proxy.$Proxy2.processTestClass(Unknown Source)
    at org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:105)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:360)
    at org.gradle.internal.concurrent.DefaultExecutorFactory$StoppableExecutorImpl$1.run(DefaultExecutorFactory.java:64)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
    at java.lang.Thread.run(Thread.java:745)
```

Also getting a warning:

```
junit.framework.AssertionFailedError: Exception in constructor: testApplicationTestCaseSetUpProperly (java.lang.RuntimeException: Stub!
    at android.test.AndroidTestCase.<init>(AndroidTestCase.java:5)
    at android.test.ApplicationTestCase.<init>(ApplicationTestCase.java:5)
    at com.greenlifesoftware.calculator.ApplicationTest.<init>(ApplicationTest.java:11)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:526)
    at junit.framework.TestSuite.createTest(TestSuite.java:60)
    at junit.framework.TestSuite.addTestMethod(TestSuite.java:307)
    at junit.framework.TestSuite.addTestsFromTestCase(TestSuite.java:150)
    at junit.framework.TestSuite.<init>(TestSuite.java:129)
    at org.junit.internal.runners.JUnit38ClassRunner.<init>(JUnit38ClassRunner.java:74)
    at org.junit.internal.builders.JUnit3Builder.runnerForClass(JUnit3Builder.java:11)
    at org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:59)
    at org.junit.internal.builders.AllDefaultPossibilitiesBuilder.runnerForClass(AllDefaultPossibilitiesBuilder.java:26)
    at org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:59)
    at org.junit.internal.requests.ClassRequest.getRunner(ClassRequest.java:33)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:80)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69)
    at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)
    at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
    at com.sun.proxy.$Proxy2.processTestClass(Unknown Source)
    at org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:105)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:360)
    at org.gradle.internal.concurrent.DefaultExecutorFactory$StoppableExecutorImpl$1.run(DefaultExecutorFactory.java:64)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
    at java.lang.Thread.run(Thread.java:745)
)
    at junit.framework.Assert.fail(Assert.java:57)
    at junit.framework.TestCase.fail(TestCase.java:227)
    at junit.framework.TestSuite$1.runTest(TestSuite.java:97)
    at junit.framework.TestCase.runBare(TestCase.java:141)
    at junit.framework.TestResult$1.protect(TestResult.java:122)
    at junit.framework.TestResult.runProtected(TestResult.java:142)
    at junit.framework.TestResult.run(TestResult.java:125)
    at junit.framework.TestCase.run(TestCase.java:129)
    at junit.framework.TestSuite.runTest(TestSuite.java:252)
    at junit.framework.TestSuite.run(TestSuite.java:247)
    at org.junit.internal.runners.JUnit38ClassRunner.run(JUnit38ClassRunner.java:86)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69)
    at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)
    at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
    at com.sun.proxy.$Proxy2.processTestClass(Unknown Source)
    at org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:105)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:360)
    at org.gradle.internal.concurrent.DefaultExecutorFactory$StoppableExecutorImpl$1.run(DefaultExecutorFactory.java:64)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
    at java.lang.Thread.run(Thread.java:745)
```

I didn't see anywhere that `emulateSDK` is used to go back to API level 18 (top level supported by Robolectric). Was this buried in the other plugin's code somewhere?

## More Research

Rereviewing the [README and example project for clues](https://github.com/kageiit/gradle-robojava-plugin).

> Note: The project structure dialog shown is different for the latest version of Android Studio. I'm running 1.0.1. I'm not a huge fan of the view - it makes it harder to understand where the files live on disk.

Current state of my `app.gradle`:

```
apply plugin: 'com.android.application'
apply plugin: 'robolectric'

android {
    compileSdkVersion 21
    buildToolsVersion "21.1.2"

    defaultConfig {
        applicationId "com.greenlifesoftware.calculator"
        minSdkVersion 12
        targetSdkVersion 21
        versionCode 1
        versionName "1.0"
    }
    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    androidTestCompile 'junit:junit:4.12'
    androidTestCompile('org.robolectric:robolectric:2.4') {
        exclude group: "commons-logging", module: "commons-logging"
        exclude group: "org.apache.httpcomponents", module: "httpclient"
    }
    compile fileTree(dir: 'libs', include: ['*.jar'])
    compile 'com.android.support:appcompat-v7:21.0.3'
}

robolectric {
    include "com/greenlifesoftware/**/*Test.class"
}
```

I don't see any differences here. The project structure matches. Not sure what I'm missing here. Will research the error show about about API level 21.

# API Level 21

`java.lang.UnsupportedOperationException: Robolectric does not support API level 21, sorry!`

Maybe their project strucutre didn't extend the new stuff like the new wizard might? Turns out they have zero resources for this example project. Are they not extending a theme in `AndroidManifest.xml`? No theme:

```
<application android:name=".RobojavaApplication">

<activity
    android:name=".RobojavaActivity"
    android:screenOrientation="portrait">
    <intent-filter>
        <action android:name="android.intent.action.MAIN"/>
            <category android:name="android.intent.category.LAUNCHER"/>
        </intent-filter>
    </activity>

    </application>
```

Clue: http://stackoverflow.com/questions/20541630/robolectric-does-not-support-api-level

Added `emulateSDK`. Final test class:

```
package com.greenlifesoftware.calculator;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)

public class CalculatorActivityTest {
    @Test
    public void shouldNotBeNull() throws Exception {
        Activity activity = Robolectric.buildActivity(CalculatorActivity.class).create().get();
        assertNotNull(activity);
    }
}
```

Different error this time:

```
android.view.InflateException: XML file /Users/colabug/AndroidStudioProjects/Calculator/app/build/intermediates/res/debug/layout/abc_screen_toolbar.xml line #-1 (sorry, not yet implemented): Error inflating class android.support.v7.widget.Toolbar
    at android.view.LayoutInflater.createView(LayoutInflater.java:620)
    at android.view.LayoutInflater.createViewFromTag(LayoutInflater.java:696)
    at android.view.LayoutInflater.rInflate(LayoutInflater.java:755)
    at android.view.LayoutInflater.rInflate(LayoutInflater.java:758)
    at android.view.LayoutInflater.inflate(LayoutInflater.java:492)
    at android.view.LayoutInflater.inflate(LayoutInflater.java:397)
    at android.view.LayoutInflater.inflate(LayoutInflater.java:353)
    at android.support.v7.app.ActionBarActivityDelegateBase.ensureSubDecor(ActionBarActivityDelegateBase.java:273)
    at android.support.v7.app.ActionBarActivityDelegateBase.setContentView(ActionBarActivityDelegateBase.java:225)
    at android.support.v7.app.ActionBarActivity.setContentView(ActionBarActivity.java:102)
    at com.greenlifesoftware.calculator.CalculatorActivity.onCreate(CalculatorActivity.java:14)
    at android.app.Activity.performCreate(Activity.java:5133)
    at org.robolectric.internal.ReflectionHelpers$3.run(ReflectionHelpers.java:64)
    at org.robolectric.internal.ReflectionHelpers.traverseClassHierarchy(ReflectionHelpers.java:114)
    at org.robolectric.internal.ReflectionHelpers.callInstanceMethodReflectively(ReflectionHelpers.java:59)
    at org.robolectric.util.ActivityController$1.run(ActivityController.java:115)
    at org.robolectric.shadows.ShadowLooper.runPaused(ShadowLooper.java:268)
    at org.robolectric.util.ActivityController.create(ActivityController.java:111)
    at org.robolectric.util.ActivityController.create(ActivityController.java:122)
    at com.greenlifesoftware.calculator.CalculatorActivityTest.shouldNotBeNull(CalculatorActivityTest.java:19)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:236)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.robolectric.RobolectricTestRunner$1.evaluate(RobolectricTestRunner.java:158)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69)
    at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)
    at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
    at com.sun.proxy.$Proxy2.processTestClass(Unknown Source)
    at org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:105)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.remote.internal.hub.MessageHub$Handler.run(MessageHub.java:360)
    at org.gradle.internal.concurrent.DefaultExecutorFactory$StoppableExecutorImpl$1.run(DefaultExecutorFactory.java:64)
    at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1145)
    at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:615)
    at java.lang.Thread.run(Thread.java:745)
Caused by: java.lang.reflect.InvocationTargetException
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:526)
    at android.view.LayoutInflater.$$robo$$LayoutInflater_1d1f_createView(LayoutInflater.java:594)
    at android.view.LayoutInflater.createView(LayoutInflater.java)
    at android.view.LayoutInflater.$$robo$$LayoutInflater_1d1f_createViewFromTag(LayoutInflater.java:696)
    at android.view.LayoutInflater.createViewFromTag(LayoutInflater.java)
    at android.view.LayoutInflater.$$robo$$LayoutInflater_1d1f_rInflate(LayoutInflater.java:755)
    at android.view.LayoutInflater.rInflate(LayoutInflater.java)
    at android.view.LayoutInflater.$$robo$$LayoutInflater_1d1f_rInflate(LayoutInflater.java:758)
    at android.view.LayoutInflater.rInflate(LayoutInflater.java)
    at android.view.LayoutInflater.$$robo$$LayoutInflater_1d1f_inflate(LayoutInflater.java:492)
    at android.view.LayoutInflater.inflate(LayoutInflater.java)
    at android.view.LayoutInflater.$$robo$$LayoutInflater_1d1f_inflate(LayoutInflater.java:397)
    at android.view.LayoutInflater.inflate(LayoutInflater.java)
    at android.view.LayoutInflater.$$robo$$LayoutInflater_1d1f_inflate(LayoutInflater.java:353)
    at android.view.LayoutInflater.inflate(LayoutInflater.java)
    at android.support.v7.app.ActionBarActivityDelegateBase.$$robo$$ActionBarActivityDelegateBase_c4c6_ensureSubDecor(ActionBarActivityDelegateBase.java:273)
    at android.support.v7.app.ActionBarActivityDelegateBase.ensureSubDecor(ActionBarActivityDelegateBase.java)
    at android.support.v7.app.ActionBarActivityDelegateBase.$$robo$$ActionBarActivityDelegateBase_c4c6_setContentView(ActionBarActivityDelegateBase.java:225)
    at android.support.v7.app.ActionBarActivityDelegateBase.setContentView(ActionBarActivityDelegateBase.java)
    at android.support.v7.app.ActionBarActivity.$$robo$$ActionBarActivity_eab0_setContentView(ActionBarActivity.java:102)
    at android.support.v7.app.ActionBarActivity.setContentView(ActionBarActivity.java)
    at com.greenlifesoftware.calculator.CalculatorActivity.onCreate(CalculatorActivity.java:14)
    at android.app.Activity.$$robo$$Activity_c57b_performCreate(Activity.java:5133)
    at android.app.Activity.performCreate(Activity.java)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.robolectric.internal.ReflectionHelpers$3.run(ReflectionHelpers.java:64)
    at org.robolectric.internal.ReflectionHelpers.traverseClassHierarchy(ReflectionHelpers.java:114)
    at org.robolectric.internal.ReflectionHelpers.callInstanceMethodReflectively(ReflectionHelpers.java:59)
    at org.robolectric.util.ActivityController$1.run(ActivityController.java:115)
    at org.robolectric.shadows.ShadowLooper.runPaused(ShadowLooper.java:268)
    at org.robolectric.util.ActivityController.create(ActivityController.java:111)
    at org.robolectric.util.ActivityController.create(ActivityController.java:122)
    at com.greenlifesoftware.calculator.CalculatorActivityTest.shouldNotBeNull(CalculatorActivityTest.java:19)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:236)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.robolectric.RobolectricTestRunner$1.evaluate(RobolectricTestRunner.java:158)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69)
    at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)
    at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
    at com.sun.proxy.$Proxy2.processTestClass(Unknown Source)
    at org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:105)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    ... 7 more
Caused by: java.lang.NoSuchMethodError: android.content.Context.getDrawable(I)Landroid/graphics/drawable/Drawable;
    at android.support.v4.content.ContextCompatApi21.getDrawable(ContextCompatApi21.java:26)
    at android.support.v4.content.ContextCompat.getDrawable(ContextCompat.java:319)
    at android.support.v7.internal.widget.TintManager.getDrawable(TintManager.java:133)
    at android.support.v7.internal.widget.TintTypedArray.getDrawable(TintTypedArray.java:62)
    at android.support.v7.widget.Toolbar.__constructor__(Toolbar.java:249)
    at android.support.v7.widget.Toolbar.<init>(Toolbar.java:195)
    at android.support.v7.widget.Toolbar.<init>(Toolbar.java:191)
    at android.view.LayoutInflater.createView(LayoutInflater.java:594)
    at android.view.LayoutInflater.createViewFromTag(LayoutInflater.java:696)
    at android.view.LayoutInflater.rInflate(LayoutInflater.java:755)
    at android.view.LayoutInflater.rInflate(LayoutInflater.java:758)
    at android.view.LayoutInflater.inflate(LayoutInflater.java:492)
    at android.view.LayoutInflater.inflate(LayoutInflater.java:397)
    at android.view.LayoutInflater.inflate(LayoutInflater.java:353)
    at android.support.v7.app.ActionBarActivityDelegateBase.ensureSubDecor(ActionBarActivityDelegateBase.java:273)
    at android.support.v7.app.ActionBarActivityDelegateBase.setContentView(ActionBarActivityDelegateBase.java:225)
    at android.support.v7.app.ActionBarActivity.setContentView(ActionBarActivity.java:102)
    at com.greenlifesoftware.calculator.CalculatorActivity.onCreate(CalculatorActivity.java:14)
    at android.app.Activity.performCreate(Activity.java:5133)
    at org.robolectric.internal.ReflectionHelpers$3.run(ReflectionHelpers.java:64)
    at org.robolectric.internal.ReflectionHelpers.traverseClassHierarchy(ReflectionHelpers.java:114)
    at org.robolectric.internal.ReflectionHelpers.callInstanceMethodReflectively(ReflectionHelpers.java:59)
    at org.robolectric.util.ActivityController$1.run(ActivityController.java:115)
    at org.robolectric.shadows.ShadowLooper.runPaused(ShadowLooper.java:268)
    at org.robolectric.util.ActivityController.create(ActivityController.java:111)
    at org.robolectric.util.ActivityController.create(ActivityController.java:122)
    at com.greenlifesoftware.calculator.CalculatorActivityTest.shouldNotBeNull(CalculatorActivityTest.java:19)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:236)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.robolectric.RobolectricTestRunner$1.evaluate(RobolectricTestRunner.java:158)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.runTestClass(JUnitTestClassExecuter.java:86)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassExecuter.execute(JUnitTestClassExecuter.java:49)
    at org.gradle.api.internal.tasks.testing.junit.JUnitTestClassProcessor.processTestClass(JUnitTestClassProcessor.java:69)
    at org.gradle.api.internal.tasks.testing.SuiteTestClassProcessor.processTestClass(SuiteTestClassProcessor.java:48)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:35)
    at org.gradle.messaging.dispatch.ReflectionDispatch.dispatch(ReflectionDispatch.java:24)
    at org.gradle.messaging.dispatch.ContextClassLoaderDispatch.dispatch(ContextClassLoaderDispatch.java:32)
    at org.gradle.messaging.dispatch.ProxyDispatchAdapter$DispatchingInvocationHandler.invoke(ProxyDispatchAdapter.java:93)
    at com.sun.proxy.$Proxy2.processTestClass(Unknown Source)
    at org.gradle.api.internal.tasks.testing.worker.TestWorker.processTestClass(TestWorker.java:105)
    ... 7 more
```

Noticed `app/build/intermediates/res/debug/layout/abc_screen_toolbar.xml` in the stacktrace, went to source.

Ok, so the wizard uses `ActionBarActivity` as the base for the "Blank Activity" type of project from the wizard.

Since this not needed for my project, it would be helpful for other projects in the future. Changing to a normal `Activity` base so it isn't so cranky.

Changed:

```java
public class CalculatorActivity extends ActionBarActivity {}
```

to

```java
public class CalculatorActivity extends Activity {}
```

## Run from AS

Using a gradle task (side bar) for check. Equivalent should be `./gradlew check` on the command line.

Still getting an failure:

```
FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:testDebug'.
> There were failing tests. See the report at: file:///Users/colabug/AndroidStudioProjects/Calculator/app/build/test-report/debug/index.html
```

Weird there were only warnings in the report and it reported that 33% of my tests passed: 3 tests, 2 failures.

### Warnings

```
junit.framework.AssertionFailedError: Exception in constructor: testAndroidTestCaseSetupProperly (java.lang.RuntimeException: Stub!
    at android.test.AndroidTestCase.<init>(AndroidTestCase.java:5)
    at android.test.ApplicationTestCase.<init>(ApplicationTestCase.java:5)
    at com.greenlifesoftware.calculator.ApplicationTest.<init>(ApplicationTest.java:11)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
    at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:57)
    at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
    at java.lang.reflect.Constructor.newInstance(Constructor.java:526)
    at junit.framework.TestSuite.createTest(TestSuite.java:60)
    at junit.framework.TestSuite.addTestMethod(TestSuite.java:307)
    at junit.framework.TestSuite.addTestsFromTestCase(TestSuite.java:150)
    at junit.framework.TestSuite.<init>(TestSuite.java:129)
    at org.junit.internal.runners.JUnit38ClassRunner.<init>(JUnit38ClassRunner.java:74)
    at org.junit.internal.builders.JUnit3Builder.runnerForClass(JUnit3Builder.java:11)
    at org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:59)
    at org.junit.internal.builders.AllDefaultPossibilitiesBuilder.runnerForClass(AllDefaultPossibilitiesBuilder.java:26)
    at org.junit.runners.model.RunnerBuilder.safeRunnerForClass(RunnerBuilder.java:59)
    at org.junit.internal.requests.ClassRequest.getRunner(ClassRequest.java:33)
    ...
```

## Removed Default Tests

Removed what AS included by default.

Reran the gradle check task:

```
1 test completed, 1 failed
:tdd:test FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':tdd:test'.
> There were failing tests. See the report at: file:///Users/colabug/AndroidStudioProjects/Calculator/tdd/build/reports/tests/index.html
```

Opened my test report and it says everything passes.

## Clean up Test module

When I created the test module, it included `tdd/java/com/example/Tests.java` as part of the module creation wizard. Deleting the `src` directory and child files.

Run gradle task again. Failure:

```
:tdd:test

com.greenlifesoftware.calculator.CalculatorActivityTest > shouldNotBeNull FAILED
    android.content.res.Resources$NotFoundException at CalculatorActivityTest.java:19

1 test completed, 1 failed
:tdd:test FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':tdd:test'.
> There were failing tests. See the report at: file:///Users/colabug/AndroidStudioProjects/Calculator/tdd/build/reports/tests/index.html
```

Stacktrace:

```
android.content.res.Resources$NotFoundException: unknown resource 2130903063
    at org.robolectric.shadows.ShadowAssetManager.getAndResolve(ShadowAssetManager.java:311)
    at org.robolectric.shadows.ShadowAssetManager.getResourceValue(ShadowAssetManager.java:92)
    at android.content.res.AssetManager.getResourceValue(AssetManager.java)
    at android.content.res.Resources.getValue(Resources.java:1114)
    at android.content.res.Resources.loadXmlResourceParser(Resources.java:2304)
    at android.content.res.Resources.getLayout(Resources.java:934)
    at android.view.LayoutInflater.inflate(LayoutInflater.java:395)
    at android.view.LayoutInflater.inflate(LayoutInflater.java:353)
    at com.android.internal.policy.impl.PhoneWindow.setContentView(PhoneWindow.java:267)
    at android.app.Activity.setContentView(Activity.java:1895)
    at com.greenlifesoftware.calculator.CalculatorActivity.onCreate(CalculatorActivity.java:13)
    at android.app.Activity.performCreate(Activity.java:5133)
    at org.robolectric.internal.ReflectionHelpers$3.run(ReflectionHelpers.java:64)
    at org.robolectric.internal.ReflectionHelpers.traverseClassHierarchy(ReflectionHelpers.java:114)
    at org.robolectric.internal.ReflectionHelpers.callInstanceMethodReflectively(ReflectionHelpers.java:59)
    at org.robolectric.util.ActivityController$1.run(ActivityController.java:115)
    at org.robolectric.shadows.ShadowLooper.runPaused(ShadowLooper.java:268)
    at org.robolectric.util.ActivityController.create(ActivityController.java:111)
    at org.robolectric.util.ActivityController.create(ActivityController.java:122)
    at com.greenlifesoftware.calculator.CalculatorActivityTest.shouldNotBeNull(CalculatorActivityTest.java:19)
    at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:50)
    at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
    at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:47)
    at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:236)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
```

## New Example

After [Twitter conversations](https://twitter.com/corey_latislaw/status/545610122089992192), a fleshed out example was added to the plugin.

Updated my local version, synced Gradle files.

Test failures:

```
WARNING: no system properties value for ro.build.date.utc
java.lang.RuntimeException: /Users/colabug/Documents/Code/Android/OpenSource/gradle-robojava-plugin/example/robojava/src/androidTest/robojava/src/main/AndroidManifest.xml not found or not a file; it should point to your project's AndroidManifest.xml
    at org.robolectric.AndroidManifest.validate(AndroidManifest.java:134)
    at org.robolectric.AndroidManifest.getResourcePath(AndroidManifest.java:516)
    at org.robolectric.AndroidManifest.getIncludedResourcePaths(AndroidManifest.java:522)
    at org.robolectric.RobolectricTestRunner.createAppResourceLoader(RobolectricTestRunner.java:635)
    at org.robolectric.RobolectricTestRunner.getAppResourceLoader(RobolectricTestRunner.java:627)
    at org.robolectric.internal.ParallelUniverse.setUpApplicationState(ParallelUniverse.java:67)
    at org.robolectric.RobolectricTestRunner.setUpApplicationState(RobolectricTestRunner.java:440)
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:222)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.robolectric.RobolectricTestRunner$1.evaluate(RobolectricTestRunner.java:158)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.junit.runners.Suite.runChild(Suite.java:128)
    at org.junit.runners.Suite.runChild(Suite.java:27)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
    at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:74)
    at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:211)
    at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:67)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    at java.lang.reflect.Method.invoke(Method.java:606)
    at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)

java.lang.RuntimeException: java.lang.RuntimeException: /Users/colabug/Documents/Code/Android/OpenSource/gradle-robojava-plugin/example/robojava/src/androidTest/robojava/src/main/AndroidManifest.xml not found or not a file; it should point to your project's AndroidManifest.xml
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:226)
    at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:325)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
    at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.robolectric.RobolectricTestRunner$1.evaluate(RobolectricTestRunner.java:158)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.junit.runners.Suite.runChild(Suite.java:128)
    at org.junit.runners.Suite.runChild(Suite.java:27)
    at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
    at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
    at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
    at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
    at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
    at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
    at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
    at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:74)
    at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:211)
    at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:67)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
    at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)
Caused by: java.lang.RuntimeException: /Users/colabug/Documents/Code/Android/OpenSource/gradle-robojava-plugin/example/robojava/src/androidTest/robojava/src/main/AndroidManifest.xml not found or not a file; it should point to your project's AndroidManifest.xml
    at org.robolectric.AndroidManifest.validate(AndroidManifest.java:134)
    at org.robolectric.AndroidManifest.getResourcePath(AndroidManifest.java:516)
    at org.robolectric.AndroidManifest.getIncludedResourcePaths(AndroidManifest.java:522)
    at org.robolectric.RobolectricTestRunner.createAppResourceLoader(RobolectricTestRunner.java:635)
    at org.robolectric.RobolectricTestRunner.getAppResourceLoader(RobolectricTestRunner.java:627)
    at org.robolectric.internal.ParallelUniverse.setUpApplicationState(ParallelUniverse.java:67)
    at org.robolectric.RobolectricTestRunner.setUpApplicationState(RobolectricTestRunner.java:440)
    at org.robolectric.RobolectricTestRunner$2.evaluate(RobolectricTestRunner.java:222)
    ... 27 more
```

Reimported into Android Studio, maybe there was an outdated something somewhere.

## Test Runner

Created `support` package under `androidTest` and copied the [example test runner there](https://raw.githubusercontent.com/kageiit/gradle-robojava-plugin/master/example/robojava/src/androidTest/java/com/kageiit/test/RobojavaTestRunner.java).

Edited test runner to match my project names.

```java
path = path.replace("tdd", ""); //name of stub project
path = path + "/app/"; //name of android project
```

Rerun check task. Failed, didn't apply the new test runner.

```java
@RunWith(RobojavaTestRunner.class)
```

Changed class name to `RobolectricGradleTestRunner.java`.

```
More failure:

com.greenlifesoftware.calculator.CalculatorActivityTest > shouldNotBeNull FAILED
    java.lang.RuntimeException
        Caused by: java.lang.RuntimeException

1 test completed, 1 failed
:app:testDebug FAILED

FAILURE: Build failed with an exception.

* What went wrong:
Execution failed for task ':app:testDebug'.
> There were failing tests. See the report at: file:///Users/colabug/AndroidStudioProjects/Calculator/app/build/test-report/debug/index.html
```

Same stacktrace:

```
android.content.res.Resources$NotFoundException: unknown resource 2130903063
    at org.robolectric.shadows.ShadowAssetManager.getAndResolve(ShadowAssetManager.java:311)
    at org.robolectric.shadows.ShadowAssetManager.getResourceValue(ShadowAssetManager.java:92)...
```

Went back to original project. Doesn't build from the command line or from AS in the provided example. Reached out again via Twitter.

## Rejoyce!
Turns out there was a bug with the plugin. He fixed and I integrated the fix. I commited and pushed.
