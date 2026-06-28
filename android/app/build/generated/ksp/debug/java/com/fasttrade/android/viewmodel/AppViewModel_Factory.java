package com.fasttrade.android.viewmodel;

import com.fasttrade.android.data.local.TokenManager;
import com.fasttrade.android.data.repository.AppRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AppViewModel_Factory implements Factory<AppViewModel> {
  private final Provider<AppRepository> repoProvider;

  private final Provider<TokenManager> tokenManagerProvider;

  public AppViewModel_Factory(Provider<AppRepository> repoProvider,
      Provider<TokenManager> tokenManagerProvider) {
    this.repoProvider = repoProvider;
    this.tokenManagerProvider = tokenManagerProvider;
  }

  @Override
  public AppViewModel get() {
    return newInstance(repoProvider.get(), tokenManagerProvider.get());
  }

  public static AppViewModel_Factory create(Provider<AppRepository> repoProvider,
      Provider<TokenManager> tokenManagerProvider) {
    return new AppViewModel_Factory(repoProvider, tokenManagerProvider);
  }

  public static AppViewModel newInstance(AppRepository repo, TokenManager tokenManager) {
    return new AppViewModel(repo, tokenManager);
  }
}
