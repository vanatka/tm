/*
 * The MIT License (MIT)
 * Copyright (c) 2016 ivan.alyakskin@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package com.vanatka.tm.impl.baseactions;

import android.util.Log;

import com.vanatka.tm.interfaces.IInputInterface;

import okhttp3.Response;

public abstract class BaseRestAction<RES> extends BaseAction {

    private static final String TAG = "BaseRestAction";

    private Response response;

    public BaseRestAction(IInputInterface baseAPI) {
        super(baseAPI);
    }


    @Override
    public final boolean needUIThread( ) {
        return false;
    }

    public abstract Response implementation();

    public Response getResponse() {
        return response;
    }

    public abstract RES getConvertedResponse();

    public boolean validateResponse(Response response) {
        return null != response && response.isSuccessful();
    }

    @Override
    public boolean execute( ) {
        return validateResponse( run() );
    }

    public final Response run() {
        started();
        boolean exception = false;
        try {
            response = implementation();
            if (validateResponse( response )) {
                onValidResponse( getConvertedResponse() );
            } else {
                onNotValidResponse();
            }
        } catch (Exception e) {
            Log.e( TAG, "exception ", e );
            exception = true;
            onNotValidResponse();
        } finally {
            if( !exception ) {
                finishedError();
            } else {
                finishedOk();
            }
        }

        return response;
    }

    protected abstract void onValidResponse(RES response);

    protected abstract void onNotValidResponse();
}
