package com.hackathon.dto

// Generic API Response
data class ApiResponse<T>(
    val data: T?,
    val error: ErrorResponse? = null,
    val pagination: PaginationResponse? = null
)

// Error Response
data class ErrorResponse(
    val code: Int,
    val message: String
)

// Paginated Response
data class PaginationResponse(
    val currentPage: Int,
    val totalPages: Int,
    val pageSize: Int,
    val totalItems: Int
)