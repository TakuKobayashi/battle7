package com.battle7.mbs.battle7;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;

public abstract class HttpRequestBase<T> extends Request<T> {
    protected Response.Listener<T> listener;
    protected Map<String, String> params;
    protected Map<String, String> headers;

    public HttpRequestBase(int method, String url, Response.Listener<T> listener, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
    }

    public HttpRequestBase(int method, String url, ErrorListener errorListener) {
        super(method, url, errorListener);
        this.headers = new HashMap<String, String>();
        this.params = new HashMap<String, String>();
    }

    public void setParams(Map<String, String> params){
        this.params = params;
    }

    public void setHeaders(Map<String, String> headers){
        this.headers = headers;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params.isEmpty() ? super.getParams() : params;
    }
 
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers.isEmpty() ? super.getHeaders() : headers;
    }
 
    @Override
    protected void deliverResponse(T response) {
        if(listener != null) listener.onResponse(response);
    }
 
    @Override
    protected abstract Response<T> parseNetworkResponse(NetworkResponse response);;

    public interface Listener<T> extends Response.Listener<T>{
      public void onNetworkResponse(NetworkResponse response);
    }
}
