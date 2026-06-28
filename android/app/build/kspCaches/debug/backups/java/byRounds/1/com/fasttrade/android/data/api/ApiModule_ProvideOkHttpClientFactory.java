package com.fasttrade.android.data.api;

import com.fasttrade.android.data.local.TokenManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class ApiModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<TokenManager> tokenManagerProvider;

  public ApiModule_ProvideOkHttpClientFactory(Provider<TokenManager> tokenManagerProvider) {
    this.tokenManagerProvider = tokenManagerProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(tokenManagerProvider.get());
  }

  public static ApiModule_ProvideOkHttpClientFactory create(
      Provider<TokenManager> tokenManagerProvider) {
    return new ApiModule_ProvideOkHttpClientFactory(tokenManagerProvider);
  }

  public static OkHttpClient provideOkHttpClient(TokenManager tokenManager) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideOkHttpClient(tokenManager));
  }
}
