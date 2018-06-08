package io.sdmq.util;

/**
 * Created by Xs.Tao on 2018/1/12.
 */
public class Constants {

    public static final String USER_DIR          = "user.dir";
    public static       String SOFT_HOME_KEY     = "soft.home";
    public static       String SOFT_LOG_HOME_KEY = "soft.logs";
    public static       String SOFT_HOME         = System.getProperty(SOFT_HOME_KEY);
    public static       String SOFT_LOG_HOME     = System.getProperty(SOFT_LOG_HOME_KEY);

}
