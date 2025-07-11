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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.currentBackStackEntryAsState
import java.time.LocalDate


sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Heute", Icons.Filled.Home)
    object Schedule : Screen("schedule", "Spielplan", Icons.AutoMirrored.Filled.List)
    object Imprint : Screen("imprint", "Impressum", Icons.Filled.Info)
}


private val deFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm 'Uhr'")
private val germanDateFormatter = DateTimeFormatter.ofPattern("EEEE, dd. MMMM") // Für die Startseite
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
                val navController = rememberNavController()

                Scaffold(
                    topBar = { TitleBar() },
                    bottomBar = { TheaterBottomAppBar(navController) },
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(vm)
                        }
                        composable(Screen.Schedule.route) {
                            ScheduleScreen(vm)
                        }
                        composable(Screen.Imprint.route) {
                            ImprintScreen()
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TheaterBottomAppBar(navController: NavController) {
    val navItems = listOf(Screen.Home, Screen.Schedule, Screen.Imprint)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        navItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = {
                    // Verwende MaxLines=1 und overflow, damit der Text nicht umbricht
                    Text(screen.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun ScheduleScreen(vm: PerformanceVM) { // Umbenannt von EventScreen
    val uiState by vm.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(), // Füllt den verfügbaren Platz
        contentAlignment = Alignment.Center // Zentriert den Inhalt der Box
    ) {
        when (uiState) {
            is PerformanceVM.UiState.Loading -> CircularProgressIndicator()
            is PerformanceVM.UiState.Error -> Text(
                (uiState as PerformanceVM.UiState.Error).msg,
                color = Color.Red
            )
            is PerformanceVM.UiState.Success -> EventList(
                (uiState as PerformanceVM.UiState.Success).data
            )
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
            text = "Staatstheater\nAugsburg",
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
                .forEach { Text(it!!) } // it!! ist hier in Ordnung, da filter vorher war

            Spacer(Modifier.height(4.dp))

            Text("Genre: ${perf.genre}")
            Text("Ort: ${perf.location}")
            Text(formatIsoToGerman(perf.date))

            Row(verticalAlignment = Alignment.CenterVertically) {

                perf.tickets_uri?.let {
                    LinkIcon(
                        label = "Tickets",
                        url = it,
                        icon = Icons.Filled.ConfirmationNumber
                    )
                }

                perf.descr_uri?.let {
                    Spacer(Modifier.width(8.dp))
                    LinkIcon(
                        label = "Details",
                        url = "https://$it",
                        icon = Icons.Filled.Info
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
    icon: ImageVector
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

@Composable
fun HomeScreen(vm: PerformanceVM) {
    val uiState by vm.state.collectAsState()
    val today = LocalDate.now(berlinZone)

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is PerformanceVM.UiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
            is PerformanceVM.UiState.Error -> Text(
                (uiState as PerformanceVM.UiState.Error).msg,
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red
            )
            is PerformanceVM.UiState.Success -> {
                val todayPerformances = (uiState as PerformanceVM.UiState.Success).data
                    .filter {
                        OffsetDateTime.parse(it.date)
                            .atZoneSameInstant(berlinZone)
                            .toLocalDate() == today
                    }
                    .sortedBy { OffsetDateTime.parse(it.date).atZoneSameInstant(berlinZone).toLocalTime() } // Nach Uhrzeit sortieren

                Column(modifier = Modifier.fillMaxSize()) { // Haupt-Column für Überschrift und Liste
                    Text(
                        text = "Heute, ${today.format(germanDateFormatter)}",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Heutige Vorstellungen",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Divider(modifier = Modifier.padding(bottom = 8.dp)) // Trennlinie

                    if (todayPerformances.isEmpty()) {
                        Text(
                            "Heute gibt es keine Vorstellungen.",
                            modifier = Modifier
                                .fillMaxSize()
                                .wrapContentSize(Alignment.Center),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp), // Horizontales Padding für die Liste
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(todayPerformances) { perf ->
                                DetailedPerformanceCard(perf) // vm-Parameter entfernt
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailedPerformanceCard(perf: Performance, modifier: Modifier = Modifier) { // vm-Parameter entfernt
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                perf.title,
                fontWeight = FontWeight.ExtraBold, // Extra Bold für Titel
                fontSize = 24.sp, // Größere Schrift für Titel
                color = MaterialTheme.colorScheme.primary, // Titel in Theme-Farbe
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            listOf(perf.subtitle1, perf.subtitle2)
                .filter { !it.isNullOrBlank() }
                .forEach {
                    Text(
                        it!!, // it!! ist hier in Ordnung
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontSize = 16.sp, // Subtitel etwas größer
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Eine dezentere Farbe
                    )
                }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Info,
                    contentDescription = "Genre",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(4.dp))
                Text("Genre: ${perf.genre}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium) // Größer und Medium Bold
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Home, // Beispiel: Haus-Icon für Ort
                    contentDescription = "Ort",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(4.dp))
                Text("Ort: ${perf.location}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.AutoMirrored.Filled.List, // Beispiel: Liste-Icon für Datum
                    contentDescription = "Zeit",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(4.dp))
                Text(formatIsoToGerman(perf.date), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary) // Datum in Akzentfarbe
            }


            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                perf.tickets_uri?.let {
                    LinkButton(
                        label = "Tickets kaufen",
                        url = it,
                        icon = Icons.Filled.ConfirmationNumber
                    )
                }

                perf.descr_uri?.let {
                    LinkButton(
                        label = "Webseite öffnen",
                        url = "https://$it",
                        icon = Icons.Filled.Info
                    )
                }
            }
        }
    }
}

@Composable
fun LinkButton(
    label: String,
    url: String,
    icon: ImageVector
) {
    val context = LocalContext.current
    OutlinedButton(onClick = {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
        Toast.makeText(context, "$label wird im Browser geöffnet", Toast.LENGTH_SHORT).show()
    }) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(4.dp))
        Text(label)
    }
}

@Composable
fun ImprintScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Impressum",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Text(
            text =  "Erstellt wurde die App von:\n" +
                    "Patrick Wiedenmann\n" +
                    "86368 Gersthofen\n" +
                    "E-Mail: kommt noch\n\n" +
                    "Diese App zielt auf keine finanziellen Nutzen ab und wurde nur aus Übungszwecken erstellt.\n\n" +
                    "Datenschutzhinweis:\n" +
                    "Diese App sammelt keine persönlichen Daten. Externe Links unterliegen den Datenschutzbestimmungen der jeweiligen Anbieter.\n\n" +
                    "Haftungsausschluss:\n" +
                    "Alle Angaben ohne Gewähr. Für Inhalte externer Links wird keine Haftung übernommen. Die Daten des Spielplans werden aus einer OpenData des Staatstheaters gezogen.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}