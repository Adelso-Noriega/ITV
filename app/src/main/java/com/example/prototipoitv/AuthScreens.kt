package com.example.prototipoitv

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
internal fun RegisterScreen(navigate: (Screen) -> Unit, enter: () -> Unit) {
    var dialog by rememberSaveable { mutableStateOf("") }
    Spacer(Modifier.height(8.dp))
    Heading("Tu ITV, más sencilla", "Gestiona tus vehículos y prepara tu próxima inspección.")
    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        OutlinedButton(onClick={navigate(Screen.LOGIN)}, modifier=Modifier.weight(1f)) { Text("Iniciar sesión") }
        FilledTonalButton(onClick={}, modifier=Modifier.weight(1f), enabled=false,
            colors=ButtonDefaults.filledTonalButtonColors(disabledContainerColor=MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor=MaterialTheme.colorScheme.primary)) { Text("Crear cuenta") }
    }
    Panel {
        Secondary("Registro con Google") { dialog="google" }
        Text("o con tu correo / teléfono", Modifier.align(Alignment.CenterHorizontally),
            style=MaterialTheme.typography.labelMedium, color=MaterialTheme.colorScheme.onSurfaceVariant)
        Field("Correo electrónico o teléfono")
        Field("Contraseña", password=true)
        Primary("Registrarme") { dialog="terms" }
    }
    Text("Puedes explorar todas las pantallas con datos de ejemplo.",
        style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
    if(dialog=="google") AlertDialog(onDismissRequest={dialog=""},
        title={Text("Permiso de acceso a Google")},
        text={Text("¿Permites compartir los datos de tu cuenta para acceder a esta app?\n\nEsta ventana es una simulación; no se conecta con Google.")},
        confirmButton={TextButton(onClick={dialog="terms"}){Text("Conceder permiso")}},
        dismissButton={TextButton(onClick={dialog=""}){Text("Denegar")}})
    if(dialog=="terms") ConfirmDialog("Términos y condiciones",
        "Acepto los términos de uso y la política de privacidad de la app ITV.\n\nTexto de ejemplo para este prototipo académico. No se registrarán datos personales.",
        "Aceptar", {dialog=""}) { dialog=""; enter() }
}

@Composable
internal fun LoginScreen(navigate: (Screen) -> Unit, back: () -> Unit, enter: () -> Unit) {
    Heading("Bienvenido de nuevo", "Accede a tu espacio ITV.")
    Panel {
        Field("Email o teléfono")
        Field("Contraseña", password=true)
        TextButton(onClick={navigate(Screen.RECOVER)}, modifier=Modifier.align(Alignment.End)) { Text("¿Olvidaste tu contraseña?") }
        Primary("Entrar", enter)
        Secondary("Cancelar", back)
    }
    Text("Acceso de demostración: pulsa Entrar con cualquier dato o deja los campos vacíos.",
        style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
internal fun RecoverScreen(notify: (String) -> Unit, done: () -> Unit) {
    Heading("Recupera tu acceso", "Introduce tu correo y una nueva contraseña.")
    Panel {
        Field("Email", keyboard=KeyboardType.Email)
        Secondary("Enviar contraseña temporal") { notify("Simulación: utiliza cualquier contraseña temporal") }
        HorizontalDivider()
        Field("Contraseña temporal", password=true)
        Field("Nueva contraseña", password=true)
        Field("Repetir contraseña", password=true)
        Primary("Guardar") { notify("Cambio de contraseña simulado"); done() }
    }
}

@Composable
internal fun AccountScreen(navigate: (Screen) -> Unit, logout: () -> Unit) {
    var deleting by rememberSaveable { mutableStateOf(false) }
    Heading("Tu cuenta", "Configura tus datos y opciones de acceso.")
    AccountOption("account", "Perfil", "Datos personales y contacto") { navigate(Screen.PROFILE) }
    AccountOption("security", "Seguridad", "Cambiar contraseña") { navigate(Screen.SECURITY) }
    AccountOption("delete", "Eliminar cuenta", "Ver confirmación") { deleting=true }
    Spacer(Modifier.height(16.dp))
    Secondary("Cerrar sesión", logout)
    if(deleting) ConfirmDialog("Eliminar cuenta", "¿Quieres simular la eliminación de tu cuenta? Volverás a la pantalla de registro.",
        "Eliminar", {deleting=false}) { deleting=false; logout() }
}

@Composable
private fun AccountOption(icon: String, title: String, subtitle: String, click: () -> Unit) {
    OutlinedCard(onClick=click, modifier=Modifier.fillMaxWidth(), colors=CardDefaults.outlinedCardColors(containerColor=MaterialTheme.colorScheme.surface)) {
        Row(Modifier.padding(20.dp), verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(16.dp)) {
            ItvIcon(icon)
            Column(Modifier.weight(1f)) {
                Text(title, style=MaterialTheme.typography.titleMedium, fontWeight=FontWeight.SemiBold)
                Text(subtitle, style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text("›", style=MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
internal fun ProfileScreen(notify: (String) -> Unit, back: () -> Unit) {
    var editing by rememberSaveable { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
        Text("Datos personales", Modifier.weight(1f), style=MaterialTheme.typography.titleMedium)
        TextButton(onClick={editing=true}) { Text("Modificar") }
    }
    Panel {
        Field("Nombre", "Alberto", enabled=editing)
        Field("Apellidos", "Sánchez", enabled=editing)
        Field("Teléfono", "612 345 678", keyboard=KeyboardType.Phone, enabled=editing)
        Field("Dirección", "Calle Mayor, 10", enabled=editing)
        Field("Email", "alberto@ejemplo.com", keyboard=KeyboardType.Email, enabled=editing)
        if(editing) Choice("Género", listOf("Prefiero no indicarlo", "Masculino", "Femenino", "Otro"))
        else InfoRow("Género", "Prefiero no indicarlo")
        Primary("Guardar") { editing=false; notify("Perfil de ejemplo guardado"); back() }
        Secondary("Cancelar", back)
    }
}

@Composable
internal fun SecurityScreen(notify: (String) -> Unit, back: () -> Unit) {
    Heading("Cambiar contraseña")
    Panel {
        Field("Contraseña actual", password=true)
        Field("Nueva contraseña", password=true)
        Field("Repetir contraseña", password=true)
        Primary("Guardar") { notify("Cambio de contraseña simulado"); back() }
    }
}
