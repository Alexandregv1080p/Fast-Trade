package com.fasttrade.android.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fasttrade.android.data.model.UpdateProfileRequest
import com.fasttrade.android.ui.components.*
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onNavigateToConversations: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToTerms: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToFaq: () -> Unit = {},
    viewModel: AppViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isLoading by viewModel.profileLoading.collectAsState()

    var showLogoutDialog     by remember { mutableStateOf(false) }
    var showPasswordDialog   by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showEditAddressDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadProfile() }

    // ── Logout dialog ──────────────────────────────────────────────────────
    if (showLogoutDialog) {
        FtDialog(
            onDismiss = { showLogoutDialog = false },
            icon = Icons.Default.Logout,
            title = "Sair da conta",
            confirmText = "Sair",
            confirmColor = Error,
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
                onLogout()
            }
        ) {
            Text(
                "Tem certeza que deseja sair? Você precisará entrar novamente.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }

    // ── Change password dialog ─────────────────────────────────────────────
    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onConfirm = { current, new -> viewModel.changePassword(current, new) { ok, msg -> if (ok) showPasswordDialog = false } }
        )
    }

    // ── Edit profile dialog ────────────────────────────────────────────────
    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName  = profile?.name ?: "",
            currentPhone = profile?.phone ?: "",
            currentBirth = profile?.birthDate ?: "",
            onDismiss = { showEditProfileDialog = false },
            onConfirm = { name, phone, birth ->
                viewModel.updateProfileFields(UpdateProfileRequest(name = name, phone = phone, birthDate = birth)) { ok ->
                    if (ok) showEditProfileDialog = false
                }
            }
        )
    }

    // ── Edit address dialog ────────────────────────────────────────────────
    if (showEditAddressDialog) {
        EditAddressDialog(
            currentStreet = profile?.address?.street ?: "",
            currentNumber = profile?.address?.number ?: "",
            currentCity   = profile?.address?.city ?: "",
            currentState  = profile?.address?.state ?: "",
            currentZip    = profile?.address?.zipCode ?: "",
            onLookupCep = viewModel::lookupCep,
            onDismiss = { showEditAddressDialog = false },
            onConfirm = { street, number, city, state, zip ->
                viewModel.updateProfileFields(
                    UpdateProfileRequest(addressStreet = street, addressNumber = number, addressCity = city, addressState = state, addressZip = zip)
                ) { ok -> if (ok) showEditAddressDialog = false }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        FtTopBar(title = "MEU PERFIL")

        if (isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Avatar + name header ───────────────────────────────────
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(Primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = profile?.name?.take(2)?.uppercase() ?: "FT",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = profile?.name ?: "Usuário",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    profile?.email?.let { email ->
                        Text(text = email, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }

                    // Editar perfil
                    OutlinedButton(
                        onClick = { showEditProfileDialog = true },
                        shape = RoundedCornerShape(20.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Editar perfil", fontSize = 12.sp)
                    }
                }

                // ── Info card ──────────────────────────────────────────────
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        InfoRow(label = "CPF", value = profile?.cpf?.ifBlank { "—" } ?: "—")
                        FtDivider()
                        InfoRow(label = "Data de nascimento", value = profile?.birthDate?.ifBlank { "—" } ?: "—")
                        FtDivider()
                        InfoRow(label = "Telefone", value = profile?.phone?.ifBlank { "—" } ?: "—")
                        FtDivider()
                        InfoRow(label = "E-mail", value = profile?.email ?: "—")
                        FtDivider()
                        InfoRow(label = "Senha", value = "••••••••", onAlter = { showPasswordDialog = true })
                    }
                }

                // ── Address card ───────────────────────────────────────────
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Primary, modifier = Modifier.size(20.dp))
                                Text("Endereço", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                            }
                            TextButton(onClick = { showEditAddressDialog = true }) {
                                Text("ALTERAR", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }
                        }
                        val addr = profile?.address
                        if (addr != null && addr.street.isNotBlank()) {
                            Text("${addr.street}, ${addr.number}".trimEnd(',', ' '), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                            Text("${addr.zipCode} – ${addr.neighborhood}".trimEnd(' ', '–', ' '), style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("${addr.city}, ${addr.state}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        } else {
                            Text("Nenhum endereço cadastrado", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                }

                // ── Menu list ──────────────────────────────────────────────
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                    Column {
                        MenuRow(icon = Icons.Default.Info, label = "SOBRE", onClick = onNavigateToAbout)
                        FtDivider()
                        MenuRow(icon = Icons.Default.Description, label = "TERMOS DE USO", onClick = onNavigateToTerms)
                        FtDivider()
                        MenuRow(icon = Icons.Default.PrivacyTip, label = "POLÍTICAS DE PRIVACIDADE", onClick = onNavigateToPrivacy)
                        FtDivider()
                        MenuRow(icon = Icons.Default.HelpOutline, label = "FAQ", onClick = onNavigateToFaq)
                        FtDivider()
                        MenuRow(icon = Icons.Default.Forum, label = "MINHAS MENSAGENS", onClick = onNavigateToConversations)
                        FtDivider()
                        MenuRow(icon = Icons.Default.Chat, label = "FALE CONOSCO", onClick = onNavigateToSupport)
                    }
                }

                // ── Logout ─────────────────────────────────────────────────
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Surface)) {
                    MenuRow(
                        icon = Icons.Default.Logout,
                        label = "SAIR DA CONTA",
                        onClick = { showLogoutDialog = true },
                        tint = Error
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ─── Dialogs ────────────────────────────────────────────────────────────────

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (currentPw: String, newPw: String) -> Unit
) {
    var current  by remember { mutableStateOf("") }
    var newPw    by remember { mutableStateOf("") }
    var confirm  by remember { mutableStateOf("") }
    var showCurrent by remember { mutableStateOf(false) }
    var showNew     by remember { mutableStateOf(false) }
    var error    by remember { mutableStateOf<String?>(null) }

    FtDialog(
        onDismiss = onDismiss,
        icon = Icons.Default.Lock,
        title = "Alterar senha",
        confirmText = "Salvar",
        onConfirm = {
            when {
                current.isBlank() -> error = "Informe a senha atual"
                newPw.length < 8  -> error = "A nova senha deve ter no mínimo 8 caracteres"
                newPw != confirm  -> error = "As senhas não coincidem"
                else -> onConfirm(current, newPw)
            }
        }
    ) {
        if (error != null) {
            Text(error!!, color = Error, style = MaterialTheme.typography.bodySmall)
        }
        FtDialogField(
            value = current,
            onValueChange = { current = it; error = null },
            label = "Senha atual",
            leadingIcon = Icons.Default.Lock,
            isPassword = !showCurrent,
            keyboardType = KeyboardType.Password,
            trailingIcon = {
                IconButton(onClick = { showCurrent = !showCurrent }) {
                    Icon(if (showCurrent) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                }
            }
        )
        FtDialogField(
            value = newPw,
            onValueChange = { newPw = it; error = null },
            label = "Nova senha (mín. 8 caracteres)",
            leadingIcon = Icons.Default.LockReset,
            isPassword = !showNew,
            keyboardType = KeyboardType.Password,
            trailingIcon = {
                IconButton(onClick = { showNew = !showNew }) {
                    Icon(if (showNew) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                }
            }
        )
        FtDialogField(
            value = confirm,
            onValueChange = { confirm = it; error = null },
            label = "Confirmar nova senha",
            leadingIcon = Icons.Default.LockReset,
            isPassword = true,
            keyboardType = KeyboardType.Password
        )
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentPhone: String,
    currentBirth: String,
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, birth: String) -> Unit
) {
    var name  by remember { mutableStateOf(currentName) }
    var phone by remember { mutableStateOf(currentPhone) }
    var birth by remember { mutableStateOf(currentBirth) }

    FtDialog(
        onDismiss = onDismiss,
        icon = Icons.Default.Person,
        title = "Editar perfil",
        confirmText = "Salvar",
        confirmEnabled = name.isNotBlank(),
        onConfirm = { if (name.isNotBlank()) onConfirm(name, phone, birth) }
    ) {
        FtDialogField(
            value = name,
            onValueChange = { name = it },
            label = "Nome",
            leadingIcon = Icons.Default.Person
        )
        FtDialogField(
            value = phone,
            onValueChange = { phone = it },
            label = "Telefone",
            leadingIcon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone
        )
        FtDialogField(
            value = birth,
            onValueChange = { birth = it },
            label = "Data de nascimento (YYYY-MM-DD)",
            leadingIcon = Icons.Default.CalendarToday,
            keyboardType = KeyboardType.Number,
            placeholder = "1990-01-15"
        )
    }
}

@Composable
private fun EditAddressDialog(
    currentStreet: String,
    currentNumber: String,
    currentCity: String,
    currentState: String,
    currentZip: String,
    onLookupCep: (String, (com.fasttrade.android.data.model.ViaCepResponse?) -> Unit) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (street: String, number: String, city: String, state: String, zip: String) -> Unit
) {
    var street by remember { mutableStateOf(currentStreet) }
    var number by remember { mutableStateOf(currentNumber) }
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
        title = { Text("Editar endereço") },
        text = {
            val fieldShape = RoundedCornerShape(12.dp)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = zip,
                    onValueChange = { if (it.filter { c -> c.isDigit() }.length <= 8) zip = it },
                    label = { Text("CEP") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Primary) },
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
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = street,
                    onValueChange = { street = it },
                    label = { Text("Rua / Logradouro") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = Primary) },
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("Cidade") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = Primary) },
                    singleLine = true,
                    shape = fieldShape,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = number,
                        onValueChange = { number = it },
                        label = { Text("Número") },
                        leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = null, tint = Primary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = state,
                        onValueChange = { if (it.length <= 2) state = it.uppercase() },
                        label = { Text("UF") },
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = Primary) },
                        singleLine = true,
                        shape = fieldShape,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(street, number, city, state, zip) }) {
                Text("Salvar", color = Primary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// ─── Reusable row components ─────────────────────────────────────────────────

@Composable
private fun InfoRow(label: String, value: String, onAlter: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
        if (onAlter != null) {
            TextButton(onClick = onAlter) {
                Text("ALTERAR", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
private fun MenuRow(icon: ImageVector, label: String, onClick: () -> Unit, tint: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = tint,
            letterSpacing = 0.5.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextHint, modifier = Modifier.size(18.dp))
    }
}
