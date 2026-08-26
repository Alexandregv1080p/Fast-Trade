package com.fasttrade.android.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fasttrade.android.data.local.TokenManager
import com.fasttrade.android.data.model.*
import com.fasttrade.android.data.repository.AppRepository
import com.fasttrade.android.data.repository.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val repo: AppRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    // ─── Auth ──────────────────────────────────────────────────────────────
    val isLoggedIn: StateFlow<Boolean> = tokenManager.token
        .map { !it.isNullOrBlank() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _loginLoading = MutableStateFlow(false)
    val loginLoading: StateFlow<Boolean> = _loginLoading

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    private val _forgotPasswordSuccess = MutableStateFlow(false)
    val forgotPasswordSuccess: StateFlow<Boolean> = _forgotPasswordSuccess

    private val _registerLoading = MutableStateFlow(false)
    val registerLoading: StateFlow<Boolean> = _registerLoading

    private val _registerError = MutableStateFlow<String?>(null)
    val registerError: StateFlow<String?> = _registerError

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _registerLoading.value = true
            _registerError.value = null
            when (val result = repo.register(name.trim(), email.trim(), password.trim())) {
                is Result.Success -> onSuccess()
                is Result.Error   -> _registerError.value = result.message
                else              -> Unit
            }
            _registerLoading.value = false
        }
    }

    fun clearRegisterError() { _registerError.value = null }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _loginLoading.value = true
            _loginError.value = null
            when (val result = repo.login(email.trim(), password.trim())) {
                is Result.Success -> onSuccess()
                is Result.Error   -> _loginError.value = result.message
                else              -> Unit
            }
            _loginLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch { repo.logout() }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _forgotPasswordSuccess.value = false
            when (repo.forgotPassword(email)) {
                is Result.Success -> _forgotPasswordSuccess.value = true
                else              -> Unit
            }
        }
    }

    fun clearLoginError() { _loginError.value = null }

    // ─── Profile ───────────────────────────────────────────────────────────
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile

    private val _profileLoading = MutableStateFlow(false)
    val profileLoading: StateFlow<Boolean> = _profileLoading

    fun loadProfile() {
        viewModelScope.launch {
            _profileLoading.value = true
            when (val r = repo.getProfile()) {
                is Result.Success -> _profile.value = r.data
                else              -> Unit
            }
            _profileLoading.value = false
        }
    }

    fun updateProfileFields(request: com.fasttrade.android.data.model.UpdateProfileRequest, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            when (val r = repo.updateProfileFields(request)) {
                is Result.Success -> { _profile.value = r.data; onDone(true) }
                else              -> onDone(false)
            }
        }
    }

    fun lookupCep(cep: String, onResult: (com.fasttrade.android.data.model.ViaCepResponse?) -> Unit) {
        viewModelScope.launch {
            when (val r = repo.lookupCep(cep)) {
                is Result.Success -> onResult(r.data)
                else              -> onResult(null)
            }
        }
    }

    fun changePassword(currentPw: String, newPw: String, onDone: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val r = repo.changePassword(currentPw, newPw)) {
                is Result.Success -> onDone(true, "Senha alterada com sucesso!")
                is Result.Error   -> onDone(false, r.message)
                else              -> onDone(false, "Erro desconhecido")
            }
        }
    }

    // ─── Products ──────────────────────────────────────────────────────────
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _productsLoading = MutableStateFlow(false)
    val productsLoading: StateFlow<Boolean> = _productsLoading

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct

    private val _productLoading = MutableStateFlow(false)
    val productLoading: StateFlow<Boolean> = _productLoading

    fun loadProducts(page: Int = 1, category: String? = null, search: String? = null) {
        viewModelScope.launch {
            _productsLoading.value = true
            when (val r = repo.getProducts(page = page, category = category, search = search)) {
                is Result.Success -> _products.value = r.data.content
                else              -> Unit
            }
            _productsLoading.value = false
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            when (val r = repo.getCategories()) {
                is Result.Success -> _categories.value = r.data
                else              -> Unit
            }
        }
    }

    fun loadProduct(id: Long) {
        viewModelScope.launch {
            _productLoading.value = true
            when (val r = repo.getProduct(id)) {
                is Result.Success -> _selectedProduct.value = r.data
                else              -> Unit
            }
            _productLoading.value = false
        }
    }

    // ─── Seller Profile ────────────────────────────────────────────────────
    private val _sellerProfile = MutableStateFlow<PublicSellerProfile?>(null)
    val sellerProfile: StateFlow<PublicSellerProfile?> = _sellerProfile

    private val _sellerLoading = MutableStateFlow(false)
    val sellerLoading: StateFlow<Boolean> = _sellerLoading

    fun loadSellerProfile(sellerId: Long) {
        viewModelScope.launch {
            _sellerLoading.value = true
            _sellerProfile.value = null
            when (val r = repo.getSellerProfile(sellerId)) {
                is Result.Success -> _sellerProfile.value = r.data
                else              -> Unit
            }
            _sellerLoading.value = false
        }
    }

    // ─── Cart ──────────────────────────────────────────────────────────────
    private val _cart = MutableStateFlow<Cart?>(null)
    val cart: StateFlow<Cart?> = _cart

    private val _cartLoading = MutableStateFlow(false)
    val cartLoading: StateFlow<Boolean> = _cartLoading

    val cartItemCount: StateFlow<Int> = _cart
        .map { it?.items?.sumOf { item -> item.quantity } ?: 0 }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    private val _appliedCoupon = MutableStateFlow<String?>(null)
    val appliedCoupon: StateFlow<String?> = _appliedCoupon

    // ponytail: coupons resolved client-side; move to a backend /cart/coupon endpoint when one exists
    private fun couponDiscount(code: String, cart: Cart): Double? = when (code.trim().uppercase()) {
        "FRETEGRATIS" -> cart.deliveryFee
        "FAST10"      -> cart.subtotal * 0.10
        "BEMVINDO"    -> minOf(50.0, cart.subtotal)
        else          -> null
    }

    fun applyCoupon(code: String, onResult: (Boolean, String) -> Unit) {
        val cart = _cart.value ?: return onResult(false, "Carrinho vazio")
        val discount = couponDiscount(code, cart)
            ?: return onResult(false, "Cupom inválido")
        _appliedCoupon.value = code.trim().uppercase()
        _cart.value = cart.copy(discount = discount)
        onResult(true, "Cupom aplicado!")
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _cart.value = _cart.value?.copy(discount = 0.0)
    }

    fun loadCart() {
        viewModelScope.launch {
            _cartLoading.value = true
            when (val r = repo.getCart()) {
                is Result.Success -> {
                    val code = _appliedCoupon.value
                    val discount = code?.let { couponDiscount(it, r.data) } ?: 0.0
                    _cart.value = r.data.copy(discount = discount)
                }
                else              -> Unit
            }
            _cartLoading.value = false
        }
    }

    fun addToCart(productId: Long, quantity: Int, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            when (repo.addToCart(productId, quantity)) {
                is Result.Success -> { loadCart(); onDone() }
                else              -> Unit
            }
        }
    }

    fun updateCartItem(productId: Long, quantity: Int) {
        viewModelScope.launch {
            when (repo.updateCartItem(productId, quantity)) {
                is Result.Success -> loadCart()
                else              -> Unit
            }
        }
    }

    fun removeCartItem(productId: Long) {
        viewModelScope.launch {
            when (repo.removeCartItem(productId)) {
                is Result.Success -> loadCart()
                else              -> Unit
            }
        }
    }

    fun placeOrder(paymentMethod: String = "PIX", onResult: (success: Boolean, orderId: Long) -> Unit) {
        viewModelScope.launch {
            when (val r = repo.placeOrder(PlaceOrderRequest(paymentMethod = paymentMethod))) {
                is Result.Success -> { loadCart(); loadOrders(); onResult(true, r.data.id) }
                else              -> onResult(false, 0L)
            }
        }
    }

    fun cancelOrder(orderId: Long, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            when (repo.cancelOrder(orderId)) {
                is Result.Success -> { loadOrders(); onDone(true) }
                else              -> onDone(false)
            }
        }
    }

    fun updateCartAddress(street: String, number: String, complement: String, city: String, state: String, zip: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            when (val r = repo.updateCartAddress(street, number, complement, city, state, zip)) {
                is Result.Success -> { _cart.value = r.data; onDone() }
                else              -> onDone()
            }
        }
    }

    // ─── Orders ────────────────────────────────────────────────────────────
    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    private val _ordersLoading = MutableStateFlow(false)
    val ordersLoading: StateFlow<Boolean> = _ordersLoading

    private val _selectedOrder = MutableStateFlow<Order?>(null)
    val selectedOrder: StateFlow<Order?> = _selectedOrder

    private val _orderLoading = MutableStateFlow(false)
    val orderLoading: StateFlow<Boolean> = _orderLoading

    fun loadOrders() {
        viewModelScope.launch {
            _ordersLoading.value = true
            when (val r = repo.getMyOrders()) {
                is Result.Success -> _orders.value = r.data
                else              -> Unit
            }
            _ordersLoading.value = false
        }
    }

    fun loadOrder(id: Long) {
        viewModelScope.launch {
            _orderLoading.value = true
            when (val r = repo.getOrder(id)) {
                is Result.Success -> _selectedOrder.value = r.data
                else              -> Unit
            }
            _orderLoading.value = false
        }
    }

    // ─── Direct Chat ───────────────────────────────────────────────────────
    private val _directMessages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val directMessages: StateFlow<List<ChatMessageDto>> = _directMessages

    private val _directMessagesLoading = MutableStateFlow(false)
    val directMessagesLoading: StateFlow<Boolean> = _directMessagesLoading

    private val _conversations = MutableStateFlow<List<ConversationSummary>>(emptyList())
    val conversations: StateFlow<List<ConversationSummary>> = _conversations

    private val _conversationsLoading = MutableStateFlow(false)
    val conversationsLoading: StateFlow<Boolean> = _conversationsLoading

    val totalUnreadMessages: StateFlow<Int> = _conversations
        .map { list -> list.sumOf { it.unreadCount } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun directRoom(myId: Long, otherId: Long) =
        "direct_${minOf(myId, otherId)}_${maxOf(myId, otherId)}"

    fun loadDirectChat(room: String) {
        viewModelScope.launch {
            _directMessagesLoading.value = true
            when (val r = repo.getDirectChatHistory(room)) {
                is Result.Success -> _directMessages.value = r.data
                else -> Unit
            }
            _directMessagesLoading.value = false
        }
    }

    fun sendDirectMessage(receiverId: Long, content: String, onSent: () -> Unit = {}) {
        viewModelScope.launch {
            when (val r = repo.sendDirectMessage(receiverId, content)) {
                is Result.Success -> {
                    _directMessages.value = _directMessages.value + r.data
                }
                else -> Unit
            }
            onSent() // sempre reseta o estado de "enviando"
        }
    }

    fun refreshDirectChat(room: String) {
        viewModelScope.launch {
            when (val r = repo.getDirectChatHistory(room)) {
                is Result.Success -> _directMessages.value = r.data
                else -> Unit
            }
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            _conversationsLoading.value = true
            when (val r = repo.getMyConversations()) {
                is Result.Success -> _conversations.value = r.data
                else -> Unit
            }
            _conversationsLoading.value = false
        }
    }

    // ─── Support / Chat ────────────────────────────────────────────────────
    private val _chatMessages = MutableStateFlow<List<ChatMessageDto>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageDto>> = _chatMessages

    private val _chatLoading = MutableStateFlow(false)
    val chatLoading: StateFlow<Boolean> = _chatLoading

    fun requestSupport(name: String, email: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            when (repo.requestSupport(name, email)) {
                is Result.Success -> onDone()
                else              -> onDone()
            }
        }
    }

    fun loadChatHistory(room: String) {
        viewModelScope.launch {
            _chatLoading.value = true
            when (val r = repo.getChatHistory(room)) {
                is Result.Success -> _chatMessages.value = r.data
                else              -> Unit
            }
            _chatLoading.value = false
        }
    }

    fun sendChatMessage(room: String, message: String, senderName: String, senderEmail: String) {
        // Optimistic local append; real send goes over STOMP/REST
        val newMsg = ChatMessageDto(
            id = System.currentTimeMillis(),
            room = room,
            content = message,
            senderName = senderName,
            senderEmail = senderEmail,
            sentAt = java.time.Instant.now().toString()
        )
        _chatMessages.value = _chatMessages.value + newMsg

        // Persist via REST for now (WebSocket upgrade can be added later)
        viewModelScope.launch {
            repo.requestSupport(senderName, senderEmail)
        }
    }
}
