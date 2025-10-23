package com.example.timepal.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.util.Log;

import com.example.timepal.BuildConfig;
import com.example.timepal.OpenAiApi;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AiService {

    private static final String BASE_URL = "https://api.openai.com/";
    private static final String TAG = "AiService";
    private static final String PREFS_NAME = "TimePalPrefs";
    private static final String LAST_SUGGESTION_KEY = "last_ai_suggestion";

    private static AiService instance;
    private final OpenAiApi api;
    private final SharedPreferences prefs;
    private final Context appContext;

    public interface SuggestionCallback {
        void onSuccess(String steps);
        void onFailure(String errorMessage);
        void onFallback(String cachedSteps);
    }

    private AiService(Context context) {
        appContext = context.getApplicationContext();
        prefs = appContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(15, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = retrofit.create(OpenAiApi.class);
    }

    public static AiService getInstance(Context context) {
        if (instance == null) {
            instance = new AiService(context);
        }
        return instance;
    }

    public void fetchSteps(String taskTitle, SuggestionCallback callback) {
        if (!isInternetAvailable(appContext)) {
            Log.w(TAG, "Brak internetu");
            callback.onFailure("Brak połączenia z internetem");
            return;
        }

        String prompt = "Wygeneruj listę kroków potrzebnych do wykonania zadania: \"" + taskTitle + "\". Podaj tylko listę, po jednym kroku na linię.";
        OpenAiRequest.Message message = new OpenAiRequest.Message("user", prompt);
        OpenAiRequest request = new OpenAiRequest("gpt-3.5-turbo", Collections.singletonList(message));

        String authHeader = "Bearer " + BuildConfig.OPENAI_API_KEY;

        api.generateSteps(authHeader, request).enqueue(new Callback<OpenAiResponse>() {
            @Override
            public void onResponse(Call<OpenAiResponse> call, Response<OpenAiResponse> response) {
                if (response.isSuccessful() && response.body() != null &&
                        response.body().getChoices() != null && !response.body().getChoices().isEmpty()) {

                    String content = response.body().getChoices().get(0).getMessage().getContent();
                    Log.d(TAG, "AI response: " + content);
                    prefs.edit().putString(LAST_SUGGESTION_KEY, content).apply();
                    callback.onSuccess(content);
                } else {
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (IOException e) {
                        errorBody = "Błąd odczytu errorBody: " + e.getMessage();
                    }

                    Log.e(TAG, "AI response error: " + response.code() + " - " + errorBody);
                    String cached = prefs.getString(LAST_SUGGESTION_KEY, "");
                    callback.onFallback(cached);
                }
            }

            @Override
            public void onFailure(Call<OpenAiResponse> call, Throwable t) {
                Log.e(TAG, "AI failure: " + t.getMessage(), t);
                if (t instanceof IOException) {
                    callback.onFailure("Błąd połączenia z internetem");
                } else {
                    callback.onFailure("Błąd aplikacji AI");
                }
            }
        });
    }

    private boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        return false;
    }
}