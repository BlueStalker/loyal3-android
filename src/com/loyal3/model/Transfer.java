package com.loyal3.model;

import android.net.Uri;

public class Transfer extends L3BaseColumns{
    public static final String TABLE_NAME = "transfer";
    public static final Uri CONTENT_URI =  Uri.parse("content://" + L3Contract.AUTHORITY + "/" + TABLE_NAME);
}