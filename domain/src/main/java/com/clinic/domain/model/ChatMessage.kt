package com.clinic.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val messageType: MessageType = MessageType.TEXT
) : Parcelable

@Parcelize
enum class MessageType : Parcelable {
    TEXT,
    IMAGE,
    FILE
}

@Parcelize
data class ChatRoom(
    val id: String = "",
    val participants: List<String> = emptyList(),
    val lastMessage: ChatMessage? = null,
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable

