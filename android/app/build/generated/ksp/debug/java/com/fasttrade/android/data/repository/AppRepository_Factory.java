package com.fasttrade.android.data.repository;

import com.fasttrade.android.data.api.FastTradeApi;
import com.fasttrade.android.data.local.TokenManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AppRepository_Factory implements Factory<AppRepository> {
  private final Provider<FastTradeApi> apiProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  public AppRepository_Factory(Provider<FastTradeApi> apiProvider,
      Provider<TokenManager> tokenManagerProvider) {
    this.apiProvider = apiProvider;
    this.tokenManagerProvider = tokenManagerProvider;
  }

  @Override
  public AppRepository get() {
    return newInstance(apiProvider.get(), tokenManagerProvider.get());
  }

  public static AppRepository_Factory create(Provider<FastTradeApi> apiProvider,
      Provider<TokenManager> tokenManagerProvider) {
    return new AppRepository_Factory(apiProvider, tokenManagerProvider);
  }

  public static AppRepository newInstance(FastTradeApi api, TokenManager tokenManager) {
    return new AppRepository(api, tokenManager);
  }
}
