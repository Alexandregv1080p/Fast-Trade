# ─── Gson / modelos serializados ──────────────────────────────────────────────
# O release usa R8 (minifyEnabled=true). O Gson desserializa por reflexão nos
# nomes dos campos; sem estas regras o R8 renomeia os campos e o parse de
# Cart/Order/Product/etc. quebra silenciosamente em produção.
-keep class com.fasttrade.android.data.model.** { *; }
-keepclassmembers class com.fasttrade.android.data.model.** { *; }

# Preserva assinaturas genéricas (TypeToken, Response<T>) e anotações (@SerializedName)
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses, EnclosingMethod

# Campos anotados com @SerializedName não podem ser removidos/renomeados
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ─── Retrofit / OkHttp ────────────────────────────────────────────────────────
# As libs trazem regras de consumidor próprias; estes silêncios evitam ruído no R8.
-dontwarn okio.**
-dontwarn javax.annotation.**
