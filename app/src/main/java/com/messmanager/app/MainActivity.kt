package com.messmanager.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.messmanager.app.data.repository.UpdateInfo
import com.messmanager.app.data.repository.UpdateRepository
import com.messmanager.app.ui.components.UpdateDialog
import com.messmanager.app.ui.navigation.NavGraph
import com.messmanager.app.ui.theme.MessManagementTheme
import com.messmanager.app.ui.welcome.AuthViewModel
import com.messmanager.app.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    @Inject
    lateinit var updateRepository: UpdateRepository

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                authViewModel.handleGoogleSignIn(credential)
            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign In Failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        NotificationHelper.createNotificationChannels(this)

        setContent {
            MessManagementTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var updateInfoState by remember { mutableStateOf<UpdateInfo?>(null) }

                    LaunchedEffect(Unit) {
                        val info = updateRepository.checkForUpdates()
                        if (info.hasUpdate && !updateRepository.isVersionDismissed(this@MainActivity, info.latestVersion)) {
                            updateInfoState = info
                        }
                    }

                    NavGraph(
                        authViewModel = authViewModel,
                        onGoogleSignInClick = { launchGoogleSignIn() }
                    )

                    updateInfoState?.let { info ->
                        UpdateDialog(
                            updateInfo = info,
                            updateRepository = updateRepository,
                            onDismiss = {
                                updateRepository.dismissVersion(this@MainActivity, info.latestVersion)
                                updateInfoState = null
                            }
                        )
                    }
                }
            }
        }
    }

    private fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInLauncher.launch(googleSignInClient.signInIntent)
    }
}
