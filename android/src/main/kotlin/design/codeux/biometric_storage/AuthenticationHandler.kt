package design.codeux.biometric_storage

import androidx.fragment.app.FragmentActivity
import android.app.Activity
import android.content.Context
import javax.crypto.Cipher

class AuthenticationHandler(
    private val context: Context,
    private val activity: Activity,
    private val logger: CustomLogger
) {
    private val biometricsManager = BiometricsManager(activity as FragmentActivity, context, logger)
    private val devicePinManager = DevicePinManager(activity, logger)
    
    fun authenticate(
        onSuccess: (cipher: Cipher?) -> Unit,
        onFailure: ErrorCallback,
        cipher: Cipher?,
        promptInfo: AndroidPromptInfo,
        options: InitOptions
    ) {
        if (biometricsManager.hasEnrolledFingerprints() || options.androidBiometricOnly) {
            biometricsManager.authenticate(
                cipher,
                onSuccess,
                { error ->
                    onFailure(error) 
                    logger.trace("biometricsManager.authenticate failure - $error");
                },
                promptInfo,
                options)
        } else {
            logger.trace("authenticate devicePinManager")
            devicePinManager.authenticate(
                onSuccess,
                { error ->
                    onFailure(error) 
                    logger.trace("devicePinManager.authenticate - failed");
                },
                promptInfo)
        }
    }

    fun handleAuthenticationResult(requestCode: Int, resultCode: Int) {
        logger.trace("handleAuthenticationResult, resultCode=${resultCode}");
        devicePinManager.handleAuthenticationResult(requestCode, resultCode)
    }
}
