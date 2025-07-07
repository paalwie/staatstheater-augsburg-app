package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Performance
import com.example.myapplication.data.PerformanceApi
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.TheaterGreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.vector.ImageVector

private val deFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm 'Uhr'")
private val berlinZone = ZoneId.of("Europe/Berlin")

val LinkColor @Composable get() = MaterialTheme.colorScheme.primary

fun formatIsoToGerman(iso: String): String =
    OffsetDateTime.parse(iso)
        .atZoneSameInstant(berlinZone)
        .format(deFormatter)

class PerformanceVM(
    private val api: PerformanceApi = PerformanceApi.create()
) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Loading)
    val state: StateFlow<UiState> = _state

    init {
        refresh()
    }

    fun refresh() = viewModelScope.launch {
        _state.value = UiState.Loading
        runCatching { api.getAll() }
            .onSuccess { _state.value = UiState.Success(it) }
            .onFailure { _state.value = UiState.Error(it.message ?: "Unbekannter Fehler") }
    }

    sealed interface UiState {
        object Loading : UiState
        data class Success(val data: List<Performance>) : UiState
        data class Error(val msg: String) : UiState
    }
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val vmFactory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                PerformanceVM() as T
        }

        setContent {
            MyApplicationTheme {
                val vm: PerformanceVM =
                    ViewModelProvider(this@MainActivity, vmFactory)[PerformanceVM::class.java]
                Surface(Modifier.fillMaxSize()) {
                    EventScreen(vm)
                }
            }
        }
    }
}


@Composable
fun EventScreen(vm: PerformanceVM) {
    val uiState by vm.state.collectAsState()

    Scaffold(
        topBar = { TitleBar() },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (uiState) {
                is PerformanceVM.UiState.Loading -> CircularProgressIndicator(
                    Modifier.align(
                        Alignment.Center
                    )
                )

                is PerformanceVM.UiState.Error -> Text(
                    (uiState as PerformanceVM.UiState.Error).msg,
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Red
                )

                is PerformanceVM.UiState.Success -> EventList(
                    (uiState as PerformanceVM.UiState.Success).data
                )
            }
        }
    }
}

@Composable
fun TitleBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Text(
            text = "Staatstheater\nAugsburg",          // Zeilenumbruch manuell
            lineHeight = 30.sp,
            color = TheaterGreen,
            fontSize = 28.sp,
            fontFamily = FontFamily(Font(R.font.playfair_display_bold)),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(8.dp),
            style = TextStyle(
                shadow = Shadow(Color.Black, Offset(2f, 2f), 3f)
            )
        )
    }
}


@Composable
fun EventList(performances: List<Performance>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(performances) { perf ->
            EventCard(perf)
        }
    }

}

@Composable
fun EventCard(perf: Performance, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(perf.title, fontWeight = FontWeight.Bold)

            listOf(perf.subtitle1, perf.subtitle2)
                .filter { !it.isNullOrBlank() }
                .forEach { Text(it!!) }

            Spacer(Modifier.height(4.dp))

            Text("Genre: ${perf.genre}")
            Text("Ort: ${perf.location}")
            Text(formatIsoToGerman(perf.date))

            Row(verticalAlignment = Alignment.CenterVertically) {

                perf.tickets_uri?.let {
                    LinkIcon(
                        label = "Tickets",
                        url = it,
                        icon = Icons.Filled.ConfirmationNumber   // Ticket‑Icon
                    )
                }

                perf.descr_uri?.let {
                    Spacer(Modifier.width(8.dp))
                    LinkIcon(
                        label = "Details",
                        url = "https://$it",
                        icon = Icons.Filled.Info                 // Info‑Icon
                    )
                }
            }

        }
    }
}

@Composable
fun LinkIcon(
    label: String,
    url: String,
    icon: ImageVector       // <‑‑ NEU
) {
    val context = LocalContext.current
    IconButton(onClick = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
        Toast.makeText(context, "$label wird im Browser geöffnet", Toast.LENGTH_SHORT).show()
    }) {
        Icon(icon, contentDescription = label, tint = LinkColor)
    }
}


