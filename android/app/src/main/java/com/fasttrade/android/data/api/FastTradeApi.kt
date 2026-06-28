package com.fasttrade.android.data.api

import com.fasttrade.android.data.model.*
import retrofit2.Response
import retrofit2.http.*
import retrofit2.http.Body

interface FastTradeApi {

    // ── Auth ────────────────────────────────────────────────────────────────

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>

    // ── User ────────────────────────────────────────────────────────────────

    @GET("user/me")
    suspend fun getProfile(): Response<UserProfile>

    @PUT("user/me")
    suspend fun updateProfileFields(@Body request: UpdateProfileRequest): Response<UserProfile>

    @POST("user/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<MessageResponse>

    // ── Products ────────────────────────────────────────────────────────────

    @GET("products")
    suspend fun getProducts(
        @Query("page")     page: Int = 1,
        @Query("pageSize") size: Int = 20,
        @Query("category") category: String? = null,
        @Query("search")   search: String? = null
    ): Response<ProductPage>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Long): Response<Product>

    @GET("products/categories")
    suspend fun getCategories(): Response<List<String>>

    // ── Cart ────────────────────────────────────────────────────────────────

    @GET("cart")
    suspend fun getCart(): Response<Cart>

    @POST("cart/items")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<Cart>

    @PUT("cart/items/{productId}")
    suspend fun updateCartItem(
        @Path("productId") productId: Long,
        @Query("quantity") quantity: Int
    ): Response<Cart>

    @DELETE("cart/items/{productId}")
    suspend fun removeCartItem(@Path("productId") productId: Long): Response<Cart>

    @DELETE("cart")
    suspend fun clearCart(): Response<MessageResponse>

    @PATCH("cart/address")
    suspend fun updateCartAddress(@Body body: Map<String, String>): Response<Cart>

    // ── Orders ──────────────────────────────────────────────────────────────

    @GET("orders/my")
    suspend fun getMyOrders(): Response<List<Order>>

    @GET("orders/{id}")
    suspend fun getOrder(@Path("id") id: Long): Response<Order>

    @POST("orders")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<Order>

    @PATCH("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") id: Long): Response<Order>

    // ── Seller public profile ────────────────────────────────────────────────

    @GET("user/{id}/public")
    suspend fun getSellerPublicProfile(@Path("id") id: Long): Response<PublicSellerProfile>

    // ── Direct Chat ─────────────────────────────────────────────────────────

    @POST("chat/direct/send")
    suspend fun sendDirectMessage(@Body request: DirectMessageRequest): Response<ChatMessageDto>

    @GET("chat/direct/{room}")
    suspend fun getDirectChatHistory(@Path("room") room: String): Response<List<ChatMessageDto>>

    @GET("chat/direct/conversations")
    suspend fun getMyConversations(): Response<List<ConversationSummary>>

    // ── Chat/Support ────────────────────────────────────────────────────────

    @POST("chat/support/request")
    suspend fun requestSupport(@Body request: SupportRequest): Response<SupportResponse>

    @GET("chat/history/{room}")
    suspend fun getChatHistory(@Path("room") room: String): Response<List<ChatMessageDto>>
}
