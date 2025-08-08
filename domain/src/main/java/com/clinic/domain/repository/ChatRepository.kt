package com.clinic.domain.repository

import com.clinic.domain.model.ChatMessage
import com.clinic.domain.model.ChatRoom
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    
    fun getChatRooms(userId: String): Flow<List<ChatRoom>>
    
    fun getMessages(chatRoomId: String): Flow<List<ChatMessage>>
    
    suspend fun sendMessage(
        chatRoomId: String,
        senderId: String,
        receiverId: String,
        message: String
    ): Result<String>
    
    suspend fun createChatRoom(participants: List<String>): Result<String>
    
    suspend fun markMessagesAsRead(chatRoomId: String, userId: String): Result<Unit>
    
    suspend fun getChatRoomByParticipants(participants: List<String>): ChatRoom?
}

