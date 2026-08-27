package com.fasttrade.android.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.fasttrade.android.ui.screens.auth.ForgotPasswordScreen
import com.fasttrade.android.ui.screens.auth.LoginScreen
import com.fasttrade.android.ui.screens.auth.RegisterScreen
import com.fasttrade.android.ui.screens.cart.CartScreen
import com.fasttrade.android.ui.screens.cart.CheckoutScreen
import com.fasttrade.android.ui.screens.cart.PaymentStatusScreen
import com.fasttrade.android.ui.screens.home.HomeScreen
import com.fasttrade.android.ui.screens.onboarding.OnboardingScreen
import com.fasttrade.android.ui.screens.orders.OrderDetailScreen
import com.fasttrade.android.ui.screens.orders.OrdersScreen
import com.fasttrade.android.ui.screens.chat.ConversationsScreen
import com.fasttrade.android.ui.screens.chat.DirectChatScreen
import com.fasttrade.android.ui.screens.product.ProductDetailScreen
import com.fasttrade.android.ui.screens.product.SellerProfileScreen
import com.fasttrade.android.ui.screens.profile.FaqScreen
import com.fasttrade.android.ui.screens.profile.ProfileScreen
import com.fasttrade.android.ui.screens.profile.StaticContentScreen
import com.fasttrade.android.ui.screens.profile.StaticPage
import com.fasttrade.android.ui.screens.splash.SplashScreen
import com.fasttrade.android.data.api.AuthEvents
import com.fasttrade.android.ui.screens.support.SupportChatScreen
import com.fasttrade.android.ui.theme.Primary
import com.fasttrade.android.ui.theme.TextHint
import com.fasttrade.android.viewmodel.AppViewModel
import androidx.hilt.navigation.compose.hiltViewModel

// ─── Route constants ────────────────────────────────────────────────────────
object Routes {
    const val SPLASH         = "splash"
    const val ONBOARDING     = "onboarding"
    const val LOGIN          = "login"
    const val REGISTER       = "register"
    const val FORGOT_PW      = "forgot_password"

    // Bottom nav roots
    const val HOME           = "home"
    const val CART           = "cart"
    const val CHECKOUT       = "checkout"
    const val PAYMENT_STATUS = "payment_status/{orderId}/{method}/{amount}/{auto}"
    const val ORDERS         = "orders"
    const val CONVERSATIONS  = "conversations"
    const val PROFILE        = "profile"

    // Detail screens (no bottom nav)
    const val PRODUCT_DETAIL  = "product/{productId}"
    const val ORDER_DETAIL    = "order/{orderId}"
    const val SELLER_PROFILE  = "seller/{sellerId}"
    const val SUPPORT         = "support"
    const val DIRECT_CHAT     = "direct_chat/{sellerId}/{sellerName}"
    const val ABOUT           = "about"
    const val TERMS           = "terms"
    const val PRIVACY         = "privacy"
    const val FAQ             = "faq"

    fun productDetail(id: Long) = "product/$id"
    fun orderDetail(id: Long)   = "order/$id"
    fun paymentStatus(orderId: Long, method: String, amount: Double, auto: Boolean = true) =
        "payment_status/$orderId/$method/$amount/$auto"
    fun sellerProfile(id: Long) = "seller/$id"
    fun directChat(sellerId: Long, sellerName: String) =
        "direct_chat/$sellerId/${android.net.Uri.encode(sellerName)}"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(Routes.HOME,          "Início",    Icons.Default.Home),
    BottomNavItem(Routes.ORDERS,        "Pedidos",   Icons.Default.Receipt),
    BottomNavItem(Routes.CART,          "Carrinho",  Icons.Default.ShoppingCart),
    BottomNavItem(Routes.CONVERSATIONS, "Chat",      Icons.Default.Forum),
    BottomNavItem(Routes.PROFILE,       "Perfil",    Icons.Default.Person)
)

// Routes that show the bottom nav bar
private val bottomNavRoutes = setOf(Routes.HOME, Routes.ORDERS, Routes.CART, Routes.CONVERSATIONS, Routes.PROFILE)

@Composable
fun AppNavigation(viewModel: AppViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val cartItemCount by viewModel.cartItemCount.collectAsState()
    val unreadMessages by viewModel.totalUnreadMessages.collectAsState()

    // Token expirado → logout automático e volta para login
    LaunchedEffect(Unit) {
        AuthEvents.unauthorizedEvent.collect {
            viewModel.logout()
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
                    bottomNavItems.forEach { item ->
                        val selected = navBackStackEntry?.destination?.hierarchy
                            ?.any { it.route == item.route } == true

                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                when {
                                    item.route == Routes.CART && cartItemCount > 0 ->
                                        BadgedBox(badge = { Badge { Text("$cartItemCount") } }) {
                                            Icon(item.icon, contentDescription = item.label)
                                        }
                                    item.route == Routes.CONVERSATIONS && unreadMessages > 0 ->
                                        BadgedBox(badge = { Badge { Text("$unreadMessages") } }) {
                                            Icon(item.icon, contentDescription = item.label)
                                        }
                                    else ->
                                        Icon(item.icon, contentDescription = item.label)
                                }
                            },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                indicatorColor = androidx.compose.ui.graphics.Color(0xFFF5E6C8),
                                unselectedIconColor = TextHint,
                                unselectedTextColor = TextHint
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.SPLASH) {
                val loggedIn by viewModel.isLoggedIn.collectAsState()
                SplashScreen(
                    isLoggedIn = loggedIn,
                    onNavigateToOnboarding = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    },
                    onNavigateToHome = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onGetStarted = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    }
                )
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onForgotPassword = { navController.navigate(Routes.FORGOT_PW) },
                    onRegister = { navController.navigate(Routes.REGISTER) }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Routes.FORGOT_PW) {
                ForgotPasswordScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.HOME) {
                HomeScreen(
                    onProductClick = { id -> navController.navigate(Routes.productDetail(id)) },
                    onCartClick = { navController.navigate(Routes.CART) },
                    onProfileClick = { navController.navigate(Routes.PROFILE) }
                )
            }

            composable(
                route = Routes.PRODUCT_DETAIL,
                arguments = listOf(navArgument("productId") { type = NavType.LongType })
            ) { backStackEntry ->
                val productId = backStackEntry.arguments?.getLong("productId") ?: return@composable
                ProductDetailScreen(
                    productId = productId,
                    onBack = { navController.popBackStack() },
                    onAddedToCart = { navController.navigate(Routes.CART) },
                    onOpenCart = { navController.navigate(Routes.CART) },
                    onSellerClick = { sellerId -> navController.navigate(Routes.sellerProfile(sellerId)) }
                )
            }

            composable(Routes.CART) {
                CartScreen(
                    onCheckout = { navController.navigate(Routes.CHECKOUT) },
                    onContinueShopping = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    viewModel = viewModel
                )
            }

            composable(Routes.CHECKOUT) {
                CheckoutScreen(
                    onBack = { navController.popBackStack() },
                    onOrderPlaced = { orderId, method, amount ->
                        navController.navigate(Routes.paymentStatus(orderId, method, amount)) {
                            popUpTo(Routes.CHECKOUT) { inclusive = true }
                        }
                    },
                    viewModel = viewModel
                )
            }

            composable(
                route = Routes.PAYMENT_STATUS,
                arguments = listOf(
                    navArgument("orderId") { type = NavType.LongType },
                    navArgument("method")  { type = NavType.StringType },
                    navArgument("amount")  { type = NavType.FloatType },
                    navArgument("auto")    { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getLong("orderId") ?: return@composable
                val method  = backStackEntry.arguments?.getString("method") ?: "PIX"
                val amount  = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
                val auto    = backStackEntry.arguments?.getBoolean("auto") ?: true
                PaymentStatusScreen(
                    orderId = orderId,
                    method = method,
                    amount = amount,
                    autoApprove = auto,
                    onDone = {
                        if (auto) {
                            navController.navigate(Routes.orderDetail(orderId)) {
                                popUpTo(Routes.PAYMENT_STATUS) { inclusive = true }
                            }
                        } else {
                            navController.popBackStack()  // reopened from the order → go back to it
                        }
                    }
                )
            }

            composable(Routes.ORDERS) {
                OrdersScreen(
                    onOrderClick = { id -> navController.navigate(Routes.orderDetail(id)) }
                )
            }

            composable(
                route = Routes.ORDER_DETAIL,
                arguments = listOf(navArgument("orderId") { type = NavType.LongType })
            ) { backStackEntry ->
                val orderId = backStackEntry.arguments?.getLong("orderId") ?: return@composable
                OrderDetailScreen(
                    orderId = orderId,
                    onBack = { navController.popBackStack() },
                    onViewPayment = { id, method, amount ->
                        navController.navigate(Routes.paymentStatus(id, method, amount, auto = false))
                    }
                )
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToSupport       = { navController.navigate(Routes.SUPPORT) },
                    onNavigateToConversations = { navController.navigate(Routes.CONVERSATIONS) },
                    onNavigateToAbout         = { navController.navigate(Routes.ABOUT) },
                    onNavigateToTerms         = { navController.navigate(Routes.TERMS) },
                    onNavigateToPrivacy       = { navController.navigate(Routes.PRIVACY) },
                    onNavigateToFaq           = { navController.navigate(Routes.FAQ) }
                )
            }

            composable(Routes.SUPPORT) {
                SupportChatScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.ABOUT) {
                StaticContentScreen(page = StaticPage.ABOUT, onBack = { navController.popBackStack() })
            }

            composable(Routes.TERMS) {
                StaticContentScreen(page = StaticPage.TERMS, onBack = { navController.popBackStack() })
            }

            composable(Routes.PRIVACY) {
                StaticContentScreen(page = StaticPage.PRIVACY, onBack = { navController.popBackStack() })
            }

            composable(Routes.FAQ) {
                FaqScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.CONVERSATIONS) {
                ConversationsScreen(
                    onConversationClick = { sellerId, sellerName ->
                        navController.navigate(Routes.directChat(sellerId, sellerName))
                    }
                    // onBack = null → tab raiz, sem seta de volta
                )
            }

            composable(
                route = Routes.DIRECT_CHAT,
                arguments = listOf(
                    navArgument("sellerId")   { type = NavType.LongType },
                    navArgument("sellerName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val sellerId   = backStackEntry.arguments?.getLong("sellerId") ?: return@composable
                val sellerName = backStackEntry.arguments?.getString("sellerName") ?: "Vendedor"
                DirectChatScreen(
                    sellerId   = sellerId,
                    sellerName = sellerName,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.SELLER_PROFILE,
                arguments = listOf(navArgument("sellerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sellerId = backStackEntry.arguments?.getLong("sellerId") ?: return@composable
                SellerProfileScreen(
                    sellerId = sellerId,
                    onBack = { navController.popBackStack() },
                    onSendMessage = { id, name ->
                        navController.navigate(Routes.directChat(id, name))
                    }
                )
            }
        }
    }
}
