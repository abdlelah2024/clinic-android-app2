package com.clinic.data.mapper

import com.clinic.domain.model.ChatMessage
import com.clinic.domain.model.ChatRoom
import com.clinic.domain.model.MessageType
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toChatMessage(): ChatMessage? {
    return try {
        ChatMessage(
            id = id,
            senderId = getString("senderId") ?: "",
            receiverId = getString("receiverId") ?: "",
            message = getString("message") ?: "",
            timestamp = getLong("timestamp") ?: System.currentTimeMillis(),
            isRead = getBoolean("isRead") ?: false,
            messageType = MessageType.valueOf(getString("messageType") ?: "TEXT")
        )
    } catch (e: Exception) {
        null
    }
}

fun ChatMessage.toChatMessageMap(chatRoomId: String): Map<String, Any> {
    return mapOf(
        "chatRoomId" to chatRoomId,
        "senderId" to senderId,
        "receiverId" to receiverId,
        "message" to message,
        "timestamp" to timestamp,
        "isRead" to isRead,
        "messageType" to messageType.name
    )
}

fun DocumentSnapshot.toChatRoom(): ChatRoom? {
    return try {
        val lastMessageData = get("lastMessage") as? Map<String, Any>
        val lastMessage = lastMessageData?.let {
            ChatMessage(
                id = it["id"] as? String ?: "",
                senderId = it["senderId"] as? String ?: "",
                receiverId = it["receiverId"] as? String ?: "",
                message = it["message"] as? String ?: "",
                timestamp = it["timestamp"] as? Long ?: 0L,
                isRead = it["isRead"] as? Boolean ?: false,
                messageType = MessageType.valueOf(it["messageType"] as? String ?: "TEXT")
            )
        }

        ChatRoom(
            id = id,
            participants = (get("participants") as? List<String>) ?: emptyList(),
            lastMessage = lastMessage,
            updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
        )
    } catch (e: Exception) {
        null
    }
}

fun ChatRoom.toChatRoomMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>(
        "participants" to participants,
        "updatedAt" to updatedAt
    )
    
    lastMessage?.let { message ->
        map["lastMessage"] = mapOf(
            "id" to message.id,
            "senderId" to message.senderId,
            "receiverId" to message.receiverId,
            "message" to message.message,
            "timestamp" to message.timestamp,
            "isRead" to message.isRead,
            "messageType" to message.messageType.name
        )
    }
    
    return map
}

