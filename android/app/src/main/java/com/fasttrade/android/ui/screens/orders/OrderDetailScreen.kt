package com.fasttrade.android.ui.screens.orders

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fasttrade.android.data.model.OrderStatus
import com.fasttrade.android.data.model.toOrderStatus
import com.fasttrade.android.ui.components.*
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@Composable
fun OrderDetailScreen(
    orderId: Long,
    onBack: () -> Unit,
    onViewPayment: (orderId: Long, method: String, amount: Double) -> Unit = { _, _, _ -> },
    viewModel: AppViewModel = hiltViewModel()
) {
    val order by viewModel.selectedOrder.collectAsState()
    val isLoading by viewModel.orderLoading.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }
    var cancelling       by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = { Text("Cancelar pedido") },
            text  = { Text("Tem certeza que deseja cancelar este pedido?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        cancelling = true
                        viewModel.cancelOrder(orderId) { success ->
                            cancelling = false
                            if (success) onBack()
                        }
                    }
                ) { Text("Sim, cancelar", color = Color(0xFFD32F2F)) }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) { Text("Voltar") }
            }
        )
    }

    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // ── Amber header ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Primary)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart).size(32.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = Color.White)
            }
            Text(
                text = "PEDIDOS / VENDA",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (isLoading || order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Primary)
            }
        } else {
            val o = order!!
            val currentStatus = o.status.toOrderStatus()
            val subtotal      = o.items.sumOf { it.subtotal }  // unitPrice * quantity
            val taxAmount     = o.tax  // calculada e cobrada pelo backend
            val isCancellable = currentStatus == OrderStatus.PENDING && !cancelling

            Box(modifier = Modifier.fillMaxSize()) {
                // Scrollable content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        // leave room for bottom buttons
                        .padding(bottom = when {
                            currentStatus == OrderStatus.PENDING                  -> 184.dp  // pagamento + cancelar
                            currentStatus == OrderStatus.TRANSIT                  -> 72.dp   // confirm button
                            else                                                  -> 24.dp
                        }),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Spacer(modifier = Modifier.height(4.dp))

                    // ── Order info card ───────────────────────────────────────
                    DetailCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Número Do Pedido ${o.orderNumber}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarToday, null, tint = TextSecondary, modifier = Modifier.size(12.dp))
                                Text("Data: ${o.createdAt.take(10).replace("-", "/")}", fontSize = 11.sp, color = TextSecondary)
                            }
                            Text(
                                text = "Valor: R\$ ${String.format("%.2f", o.total)}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    // ── Delivery address ──────────────────────────────────────
                    o.deliveryAddress?.let { addr ->
                        DetailCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(15.dp))
                                        Text("Endereço de entrega", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                                    }
                                    Text(
                                        text = "${addr.street}, ${addr.number}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextPrimary,
                                        modifier = Modifier.padding(start = 21.dp)
                                    )
                                    Text(
                                        text = "${addr.city}, ${addr.state}",
                                        fontSize = 11.sp,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(start = 21.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFE8F5E9)
                                ) {
                                    Text(
                                        "Entrega Expressa",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // ── Payment method ────────────────────────────────────────
                    if (o.paymentMethod.isNotEmpty()) {
                        DetailCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            val (pmIcon, pmLabel, pmDetail) = when (o.paymentMethod.uppercase()) {
                                "PIX"         -> Triple(Icons.Default.QrCode,    "PIX",               "Pagamento instantâneo via chave PIX")
                                "CREDIT_CARD" -> Triple(Icons.Default.CreditCard,"Cartão de Crédito", "Cobrado no cartão de crédito informado")
                                "BOLETO"      -> Triple(Icons.Default.Receipt,   "Boleto Bancário",   "Vencimento em 3 dias úteis após emissão")
                                else          -> Triple(Icons.Default.CreditCard, o.paymentMethod,    "")
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Primary.copy(alpha = 0.10f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(pmIcon, null, tint = Primary, modifier = Modifier.size(20.dp))
                                }
                                Column {
                                    Text("Forma de pagamento", fontSize = 11.sp, color = TextSecondary)
                                    Text(pmLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                    if (pmDetail.isNotEmpty()) {
                                        Text(pmDetail, fontSize = 11.sp, color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }

                    // ── Status stepper ────────────────────────────────────────
                    if (currentStatus != OrderStatus.CANCELLED) {
                        DetailCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text("Status da entrega", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                IconStepper(currentStatus = currentStatus)
                            }
                        }
                    }

                    // ── Products ──────────────────────────────────────────────
                    DetailCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text("Produtos", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            o.items.forEachIndexed { idx, item ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF0F0F0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (item.product.imageUrl != null) {
                                            AsyncImage(
                                                model = item.product.imageUrl,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Icon(Icons.Default.Image, null, tint = Color(0xFFCCCCCC), modifier = Modifier.size(24.dp))
                                        }
                                    }
                                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                        Text(item.displayName, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                                        Text("Quantidade: ${item.quantity}", fontSize = 11.sp, color = TextSecondary)
                                    }
                                    Text(
                                        text = "R\$ ${String.format("%.2f", item.subtotal)}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Primary
                                    )
                                }
                                if (idx < o.items.size - 1) HorizontalDivider(color = Color(0xFFF0F0F0))
                            }
                        }
                    }

                    // ── Delivery options ──────────────────────────────────────
                    DetailCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Opções de entrega", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Person, null, tint = Primary, modifier = Modifier.size(16.dp))
                                Text(
                                    "Prefiro que me entreguem pessoalmente",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }

                    // ── Location ──────────────────────────────────────────────
                    DetailCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Place, null, tint = Primary, modifier = Modifier.size(16.dp))
                            Column {
                                Text("Localização", fontSize = 11.sp, color = TextSecondary)
                                Text(
                                    text = o.deliveryAddress?.let { "${it.neighborhood}, ${it.city}, ${it.state}" }
                                        ?: "Bairro, Cidade, Estado",
                                    fontSize = 13.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // ── Tracking code (transit / delivered) ───────────────────
                    if (currentStatus == OrderStatus.TRANSIT || currentStatus == OrderStatus.DELIVERED) {
                        DetailCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Código de rastreio", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    text = "000000000000",
                                    fontSize = 14.sp,
                                    color = TextSecondary,
                                    letterSpacing = 2.sp
                                )
                            }
                        }
                    }

                    // ── Price summary ─────────────────────────────────────────
                    DetailCard(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Resumo", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            SummaryRow("Total produtos", "R\$ ${String.format("%.2f", subtotal)}", TextPrimary)
                            SummaryRow("Frete", "R\$ ${String.format("%.2f", o.deliveryFee)}", TextPrimary)
                            SummaryRow("Taxa de troca", "R\$ ${String.format("%.2f", taxAmount)}", TextPrimary)
                            HorizontalDivider(color = Color(0xFFF0F0F0))
                            SummaryRow("Total:", "R\$ ${String.format("%.2f", o.total)}", TextPrimary, isBold = true)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // ── Bottom action button(s) ───────────────────────────────────
                val showConfirmDelivery = currentStatus == OrderStatus.TRANSIT
                val showCancel         = isCancellable
                val showPayment        = currentStatus == OrderStatus.PENDING && o.paymentMethod.isNotEmpty()

                if (showConfirmDelivery || showCancel || showPayment) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(Color(0xFFF5F5F5))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (showPayment) {
                            Button(
                                onClick = { onViewPayment(o.id, o.paymentMethod, o.total) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Icon(Icons.Default.QrCode, null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("VER PAGAMENTO", fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                            }
                        }

                        if (showConfirmDelivery) {
                            Button(
                                onClick = { /* CONFIRMAR RECEBIMENTO — future feature */ },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C))
                            ) {
                                Text("CONFIRMAR RECEBIMENTO", fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 1.sp)
                            }
                        }

                        if (showCancel) {
                            OutlinedButton(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD32F2F))
                            ) {
                                if (cancelling) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp),
                                        color = Color(0xFFD32F2F), strokeWidth = 2.dp)
                                } else {
                                    Text("CANCELAR PEDIDO", fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ── Icon-based stepper ────────────────────────────────────────────────────────
@Composable
private fun IconStepper(currentStatus: OrderStatus) {
    data class Step(val icon: ImageVector, val label: String, val status: OrderStatus)

    val steps = listOf(
        Step(Icons.Default.HourglassEmpty, "Aguardando\nConfirmação", OrderStatus.PENDING),
        Step(Icons.Default.CheckCircle,    "Confirmado",               OrderStatus.CONFIRMED),
        Step(Icons.Default.Inventory2,     "Em\nseparação",            OrderStatus.SEPARATING),
        Step(Icons.Default.LocalShipping,  "Transporte",               OrderStatus.TRANSIT),
        Step(Icons.Default.Home,           "Entregue",                 OrderStatus.DELIVERED)
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        steps.forEachIndexed { index, step ->
            val isDone   = step.status.step <= currentStatus.step
            val isActive = step.status == currentStatus

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                // Icon circle
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                isActive -> Primary
                                isDone   -> Color(0xFF388E3C)
                                else     -> Color(0xFFEEEEEE)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = step.icon,
                        contentDescription = null,
                        tint = if (isDone || isActive) Color.White else Color(0xFFBDBDBD),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Connector (except last)
                if (index < steps.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                if (steps[index + 1].status.step <= currentStatus.step)
                                    Color(0xFF388E3C) else Color(0xFFE0E0E0)
                            )
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = step.label,
                    fontSize = 9.sp,
                    textAlign = TextAlign.Center,
                    color = when {
                        isActive -> Primary
                        isDone   -> Color(0xFF2E7D32)
                        else     -> Color(0xFF9E9E9E)
                    },
                    fontWeight = if (isActive || isDone) FontWeight.Bold else FontWeight.Normal,
                    lineHeight = 11.sp
                )
            }
        }
    }
}

// ── Detail card wrapper ───────────────────────────────────────────────────────
@Composable
private fun DetailCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), content = content)
    }
}

// ── Summary row ───────────────────────────────────────────────────────────────
@Composable
private fun SummaryRow(label: String, value: String, valueColor: Color, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = if (isBold) 14.sp else 12.sp,
            color = if (isBold) TextPrimary else TextSecondary,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = if (isBold) 14.sp else 12.sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Medium
        )
    }
}

