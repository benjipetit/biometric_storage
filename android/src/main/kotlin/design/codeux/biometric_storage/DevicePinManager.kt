package design.codeux.biometric_storage

import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import javax.crypto.Cipher

class DevicePinManager(
    private val activity: Activity,
    private val logger: CustomLogger
    ) {

    private val REQUEST_CODE = 1001
    private val keyguardManager = activity.getSystemService(KeyguardManager::class.java)
    private var onSuccess: ((cipher: Cipher?) -> Unit)? = null
    private var onFailure: ErrorCallback? = null

    fun authenticate(onSuccess: (cipher: Cipher?) -> Unit,
                     onFailure: ErrorCallback,
                     promptInfo: AndroidPromptInfo
    ) {
        logger.trace("DevicePinManager.authenticate()")
        this.onSuccess = onSuccess
        this.onFailure = onFailure
        val intent = keyguardManager.createConfirmDeviceCredentialIntent(promptInfo.title, promptInfo.subtitle)
        activity.startActivityForResult(intent, REQUEST_CODE)
    }

    fun handleAuthenticationResult(requestCode: Int, resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            this.onSuccess?.invoke(null)
        } else {
            this.onFailure?.invoke(
                AuthenticationErrorInfo(
                    AuthenticationError.ResetBiometrics,
                    "Incorrect result code, resultCode = ${resultCode}",
                )
            )
        }
    }
}
