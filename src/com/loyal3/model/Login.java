package com.loyal3.model;

import android.net.Uri;

/**
 * Created with IntelliJ IDEA.
 * User: curt
 */
public class Login extends L3BaseColumns {
    public static final String TABLE_NAME = "logins";
    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);
}
