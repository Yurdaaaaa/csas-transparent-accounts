package com.zj.core.csastest.di

import android.content.Context
import android.content.pm.PackageManager
import com.squareup.moshi.Moshi
import com.zj.core.csastest.data.TransparentAccountRepository
import com.zj.core.csastest.data.mapper.ApiToDbTransparentAccountMapper
import com.zj.core.csastest.net.TransparentAccountsApi
import com.zj.core.csastest.net.TransparentAccountsApiClient
import com.zj.core.csastest.net.TransparentAccountsNetworkManager
import com.zj.core.csastest.net.interceptor.IdempotencyInterceptor
import com.zj.core.csastest.net.interceptor.UserAgentInterceptor
import com.zj.core.csastest.net.interceptor.WebApiKeyInterceptor
import com.zj.csastest.core.BuildConfig
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit


@Module
object NetModule {

    @JvmStatic
    private fun userAgent(context: Context): String {
        val packageName = context.packageName ?: "csastest"

        val packageInfo = try {
            context.packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }

        val versionName = packageInfo?.versionName ?: "-1"
        val versionCode = packageInfo?.versionCode ?: -1 // Yes, i could have set minSdk to 28

        return "$packageName/v$versionName(vc$versionCode)/Android"
    }

    @AppScope
    @Provides
    @JvmStatic
    @BaseOkHttp
    fun baseOkHttpClient(context: Context): OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addNetworkInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        }

//        if (!BuildConfig.DEBUG) {
//            cache(Cache(context.cacheDir, (5 * 1024 * 1024).toLong()))
//        }
    }.build()

    @AppScope
    @Provides
    @JvmStatic
    @CsasOkHttp
    fun csasOkHttp(
        @BaseOkHttp baseOkHttpClient: OkHttpClient,
        context: Context
    ): OkHttpClient {
//        val errorParser = ErrorParser(moshi) // no error codes so this is redundant

        return baseOkHttpClient.newBuilder().apply {
            connectTimeout(40, TimeUnit.SECONDS)
            readTimeout(40, TimeUnit.SECONDS)
            writeTimeout(40, TimeUnit.SECONDS)

            addInterceptor(UserAgentInterceptor(userAgent(context)))
            addInterceptor(IdempotencyInterceptor()) // all csas request are @GET so this is redundant
            addInterceptor(WebApiKeyInterceptor(BuildConfig.API_KEY)) // add keys to local.properties !
        }.build()
    }

    @AppScope
    @Provides
    @JvmStatic
    fun transparentAccountsApiClient(
        @CsasOkHttp csasOkHttp: OkHttpClient,
        converterFactory: MoshiConverterFactory
    ): TransparentAccountsApiClient {

        val retrofit = Retrofit.Builder().baseUrl(
            if (BuildConfig.DEBUG) {
                BuildConfig.CSAS_SANDBOX_API // add keys to local.properties !
            } else {
                BuildConfig.CSAS_PROD_API // add keys to local.properties !
            }
        )
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(converterFactory)
        .client(csasOkHttp).build()

        val api = retrofit.create(TransparentAccountsApi::class.java)
        return TransparentAccountsApiClient(api)
    }

    @AppScope
    @Provides
    @JvmStatic
    fun moshi(): Moshi {
        return Moshi.Builder()
//            .add(LocalDateTime::class.java, Rfc3339DateJsonAdapter().nullSafe()) => if we would want to show data immediately this mosh-adapter would parse string to LocalDateTime
            .build()
    }

    @AppScope
    @Provides
    @JvmStatic
    fun moshiConverterFactory(moshi: Moshi): MoshiConverterFactory = MoshiConverterFactory.create(moshi)

    @AppScope
    @Provides
    @JvmStatic
    fun rxJava2CallAdapterFactory(): RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

    @AppScope
    @JvmStatic
    @Provides
    fun transparentAccountsNetworkManager(
        transparentAccountsApiClient: TransparentAccountsApiClient,
        transparentAccountRepository: TransparentAccountRepository,
        apiToDbTransparentAccountMapper: ApiToDbTransparentAccountMapper
    ) = TransparentAccountsNetworkManager(
        transparentAccountsApiClient,
        transparentAccountRepository,
        apiToDbTransparentAccountMapper,
        ioThreadScheduler()
    )

    @JvmStatic
    private fun ioThreadScheduler(): Scheduler {
        return Schedulers.io()  // Schedulers.io is designed as singleton tbh
    }
}