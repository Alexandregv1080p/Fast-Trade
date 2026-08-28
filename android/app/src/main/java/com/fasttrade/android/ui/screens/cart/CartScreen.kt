package com.fasttrade.android.ui.screens.cart

import androidx.compose.foundation.BorderStroke
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
    onCheckout: () -> Unit,
    onContinueShopping: () -> Unit = {},
    viewModel: AppViewModel = hiltViewModel()
) {
    val cart by viewModel.cart.collectAsState()
    val isLoading by viewModel.cartLoading.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    var showAddressDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadCart() }

    // ── Change address dialog ─────────────────────────────────────────────
    if (showAddressDialog) {
        val addr = cart?.deliveryAddress
        ChangeAddressDialog(
            currentStreet     = addr?.street ?: "",
            currentNumber     = addr?.number ?: "",
            currentComplement = addr?.complement ?: "",
            currentCity       = addr?.city ?: "",
            currentState      = addr?.state ?: "",
            currentZip        = addr?.zipCode ?: "",
            onLookupCep = viewModel::lookupCep,
            onDismiss = { showAddressDialog = false },
            onConfirm = { street, number, complement, city, state, zip ->
                viewModel.updateCartAddress(street, number, complement, city, state, zip) { showAddressDialog = false }
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

                    // Coupon
                    item {
                        CouponField(
                            applied = appliedCoupon,
                            onApply = { code, cb -> viewModel.applyCoupon(code, cb) },
                            onRemove = { viewModel.removeCoupon() }
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
                            if (c.tax > 0) {
                                SummaryRow("Taxa de troca", "R$${String.format("%.2f", c.tax)}")
                            }
                            if (c.discount > 0) {
                                SummaryRow("Desconto", "-R$${String.format("%.2f", c.discount)}", isDiscount = true)
                            }
                            FtDivider()
                            SummaryRow("Total", "R$${String.format("%.2f", c.total)}", isBold = true)
                        }
                    }

                    item {
                        OutlinedButton(
                            onClick = onContinueShopping,
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Primary),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, null, tint = Primary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Continuar comprando", color = Primary, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }

                // Continue to payment
                Surface(shadowElevation = 8.dp, color = Color.White) {
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                        FtButton(
                            text = "Ir para pagamento  •  R$${String.format("%.2f", c.total)}",
                            onClick = onCheckout
                        )
                    }
                }
            }
        }
    }
}

// ─── Coupon Field ─────────────────────────────────────────────────────────────

@Composable
private fun CouponField(
    applied: String?,
    onApply: (String, (Boolean, String) -> Unit) -> Unit,
    onRemove: () -> Unit
) {
    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    if (applied != null) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.08f)),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, Primary, RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.LocalOffer, null, tint = Primary, modifier = Modifier.size(20.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Cupom $applied", fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium, color = Primary)
                    Text("Desconto aplicado", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                TextButton(onClick = { onRemove(); message = null }) {
                    Text("Remover", color = Error, fontSize = 12.sp)
                }
            }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase(); isError = false; message = null },
                    label = { Text("Cupom de desconto") },
                    leadingIcon = { Icon(Icons.Default.LocalOffer, null, tint = Primary) },
                    singleLine = true,
                    isError = isError,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
                FilledTonalButton(
                    onClick = {
                        if (code.isNotBlank()) onApply(code) { ok, msg ->
                            isError = !ok; message = msg
                            if (ok) code = ""
                        }
                    },
                    enabled = code.isNotBlank(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(56.dp)
                ) { Text("Aplicar") }
            }
            message?.let {
                Text(it, style = MaterialTheme.typography.bodySmall,
                    color = if (isError) Error else Success)
            }
        }
    }
}

// ─── Change Address Dialog ────────────────────────────────────────────────────

@Composable
private fun ChangeAddressDialog(
    currentStreet: String,
    currentNumber: String,
    currentComplement: String,
    currentCity: String,
    currentState: String,
    currentZip: String,
    onLookupCep: (String, (com.fasttrade.android.data.model.ViaCepResponse?) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (street: String, number: String, complement: String, city: String, state: String, zip: String) -> Unit
) {
    var street     by remember { mutableStateOf(currentStreet) }
    var number     by remember { mutableStateOf(currentNumber) }
    var complement by remember { mutableStateOf(currentComplement) }
    var city       by remember { mutableStateOf(currentCity) }
    var state      by remember { mutableStateOf(currentState) }
    var zip        by remember { mutableStateOf(currentZip) }
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

    val fieldShape = RoundedCornerShape(12.dp)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Primary,
        unfocusedBorderColor = Color(0xFFDADCE0),
        focusedLabelColor = Primary,
        focusedLeadingIconColor = Primary,
        unfocusedLeadingIconColor = TextSecondary,
        cursorColor = Primary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = Color.White,
        icon = {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(50)).background(Primary.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.LocationOn, null, tint = Primary, modifier = Modifier.size(24.dp))
            }
        },
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Endereço de entrega", fontWeight = FontWeight.Bold)
                Text(
                    "Informe o CEP para preencher automaticamente",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = zip,
                    onValueChange = { if (it.filter { c -> c.isDigit() }.length <= 8) zip = it },
                    label = { Text("CEP") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                    trailingIcon = {
                        if (isLoadingCep) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Primary)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = cepError,
                    supportingText = { if (cepError) Text("CEP não encontrado") },
                    shape = fieldShape,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Rua / Logradouro") },
                    leadingIcon = { Icon(Icons.Default.Home, null) },
                    singleLine = true,
                    shape = fieldShape,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = number,
                        onValueChange = { number = it },
                        label = { Text("Número") },
                        singleLine = true,
                        shape = fieldShape,
                        colors = fieldColors,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = complement,
                        onValueChange = { complement = it },
                        label = { Text("Complemento") },
                        singleLine = true,
                        shape = fieldShape,
                        colors = fieldColors,
                        modifier = Modifier.weight(2f)
                    )
                }
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Cidade") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, null) },
                    singleLine = true,
                    shape = fieldShape,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = { if (it.length <= 2) state = it.uppercase() },
                    label = { Text("UF") },
                    leadingIcon = { Icon(Icons.Default.Public, null) },
                    singleLine = true,
                    shape = fieldShape,
                    colors = fieldColors,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(street, number, complement, city, state, zip) },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Salvar", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = TextSecondary) }
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
internal fun SummaryRow(
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
