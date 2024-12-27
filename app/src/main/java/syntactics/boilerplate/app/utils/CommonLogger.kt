package syntactics.boilerplate.app.utils

import android.annotation.SuppressLint
import android.util.Log
import syntactics.android.app.BuildConfig
import syntactics.boilerplate.app.base.CommonsLib


/**
 * The base class for all application log with remote reporting tool
 *
 * android.util.Log will only work on Debug Mode
 *
 */

@SuppressLint("LogNotTimber")
class CommonLogger {

    private val isDebugMode = BuildConfig.DEBUG

    fun devLog(tag: String = "Default", message: Any?, throwable: Throwable? = null) {
        if (isDebugMode && CommonsLib.isDevCommonLoggerEnable)
            Log.i("$DEV_LOG/$tag", message.toString())
    }

    fun sysLog(tag: String = "Default", message: Any?, throwable: Throwable? = null) {
        instance.sysLogI(tag, message ?: "null", throwable)
    }

    fun sysLogV(tag: String = "Default", message: Any? = "-", throwable: Throwable? = null) {
        if (isDebugMode && CommonsLib.isSystemCommonLoggerEnable)
            Log.v("$SYS_LOG/$tag", "$message", throwable)
    }

    fun sysLogD(tag: String = "Default", message: Any? = "-", throwable: Throwable? = null) {
        if (isDebugMode && CommonsLib.isSystemCommonLoggerEnable)
            Log.d("$SYS_LOG/$tag", "$message", throwable)
    }

    fun sysLogI(tag: String = "Default", message: Any? = "-", throwable: Throwable? = null) {
        if (isDebugMode && CommonsLib.isSystemCommonLoggerEnable)
            Log.i("$SYS_LOG/$tag", "$message", throwable)
    }

    fun sysLogW(tag: String = "Default", message: Any? = "-", throwable: Throwable? = null) {
        if (isDebugMode && CommonsLib.isSystemCommonLoggerEnable)
            Log.w("$SYS_LOG/$tag", "$message", throwable)
    }

    fun sysLogE(tag: String = "Default", message: Any? = "-", throwable: Throwable? = null) {
        if (isDebugMode && CommonsLib.isSystemCommonLoggerEnable)
            Log.e("$SYS_LOG/$tag", "$message", throwable)
    }

    fun sysLogWTF(tag: String = "Default", message: Any? = "-", throwable: Throwable? = null) {
        if (isDebugMode && CommonsLib.isSystemCommonLoggerEnable)
            Log.wtf("$SYS_LOG/$tag", "$message", throwable)
    }

    companion object {

        private const val DEV_LOG = "DevLog"
        private const val SYS_LOG = "SysLog"

        val instance: CommonLogger by lazy {
            CommonLogger()
        }

        /**
         * devlog use for debuging purpose
         * @param tag - Class Tag
         * @param message - Log message
         * @param throwable - class for all errors and exceptions.
         */
        fun devLog(tag: String = "Default", message: Any?, throwable: Throwable? = null) {
            instance.devLog(tag, message ?: "null", throwable)
        }


        /**
         * sysLog use for Lyka System Info Log
         * @param tag - Class Tag
         * @param message - Log message
         * @param throwable - class for all errors and exceptions.
         */
        fun sysLog(tag: String = "Default", message: Any?, throwable: Throwable? = null) {
            instance.sysLogI(tag, message ?: "null", throwable)
        }

        /**
         * sysLog use for Lyka System Info Log as an Error
         * @param tag - Class Tag
         * @param message - Log message
         * @param throwable - class for all errors and exceptions.
         */
        fun sysLogE(tag: String = "Default", message: Any?, throwable: Throwable? = null) {
            instance.sysLogE(tag, message ?: "null", throwable)
        }

    }
}