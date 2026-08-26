package com.fasttrade.android.ui.screens.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fasttrade.android.data.model.CartItem
import com.fasttrade.android.ui.components.*
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@Composable
fun CartScreen(
    onOrderPlaced: (Long) -> Unit,
    viewModel: AppViewModel = hiltViewModel()
) {
    val cart by viewModel.cart.collectAsState()
    val isLoading by viewModel.cartLoading.collectAsState()
    var placingOrder      by remember { mutableStateOf(false) }
    var showAddressDialog by remember { mutableStateOf(false) }
    var orderError        by remember { mutableStateOf<String?>(null) }
    var selectedPayment   by remember { mutableStateOf("PIX") }
    // Card fields (only used when selectedPayment == "CREDIT_CARD")
    var cardNumber   by remember { mutableStateOf("") }
    var cardHolder   by remember { mutableStateOf("") }
    var cardExpiry   by remember { mutableStateOf("") }
    var cardCvv      by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadCart() }

    // ── Change address dialog ─────────────────────────────────────────────
    if (showAddressDialog) {
        val addr = cart?.deliveryAddress
        ChangeAddressDialog(
            currentStreet = addr?.street ?: "",
            currentCity   = addr?.city ?: "",
            currentState  = addr?.state ?: "",
            currentZip    = addr?.zipCode ?: "",
            onLookupCep = viewModel::lookupCep,
            onDismiss = { showAddressDialog = false },
            onConfirm = { street, city, state, zip ->
                viewModel.updateCartAddress(street, city, state, zip) { showAddressDialog = false }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        FtTopBar(title = "Carrinho")

        when {
            isLoading -> LoadingScreen()
            cart == null || cart!!.items.isEmpty() -> {
                EmptyState(
                    message = "Seu carrinho está vazio.\nAdicione produtos para continuar.",
                    icon = {
                        Icon(
                            Icons.Default.ShoppingCart, contentDescription = null,
                            tint = TextSecondary, modifier = Modifier.size(56.dp)
                        )
                    }
                )
            }
            else -> {
                val c = cart!!
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Delivery address card
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn, contentDescription = null,
                                            tint = Primary, modifier = Modifier.size(20.dp)
                                        )
                                        Column {
                                            Text(
                                                "Endereço de entrega",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = TextSecondary
                                            )
                                            val addrText = c.deliveryAddress?.let {
                                                buildString {
                                                    if (it.street.isNotBlank()) append(it.street)
                                                    if (it.city.isNotBlank()) append(", ${it.city}")
                                                    if (it.state.isNotBlank()) append(" - ${it.state}")
                                                }
                                            }?.takeIf { it.isNotBlank() } ?: "Nenhum endereço cadastrado"
                                            Text(
                                                text = addrText,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                    TextButton(onClick = { showAddressDialog = true }) {
                                        Text("Mudar", color = Primary, fontSize = 12.sp)
                                    }
                                }
                                FtDivider()
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Default.LocalShipping, contentDescription = null,
                                            tint = TextSecondary, modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            "Frete: R$${String.format("%.2f", c.deliveryFee)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    if (c.estimatedDelivery.isNotEmpty()) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.CalendarToday, contentDescription = null,
                                                tint = TextSecondary, modifier = Modifier.size(14.dp)
                                            )
                                            Text(
                                                "Estimativa: ${c.estimatedDelivery}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Products section header
                    item {
                        Text(
                            "Produtos",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(c.items) { item ->
                        CartItemRow(
                            item = item,
                            onIncrement = { viewModel.updateCartItem(item.product.id, item.quantity + 1) },
                            onDecrement = {
                                if (item.quantity > 1) viewModel.updateCartItem(item.product.id, item.quantity - 1)
                                else viewModel.removeCartItem(item.product.id)
                            },
                            onRemove = { viewModel.removeCartItem(item.product.id) }
                        )
                    }

                    // Summary
                    item {
                        FtDivider()
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 12.dp)
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

                    // Payment method
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

                    // Error message
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

                // Finalize button
                Surface(shadowElevation = 8.dp, color = Color.White) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                        FtButton(
                            text = if (placingOrder) "Processando..." else "Finalizar Pedido  •  R$${String.format("%.2f", c.total)}",
                            onClick = {
                                if (!placingOrder) {
                                    placingOrder = true
                                    orderError = null
                                    viewModel.placeOrder(selectedPayment) { success, orderId ->
                                        placingOrder = false
                                        if (success) onOrderPlaced(orderId)
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
    PaymentOption("PIX",           "PIX",                  "Aprovação instantânea",              Icons.Default.QrCode),
    PaymentOption("CREDIT_CARD",   "Cartão de Crédito",    "Até 12x sem juros",                  Icons.Default.CreditCard),
    PaymentOption("BOLETO",        "Boleto Bancário",       "Vence em 3 dias úteis",              Icons.Default.Receipt)
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

            // Credit card fields — expand when Cartão is selected
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

            // PIX info
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

            // Boleto info
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

// ─── Change Address Dialog ────────────────────────────────────────────────────

@Composable
private fun ChangeAddressDialog(
    currentStreet: String,
    currentCity: String,
    currentState: String,
    currentZip: String,
    onLookupCep: (String, (com.fasttrade.android.data.model.ViaCepResponse?) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (street: String, city: String, state: String, zip: String) -> Unit
) {
    var street by remember { mutableStateOf(currentStreet) }
    var city   by remember { mutableStateOf(currentCity) }
    var state  by remember { mutableStateOf(currentState) }
    var zip    by remember { mutableStateOf(currentZip) }
    var isLoadingCep by remember { mutableStateOf(false) }
    var cepError by remember { mutableStateOf(false) }

    val cepDigits = zip.filter { it.isDigit() }
    LaunchedEffect(cepDigits) {
        cepError = false
        if (cepDigits.length == 8) {
            isLoadingCep = true
            onLookupCep(cepDigits) { result ->
                isLoadingCep = false
                if (result != null) {
                    street = result.street
                    city = result.city
                    state = result.state
                } else {
                    cepError = true
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Endereço de entrega") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = zip,
                    onValueChange = { if (it.filter { c -> c.isDigit() }.length <= 8) zip = it },
                    label = { Text("CEP") },
                    trailingIcon = {
                        if (isLoadingCep) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Primary)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = cepError,
                    supportingText = { if (cepError) Text("CEP não encontrado") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Rua / Logradouro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Cidade") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = { if (it.length <= 2) state = it.uppercase() },
                    label = { Text("UF") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(street, city, state, zip) }) {
                Text("Salvar", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ─── Cart Item Row ────────────────────────────────────────────────────────────

@Composable
private fun CartItemRow(
    item: CartItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceVariant),
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
                    Icon(Icons.Default.Image, contentDescription = null, tint = TextHint)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        item.product.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                        Icon(
                            Icons.Default.Close, contentDescription = "Remover",
                            tint = TextSecondary, modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    "R$${String.format("%.2f", item.product.price)}",
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
                item.product.category?.name?.takeIf { it.isNotEmpty() }?.let { CategoryBadge(label = it) }
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(50)).background(SurfaceVariant)
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "-", modifier = Modifier.size(14.dp))
                    }
                    Text("${item.quantity}", fontWeight = FontWeight.Bold)
                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(28.dp).clip(RoundedCornerShape(50)).background(Primary)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "+", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("R$${String.format("%.2f", item.subtotal)}", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

// ─── Summary Row ─────────────────────────────────────────────────────────────

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    isDiscount: Boolean = false
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            label,
            style = if (isBold) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal,
            color = TextSecondary
        )
        Text(
            value,
            style = if (isBold) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal,
            color = if (isDiscount) Success else if (isBold) TextPrimary else TextSecondary
        )
    }
}
