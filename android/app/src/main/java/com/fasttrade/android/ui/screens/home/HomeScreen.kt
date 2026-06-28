package com.fasttrade.android.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.fasttrade.android.R
import com.fasttrade.android.data.model.Product
import com.fasttrade.android.ui.theme.*
import com.fasttrade.android.viewmodel.AppViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onProductClick: (Long) -> Unit,
    onCartClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    viewModel: AppViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsState()
    val profile by viewModel.profile.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.productsLoading.collectAsState()
    var balanceVisible by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
        viewModel.loadCategories()
    }

    LaunchedEffect(selectedCategory) {
        viewModel.loadProducts(category = selectedCategory)
    }

    // Split products into featured (top 3), "ofertas" and "selecionados"
    val featured   = products.take(3)
    val ofertas    = products.drop(3).take(6)
    val selecionados = products.drop(9).take(6)

    // Banners estáticos usados apenas enquanto produtos não carregam
    val staticBanners = listOf(
        BannerItem("🔥 Ofertas do dia",      "Produtos com até 40% off",      Primary,               null),
        BannerItem("🆕 Novidades",           "Chegaram produtos novos",        Color(0xFF1B3A2A),     null),
        BannerItem("⭐ Selecionados p/ você","Baseado no seu perfil",          Color(0xFF4A3000),     null)
    )
    val bannerItems = if (featured.isEmpty()) staticBanners
                     else featured.map { p ->
                         BannerItem(p.name, "${String.format("%.2f", p.price)} TRADEs", Primary, p)
                     }
    val bannerPagerState = rememberPagerState(pageCount = { bannerItems.size })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // ── Header (amber background) ─────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Primary)
                    .padding(horizontal = 16.dp)
                    .padding(top = 12.dp, bottom = 16.dp)
            ) {
                Column {
                    // Address row + avatar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = run {
                                    val addr = profile?.address
                                    if (addr != null && addr.city.isNotBlank())
                                        "${addr.street}, ${addr.city} - ${addr.state}"
                                    else
                                        "Endereço, bairro, cidade, estado"
                                },
                                color = Color.White,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f))
                                .clickable { onProfileClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = profile?.name?.firstOrNull()?.toString() ?: "U",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Search bar + cart button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Search field
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = TextHint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "O QUE VOCÊ PROCURA?",
                                    color = TextHint,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                        // Cart button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            modifier = Modifier
                                .size(48.dp)
                                .clickable { onCartClick() }
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Carrinho",
                                    tint = Primary,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Balance card ──────────────────────────────────────────────────────
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
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
                            text = if (balanceVisible) "0,00 TRADEs" else "•••• TRADEs",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Saldo",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Ver histórico",
                            fontSize = 12.sp,
                            color = Primary,
                            fontWeight = FontWeight.SemiBold
                        )
                        IconButton(
                            onClick = { balanceVisible = !balanceVisible },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (balanceVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = Primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // ── Category filter ───────────────────────────────────────────────────
        item {
            CategoryFilterRow(
                categories = categories,
                selected = selectedCategory,
                onSelect = { selectedCategory = it }
            )
        }

        if (selectedCategory == null) {
            // ── Banner carousel ───────────────────────────────────────────────
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    HorizontalPager(
                        state = bannerPagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) { page ->
                        FeaturedBanner(
                            item = bannerItems[page],
                            onClick = { bannerItems[page].product?.let { onProductClick(it.id) } }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dot indicators
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(bannerItems.size) { index ->
                            val isSelected = bannerPagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (isSelected) 8.dp else 6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Primary else Color(0xFFCCCCCC))
                            )
                        }
                    }
                }
            }

            // ── Ofertas do dia ────────────────────────────────────────────────
            item {
                SectionHeader(
                    title = "Ofertas do dia",
                    onVerMais = { /* TODO */ }
                )
            }

            item {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(if (ofertas.isEmpty()) dummyProducts else ofertas) { product ->
                            ProductCardVertical(
                                product = product,
                                onClick = { if (product.id > 0) onProductClick(product.id) }
                            )
                        }
                    }
                }
            }

            // ── Selecionados para você ───────────────────────────────────────
            item {
                SectionHeader(
                    title = "Selecionados para você",
                    onVerMais = { /* TODO */ }
                )
            }

            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(if (selecionados.isEmpty()) dummyProducts else selecionados) { product ->
                        ProductCardVertical(
                            product = product,
                            onClick = { if (product.id > 0) onProductClick(product.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            // ── Resultados filtrados por categoria ──────────────────────────────
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 20.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${selectedCategory.displayCategory()} (${products.size})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Limpar filtro",
                        fontSize = 13.sp,
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { selectedCategory = null }
                    )
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Primary)
                    }
                }
            } else if (products.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = TextHint,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Nenhum produto encontrado nesta categoria",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(products.chunked(2)) { rowProducts ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowProducts.forEach { product ->
                            ProductCardVertical(
                                product = product,
                                onClick = { if (product.id > 0) onProductClick(product.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowProducts.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

private fun String?.displayCategory(): String =
    this?.replace("-", " ")?.replaceFirstChar { it.uppercase() } ?: "Todos"

// ── Section header ────────────────────────────────────────────────────────────
@Composable
private fun SectionHeader(title: String, onVerMais: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 20.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(
            text = "Ver mais",
            fontSize = 13.sp,
            color = Primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onVerMais() }
        )
    }
}

// ── Category filter row ─────────────────────────────────────────────────────
@Composable
private fun CategoryFilterRow(
    categories: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        Text(
            text = "Categorias",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 10.dp)
        )
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                CategoryChip(
                    label = "Todos",
                    icon = Icons.Default.Apps,
                    isSelected = selected == null,
                    onClick = { onSelect(null) }
                )
            }
            items(categories) { category ->
                CategoryChip(
                    label = category.displayCategory(),
                    icon = categoryIcon(category),
                    isSelected = selected == category,
                    onClick = { onSelect(if (selected == category) null else category) }
                )
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Primary else Color.White,
        border = BorderStroke(1.dp, if (isSelected) Primary else Color(0xFFE0E0E0)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else TextSecondary,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else TextPrimary
            )
        }
    }
}

private fun categoryIcon(category: String): ImageVector {
    val key = category.lowercase()
    return when {
        key.contains("eletron")                       -> Icons.Default.Devices
        key.contains("roupa") || key.contains("moda")  -> Icons.Default.Checkroom
        key.contains("casa")                           -> Icons.Default.Chair
        key.contains("esport")                          -> Icons.Default.SportsBasketball
        key.contains("livro")                           -> Icons.Default.MenuBook
        key.contains("game")                            -> Icons.Default.SportsEsports
        key.contains("beleza")                          -> Icons.Default.Spa
        key.contains("aliment") || key.contains("comida") -> Icons.Default.Restaurant
        key.contains("brinque")                         -> Icons.Default.Toys
        key.contains("automo") || key.contains("carro") -> Icons.Default.DirectionsCar
        else                                            -> Icons.Default.Category
    }
}

// ── Vertical product card (used in horizontal scroll rows) ────────────────────
@Composable
fun ProductCardVertical(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.width(160.dp)
) {
    var isFavorite by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Image + heart icon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                val categoryKey = (product.category?.slug ?: product.category?.name ?: "")
                    .lowercase()
                    .replace("ô", "o").replace("ç", "c").replace("ã", "a").replace(" ", "-")
                val placeholderRes = when {
                    categoryKey.contains("eletron") -> R.drawable.placeholder_eletronicos
                    categoryKey.contains("roupa")   -> R.drawable.placeholder_roupas
                    categoryKey.contains("casa")    -> R.drawable.placeholder_casa
                    categoryKey.contains("esport")  -> R.drawable.placeholder_esportes
                    categoryKey.contains("livro")   -> R.drawable.placeholder_livros
                    categoryKey.contains("game")    -> R.drawable.placeholder_games
                    else                            -> R.drawable.placeholder_produto
                }

                if (product.imageUrl != null) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Crop,
                        error = painterResource(placeholderRes),
                        placeholder = painterResource(placeholderRes)
                    )
                } else {
                    Image(
                        painter = painterResource(placeholderRes),
                        contentDescription = product.name,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                        contentScale = ContentScale.Fit
                    )
                }
                // Heart icon (top-right)
                IconButton(
                    onClick = { isFavorite = !isFavorite },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(36.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favoritar",
                        tint = if (isFavorite) Color.Red else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Name + price
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp
                )
                Text(
                    text = "${String.format("%.2f", product.price)} TRADEs",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
        }
    }
}

// ── Banner de destaque ────────────────────────────────────────────────────────

data class BannerItem(
    val title: String,
    val subtitle: String,
    val bgColor: Color,
    val product: Product?
)

@Composable
private fun FeaturedBanner(item: BannerItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(enabled = item.product != null) { onClick() }
    ) {
        // Imagem de fundo do produto (se disponível)
        if (item.product?.imageUrl != null) {
            AsyncImage(
                model = item.product.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(item.bgColor))
        }

        // Overlay gradiente escuro (para legibilidade)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.72f),
                            Color.Black.copy(alpha = 0.15f)
                        )
                    )
                )
        )

        // Textos sobrepostos
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .fillMaxWidth(0.65f),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (item.product != null) {
                // Badge "DESTAQUE"
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Primary
                ) {
                    Text(
                        text = "DESTAQUE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        letterSpacing = 1.sp
                    )
                }
            }
            Text(
                text = item.title,
                fontSize = if (item.product != null) 16.sp else 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )
            Text(
                text = item.subtitle,
                fontSize = 13.sp,
                fontWeight = if (item.product != null) FontWeight.Bold else FontWeight.Normal,
                color = if (item.product != null) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.85f)
            )
            if (item.product != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "Ver produto →",
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

// Dummy products para quando a API ainda não carregou
private val dummyProducts = listOf(
    Product(id = 0, name = "Carregando...", price = 0.0),
    Product(id = 0, name = "Carregando...", price = 0.0),
    Product(id = 0, name = "Carregando...", price = 0.0),
)
