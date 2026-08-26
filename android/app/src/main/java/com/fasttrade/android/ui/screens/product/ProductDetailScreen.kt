package com.fasttrade.android.ui.screens.product

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.fasttrade.android.ui.components.*
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    productId: Long,
    onBack: () -> Unit,
    onAddedToCart: () -> Unit,
    onOpenCart: () -> Unit = {},
    onSellerClick: (Long) -> Unit = {},
    viewModel: AppViewModel = hiltViewModel()
) {
    val product by viewModel.selectedProduct.collectAsState()
    val isLoading by viewModel.productLoading.collectAsState()
    var selectedDelivery by remember { mutableStateOf(0) }
    var addingToCart by remember { mutableStateOf(false) }
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(productId) { viewModel.loadProduct(productId) }

    Scaffold(
        topBar = {
            FtTopBar(title = "Buscar", onBack = onBack)
        }
    ) { padding ->
        if (isLoading || product == null) {
            LoadingScreen()
        } else {
            val p = product!!

            // Simula até 3 imagens com a imageUrl (em prod o backend pode retornar lista)
            val images = listOfNotNull(p.imageUrl, p.imageUrl, p.imageUrl)
            val pagerState = rememberPagerState(pageCount = { images.size })

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Carousel de imagens ──────────────────────────────────────
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) { page ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFF5F5F5)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = images[page],
                            contentDescription = p.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Dots de paginação
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(images.size) { i ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (i == pagerState.currentPage) 10.dp else 7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (i == pagerState.currentPage) Primary
                                    else Color(0xFFDDDDDD)
                                )
                        )
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Nome + ações ─────────────────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = p.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f),
                            lineHeight = 22.sp
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.ChatBubbleOutline,
                                contentDescription = "Mensagem",
                                tint = TextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            Icon(
                                if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (isFavorite) Color(0xFFE53935) else TextSecondary,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { isFavorite = !isFavorite }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ── Miniaturas clicáveis ─────────────────────────────────
                    val scope = rememberCoroutineScope()
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        images.forEachIndexed { idx, url ->
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(
                                        width = if (idx == pagerState.currentPage) 2.dp else 1.dp,
                                        color = if (idx == pagerState.currentPage) Primary else Color(0xFFDDDDDD),
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .background(Color(0xFFF5F5F5))
                                    .clickable {
                                        scope.launch {
                                            pagerState.animateScrollToPage(idx)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                AsyncImage(
                                    model = url,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Variação de tamanho ──────────────────────────────────
                    Text(
                        text = "Variação do tamanho",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(6.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Único",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextPrimary
                            )
                            Icon(
                                Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Preço + botão carrinho ───────────────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Valor do produto",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "R$",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                                Text(
                                    text = " ${String.format("%.2f", p.price)}",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Primary
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "Comissão: R$ ${String.format("%.2f", p.price * 0.05)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(Primary)
                                .clickable { onOpenCart() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Abrir carrinho",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Botão adicionar ao carrinho ──────────────────────────
                    Button(
                        onClick = {
                            addingToCart = true
                            viewModel.addToCart(p.id, 1) {
                                addingToCart = false
                                onAddedToCart()
                            }
                        },
                        enabled = !addingToCart,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        if (addingToCart) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Adicionar ao carrinho", fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Localização ──────────────────────────────────────────
                    Text(
                        text = "Localização",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "São Paulo, SP, Brasil",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Opções de entrega ────────────────────────────────────
                    Text(
                        text = "Opções de entrega",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DeliveryOption(
                        label = "Prefiro que me entreguem via frete",
                        selected = selectedDelivery == 0,
                        onClick = { selectedDelivery = 0 }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DeliveryOption(
                        label = "Prefiro retirar no endereço",
                        selected = selectedDelivery == 1,
                        onClick = { selectedDelivery = 1 }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Card do Vendedor ─────────────────────────────────────
                    val seller = p.sellerInfo
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Avatar
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(Primary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = seller?.name?.take(1)?.uppercase() ?: "V",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Primary
                                )
                            }
                            Column {
                                Text(
                                    text = seller?.name ?: "Vendedor",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (seller != null) "Score ${seller.score} · ${seller.trades} vendas" else "Ativo",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextSecondary
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = { seller?.id?.let { onSellerClick(it) } },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Primary)
                        ) {
                            Text(
                                text = "Ver perfil",
                                style = MaterialTheme.typography.labelSmall,
                                color = Primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats do vendedor (dados reais)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SellerStat(value = if ((seller?.score ?: 0) > 0) "${seller!!.score / 20.0}" else "5.0", label = "Avaliação")
                        SellerStatDivider()
                        SellerStat(value = (seller?.trades ?: 0).toString(), label = "Vendas")
                        SellerStatDivider()
                        SellerStat(value = "95%", label = "Resposta")
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Observação ───────────────────────────────────────────
                    Text(
                        text = "Observação",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (p.specifications.isNotBlank()) p.specifications
                        else "Produto em excelente estado. Entre em contato para mais informações sobre disponibilidade e condições.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // ── Descrição do produto ─────────────────────────────────
                    Text(
                        text = "Descrição do produto",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Detalhes",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = p.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun DeliveryOption(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = selected,
            onCheckedChange = { onClick() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Primary,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFBDBDBD)
            ),
            modifier = Modifier.scale(0.75f)
        )
    }
}

@Composable
private fun SellerStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
    }
}

@Composable
private fun SellerStatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(28.dp)
            .background(Color(0xFFE0E0E0))
    )
}
