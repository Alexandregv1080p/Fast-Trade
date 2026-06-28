package com.fasttrade.android.data.api;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class ApiModule_ProvideApiFactory implements Factory<FastTradeApi> {
  private final Provider<Retrofit> retrofitProvider;

  public ApiModule_ProvideApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public FastTradeApi get() {
    return provideApi(retrofitProvider.get());
  }

  public static ApiModule_ProvideApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new ApiModule_ProvideApiFactory(retrofitProvider);
  }

  public static FastTradeApi provideApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(ApiModule.INSTANCE.provideApi(retrofit));
  }
}
