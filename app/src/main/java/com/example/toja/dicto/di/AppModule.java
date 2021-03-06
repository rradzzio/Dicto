package com.example.toja.dicto.di;

import android.app.Application;

import androidx.room.Room;

import com.example.toja.dicto.persistance.TranslationDatabase;
import com.example.toja.dicto.utils.Keys;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.toja.dicto.persistance.TranslationDatabase.DATABASE_NAME;
import static com.example.toja.dicto.utils.Constants.WORDS_API_BASE_URL;
import static com.example.toja.dicto.utils.Constants.X_RAPIDAPI_HOST;
import static com.example.toja.dicto.utils.Constants.X_RAPIDAPI_HOST_VALUE;
import static com.example.toja.dicto.utils.Constants.X_RAPIDAPI_KEY;

@Module
public abstract class AppModule {

    private static final String TAG = "AppModule";

    @Singleton
    @Provides
    static TranslationDatabase provideTranslationDatabase(Application application) {
        return Room.databaseBuilder(
                application,
                TranslationDatabase.class,
                DATABASE_NAME
        ).build();
    }

    @Singleton
    @Provides
    static Retrofit provideRetrofitInstance() {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request originalRequest = chain.request();

                    Request newRequest = originalRequest.newBuilder()
                            .header(X_RAPIDAPI_HOST, X_RAPIDAPI_HOST_VALUE)
                            .header(X_RAPIDAPI_KEY,Keys.INSTANCE.apiKey())
                            .build();
                    return chain.proceed(newRequest);
                })
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(false)
                .build();

        return new Retrofit.Builder()
                .baseUrl(WORDS_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build();
    }
}
