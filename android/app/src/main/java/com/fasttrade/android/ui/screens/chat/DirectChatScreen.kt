package com.fasttrade.android.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fasttrade.android.data.model.ChatMessageDto
import com.fasttrade.android.ui.components.FtTopBar
import com.fasttrade.android.ui.components.LoadingScreen
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DirectChatScreen(
    sellerId: Long,
    sellerName: String,
    onBack: () -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val messages by viewModel.directMessages.collectAsState()
    val isLoading by viewModel.directMessagesLoading.collectAsState()

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var messageText by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }

    val myId = profile?.id ?: 0L

    // Garante que o profile carrega primeiro
    LaunchedEffect(Unit) {
        if (profile == null) viewModel.loadProfile()
    }

    // Só computa a room e busca mensagens quando o myId é válido
    val room = if (myId > 0L) viewModel.directRoom(myId, sellerId) else null

    LaunchedEffect(room) {
        if (room != null) viewModel.loadDirectChat(room)
    }

    // Polling a cada 4 segundos (só quando room válida)
    LaunchedEffect(room) {
        if (room == null) return@LaunchedEffect
        while (true) {
            delay(4_000)
            viewModel.refreshDirectChat(room)
        }
    }

    // Scroll para última mensagem
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        FtTopBar(title = sellerName, onBack = onBack)

        // Header da conversa
        Surface(color = PrimaryLight, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Primary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sellerName.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Column {
                    Text(sellerName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Vendedor", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
                Spacer(Modifier.weight(1f))
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Success))
            }
        }

        // Mensagens
        when {
            isLoading && messages.isEmpty() -> LoadingScreen()
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (messages.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("💬", fontSize = 40.sp)
                                Text(
                                    "Inicie a conversa com $sellerName",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    items(messages, key = { it.id }) { msg ->
                        DirectChatBubble(message = msg, myId = myId)
                    }

                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }

        // Campo de entrada
        Surface(shadowElevation = 4.dp, color = Color.White) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("Digite sua mensagem...", color = TextHint) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Outline
                    )
                )
                IconButton(
                    onClick = {
                        val text = messageText.trim()
                        if (text.isNotEmpty() && !sending && myId > 0L && room != null) {
                            sending = true
                            messageText = ""
                            viewModel.sendDirectMessage(sellerId, text) { sending = false }
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (messageText.isNotBlank()) Primary else Outline)
                ) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "Enviar",
                        tint = if (messageText.isNotBlank()) Color.White else TextHint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectChatBubble(message: ChatMessageDto, myId: Long) {
    val isOwn = message.senderId == myId

    if (message.senderRole == "SYSTEM") {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFEEEEEE))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
        return
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isOwn) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    message.senderName.take(1).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            Spacer(Modifier.width(6.dp))
        }

        Column(
            horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            if (!isOwn) {
                Text(
                    message.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isOwn) 16.dp else 4.dp,
                    bottomEnd = if (isOwn) 4.dp else 16.dp
                ),
                color = if (isOwn) Primary else Color.White,
                shadowElevation = 1.dp
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isOwn) Color.White else TextPrimary
                )
            }
            // Horário
            val time = runCatching {
                message.sentAt.substring(11, 16) // "HH:mm"
            }.getOrDefault("")
            if (time.isNotEmpty()) {
                Text(
                    text = time,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextHint,
                    modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                )
            }
        }
    }
}
