package com.piotrgluszek.announcementboard.auth

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(val tokenStorage: TokenStorage) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        if (tokenStorage.token != null) {
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer " + tokenStorage.token).build()
        }
        return chain.proceed(request)
    }
}