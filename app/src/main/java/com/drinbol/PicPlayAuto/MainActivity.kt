package com.drinbol.PicPlayAuto

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.drinbol.PicPlayAuto.ui.theme.PicPlayAutoTheme
import coil3.compose.AsyncImage
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Enable full-screen display
        enableEdgeToEdge()

        // Use systemUiVisibility for API 28 to 29 to hide navigation and status bar
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }

        setContent {
            PicPlayAutoTheme {
                Log.i("我的日志", "开始")
                val url = "https://www.runoob.com/try/demo_source/movie.mp4"
                //val url = "https://global.bing.com/th?id=OHR.GuanacosChile_ZH-CN7011761081_1080x1920.jpg"
                ContentScreen(url)
            }
        }
    }
}

@Composable
fun ContentScreen(url: String, modifier: Modifier = Modifier) {
    if (isImageUrl(url)) {
        DisplayImage(url, modifier)
    } else if (isVideoUrl(url)) {
        DisplayVideo(url, modifier)
    }
}

fun isImageUrl(url: String): Boolean {
    return url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") || url.endsWith(".gif")
}

fun isVideoUrl(url: String): Boolean {
    return url.endsWith(".mp4") || url.endsWith(".mkv") || url.endsWith(".flv")
}

@Composable
fun DisplayImage(url: String, modifier: Modifier) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
fun DisplayVideo(url: String, modifier: Modifier) {
        Log.i("我的日志", "url: $url")
        var playerView: PlayerView? = null
        val context = LocalContext.current
        var player = ExoPlayer.Builder(context).build()
        val mediaItem = MediaItem.fromUri(Uri.parse(url))
        player.setMediaItem(mediaItem)

        LaunchedEffect(mediaItem) {
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }

        // Manage lifecycle events
        DisposableEffect(Unit) {
            onDispose {
                player.release()
            }
        }

    // Use AndroidView to embed an Android View (PlayerView) into Compose
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = player
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Set your desired height
        )
    }

@Preview(showBackground = true)
@Composable
fun ImageScreenPreview() {
    PicPlayAutoTheme {
        ContentScreen("https://global.bing.com/th?id=OHR.GuanacosChile_ZH-CN7011761081_1080x1920.jpg")
    }
}
