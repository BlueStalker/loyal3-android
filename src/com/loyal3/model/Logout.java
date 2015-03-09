package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/9/14
 * Time: 10:18 AM
 * To change this template use File | Settings | File Templates.
 */
public class Logout extends L3BaseColumns {
    public static final String TABLE_NAME = "logouts";
    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);
}
