package design.codeux.biometric_storage

import androidx.biometric.*
import androidx.biometric.BiometricManager.Authenticators.*
import androidx.fragment.app.FragmentActivity
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import javax.crypto.Cipher

class BiometricsManager(
    private val activity: FragmentActivity,
    private val context: Context,
    private val logger: CustomLogger
) {

    private lateinit var fingerprintManager: FingerprintManagerCompat
    private val isAndroidROrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
    
    fun authenticate(cipher: Cipher?,
                     onSuccess: (cipher: Cipher?) -> Unit,
                     onFailure: ErrorCallback,
                     promptInfo: AndroidPromptInfo,
                     options: InitOptions
    ) {

        val prompt = BiometricPrompt(activity, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                logger.trace("onAuthenticationError($errorCode, $errString)")
                onFailure(AuthenticationErrorInfo(AuthenticationError.forCode(errorCode), errString))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                logger.trace("onAuthenticationSucceeded($result)")
                onSuccess(result.cryptoObject?.cipher)
            }

            override fun onAuthenticationFailed() {
                logger.trace("onAuthenticationFailed()")
            }
        })

        val promptBuilder = BiometricPrompt.PromptInfo.Builder()
            .setTitle(promptInfo.title)
            .setSubtitle(promptInfo.subtitle)
            .setDescription(promptInfo.description)
            .setConfirmationRequired(promptInfo.confirmationRequired)

        val biometricOnly = options.androidBiometricOnly

        if (biometricOnly) {
            promptBuilder.setAllowedAuthenticators(BIOMETRIC_STRONG)
            promptBuilder.setNegativeButtonText(promptInfo.negativeButton)
        } else if (isAndroidROrAbove){
            promptBuilder.setAllowedAuthenticators(DEVICE_CREDENTIAL or BIOMETRIC_STRONG)
        } else {
            promptBuilder.setNegativeButtonText(promptInfo.negativeButton)
        }

        if (cipher == null || options.authenticationValidityDurationSeconds >= 0) {
            // if authenticationValidityDurationSeconds is not -1 we can't use a CryptoObject
            logger.debug("Authenticating without cipher. ${options.authenticationValidityDurationSeconds}")
            prompt.authenticate(promptBuilder.build())
        } else {
            prompt.authenticate(promptBuilder.build(), BiometricPrompt.CryptoObject(cipher))
        }
    }

    fun hasEnrolledFingerprints(): Boolean {
        if (isAndroidROrAbove) { 
            return true
        }

        fingerprintManager = FingerprintManagerCompat.from(context)
        return fingerprintManager.hasEnrolledFingerprints()
    }
}
