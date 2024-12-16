package syntactics.boilerplate.app.data.repositories

class AppRetrofitService {

    /**
     * Builder for retrofit service, example usage is as follow
     *
     * ```
     * val somService: Service = RetrofitService.Builder()
     *                           .build(baseUrl="https://some-base.com", Service::class.java)
     * ```
     *
     *
     * or you can customize the properties before calling `Builder.build`
     * ```
     * val somService: Service = RetrofitService.Builder()
     *                           .withUserInterceptor(false)
     *                           .isDevEnv(false)
     *                           .instaBugLogsEnable(false)
     *                           .build(baseUrl="https://some-base.com", Service::class.java)
     * ```
     *
     *
     */
    data class Builder(
        var withUserInterceptor: Boolean = true,
        var isDevEnv: Boolean = true
        ) {

        fun withUserInterceptor(withUserInterceptor: Boolean) =
            apply { this.withUserInterceptor = withUserInterceptor }

        fun isDevEnv(isDevEnv: Boolean) = apply { this.isDevEnv = isDevEnv }

        fun <T> build(baseUrl: String, service: Class<*>): T {
            val remoteDataSource = BaseRemoteDataSource(
                url = baseUrl,
                withUserInterceptor = this.withUserInterceptor,
                isDevEnv = this.isDevEnv,
            )
            return remoteDataSource.createRetrofitService(service)

        }
    }
}

private class BaseRemoteDataSource(
    url: String,
    withUserInterceptor: Boolean,
    isDevEnv: Boolean,
) : BaseRepository(
    url = url,
    withUserInterceptor = withUserInterceptor,
    isDevEnv = isDevEnv,
) {
    @Suppress("UNCHECKED_CAST")
    fun <T> createRetrofitService(service: Class<*>): T {
        return retrofit.create(service) as T
    }

}