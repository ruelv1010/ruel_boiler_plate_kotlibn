package syntactics.boilerplate.app.data.repositories.interceptor


import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import syntactics.boilerplate.app.utils.CommonLogger

class NoAccessTokenInterceptor(private val commonLogger: CommonLogger = CommonLogger.instance): Interceptor {

    companion object {
        private val TAG: String = AccessTokenInterceptor::class.java.simpleName
        var interceptorLogId = 0

        fun nextId(): Int {
            interceptorLogId++
            return interceptorLogId
        }
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val generatedId = nextId()
        commonLogger.sysLog(TAG, "Start of Interception $generatedId")
        val request = newRequestWithAccessToken(chain.request())
        return chain.proceed(request)
    }

    private fun newRequestWithAccessToken(request: Request): Request {
        return request.newBuilder()
                .build()
    }
}
