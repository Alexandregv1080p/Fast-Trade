package com.fasttrade.android.ui.screens.cart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fasttrade.android.ui.components.FtButton
import com.fasttrade.android.ui.components.FtTopBar
import com.fasttrade.android.ui.theme.*
import kotlinx.coroutines.delay

private enum class PayStatus { WAITING, APPROVED }

// ponytail: payment approval is simulated client-side (no gateway); wire to a real payment webhook/poll when the backend has one
@Composable
fun PaymentStatusScreen(
    orderId: Long,
    method: String,
    amount: Double,
    onDone: () -> Unit,
    autoApprove: Boolean = true
) {
    // On the fresh checkout path PIX/card auto-approve after a short wait; boleto stays pending.
    // When reopened from an order, just show the payment info (no fake approval).
    var status by remember { mutableStateOf(PayStatus.WAITING) }
    LaunchedEffect(Unit) {
        if (autoApprove && (method == "PIX" || method == "CREDIT_CARD")) {
            delay(4500)
            status = PayStatus.APPROVED
            delay(1600)
            onDone()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        FtTopBar(title = "Pagamento", onBack = onDone)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (status == PayStatus.APPROVED) {
                ApprovedContent(amount)
            } else when (method) {
                "PIX"         -> PixContent(orderId, amount)
                "CREDIT_CARD" -> CardProcessingContent(amount)
                else          -> BoletoContent(orderId, amount)
            }
        }

        // Boleto never auto-approves, and any reopened screen needs an explicit way out
        val showExit = status == PayStatus.WAITING && (!autoApprove || method == "BOLETO")
        if (showExit) {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    FtButton(text = "Ver meu pedido", onClick = onDone)
                }
            }
        }
    }
}

// ─── Approved ─────────────────────────────────────────────────────────────────

@Composable
private fun ApprovedContent(amount: Double) {
    Spacer(modifier = Modifier.height(40.dp))
    Box(
        modifier = Modifier.size(96.dp).clip(RoundedCornerShape(50)).background(Primary.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Default.CheckCircle, null, tint = Primary, modifier = Modifier.size(60.dp))
    }
    Text("Pagamento aprovado!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(
        "Recebemos R$${String.format("%.2f", amount)}. Redirecionando para o seu pedido...",
        style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
    CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.dp, color = Primary)
}

// ─── PIX ──────────────────────────────────────────────────────────────────────

@Composable
private fun PixContent(orderId: Long, amount: Double) {
    val clipboard = LocalClipboardManager.current
    val pixCode = remember(orderId, amount) {
        "00020126FASTTRADE580014BR5204000053039865406${String.format("%.2f", amount)}6304ORD$orderId"
    }

    Text("Pague com PIX", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(
        "Escaneie o QR Code no app do seu banco",
        style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )

    Box(
        modifier = Modifier
            .size(220.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        QrPlaceholder(seed = orderId.toInt() + amount.toInt())
    }

    Text("R$${String.format("%.2f", amount)}", style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold, color = Primary)

    OutlinedButton(
        onClick = { clipboard.setText(AnnotatedString(pixCode)) },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp), tint = Primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Copiar código PIX", color = Primary)
    }

    WaitingRow("Aguardando confirmação do pagamento...")
}

// ─── Credit card ──────────────────────────────────────────────────────────────

@Composable
private fun CardProcessingContent(amount: Double) {
    Spacer(modifier = Modifier.height(40.dp))
    Box(
        modifier = Modifier.size(96.dp).clip(RoundedCornerShape(50)).background(Primary.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(56.dp), strokeWidth = 3.dp, color = Primary)
    }
    Text("Confirmando pagamento", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(
        "Estamos validando o seu cartão com a operadora. Isso pode levar alguns segundos.",
        style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
    Text("R$${String.format("%.2f", amount)}", style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold, color = Primary)
}

// ─── Boleto ───────────────────────────────────────────────────────────────────

@Composable
private fun BoletoContent(orderId: Long, amount: Double) {
    val clipboard = LocalClipboardManager.current
    val line = remember(orderId, amount) {
        val cents = (amount * 100).toLong().toString().padStart(10, '0')
        "34191.79001 01043.510047 91020.150008 8 ${orderId.toString().padStart(4, '0')}$cents"
    }

    Text("Boleto gerado", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
    Text(
        "O pagamento pode levar até 3 dias úteis para ser compensado.",
        style = MaterialTheme.typography.bodyMedium, color = TextSecondary,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Barcode()
            Text(line, fontFamily = FontFamily.Monospace, fontSize = 12.sp,
                color = TextPrimary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Text("R$${String.format("%.2f", amount)}", style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold, color = Primary)
        }
    }

    OutlinedButton(
        onClick = { clipboard.setText(AnnotatedString(line)) },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(Icons.Default.ContentCopy, null, modifier = Modifier.size(18.dp), tint = Primary)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Copiar código de barras", color = Primary)
    }

    WaitingRow("Aguardando pagamento do boleto")
}

// ─── Shared bits ──────────────────────────────────────────────────────────────

@Composable
private fun WaitingRow(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Primary)
        Text(text, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

/** Decorative QR-like grid (not scannable) — placeholder until a real PIX payload/QR lib is wired. */
@Composable
private fun QrPlaceholder(seed: Int) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val n = 25
        val cell = size.width / n
        fun rect(r: Int, c: Int) = drawRect(
            color = Color.Black,
            topLeft = Offset(c * cell, r * cell),
            size = Size(cell, cell)
        )
        fun finder(r0: Int, c0: Int) {
            for (r in 0 until 7) for (c in 0 until 7) {
                val edge = r == 0 || r == 6 || c == 0 || c == 6
                val core = r in 2..4 && c in 2..4
                if (edge || core) rect(r0 + r, c0 + c)
            }
        }
        // data modules (pseudo-random, deterministic)
        for (r in 0 until n) for (c in 0 until n) {
            val inFinder = (r < 8 && c < 8) || (r < 8 && c > n - 9) || (r > n - 9 && c < 8)
            if (!inFinder && ((r * 31 + c * 17 + seed) % 5) < 2) rect(r, c)
        }
        finder(0, 0)
        finder(0, n - 7)
        finder(n - 7, 0)
    }
}

/** Decorative barcode (not scannable). */
@Composable
private fun Barcode() {
    Canvas(modifier = Modifier.fillMaxWidth().height(56.dp)) {
        var x = 0f
        var i = 0
        while (x < size.width) {
            val w = (2 + (i * 13 % 4)).dp.toPx()
            if (i % 2 == 0) drawRect(Color.Black, topLeft = Offset(x, 0f), size = Size(w, size.height))
            x += w
            i++
        }
    }
}
