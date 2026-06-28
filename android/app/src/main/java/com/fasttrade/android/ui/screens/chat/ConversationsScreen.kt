package com.fasttrade.android.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fasttrade.android.data.model.ConversationSummary
import com.fasttrade.android.ui.components.FtTopBar
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@Composable
fun ConversationsScreen(
    onConversationClick: (sellerId: Long, sellerName: String) -> Unit,
    onBack: (() -> Unit)? = null,
    viewModel: AppViewModel = hiltViewModel()
) {
    val conversations by viewModel.conversations.collectAsState()
    val isLoading by viewModel.conversationsLoading.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadConversations() }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))) {
        FtTopBar(title = "Mensagens", onBack = onBack)

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
            conversations.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            "Nenhuma conversa ainda",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            "Entre em contato com um vendedor\npara iniciar uma conversa",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(conversations, key = { it.room }) { conv ->
                        ConversationItem(
                            conv = conv,
                            onClick = { onConversationClick(conv.otherUserId, conv.otherUserName) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(start = 76.dp),
                            color = Color(0xFFEEEEEE)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(
    conv: ConversationSummary,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(PrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = conv.otherUserName.take(1).uppercase(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Primary
            )
        }

        // Conteúdo
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conv.otherUserName.ifBlank { "Usuário #${conv.otherUserId}" },
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                if (conv.lastMessageAt.isNotBlank()) {
                    val time = runCatching { conv.lastMessageAt.substring(11, 16) }.getOrDefault("")
                    Text(
                        text = time,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = conv.lastMessage.ifBlank { "Sem mensagens ainda" },
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                if (conv.unreadCount > 0) {
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = conv.unreadCount.toString(),
                            fontSize = 10.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
