package com.example.textchatapp.Fragments;

import com.example.textchatapp.Notifications.MyResponse;
import com.example.textchatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AIzaSyCVc2NhglUBrLYg-cPJi3vEjNxZs6X8qbI"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
