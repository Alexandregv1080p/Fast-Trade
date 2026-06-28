package com.fasttrade.android.data.model

import com.google.gson.annotations.SerializedName

// ── Auth ────────────────────────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    @SerializedName("accessToken") val token: String,
    val name: String,
    val email: String,
    val role: String
)

data class ForgotPasswordRequest(val email: String)

// ── User ────────────────────────────────────────────────────────────────────

data class UserProfile(
    val id: Long = 0,
    val name: String = "",
    val email: String = "",
    @SerializedName("cpfCnpj") val cpf: String = "",
    val birthDate: String = "",
    val phone: String = "",
    val avatarUrl: String? = null,
    val address: Address? = null
)

data class Address(
    val id: Long = 0,
    val street: String = "",
    val number: String = "",
    val neighborhood: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = ""
) {
    fun formatted(): String = "$street, $number - $neighborhood, $city - $state, $zipCode"
}

// ── Product ─────────────────────────────────────────────────────────────────

data class CategoryInfo(
    val id: Long = 0,
    val name: String = "",
    val slug: String = ""
)

data class SellerInfo(
    val id: Long = 0,
    val name: String = "",
    val phone: String = "",
    val score: Int = 0,
    val trades: Int = 0
)

data class PublicSellerProfile(
    val id: Long = 0,
    val name: String = "",
    val phone: String = "",
    val score: Int = 0,
    val trades: Int = 0,
    val city: String = "",
    val state: String = "",
    val memberSince: String = ""
)

data class Product(
    val id: Long = 0,
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val category: CategoryInfo? = null,
    val stock: Int = 0,
    val sellerInfo: SellerInfo? = null,
    val specifications: String = "",
    @SerializedName("isActive") val active: Boolean = true
)

data class ProductPage(
    val data: List<Product> = emptyList(),       // backend field name is "data"
    val page: Int = 0,
    val pageSize: Int = 0,
    val total: Long = 0,
    val totalPages: Int = 0
) {
    val content: List<Product> get() = data      // convenience alias used in ViewModel
}

// ── Cart ────────────────────────────────────────────────────────────────────

data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    val subtotal: Double get() = product.price * quantity
}

data class Cart(
    val items: List<CartItem> = emptyList(),
    val deliveryFee: Double = 10.0,
    val discount: Double = 0.0,
    val estimatedDelivery: String = "",
    val deliveryAddress: Address? = null
) {
    val subtotal: Double get() = items.sumOf { it.subtotal }
    val total: Double get() = subtotal + deliveryFee - discount
}

data class AddToCartRequest(
    val productId: Long,
    val quantity: Int
)

// ── Order ───────────────────────────────────────────────────────────────────

data class Order(
    val id: Long = 0,
    val orderNumber: String = "",
    val status: String = "PENDING",
    val total: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val discount: Double = 0.0,
    @SerializedName("createdAt") val createdAt: String = "",
    val items: List<OrderItem> = emptyList(),
    val deliveryAddress: Address? = null,
    val paymentMethod: String = "",
    val sellerName: String = "",
    val sellerDistance: String = ""
)

data class OrderItem(
    val id: Long = 0,
    val productName: String = "",          // backend field
    val product: Product = Product(),      // may be empty in order responses
    val quantity: Int = 1,
    @SerializedName("price") val unitPrice: Double = 0.0   // backend sends "price"
) {
    val subtotal: Double get() = unitPrice * quantity
    val displayName: String get() = productName.ifBlank { product.name }
}

enum class OrderStatus(val label: String, val step: Int) {
    PENDING("Enviado", 0),
    CONFIRMED("Confirmado", 1),
    SEPARATING("Em Separação", 2),
    TRANSIT("Transporte", 3),
    DELIVERED("Entregue", 4),
    CANCELLED("Cancelado", -1)
}

fun String.toOrderStatus(): OrderStatus = when (this.uppercase()) {
    "PENDING"    -> OrderStatus.PENDING
    "CONFIRMED"  -> OrderStatus.CONFIRMED
    "SEPARATING", "EM_SEPARACAO" -> OrderStatus.SEPARATING
    "TRANSIT"    -> OrderStatus.TRANSIT
    "DELIVERED"  -> OrderStatus.DELIVERED
    "CANCELLED"  -> OrderStatus.CANCELLED
    else -> OrderStatus.PENDING
}

data class PlaceOrderRequest(
    val deliveryAddressId: Long? = null,
    val paymentMethod: String = "PIX"
)

// ── Chat ────────────────────────────────────────────────────────────────────

data class SupportRequest(
    val name: String,
    val email: String
)

data class SupportResponse(val room: String)

data class ChatMessageDto(
    val id: Long = 0,
    val room: String = "",
    val senderId: Long = 0,
    val senderName: String = "",
    val senderEmail: String = "",
    val senderRole: String = "",
    val content: String = "",
    val type: String = "TEXT",
    val sentAt: String = "",
    val readByAdmin: Boolean = false
)

// ── Direct Chat ─────────────────────────────────────────────────────────────

data class DirectMessageRequest(
    val receiverId: Long,
    val content: String
)

data class ConversationSummary(
    val room: String = "",
    val otherUserId: Long = 0,
    val otherUserName: String = "",
    val lastMessage: String = "",
    val lastMessageAt: String = "",
    val unreadCount: Int = 0
)

// ── Profile update ──────────────────────────────────────────────────────────

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)

data class UpdateProfileRequest(
    val name: String? = null,
    val phone: String? = null,
    val birthDate: String? = null,
    val addressStreet: String? = null,
    val addressCity: String? = null,
    val addressState: String? = null,
    val addressZip: String? = null
)

// ── Generic ─────────────────────────────────────────────────────────────────

data class ApiError(
    val message: String = "Erro desconhecido",
    val status: Int = 0
)

data class MessageResponse(val message: String)
