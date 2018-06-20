package com.example.abima.myweatherapp;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

class MySingleton {

    private static MySingleton mInstance;
    private static RequestQueue mRequest;
    private Context mContext;

    private MySingleton(Context context){
        mContext = context;
        mRequest = getRequestQueue();
    }


    private RequestQueue getRequestQueue() {
        if(mRequest == null){
            mRequest = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequest;
    }

    public static synchronized MySingleton getInstance(Context context) {
        if(mInstance == null){
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }

    public void addToRequestQueue(Request request) {
        mRequest.add(request);
    }
}
