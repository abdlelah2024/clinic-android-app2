package com.clinic.app.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.clinic.domain.model.ChatMessage
import com.clinic.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun loadMessages(chatRoomId: String) {
        viewModelScope.launch {
            chatRepository.getMessages(chatRoomId)
                .catch { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message ?: "حدث خطأ أثناء تحميل الرسائل",
                        isLoading = false
                    )
                }
                .collect { messages ->
                    _uiState.value = _uiState.value.copy(
                        messages = messages,
                        isLoading = false,
                        error = null
                    )
                }
        }
    }

    fun sendMessage(chatRoomId: String, senderId: String, message: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSending = true)
            
            // تحديد receiverId (يجب تمريره من الشاشة أو الحصول عليه من غرفة الدردشة)
            val receiverId = getCurrentChatReceiverId(chatRoomId, senderId)
            
            chatRepository.sendMessage(
                chatRoomId = chatRoomId,
                senderId = senderId,
                receiverId = receiverId,
                message = message
            ).onSuccess {
                _uiState.value = _uiState.value.copy(isSending = false)
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    error = error.message ?: "فشل في إرسال الرسالة",
                    isSending = false
                )
            }
        }
    }

    fun markMessagesAsRead(chatRoomId: String, userId: String) {
        viewModelScope.launch {
            chatRepository.markMessagesAsRead(chatRoomId, userId)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private suspend fun getCurrentChatReceiverId(chatRoomId: String, senderId: String): String {
        // هذه دالة مساعدة للحصول على معرف المستقبل
        // يمكن تحسينها بناءً على بنية البيانات الفعلية
        return "receiver_id" // يجب استبدالها بالمنطق الفعلي
    }
}

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = true,
    val isSending: Boolean = false,
    val error: String? = null
)

