package com.loyal3.model;

import android.provider.BaseColumns;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 8/22/14
 * Time: 2:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class L3BaseColumns implements BaseColumns {
    public static final String AUTHORITY = "com.loyal3.provider.model.loyal3provider";
    // URI DEFS
    static final String SCHEME = "content://";
    public static final String URI_PREFIX = SCHEME + AUTHORITY;

    public static final String LAST_UPDATED = "last_updated";

}
