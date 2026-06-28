package com.fasttrade.android.ui.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fasttrade.android.data.model.Order
import com.fasttrade.android.ui.components.*
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@Composable
fun OrdersScreen(
    onOrderClick: (Long) -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val orders by viewModel.orders.collectAsState()
    val isLoading by viewModel.ordersLoading.collectAsState()

    // filter state: null = Todos, "COMPRA" = Compras, "VENDA" = Vendas
    var selectedFilter by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { viewModel.loadOrders() }

    val compras = orders.filter { it.status !in listOf("CANCELLED") }
    val vendas  = emptyList<Order>() // buyer-only app; vendas would come from a different endpoint

    val filtered = when (selectedFilter) {
        "COMPRA" -> compras
        "VENDA"  -> vendas
        else     -> orders
    }

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
            Text(
                text = "PEDIDOS",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        LazyColumn(
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ── Balance card ──────────────────────────────────────────────────
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "0,00 TRADEs",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text("Saldo", fontSize = 12.sp, color = TextSecondary)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "Ver histórico",
                                fontSize = 12.sp,
                                color = Primary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // ── Stats row ─────────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        amount = "0,00 TRADEs",
                        quantity = compras.size,
                        label = "Compras"
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        amount = "0,00 TRADEs",
                        quantity = 0,
                        label = "Vendas"
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Nav cards ─────────────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    NavCard(
                        title = "Compras",
                        subtitle = "Clique aqui para ser direcionado(a) aos pedidos de compra",
                        onClick = { selectedFilter = if (selectedFilter == "COMPRA") null else "COMPRA" },
                        isSelected = selectedFilter == "COMPRA"
                    )
                    NavCard(
                        title = "Vendas",
                        subtitle = "Clique aqui para ser direcionado(a) aos pedidos de venda",
                        onClick = { selectedFilter = if (selectedFilter == "VENDA") null else "VENDA" },
                        isSelected = selectedFilter == "VENDA"
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Filter chips ──────────────────────────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(null to "Todos", "PENDING" to "Pendentes", "SEPARATING" to "Em separação", "DELIVERED" to "Entregues").forEach { (value, label) ->
                        FilterChip(
                            selected = selectedFilter == value,
                            onClick = { selectedFilter = if (selectedFilter == value) null else value },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Primary,
                                selectedLabelColor = Color.White
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selectedFilter == value,
                                selectedBorderColor = Primary
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Order list ────────────────────────────────────────────────────
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            } else if (filtered.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, tint = TextHint, modifier = Modifier.size(48.dp))
                        Text("Nenhum pedido encontrado", color = TextSecondary, fontSize = 14.sp)
                    }
                }
            } else {
                items(filtered) { order ->
                    OrderListCard(
                        order = order,
                        onClick = { onOrderClick(order.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

// ── Stat card ─────────────────────────────────────────────────────────────────
@Composable
private fun StatCard(modifier: Modifier, amount: String, quantity: Int, label: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(amount, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Qt: $quantity", fontSize = 11.sp, color = TextSecondary)
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Primary)
        }
    }
}

// ── Nav card ──────────────────────────────────────────────────────────────────
@Composable
private fun NavCard(title: String, subtitle: String, onClick: () -> Unit, isSelected: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF8E1) else Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(subtitle, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
        }
    }
}

// ── Order list card ───────────────────────────────────────────────────────────
@Composable
fun OrderListCard(order: Order, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val statusInfo = orderStatusDisplay(order.status)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Top: order number + type
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pedido: ${order.orderNumber}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Compra",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Error
                )
            }

            // Status badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = statusInfo.second
            ) {
                Text(
                    text = statusInfo.first,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = statusInfo.third,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
                )
            }

            // Date + value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Data: ${order.createdAt.take(10).replace("-", "/")}",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Text(
                    text = "R\$ ${String.format("%.2f", order.total)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }
    }
}

// Status display helper: (label, background, textColor)
private fun orderStatusDisplay(status: String): Triple<String, Color, Color> = when (status.uppercase()) {
    "PENDING"              -> Triple("Aguardando",    Color(0xFFFFF9C4), Color(0xFF827717))
    "CONFIRMED"            -> Triple("Confirmado",    Color(0xFFE8F5E9), Color(0xFF2E7D32))
    "SEPARATING", "PICKING", "PACKED" -> Triple("Em separação", Color(0xFFFFF3E0), Color(0xFFE65100))
    "TRANSIT", "SHIPPED"   -> Triple("Em transporte", Color(0xFFE3F2FD), Color(0xFF1565C0))
    "DELIVERED"            -> Triple("Entregue",      Color(0xFFE8F5E9), Color(0xFF2E7D32))
    "CANCELLED"            -> Triple("Cancelado",     Color(0xFFFFEBEE), Color(0xFFC62828))
    "REFUNDED"             -> Triple("Reembolsado",   Color(0xFFF3E5F5), Color(0xFF6A1B9A))
    "DEVOLUTION"           -> Triple("Devolução",     Color(0xFFFCE4EC), Color(0xFFAD1457))
    else                   -> Triple(status,          Color(0xFFF5F5F5), Color(0xFF616161))
}
