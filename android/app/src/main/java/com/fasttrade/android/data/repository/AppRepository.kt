package com.fasttrade.android.data.repository

import com.fasttrade.android.data.api.FastTradeApi
import com.fasttrade.android.data.local.TokenManager
import com.fasttrade.android.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val code: Int = 0) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Singleton
class AppRepository @Inject constructor(
    private val api: FastTradeApi,
    private val tokenManager: TokenManager
) {
    // ── Auth ────────────────────────────────────────────────────────────────

    suspend fun login(email: String, password: String): Result<LoginResponse> = safeCall {
        val r = api.login(LoginRequest(email, password))
        if (r.isSuccessful) {
            val body = r.body()!!
            tokenManager.saveSession(body.token, body.name, body.email, body.role)
            Result.Success(body)
        } else Result.Error(r.message(), r.code())
    }

    suspend fun logout() { tokenManager.clearSession() }

    suspend fun forgotPassword(email: String): Result<MessageResponse> = safeCall {
        val r = api.forgotPassword(ForgotPasswordRequest(email))
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    // ── Profile ─────────────────────────────────────────────────────────────

    suspend fun getProfile(): Result<UserProfile> = safeCall {
        val r = api.getProfile()
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun updateProfileFields(request: UpdateProfileRequest): Result<UserProfile> = safeCall {
        val r = api.updateProfileFields(request)
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<MessageResponse> = safeCall {
        val r = api.changePassword(ChangePasswordRequest(currentPassword, newPassword))
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(
            when (r.code()) {
                401 -> "Senha atual incorreta"
                400 -> "A nova senha deve ter no mínimo 8 caracteres"
                else -> "Erro ao alterar senha"
            },
            r.code()
        )
    }

    // ── Products ────────────────────────────────────────────────────────────

    suspend fun getProducts(
        page: Int = 0, category: String? = null, search: String? = null
    ): Result<ProductPage> = safeCall {
        val r = api.getProducts(page = page, category = category, search = search)
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun getProduct(id: Long): Result<Product> = safeCall {
        val r = api.getProduct(id)
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun getCategories(): Result<List<String>> = safeCall {
        val r = api.getCategories()
        if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
        else Result.Error(r.message(), r.code())
    }

    // ── Cart ────────────────────────────────────────────────────────────────

    suspend fun getCart(): Result<Cart> = safeCall {
        val r = api.getCart()
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun addToCart(productId: Long, quantity: Int): Result<Cart> = safeCall {
        val r = api.addToCart(AddToCartRequest(productId, quantity))
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun updateCartItem(productId: Long, quantity: Int): Result<Cart> = safeCall {
        val r = api.updateCartItem(productId, quantity)
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun removeCartItem(productId: Long): Result<Cart> = safeCall {
        val r = api.removeCartItem(productId)
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun updateCartAddress(street: String, city: String, state: String, zip: String): Result<Cart> = safeCall {
        val r = api.updateCartAddress(mapOf("street" to street, "city" to city, "state" to state, "zip" to zip))
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    // ── Orders ──────────────────────────────────────────────────────────────

    suspend fun getMyOrders(): Result<List<Order>> = safeCall {
        val r = api.getMyOrders()
        if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
        else Result.Error(r.message(), r.code())
    }

    suspend fun getOrder(id: Long): Result<Order> = safeCall {
        val r = api.getOrder(id)
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun placeOrder(request: PlaceOrderRequest): Result<Order> = safeCall {
        val r = api.placeOrder(request)
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun cancelOrder(id: Long): Result<Order> = safeCall {
        val r = api.cancelOrder(id)
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    // ── Seller public profile ────────────────────────────────────────────────

    suspend fun getSellerProfile(sellerId: Long): Result<PublicSellerProfile> = safeCall {
        val r = api.getSellerPublicProfile(sellerId)
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    // ── Direct Chat ─────────────────────────────────────────────────────────

    suspend fun sendDirectMessage(receiverId: Long, content: String): Result<ChatMessageDto> = safeCall {
        val r = api.sendDirectMessage(DirectMessageRequest(receiverId, content))
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun getDirectChatHistory(room: String): Result<List<ChatMessageDto>> = safeCall {
        val r = api.getDirectChatHistory(room)
        if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
        else Result.Error(r.message(), r.code())
    }

    suspend fun getMyConversations(): Result<List<ConversationSummary>> = safeCall {
        val r = api.getMyConversations()
        if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
        else Result.Error(r.message(), r.code())
    }

    // ── Support ─────────────────────────────────────────────────────────────

    suspend fun requestSupport(name: String, email: String): Result<SupportResponse> = safeCall {
        val r = api.requestSupport(SupportRequest(name, email))
        if (r.isSuccessful) Result.Success(r.body()!!)
        else Result.Error(r.message(), r.code())
    }

    suspend fun getChatHistory(room: String): Result<List<ChatMessageDto>> = safeCall {
        val r = api.getChatHistory(room)
        if (r.isSuccessful) Result.Success(r.body() ?: emptyList())
        else Result.Error(r.message(), r.code())
    }

    // ── Helpers ─────────────────────────────────────────────────────────────

    private suspend fun <T> safeCall(call: suspend () -> Result<T>): Result<T> =
        try { call() } catch (e: Exception) { Result.Error(e.message ?: "Erro de conexão") }
}
