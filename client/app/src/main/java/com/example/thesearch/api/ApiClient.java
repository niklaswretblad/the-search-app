package com.example.thesearch.api;

import com.example.thesearch.model.UserManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://the-search-client.azurewebsites.net/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    .addInterceptor(new AuthInterceptor())
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    private static class AuthInterceptor implements Interceptor {
        private UserManager userManager;

        public AuthInterceptor() {
            this.userManager = UserManager.getInstance();
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            String token = userManager.getAuthToken();
            if (token != null && !originalRequest.url().encodedPath().endsWith("/user/login")) {
                Request.Builder builder = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token);
                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
            return chain.proceed(originalRequest);
        }
    }
}
