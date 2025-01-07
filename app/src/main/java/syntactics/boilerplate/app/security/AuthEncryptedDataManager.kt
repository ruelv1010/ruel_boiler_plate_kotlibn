package syntactics.boilerplate.app.security

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import syntactics.boilerplate.app.base.CommonsLib
import syntactics.boilerplate.app.data.repositories.auth.response.AvatarData
import syntactics.boilerplate.app.data.repositories.auth.response.DateCreatedData
import syntactics.boilerplate.app.data.repositories.auth.response.UserData


class AuthEncryptedDataManager {

    private val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        ENCRYPTED_ALIAS_NAME,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    ).setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setKeySize(256)
        .build()

    private val masterKeyAlias = MasterKey.Builder(CommonsLib.context!!, ENCRYPTED_ALIAS_NAME)
        .setKeyGenParameterSpec(keyGenParameterSpec)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        CommonsLib.context!!,
        ENCRYPTED_PREFS_NAME,
        masterKeyAlias,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Function used to save the user access token in this sharedPref
     *
     */

    fun setID(myID: String) {
        sharedPreferences.edit(true) {
            putString(MYID, myID)
        }
    }
    fun setEmail(myEmail: String) {
        sharedPreferences.edit(true) {
            putString(EMAIL, myEmail)
        }
    }

    fun setPassword(myPass: String) {
        sharedPreferences.edit(true) {
            putString(PASSWORD, myPass)
        }
    }
    fun setAccessToken(token: String) {
        sharedPreferences.edit(true) {
            putString(ACCESS_TOKEN, token)
        }
    }

    /**
     * Function used to get the user access token in this sharedPref
     */
    fun getAccessToken() = sharedPreferences.getString(ACCESS_TOKEN, "") ?: ""
    fun getMYID() = sharedPreferences.getString(MYID, "") ?: ""
    fun getMyEmail() = sharedPreferences.getString(EMAIL, "") ?: ""
    fun getMyPassword() = sharedPreferences.getString(PASSWORD, "") ?: ""
    private var inMemoryUserData: UserData? = null
    private var inMemoryAvatarData: AvatarData? = null
    private var inMemoryDateCreatedData: DateCreatedData? = null

    /**
     * Function to set user's basic info
     */
    fun setUserBasicInfo(userInfo: UserData) {
        inMemoryUserData = userInfo

        setUserAvatarInfo(userInfo.avatar?: AvatarData())
        setUserDateInfo(userInfo.date_created?: DateCreatedData())

        sharedPreferences.edit(true) {
            putInt(USER_INFO_ID, userInfo.user_id?: 0)
            putString(USER_NAME, userInfo.name)
            putString(USER_FIRST_NAME, userInfo.firstname)
            putString(USER_LAST_NAME, userInfo.lastname)
            putString(USER_EMAIL, userInfo.email)
            putString(USER_USERNAME, userInfo.username)
            putString(USER_MIDDLE_NAME, userInfo.middlename)
        }
    }

    /**
     * Function used to get user's basic info
     */
    fun getUserBasicInfo(): UserData {
        if (inMemoryUserData == null) {
            inMemoryUserData = UserData().apply {
                user_id = sharedPreferences.getInt(USER_INFO_ID, 0)
                firstname = sharedPreferences.getString(USER_FIRST_NAME, "")
                email = sharedPreferences.getString(USER_EMAIL, "")
                name = sharedPreferences.getString(USER_NAME, "0")
                lastname = sharedPreferences.getString(USER_LAST_NAME, "0")
                middlename = sharedPreferences.getString(USER_MIDDLE_NAME, "0")
                username = sharedPreferences.getString(USER_USERNAME, "0")
            }
        }
        return inMemoryUserData ?: UserData()
    }

    /**
     * Function to set user's avatar info
     */
    fun setUserAvatarInfo(avatarInfo: AvatarData) {
        inMemoryAvatarData = avatarInfo
        sharedPreferences.edit(true) {
            putString(USER_AVATAR_DIRECTORY, avatarInfo.directory)
            putString(USER_AVATAR_FILENAME, avatarInfo.filename)
            putString(USER_AVATAR_FULL_PATH, avatarInfo.full_path)
            putString(USER_AVATAR_PATH, avatarInfo.path)
            putString(USER_AVATAR_THUMB_PATH, avatarInfo.thumb_path)
        }
    }

    /**
     * Function used to get user's avatar info
     */
    fun getUserAvatarInfo(): AvatarData {
        if (inMemoryAvatarData == null) {
            inMemoryAvatarData = AvatarData().apply {
                directory = sharedPreferences.getString(USER_AVATAR_DIRECTORY, "")
                filename = sharedPreferences.getString(USER_AVATAR_FILENAME, "")
                full_path = sharedPreferences.getString(USER_AVATAR_FULL_PATH, "")
                path = sharedPreferences.getString(USER_AVATAR_PATH, "")
                thumb_path = sharedPreferences.getString(USER_AVATAR_THUMB_PATH, "")
            }
        }
        return inMemoryAvatarData ?: AvatarData()
    }

    /**
     * Function to set user's date created info
     */
    fun setUserDateInfo(dateInfo: DateCreatedData) {
        inMemoryDateCreatedData = dateInfo
        sharedPreferences.edit(true) {
            putString(USER_DATE_DB, dateInfo.date_db)
            putString(USER_DATE_MONTH_YEAR, dateInfo.month_year)
            putString(USER_DATE_TIME_PASSED, dateInfo.time_passed)
            putString(USER_DATE_TIMESTAMP, dateInfo.timestamp)
        }
    }

    /**
     * Function used to get user's date created info
     */
    fun getUserDateInfo(): DateCreatedData {
        if (inMemoryDateCreatedData == null) {
            inMemoryDateCreatedData = DateCreatedData().apply {
                date_db = sharedPreferences.getString(USER_DATE_DB, "")
                month_year = sharedPreferences.getString(USER_DATE_MONTH_YEAR, "")
                time_passed = sharedPreferences.getString(USER_DATE_TIME_PASSED, "")
                timestamp = sharedPreferences.getString(USER_DATE_TIMESTAMP, "")
            }
        }
        return inMemoryDateCreatedData ?: DateCreatedData()
    }

    fun isLoggedIn(): Boolean {
        return getAccessToken().isNotEmpty()
    }

    /**
     * Function used to clear all saved user info in this sharedPref
     * commonly used after success logout
     */
    fun clearUserInfo(){
        inMemoryUserData = UserData()
        inMemoryDateCreatedData = DateCreatedData()
        setAccessToken("")
    }

    //TODO Don't use Clear, Buggy on Library's current version
    fun clear() {
        sharedPreferences.edit(true) {
            clear()
            commit()
        }
    }

    /**
     * Function used to clear saved accessToken in this sharedPref
     * commonly used after success logout
     */
    fun resetToken() {
        setAccessToken("")
    }

    companion object{
        private const val MYID = "MYID"
        private const val EMAIL = "EMAIL"
        private const val PASSWORD = "PASSWORD"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val ENCRYPTED_PREFS_NAME = "ENCRYPTED_PREFS_NAME"
        private const val ENCRYPTED_ALIAS_NAME = "ENCRYPTED_ALIAS_NAME"

        private const val USER_INFO_ID = "USER_INFO_ID"
        private const val USER_FIRST_NAME = "USER_FIRST_NAME"
        private const val USER_LAST_NAME = "USER_LAST_NAME"
        private const val USER_MIDDLE_NAME = "USER_MIDDLE_NAME"
        private const val USER_NAME = "USER_NAME"
        private const val USER_EMAIL = "USER_EMAIL"
        private const val USER_USERNAME = "USER_USERNAME"

        private const val USER_AVATAR_DIRECTORY = "USER_AVATAR_DIRECTORY"
        private const val USER_AVATAR_FILENAME = "USER_AVATAR_FILENAME"
        private const val USER_AVATAR_FULL_PATH = "USER_AVATAR_FULL_PATH"
        private const val USER_AVATAR_PATH = "USER_AVATAR_PATH"
        private const val USER_AVATAR_THUMB_PATH = "USER_AVATAR_THUMB_PATH"

        private const val USER_DATE_DB = "USER_DATE_DB"
        private const val USER_DATE_MONTH_YEAR = "USER_DATE_MONTH_YEAR"
        private const val USER_DATE_TIME_PASSED = "USER_DATE_TIME_PASSED"
        private const val USER_DATE_TIMESTAMP = "USER_DATE_TIMESTAMP"

    }

}