package com.fasttrade.android.ui.screens.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fasttrade.android.ui.components.*
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onOrderPlaced: (orderId: Long, method: String, amount: Double) -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val cart by viewModel.cart.collectAsState()
    var placingOrder    by remember { mutableStateOf(false) }
    var orderError      by remember { mutableStateOf<String?>(null) }
    var selectedPayment by remember { mutableStateOf("PIX") }
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv    by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        FtTopBar(title = "Pagamento", onBack = onBack)

        val c = cart
        if (c == null || c.items.isEmpty()) {
            EmptyState(message = "Seu carrinho está vazio.")
            return@Column
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SummaryRow("Subtotal", "R$${String.format("%.2f", c.subtotal)}")
                        SummaryRow("Frete", "R$${String.format("%.2f", c.deliveryFee)}")
                        if (c.discount > 0) {
                            SummaryRow("Desconto", "-R$${String.format("%.2f", c.discount)}", isDiscount = true)
                        }
                        FtDivider()
                        SummaryRow("Total", "R$${String.format("%.2f", c.total)}", isBold = true)
                    }
                }
            }

            item {
                PaymentMethodSelector(
                    selected = selectedPayment,
                    onSelect = { selectedPayment = it },
                    cardNumber = cardNumber, onCardNumber = { cardNumber = it },
                    cardHolder = cardHolder, onCardHolder = { cardHolder = it },
                    cardExpiry = cardExpiry, onCardExpiry = { cardExpiry = it },
                    cardCvv    = cardCvv,    onCardCvv    = { cardCvv = it }
                )
            }

            if (orderError != null) {
                item {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Error.copy(alpha = 0.1f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Error, modifier = Modifier.size(18.dp))
                            Text(orderError!!, style = MaterialTheme.typography.bodySmall, color = Error)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        Surface(shadowElevation = 8.dp, color = Color.White) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                FtButton(
                    text = if (placingOrder) "Processando..." else "Finalizar Pedido  •  R$${String.format("%.2f", c.total)}",
                    onClick = {
                        if (!placingOrder) {
                            placingOrder = true
                            orderError = null
                            val total = c.total
                            viewModel.placeOrder(selectedPayment) { success, orderId ->
                                placingOrder = false
                                if (success) onOrderPlaced(orderId, selectedPayment, total)
                                else orderError = "Não foi possível finalizar o pedido. Tente novamente."
                            }
                        }
                    },
                    loading = placingOrder
                )
            }
        }
    }
}

// ─── Payment Method Selector ──────────────────────────────────────────────────

private data class PaymentOption(
    val key: String,
    val label: String,
    val subtitle: String,
    val icon: ImageVector
)

private val paymentOptions = listOf(
    PaymentOption("PIX",         "PIX",               "Aprovação instantânea", Icons.Default.QrCode),
    PaymentOption("CREDIT_CARD", "Cartão de Crédito", "Até 12x sem juros",     Icons.Default.CreditCard),
    PaymentOption("BOLETO",      "Boleto Bancário",   "Vence em 3 dias úteis", Icons.Default.Receipt)
)

@Composable
private fun PaymentMethodSelector(
    selected: String,
    onSelect: (String) -> Unit,
    cardNumber: String, onCardNumber: (String) -> Unit,
    cardHolder: String, onCardHolder: (String) -> Unit,
    cardExpiry: String, onCardExpiry: (String) -> Unit,
    cardCvv: String,    onCardCvv: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Forma de pagamento", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        paymentOptions.forEach { option ->
            val isSelected = selected == option.key
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) Primary.copy(alpha = 0.08f) else Surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Primary else Color(0xFFE0E0E0),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(option.key) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(option.icon, contentDescription = null,
                        tint = if (isSelected) Primary else TextSecondary,
                        modifier = Modifier.size(22.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(option.label, fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) Primary else TextPrimary)
                        Text(option.subtitle, style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary)
                    }
                    RadioButton(selected = isSelected, onClick = { onSelect(option.key) },
                        colors = RadioButtonDefaults.colors(selectedColor = Primary))
                }
            }

            AnimatedVisibility(
                visible = isSelected && option.key == "CREDIT_CARD",
                enter = expandVertically(),
                exit  = shrinkVertically()
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { raw ->
                                val digits = raw.filter { it.isDigit() }.take(16)
                                onCardNumber(digits.chunked(4).joinToString(" "))
                            },
                            label = { Text("Número do cartão") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            placeholder = { Text("0000 0000 0000 0000") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.CreditCard, null) }
                        )
                        OutlinedTextField(
                            value = cardHolder,
                            onValueChange = { onCardHolder(it.uppercase()) },
                            label = { Text("Nome do titular") },
                            placeholder = { Text("NOME COMO NO CARTÃO") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = cardExpiry,
                                onValueChange = { raw ->
                                    val digits = raw.filter { it.isDigit() }.take(4)
                                    onCardExpiry(if (digits.length > 2) "${digits.take(2)}/${digits.drop(2)}" else digits)
                                },
                                label = { Text("Validade") },
                                placeholder = { Text("MM/AA") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cardCvv,
                                onValueChange = { if (it.length <= 4 && it.all(Char::isDigit)) onCardCvv(it) },
                                label = { Text("CVV") },
                                placeholder = { Text("123") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                trailingIcon = { Icon(Icons.Default.Lock, null, modifier = Modifier.size(16.dp)) }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isSelected && option.key == "PIX",
                enter = expandVertically(),
                exit  = shrinkVertically()
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF388E3C), modifier = Modifier.size(18.dp))
                        Column {
                            Text("Pagamento instantâneo", fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp, color = Color(0xFF2E7D32))
                            Text("O QR Code será gerado após a confirmação do pedido.",
                                fontSize = 11.sp, color = Color(0xFF388E3C))
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = isSelected && option.key == "BOLETO",
                enter = expandVertically(),
                exit  = shrinkVertically()
            ) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, null, tint = Color(0xFFF57F17), modifier = Modifier.size(18.dp))
                        Column {
                            Text("Boleto Bancário", fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp, color = Color(0xFFE65100))
                            Text("O boleto será enviado por e-mail após a confirmação. Vence em 3 dias úteis.",
                                fontSize = 11.sp, color = Color(0xFFF57F17))
                        }
                    }
                }
            }
        }
    }
}
