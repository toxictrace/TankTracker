package com.toxictrace.tanktracker.ui.screens

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.toxictrace.tanktracker.auth.AuthManager
import com.toxictrace.tanktracker.ui.theme.*

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LoginScreen(
    onLoginSuccess: (accountId: Long) -> Unit,
    onSkip: () -> Unit
) {
    val context = LocalContext.current
    var showWebView by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    if (showWebView) {
        WgWebViewScreen(
            url = AuthManager.AUTH_URL,
            onBack = { showWebView = false },
            onTokenReceived = { token, accountId, expiresAt ->
                // Fetch nickname then call onLoginSuccess
                AuthManager.saveAuth(context, token, accountId, "", expiresAt)
                onLoginSuccess(accountId)
            }
        )
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF0A0C0E), DarkBg, Color(0xFF0D1014))))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Text("⬡", color = NeonOrange, fontSize = 56.sp, fontWeight = FontWeight.Black)
            Spacer(modifier = Modifier.height(12.dp))
            Text("TANK TRACKER", color = Color.White, fontSize = 24.sp,
                fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace, letterSpacing = 4.sp)
            Text("EU SERVER STATISTICS", color = SteelGray, fontSize = 10.sp,
                fontFamily = FontFamily.Monospace, letterSpacing = 2.sp)

            Spacer(modifier = Modifier.height(48.dp))

            // WG Login button
            Button(
                onClick = { showWebView = true },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonOrange),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    "LOGIN WITH WARGAMING",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Benefits
            TacticalInfoBox()

            Spacer(modifier = Modifier.height(20.dp))

            // Skip / manual search
            TextButton(onClick = onSkip) {
                Text(
                    "Search by nickname instead",
                    color = SteelGray,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TacticalInfoBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurface, RoundedCornerShape(10.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(10.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("WHY LOGIN?", color = NeonOrange, fontSize = 9.sp,
            fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
        BenefitRow("✓", "Auto-load your profile every time")
        BenefitRow("✓", "No need to type your nickname")
        BenefitRow("✓", "Session tracker starts with your data")
        BenefitRow("✓", "Secure WG OAuth2 — no password stored")
    }
}

@Composable
private fun BenefitRow(icon: String, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(icon, color = NeonGreen, fontSize = 11.sp, fontWeight = FontWeight.Black)
        Text(text, color = SteelGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
    }
}

// ── WebView for WG OAuth ──────────────────────────────────────────────────────

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WgWebViewScreen(
    url: String,
    onBack: () -> Unit,
    onTokenReceived: (token: String, accountId: Long, expiresAt: Long) -> Unit
) {
    var pageTitle by remember { mutableStateOf("Wargaming Login") }

    Column(modifier = Modifier.fillMaxSize().background(DarkBg)) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth()
                .background(DarkSurface)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Default.ArrowBack, null, tint = SteelGray)
            }
            Text(
                pageTitle,
                color = SteelGray,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.weight(1f)
            )
        }

        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                            val requestUrl = request.url.toString()
                            // WG redirects to our redirect_uri with token in URL fragment or query
                            if (requestUrl.startsWith(AuthManager.REDIRECT_URI)) {
                                parseWgCallback(requestUrl)?.let { (token, accountId, expiresAt) ->
                                    onTokenReceived(token, accountId, expiresAt)
                                }
                                return true
                            }
                            return false
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            pageTitle = view?.title ?: "Wargaming Login"
                        }
                    }
                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

// Parse WG OAuth callback URL
// WG returns: https://redirect/?status=ok&access_token=TOKEN&account_id=ID&nickname=NICK&expires_at=TS
private fun parseWgCallback(url: String): Triple<String, Long, Long>? {
    return try {
        val uri = Uri.parse(url)
        val status = uri.getQueryParameter("status")
        if (status != "ok") return null
        val token     = uri.getQueryParameter("access_token") ?: return null
        val accountId = uri.getQueryParameter("account_id")?.toLongOrNull() ?: return null
        val expiresAt = uri.getQueryParameter("expires_at")?.toLongOrNull() ?: 0L
        Triple(token, accountId, expiresAt)
    } catch (e: Exception) {
        null
    }
}
