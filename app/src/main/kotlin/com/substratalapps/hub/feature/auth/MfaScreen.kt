package com.substratalapps.hub.feature.auth

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.substratalapps.hub.network.MfaChallenge

/** AUTH-02 Two-factor challenge: a 6-digit TOTP code, or a recovery code. */
@Composable
fun MfaScreen(challenge: MfaChallenge, viewModel: AuthViewModel = hiltViewModel()) {
    val ui by viewModel.ui.collectAsStateWithLifecycle()
    var code by rememberSaveable { mutableStateOf("") }
    var recovery by rememberSaveable { mutableStateOf(false) }
    val valid = if (recovery) code.isNotBlank() else code.length == 6

    AuthFrame {
        Text("Two-factor authentication", style = MaterialTheme.typography.headlineMedium)
        Text(
            if (recovery) "Enter one of your recovery codes." else "Enter the 6-digit code from your authenticator app.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        OutlinedTextField(
            value = code,
            onValueChange = { v -> code = if (recovery) v else v.filter(Char::isDigit).take(6) },
            label = { Text(if (recovery) "Recovery code" else "Code") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = if (recovery) KeyboardType.Ascii else KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth().semantics { contentType = ContentType.SmsOtpCode },
        )
        ui.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Button(onClick = { viewModel.verify(code, recovery) }, enabled = valid && !ui.busy, modifier = Modifier.fillMaxWidth()) {
            if (ui.busy) CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp) else Text("Verify")
        }
        Row {
            if ("recovery_code" in challenge.methods) {
                TextButton(onClick = { recovery = !recovery; code = "" }) {
                    Text(if (recovery) "Use authenticator code" else "Use a recovery code")
                }
            }
            TextButton(onClick = viewModel::cancelMfa) { Text("Back to sign in") }
        }
    }
}
