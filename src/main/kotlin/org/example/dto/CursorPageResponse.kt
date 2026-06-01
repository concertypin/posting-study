package org.example.dto

import java.time.LocalDateTime

data class CursorPageResponse<T>(
    val data: List<T>,
    val nextCursor: LocalDateTime? // null = no more pages
)
