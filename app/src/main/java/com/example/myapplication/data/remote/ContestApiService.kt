package com.example.myapplication.data.remote

import retrofit2.http.GET

interface ContestApiService {

    @GET("contests/upcoming")
    suspend fun getUpcomingContests(): List<Contest>
}
