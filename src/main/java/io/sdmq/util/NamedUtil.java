package io.sdmq.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Xs.Tao on 2017/7/18.
 */
public class NamedUtil {

    public static final String SPLITE_CHAR = ":";
    public static final String LOCK_CHAR   = "LOCK";

    public static String buildBucketName(String prefix, String name, int index) {
        List<Object> lst = Lists.newArrayList();
        lst.add(prefix);
        lst.add(name);
        lst.add(index);
        return Joiner.on(SPLITE_CHAR).join(lst);
    }

    public static String buildPoolName(String prefix, String name, String pool) {
        return Joiner.on(SPLITE_CHAR).join(Lists.newArrayList(prefix, name, pool));
    }

    public static String buildRealTimeName(String prefix, String name, String readTimeName) {
        return Joiner.on(SPLITE_CHAR).join(Lists.newArrayList(prefix, name, readTimeName));
    }

    public static String buildLockName(String prefix) {
        return Joiner.on(SPLITE_CHAR).join(Lists.newArrayList(prefix.concat(":" + LOCK_CHAR)));
    }


}
