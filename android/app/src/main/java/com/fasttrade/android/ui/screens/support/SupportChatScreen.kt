package com.fasttrade.android.ui.screens.support

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.fasttrade.android.ui.components.*
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel
import kotlinx.coroutines.launch

@Composable
fun SupportChatScreen(
    onBack: () -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val chatLoading by viewModel.chatLoading.collectAsState()

    var guestName by remember { mutableStateOf("") }
    var guestEmail by remember { mutableStateOf("") }
    var roomId by remember { mutableStateOf<String?>(null) }
    var messageText by remember { mutableStateOf("") }
    var formSubmitted by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // If user is logged in, skip the name/email form
    LaunchedEffect(profile) {
        if (profile != null) {
            guestName = profile!!.name
            guestEmail = profile!!.email
            formSubmitted = true
            val room = "customer_${profile!!.email.replace("@", "_").replace(".", "_")}"
            roomId = room
            viewModel.loadChatHistory(room)
        }
    }

    // Scroll to bottom on new message
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(chatMessages.size - 1) }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        FtTopBar(title = "Suporte", onBack = onBack)

        if (!formSubmitted) {
            // Guest form: collect name + email
            GuestInfoForm(
                name = guestName,
                email = guestEmail,
                onNameChange = { guestName = it },
                onEmailChange = { guestEmail = it },
                onStart = {
                    if (guestName.isNotBlank() && guestEmail.isNotBlank()) {
                        formSubmitted = true
                        val room = "customer_${guestEmail.replace("@", "_").replace(".", "_")}"
                        roomId = room
                        viewModel.requestSupport(guestName, guestEmail) {
                            viewModel.loadChatHistory(room)
                        }
                    }
                }
            )
        } else {
            // Chat area
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat header with agent info
                    Surface(
                        color = PrimaryLight,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                            Column {
                                Text("Equipe Fast Trade", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                                Text("Geralmente responde em minutos", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Success))
                        }
                    }

                    when {
                        chatLoading -> LoadingScreen()
                        else -> {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.weight(1f),
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (chatMessages.isEmpty()) {
                                    item {
                                        Column(
                                            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Icon(Icons.Default.Chat, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                                            Text("Olá, ${guestName.split(" ").first()}! 👋", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                            Text("Como podemos ajudar você hoje?", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }

                                items(chatMessages) { msg ->
                                    ChatBubble(message = msg, currentUserEmail = guestEmail)
                                }

                                item { Spacer(modifier = Modifier.height(8.dp)) }
                            }

                            // Message input
                            Surface(
                                shadowElevation = 4.dp,
                                color = Color.White
                            ) {
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
                                            if (text.isNotEmpty() && roomId != null) {
                                                viewModel.sendChatMessage(
                                                    room = roomId!!,
                                                    message = text,
                                                    senderName = guestName,
                                                    senderEmail = guestEmail
                                                )
                                                messageText = ""
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
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessageDto, currentUserEmail: String) {
    val isOwn = message.senderEmail == currentUserEmail
    val isSystem = message.senderEmail == "system"

    if (isSystem) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text(
                text = message.content,
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceVariant)
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
                modifier = Modifier.size(28.dp).clip(CircleShape).background(PrimaryLight),
                contentAlignment = Alignment.Center
            ) {
                Text(message.senderName.take(1).uppercase(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Primary)
            }
            Spacer(modifier = Modifier.width(6.dp))
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
                color = if (isOwn) Primary else Surface,
                shadowElevation = 1.dp
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isOwn) Color.White else TextPrimary
                )
            }
            Text(
                text = message.sentAt.takeLast(8).take(5),
                style = MaterialTheme.typography.labelSmall,
                color = TextHint,
                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
            )
        }
    }
}

@Composable
private fun GuestInfoForm(
    name: String,
    email: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.size(72.dp).clip(CircleShape).background(PrimaryLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Primary, modifier = Modifier.size(40.dp))
        }

        Text(
            text = "Fale Conosco",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = "Informe seu nome e e-mail para iniciar o atendimento.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(8.dp))

        FtTextField(
            value = name,
            onValueChange = onNameChange,
            label = "Seu nome"
        )

        FtTextField(
            value = email,
            onValueChange = onEmailChange,
            label = "Seu e-mail"
        )

        Spacer(modifier = Modifier.height(8.dp))

        FtButton(
            text = "Iniciar Atendimento",
            onClick = onStart,
            enabled = name.isNotBlank() && email.isNotBlank()
        )
    }
}
