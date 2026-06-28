package com.fasttrade.android.ui.screens.product

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fasttrade.android.ui.components.FtTopBar
import com.fasttrade.android.ui.components.LoadingScreen
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@Composable
fun SellerProfileScreen(
    sellerId: Long,
    onBack: () -> Unit,
    onSendMessage: (Long, String) -> Unit = { _, _ -> },
    viewModel: AppViewModel = hiltViewModel()
) {
    val profile by viewModel.sellerProfile.collectAsState()
    val isLoading by viewModel.sellerLoading.collectAsState()

    LaunchedEffect(sellerId) { viewModel.loadSellerProfile(sellerId) }

    Scaffold(
        topBar = { FtTopBar(title = "Perfil do Vendedor", onBack = onBack) }
    ) { padding ->
        if (isLoading) {
            LoadingScreen()
        } else if (profile == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Perfil não encontrado", color = TextSecondary)
            }
        } else {
            val s = profile!!
            val rating = if (s.score > 0) String.format("%.1f", s.score / 20.0) else "5.0"

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F8F8))
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Header com avatar e nome ──────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Primary)
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Avatar com inicial
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = s.name.take(1).uppercase(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 36.sp
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = s.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (s.city.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, null, tint = Color.White.copy(0.8f), modifier = Modifier.size(14.dp))
                                Text(
                                    text = "${s.city}, ${s.state}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                        if (s.memberSince.isNotBlank()) {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Membro desde ${s.memberSince.take(7).replace("-", "/")}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                // ── Stats ─────────────────────────────────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileStat(value = rating, label = "Avaliação", icon = Icons.Default.Star)
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        ProfileStat(value = "${s.trades}", label = "Vendas", icon = Icons.Default.ShoppingBag)
                        VerticalDivider(modifier = Modifier.height(40.dp))
                        ProfileStat(value = "95%", label = "Resposta", icon = Icons.Default.Chat)
                    }
                }

                // ── Informações de contato ───────────────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Informações",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(Modifier.height(12.dp))

                        if (s.phone.isNotBlank()) {
                            InfoRow(icon = Icons.Default.Phone, label = "Telefone", value = s.phone)
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
                        }
                        InfoRow(icon = Icons.Default.WorkspacePremium, label = "Score", value = "${s.score} pontos")
                        if (s.city.isNotBlank()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFEEEEEE))
                            InfoRow(icon = Icons.Default.LocationOn, label = "Localização", value = "${s.city} - ${s.state}")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Botão de contato ─────────────────────────────────────────
                Button(
                    onClick = { onSendMessage(s.id, s.name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Enviar mensagem", color = Color.White, fontWeight = FontWeight.SemiBold)
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = Primary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, null, tint = Primary, modifier = Modifier.size(18.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, fontWeight = FontWeight.Medium)
        }
    }
}
