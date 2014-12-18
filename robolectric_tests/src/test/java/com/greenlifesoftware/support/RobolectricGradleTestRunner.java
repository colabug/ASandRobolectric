package com.greenlifesoftware.support;

import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

import java.io.File;
import java.io.IOException;

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
            path = path.replace("robolectric_tests", ""); //name of stub project
            path = path.replace("app", "");
            path = path + "/app/"; //name of android project
        } catch (IOException ignored) {
        }
        return path;
    }
}
