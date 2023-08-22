package com.example.executiongate.service.dto

import com.example.executiongate.db.CommentPayload
import com.example.executiongate.db.Payload
import com.example.executiongate.db.ReviewPayload
import com.example.executiongate.db.User
import java.io.Serializable
import java.time.LocalDateTime


@JvmInline
value class EventId(private val id: String): Serializable {
    override fun toString() = id
}

enum class EventType {
    REVIEW,
    COMMENT,
}

enum class ReviewAction {
    APPROVE,
    COMMENT,
    REQUEST_CHANGE,
}

/**
 * A DTO for the {@link com.example.executiongate.db.EventEntity} entity
 */
abstract class Event(
    val type: EventType,
    open val createdAt: LocalDateTime = LocalDateTime.now(),
) {
    abstract val id: String
    abstract val author: User

    companion object {
        fun create(id: String, author: User, createdAt: LocalDateTime, payload: Payload): Event = when (payload) {
            is CommentPayload -> CommentEvent(id, author, createdAt, payload.comment)
            is ReviewPayload -> ReviewEvent(id, author, createdAt, payload.comment, payload.action)
            else -> throw IllegalArgumentException("Unknown payload type: ${payload::class}")
        }
    }
}

data class CommentEvent(
    override val id: String,
    override val author: User,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    val comment: String,
): Event(EventType.COMMENT, createdAt)

data class ReviewEvent(
    override val id: String,
    override val author: User,
    override val createdAt: LocalDateTime = LocalDateTime.now(),
    val comment: String,
    val action: ReviewAction,
): Event(EventType.REVIEW, createdAt)
