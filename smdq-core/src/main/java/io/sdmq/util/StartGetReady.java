package io.sdmq.util;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Xs.Tao on 2018/1/12.
 */
public class StartGetReady {

    public static void ready() {
        if (System.getProperty(Constants.SOFT_HOME_KEY) == null) {
            System.setProperty(Constants.SOFT_HOME_KEY, getClazzPathUrl());
        }
        if (System.getProperty(Constants.SOFT_LOG_HOME_KEY) == null) {
            System.setProperty(Constants.SOFT_LOG_HOME_KEY, "".concat("/logs"));
        }
    }

    private static String getClazzPathUrl() {
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (!path.exists())
            path = new File("");
        return path.getAbsolutePath();
    }

}
