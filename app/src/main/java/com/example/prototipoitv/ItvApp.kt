package com.example.prototipoitv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// Only navigation and transient UI state. Displayed records are sample fixtures.
internal enum class Screen(val title: String, val section: String = "", val public: Boolean = false) {
    REGISTER("Crear cuenta", public = true), LOGIN("Inicio de sesión", public = true),
    RECOVER("Recuperar contraseña", public = true), HOME("Inicio", "home"),
    ACCOUNT("Cuenta", "account"), PROFILE("Perfil de usuario", "account"),
    SECURITY("Seguridad", "account"), VEHICLES("Mis vehículos", "vehicles"),
    VEHICLE_NEW("Registrar vehículo", "vehicles"), VEHICLE_DETAIL("Detalle del vehículo", "vehicles"),
    CENTERS("Centros ITV", "home"), RESULTS("Resultados ITV", "home"),
    APPOINTMENTS("Mis citas", "appointments"), APPOINTMENT_NEW("Agendar cita", "appointments"),
    APPOINTMENT_EDIT("Modificar cita", "appointments"), CHECK_IN("Check-in", "appointments")
}

@Composable
internal fun ItvTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = lightColorScheme(
        primary = Color(0xFF073CB9), onPrimary = Color.White,
        primaryContainer = Color(0xFFEEF2FF), onPrimaryContainer = Color(0xFF102337),
        secondary = Color(0xFF596176), background = Color(0xFFF7F8FD),
        surface = Color.White, surfaceVariant = Color(0xFFEEF2FF),
        onSurface = Color(0xFF102337), onSurfaceVariant = Color(0xFF596176),
        outline = Color(0xFF77879A), outlineVariant = Color(0xFFDCE3EB)
    ), shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(20.dp),
        large = RoundedCornerShape(24.dp)), content = content)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ItvApp() {
    var stack by rememberSaveable { mutableStateOf(arrayListOf(Screen.REGISTER.name)) }
    val screen = Screen.valueOf(stack.last())
    val stateHolder = rememberSaveableStateHolder()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val notify: (String) -> Unit = { message -> scope.launch { snackbar.showSnackbar(message) }; Unit }
    val navigate: (Screen) -> Unit = { destination ->
        if (destination != screen) stack = ArrayList(stack + destination.name)
    }
    val root: (Screen) -> Unit = { destination ->
        stack = if (destination == Screen.HOME) arrayListOf(Screen.HOME.name)
        else arrayListOf(Screen.HOME.name, destination.name)
    }
    val back: () -> Unit = { if (stack.size > 1) stack = ArrayList(stack.dropLast(1)) }
    val logout: () -> Unit = {
        Screen.entries.filter { !it.public }.forEach { stateHolder.removeState(it.name) }
        stack = arrayListOf(Screen.REGISTER.name)
    }
    BackHandler(enabled = stack.size > 1, onBack = back)
    Scaffold(
        containerColor = if (screen == Screen.REGISTER) Color(0xFFF7F8FD) else MaterialTheme.colorScheme.background,
        topBar = {
            if (screen != Screen.REGISTER && screen != Screen.LOGIN) TopAppBar(title = { Column {
                Text(screen.title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                Text("ITV · Prototipo visual", style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } }, navigationIcon = {
                if (stack.size > 1) IconButton(onClick = back) { ItvIcon("back", "Volver") }
            }, actions = {
                if (screen == Screen.HOME) IconButton(onClick = { navigate(Screen.ACCOUNT) }) {
                    ItvIcon("account", "Abrir cuenta")
                }
            }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF7F8FD)))
        },
        bottomBar = {
            if (!screen.public) NavigationBar(containerColor = Color.White) {
                listOf(Triple(Screen.HOME, "home", "Inicio"), Triple(Screen.APPOINTMENTS, "appointments", "Citas"),
                    Triple(Screen.VEHICLES, "vehicles", "Vehículos"), Triple(Screen.ACCOUNT, "account", "Cuenta")
                ).forEach { (destination, icon, label) ->
                    NavigationBarItem(selected = screen.section == destination.section,
                        colors = NavigationBarItemDefaults.colors(indicatorColor = Color(0xFFEEF2FF),
                            selectedIconColor = Color(0xFF073CB9), selectedTextColor = Color(0xFF073CB9)),
                        onClick = { root(destination) }, icon = { ItvIcon(icon) }, label = { Text(label) })
                }
            }
        }, snackbarHost = { SnackbarHost(snackbar) }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding).consumeWindowInsets(padding).imePadding(), contentAlignment = Alignment.TopCenter) {
            stateHolder.SaveableStateProvider(if (screen == Screen.LOGIN) Screen.REGISTER.name else screen.name) {
                val scrollState = rememberScrollState()
                val focusRequester = remember { FocusRequester() }
                LaunchedEffect(screen) { focusRequester.requestFocus() }
                Column(Modifier.widthIn(max = 600.dp).fillMaxWidth()
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && scrollState.maxValue > 0 &&
                            (event.key == Key.DirectionDown || event.key == Key.DirectionUp)) {
                            val direction = if (event.key == Key.DirectionDown) 1 else -1
                            scope.launch { scrollState.scrollBy(direction * scrollState.viewportSize * 0.6f) }
                            true
                        } else false
                    }
                    .focusRequester(focusRequester).focusable()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    when (screen) {
                        Screen.REGISTER, Screen.LOGIN -> RegisterScreen(navigate, back, screen == Screen.LOGIN) { root(Screen.HOME) }
                        Screen.RECOVER -> RecoverScreen(notify) { stack = arrayListOf(Screen.REGISTER.name, Screen.LOGIN.name) }
                        Screen.HOME -> HomeScreen(navigate)
                        Screen.ACCOUNT -> AccountScreen(navigate, logout)
                        Screen.PROFILE -> ProfileScreen(notify, back)
                        Screen.SECURITY -> SecurityScreen(notify, back)
                        Screen.VEHICLES -> VehiclesScreen(navigate, notify)
                        Screen.VEHICLE_NEW -> VehicleFormScreen(false, notify, back)
                        Screen.VEHICLE_DETAIL -> VehicleDetailScreen(notify)
                        Screen.CENTERS -> CentersScreen(navigate, notify)
                        Screen.RESULTS -> ResultsScreen(notify)
                        Screen.APPOINTMENTS -> AppointmentsScreen(navigate, notify)
                        Screen.APPOINTMENT_NEW -> AppointmentFormScreen(false, notify) { root(Screen.APPOINTMENTS) }
                        Screen.APPOINTMENT_EDIT -> AppointmentFormScreen(true, notify) { root(Screen.APPOINTMENTS) }
                        Screen.CHECK_IN -> CheckInScreen()
                    }
                    Spacer(Modifier.height(8.dp))
                }

            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AppPreview() { ItvTheme { ItvApp() } }
