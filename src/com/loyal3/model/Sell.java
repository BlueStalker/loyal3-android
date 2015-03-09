package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 * Date: 9/18/14
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class Sell extends L3BaseColumns{
    public static final String TABLE_NAME = "sell";
    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);
}
