package com.example.prototipoitv

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun RegisterScreen(navigate: (Screen) -> Unit, back: () -> Unit, isLogin: Boolean, enter: () -> Unit) {
    var dialog by rememberSaveable { mutableStateOf("") }
    val blue = Color(0xFF073CB9)
    val ink = Color(0xFF102337)
    val muted = Color(0xFF596176)
    val fieldColor = Color(0xFFEEF2FF)

    MaterialTheme(colorScheme = MaterialTheme.colorScheme.copy(
        primary = blue, onPrimary = Color.White, onSurface = ink, onSurfaceVariant = muted
    )) {
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(28.dp)) {
            Column(Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(shape = RoundedCornerShape(22.dp), color = blue,
                    shadowElevation = 4.dp) {
                    Box(Modifier.size(76.dp), contentAlignment = Alignment.Center) {
                        WelcomeShield()
                    }
                }
                Text("Tu ITV, más sencilla", fontSize = 28.sp, lineHeight = 34.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = (-0.8).sp,
                    color = ink, textAlign = TextAlign.Center)
                Text("Gestiona tus vehículos y prepara tu próxima inspección.",
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = muted, textAlign = TextAlign.Center)
            }
            Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp),
                color = Color.White, shadowElevation = 3.dp) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
                    Surface(shape = RoundedCornerShape(14.dp), color = fieldColor) {
                        BoxWithConstraints(Modifier.fillMaxWidth().padding(4.dp)) {
                            val width = maxWidth / 2
                            val offset by animateDpAsState(if (isLogin) width else 0.dp,
                                animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing), label = "authTab")
                            Surface(Modifier.offset(x = offset).width(width).height(48.dp),
                                shape = RoundedCornerShape(11.dp), color = Color.White) { }
                            Row(Modifier.fillMaxWidth()) {
                                TextButton(onClick = { if (isLogin) back() },
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    colors = ButtonDefaults.textButtonColors(contentColor = if (!isLogin) blue else muted)
                                ) { Text("Crear cuenta", fontWeight = FontWeight.SemiBold) }
                                TextButton(onClick = { if (!isLogin) navigate(Screen.LOGIN) },
                                    modifier = Modifier.weight(1f).heightIn(min = 48.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp),
                                    colors = ButtonDefaults.textButtonColors(contentColor = if (isLogin) blue else muted)
                                ) { Text("Iniciar sesión", fontWeight = FontWeight.SemiBold) }
                            }
                        }
                    }
                    AnimatedContent(targetState = isLogin, label = "authForm",
                        transitionSpec = {
                            val duration = 320
                            val enter = slideInHorizontally(tween(duration, easing = FastOutSlowInEasing)) {
                                if (targetState) it / 8 else -it / 8
                            } + fadeIn(tween(duration))
                            val exit = slideOutHorizontally(tween(duration, easing = FastOutSlowInEasing)) {
                                if (targetState) -it / 8 else it / 8
                            } + fadeOut(tween(duration))
                            (enter togetherWith exit).using(SizeTransform { _, _ ->
                                tween(durationMillis = duration, easing = FastOutSlowInEasing)
                            })
                        }) { login ->
                        Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                            if (login) {
                                WelcomeField("Email o teléfono", "", fieldColor)
                                WelcomeField("Contraseña", "", fieldColor, password = true)
                                TextButton(onClick = { navigate(Screen.RECOVER) }, modifier = Modifier.align(Alignment.End),
                                    contentPadding = PaddingValues(horizontal = 0.dp)) { Text("¿Olvidaste tu contraseña?") }
                                Button(onClick = enter, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                                    shape = RoundedCornerShape(12.dp)) {
                                    Text("Entrar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                }
                                OutlinedButton(onClick = back, modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                                    shape = RoundedCornerShape(12.dp)) {
                                    Text("Cancelar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                }
                                Text("Acceso de demostración: pulsa Entrar con cualquier dato o deja los campos vacíos.",
                                    style = MaterialTheme.typography.bodySmall, color = muted, textAlign = TextAlign.Center)
                            } else {
                    Button(onClick = { dialog = "google" },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = fieldColor, contentColor = ink)
                    ) {
                        WelcomeGoogleMark()
                        Spacer(Modifier.width(12.dp))
                        Text("Registro con Google", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HorizontalDivider(Modifier.weight(1f), color = Color(0xFFDCE5FF))
                        Text("o con tu correo / teléfono", style = MaterialTheme.typography.bodySmall,
                            color = muted, textAlign = TextAlign.Center, modifier = Modifier.weight(3f))
                        HorizontalDivider(Modifier.weight(1f), color = Color(0xFFDCE5FF))
                    }
                    WelcomeField("Correo electrónico o teléfono", "Correo o teléfono", fieldColor)
                    WelcomeField("Contraseña", "Tu contraseña", fieldColor, password = true)
                    Button(onClick = { dialog = "terms" },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Registrarme", style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                    }
                            }
                        }
                    }
                }
            }
        }
    }
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
private fun WelcomeField(label: String, placeholder: String, background: Color, password: Boolean = false) {
    var value by rememberSaveable { mutableStateOf("") }
    var visible by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface)
        TextField(value = value, onValueChange = { value = it }, singleLine = true,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = label }, shape = RoundedCornerShape(12.dp),
            placeholder = { Text(placeholder, color = Color(0xFF767D90)) },
            leadingIcon = { if (password) WelcomeLock() else WelcomeEnvelope() },
            keyboardOptions = KeyboardOptions(keyboardType = if (password) KeyboardType.Password else KeyboardType.Text),
            visualTransformation = if (password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (password) ({
                IconButton(onClick = { visible = !visible }) {
                    Canvas(Modifier.size(24.dp).semantics {
                        contentDescription = if (visible) "Ocultar" else "Ver"
                    }) {
                        val path = Path().apply {
                            moveTo(size.width * .08f, size.height * .5f)
                            cubicTo(size.width * .28f, size.height * .12f, size.width * .72f, size.height * .12f, size.width * .92f, size.height * .5f)
                            cubicTo(size.width * .72f, size.height * .88f, size.width * .28f, size.height * .88f, size.width * .08f, size.height * .5f)
                        }
                        drawPath(path, Color(0xFF596176), style = Stroke(2.dp.toPx()))
                        drawCircle(Color(0xFF596176), size.width * .13f, style = Stroke(2.dp.toPx()))
                        if (visible) drawLine(Color(0xFF596176), Offset(0f, size.height), Offset(size.width, 0f), 2.dp.toPx())
                    }
                }
            }) else null,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = background, unfocusedContainerColor = background,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent,
                focusedLeadingIconColor = Color(0xFF596176), unfocusedLeadingIconColor = Color(0xFF596176)))
    }
}

@Composable
private fun WelcomeShield() {
    Canvas(Modifier.size(40.dp)) {
        val shield = Path().apply {
            moveTo(size.width * .5f, size.height * .04f)
            lineTo(size.width * .86f, size.height * .18f)
            lineTo(size.width * .86f, size.height * .47f)
            cubicTo(size.width * .86f, size.height * .73f, size.width * .67f, size.height * .91f, size.width * .5f, size.height * .97f)
            cubicTo(size.width * .33f, size.height * .91f, size.width * .14f, size.height * .73f, size.width * .14f, size.height * .47f)
            lineTo(size.width * .14f, size.height * .18f)
            close()
        }
        drawPath(shield, Color.White)
        val tick = Path().apply {
            moveTo(size.width * .32f, size.height * .49f)
            lineTo(size.width * .45f, size.height * .62f)
            lineTo(size.width * .69f, size.height * .36f)
        }
        drawPath(tick, Color(0xFF073CB9), style = Stroke(3.dp.toPx()))
    }
}

@Composable
private fun WelcomeGoogleMark() {
    Canvas(Modifier.size(22.dp)) {
        val stroke = Stroke(4.dp.toPx())
        val inset = 2.dp.toPx()
        val arcSize = Size(size.width - inset * 2, size.height - inset * 2)
        listOf(45f to Color(0xFF34A853), 135f to Color(0xFFFBBC05), 190f to Color(0xFFEA4335), -5f to Color(0xFF4285F4)).forEach { (start, color) ->
            drawArc(color, start, if (start == 135f) 55f else if (start == 190f) 100f else 90f,
                false, Offset(inset, inset), arcSize, style = stroke)
        }
        drawLine(Color(0xFF4285F4), Offset(size.width * .53f, size.height * .5f),
            Offset(size.width - inset, size.height * .5f), 4.dp.toPx())
    }
}

@Composable
private fun WelcomeEnvelope() {
    Canvas(Modifier.size(22.dp)) {
        val color = Color(0xFF596176)
        drawRect(color, Offset(size.width * .08f, size.height * .18f),
            Size(size.width * .84f, size.height * .64f), style = Stroke(1.7.dp.toPx()))
        val fold = Path().apply {
            moveTo(size.width * .08f, size.height * .18f)
            lineTo(size.width * .5f, size.height * .52f)
            lineTo(size.width * .92f, size.height * .18f)
        }
        drawPath(fold, color, style = Stroke(1.7.dp.toPx()))
    }
}

@Composable
private fun WelcomeLock() {
    Canvas(Modifier.size(22.dp)) {
        val color = Color(0xFF596176)
        drawRect(color, Offset(size.width * .23f, size.height * .42f),
            Size(size.width * .54f, size.height * .48f), style = Stroke(1.7.dp.toPx()))
        drawArc(color, 180f, 180f, false, Offset(size.width * .34f, size.height * .1f),
            Size(size.width * .32f, size.height * .5f), style = Stroke(1.7.dp.toPx()))
        drawLine(color, Offset(size.width * .5f, size.height * .61f),
            Offset(size.width * .5f, size.height * .73f), 1.7.dp.toPx(), cap = StrokeCap.Round)
    }
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
