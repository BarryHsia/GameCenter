package com.kgzn.gamecenter.data.remote.interceptor;

import androidx.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class RetryInterceptor implements Interceptor {
    private final int maxRetry;
    private final long retryDelay;

    public RetryInterceptor(int maxRetry, long retryDelay) {
        this.maxRetry = maxRetry;
        this.retryDelay = retryDelay;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = null;
        int attempt = 0;
        while (attempt < maxRetry) {
            try {
                response = chain.proceed(request);
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (IOException e) {
                if (attempt >= maxRetry - 1) {
                    throw e; // 抛出最后一次异常
                }
            }
            attempt++;
            try {
                Thread.sleep(retryDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        assert response != null;
        return response;
    }
}