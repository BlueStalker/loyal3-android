package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/16/14
 * Time: 2:23 PM
 * To change this template use File | Settings | File Templates.
 */
public class Buy extends L3BaseColumns{
    public static final String TABLE_NAME = "buy";
    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);
}
