package com.example.timepal;

import com.example.timepal.network.OpenAiRequest;
import com.example.timepal.network.OpenAiResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface OpenAiApi {

    @POST("v1/chat/completions")
    Call<OpenAiResponse> generateSteps(
            @Header("Authorization") String authHeader,
            @Body OpenAiRequest request
    );
}
