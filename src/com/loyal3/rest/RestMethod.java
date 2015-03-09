package com.loyal3.rest;

import com.loyal3.rest.resource.ResponseResource;

public interface RestMethod<T extends ResponseResource>{

    public RestMethodResult<T> execute();
}