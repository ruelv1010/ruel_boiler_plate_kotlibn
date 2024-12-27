package syntactics.boilerplate.app.data.repositories

import android.annotation.SuppressLint


import okhttp3.*
import okhttp3.CipherSuite.Companion.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.Companion.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256
import okhttp3.CipherSuite.Companion.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import syntactics.android.app.BuildConfig
import syntactics.boilerplate.app.data.repositories.interceptor.AccessTokenInterceptor
import syntactics.boilerplate.app.data.repositories.interceptor.NoAccessTokenInterceptor
import java.net.InetAddress
import java.security.GeneralSecurityException
import java.security.cert.X509Certificate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * https://square.github.io/okhttp/https/
 */
abstract class BaseRepository constructor(
    private var url: String,
    private var interceptor: Interceptor? = null,
    private var loggingInterceptor: HttpLoggingInterceptor? = defaultHttpLoggingInterceptor(),
    private var environment: Environment = Environment.PROD
) {

    /**
     * Legacy constructor still valid and supported
     *
     * To use custom authenticator and interceptor use the primary constructor
     */
    constructor(url: String, withUserInterceptor: Boolean = true, isDevEnv: Boolean = false) : this(
            url = url,

            // Add default Access Token Interceptor
            interceptor = if (withUserInterceptor) AccessTokenInterceptor() else NoAccessTokenInterceptor(),

            // Add environment
            environment = if (isDevEnv) Environment.DEV else Environment.PROD
    )

    val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(MoshiConverterFactory.create())
               .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
               .client(createOkHttpClient())
                .build()
    }

    private fun createOkHttpClient(): OkHttpClient {
        val builder = createDefaultBuilder()

        if (interceptor != null) {
            // Add interceptor for Lyka required headers
            builder.addInterceptor(interceptor!!)
        }

        if (BuildConfig.DEBUG && loggingInterceptor != null) {
            // Add logging interceptor (Console log) Default is in debug mode only
            builder.addInterceptor(loggingInterceptor!!)
        }

        if (environment == Environment.DEV && createSSLSocketFactory != null) {
            // Add SSL Socket Factory
            // Dev environment is the only one uses this
            builder.sslSocketFactory(createSSLSocketFactory!!.first, createSSLSocketFactory!!.second)
        }

        return builder.build()
    }


    private fun createDefaultBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .connectionSpecs(Collections.singletonList(createConnectionSpec()))
                .dns(object : Dns {
                    override fun lookup(hostname: String) =
                            InetAddress.getAllByName(hostname).toList()
                })
    }

    /**
     *  MODERN_TLS is a secure configuration that connects to modern HTTPS servers.
     *  You can build your own connection spec with a custom set of TLS versions and cipher suites.
     *  For example, this configuration is limited to three highly-regarded cipher suites.
     *  Its drawback is that it requires Android 5.0+ and a similarly current webserver.
     */
    private fun createConnectionSpec(): ConnectionSpec {
        return ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build()
    }

    /**
     * Use CertificatePinner to restrict which certificates
     * and certificate authorities are trusted.
     * Certificate pinning increases security,
     * but limits your server team’s abilities to update their TLS certificates.
     * Do not use certificate pinning without the blessing of your server’s TLS administrator!
     */
    private val createSSLSocketFactory: Pair<SSLSocketFactory, X509TrustManager>?
        get() {
            val sslSocketFactory: SSLSocketFactory
            val trustManager: X509TrustManager
            try {
                trustManager = object : X509TrustManager {
                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(java.security.cert.CertificateException::class)
                    override fun checkClientTrusted(chain: Array<X509Certificate>,
                                                    authType: String) = Unit

                    @SuppressLint("TrustAllX509TrustManager")
                    @Throws(java.security.cert.CertificateException::class)
                    override fun checkServerTrusted(chain: Array<X509Certificate>,
                                                    authType: String) = Unit

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, arrayOf<TrustManager>(trustManager), java.security.SecureRandom())
                sslSocketFactory = sslContext.socketFactory
            } catch (e: GeneralSecurityException) {
                throw RuntimeException(e)
            }
            return Pair(sslSocketFactory, trustManager)
        }

    enum class Environment {
        DEV,
        ALPHA,
        BETA,
        PROD,
    }

    companion object {
        private const val TIME_OUT = 30L

        private fun defaultHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
    }
}