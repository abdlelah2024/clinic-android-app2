package com.clinic.data.repository

import com.clinic.data.mapper.toChatMessage
import com.clinic.data.mapper.toChatMessageMap
import com.clinic.data.mapper.toChatRoom
import com.clinic.data.mapper.toChatRoomMap
import com.clinic.domain.model.ChatMessage
import com.clinic.domain.model.ChatRoom
import com.clinic.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    private val chatRoomsCollection = firestore.collection("chatRooms")
    private val messagesCollection = firestore.collection("messages")

    override fun getChatRooms(userId: String): Flow<List<ChatRoom>> = callbackFlow {
        val listener = chatRoomsCollection
            .whereArrayContains("participants", userId)
            .orderBy("updatedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chatRooms = snapshot?.documents?.mapNotNull { doc ->
                    doc.toChatRoom()
                } ?: emptyList()

                trySend(chatRooms)
            }

        awaitClose { listener.remove() }
    }

    override fun getMessages(chatRoomId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = messagesCollection
            .whereEqualTo("chatRoomId", chatRoomId)
            .orderBy("timestamp", Query.Direction.ASC)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toChatMessage()
                } ?: emptyList()

                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(
        chatRoomId: String,
        senderId: String,
        receiverId: String,
        message: String
    ): Result<String> {
        return try {
            val chatMessage = ChatMessage(
                id = messagesCollection.document().id,
                senderId = senderId,
                receiverId = receiverId,
                message = message,
                timestamp = System.currentTimeMillis()
            )

            // إضافة الرسالة
            messagesCollection
                .document(chatMessage.id)
                .set(chatMessage.toChatMessageMap(chatRoomId))
                .await()

            // تحديث غرفة الدردشة
            updateChatRoom(chatRoomId, chatMessage)

            Result.success(chatMessage.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createChatRoom(participants: List<String>): Result<String> {
        return try {
            val chatRoom = ChatRoom(
                id = chatRoomsCollection.document().id,
                participants = participants,
                updatedAt = System.currentTimeMillis()
            )

            chatRoomsCollection
                .document(chatRoom.id)
                .set(chatRoom.toChatRoomMap())
                .await()

            Result.success(chatRoom.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markMessagesAsRead(chatRoomId: String, userId: String): Result<Unit> {
        return try {
            val unreadMessages = messagesCollection
                .whereEqualTo("chatRoomId", chatRoomId)
                .whereEqualTo("receiverId", userId)
                .whereEqualTo("isRead", false)
                .get()
                .await()

            val batch = firestore.batch()
            unreadMessages.documents.forEach { doc ->
                batch.update(doc.reference, "isRead", true)
            }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getChatRoomByParticipants(participants: List<String>): ChatRoom? {
        return try {
            val sortedParticipants = participants.sorted()
            val snapshot = chatRoomsCollection
                .whereArrayContainsAny("participants", sortedParticipants)
                .get()
                .await()

            snapshot.documents
                .mapNotNull { it.toChatRoom() }
                .find { room ->
                    room.participants.sorted() == sortedParticipants
                }
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun updateChatRoom(chatRoomId: String, lastMessage: ChatMessage) {
        try {
            chatRoomsCollection
                .document(chatRoomId)
                .update(
                    mapOf(
                        "lastMessage" to lastMessage.toChatMessageMap(chatRoomId),
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
        } catch (e: Exception) {
            // تجاهل الأخطاء في تحديث غرفة الدردشة
        }
    }
}

