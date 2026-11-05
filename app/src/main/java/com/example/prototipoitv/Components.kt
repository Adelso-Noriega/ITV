package com.example.prototipoitv

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
internal fun ItvIcon(kind: String, description: String? = null, modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier.size(24.dp).then(if (description == null) Modifier else Modifier.semantics { contentDescription = description })) {
        scale(size.width / 24f, size.height / 24f, pivot = Offset.Zero) {
            val stroke = Stroke(1.7f)
            fun line(x: Float, y: Float, x2: Float, y2: Float) = drawLine(color, Offset(x, y), Offset(x2, y2), 1.7f)
            fun rect(x: Float, y: Float, w: Float, h: Float) = drawRoundRect(color, Offset(x,y), Size(w,h), CornerRadius(1.5f), style=stroke)
            when (kind) {
                "home" -> {
                    val path = Path().apply { moveTo(3f,11f); lineTo(12f,3f); lineTo(21f,11f) }
                    drawPath(path,color,style=stroke); line(5f,10f,5f,21f); line(19f,10f,19f,21f)
                    line(5f,21f,19f,21f); rect(10f,14f,4f,7f)
                }
                "vehicles" -> {
                    rect(3f,10f,18f,9f); line(5f,10f,7f,5f); line(7f,5f,17f,5f); line(17f,5f,19f,10f)
                    line(6f,19f,6f,22f); line(18f,19f,18f,22f); line(6f,14f,9f,14f); line(15f,14f,18f,14f)
                }
                "account" -> {
                    drawCircle(color,4f,Offset(12f,7f),style=stroke)
                    val path=Path().apply { moveTo(4f,22f); lineTo(4f,18f); cubicTo(4f,11f,20f,11f,20f,18f); lineTo(20f,22f); close() }
                    drawPath(path,color,style=stroke)
                }
                "appointments", "results" -> {
                    rect(4f,5f,16f,17f); line(4f,10f,20f,10f); line(8f,2f,8f,7f); line(16f,2f,16f,7f)
                    if (kind=="results") { line(8f,16f,11f,19f); line(11f,19f,17f,13f) }
                    else { line(8f,14f,10f,14f); line(14f,14f,16f,14f); line(8f,18f,10f,18f) }
                }
                "security" -> {
                    val path=Path().apply { moveTo(12f,2f); lineTo(21f,6f); lineTo(19f,15f); lineTo(12f,22f); lineTo(5f,15f); lineTo(3f,6f); close() }
                    drawPath(path,color,style=stroke); line(8f,11f,11f,14f); line(11f,14f,16f,8f)
                }
                "centers" -> { rect(3f,9f,18f,13f); rect(8f,13f,8f,9f); line(2f,9f,12f,2f); line(12f,2f,22f,9f) }
                "back" -> { line(19f,12f,5f,12f); line(5f,12f,11f,6f); line(5f,12f,11f,18f) }
                "plus" -> { line(12f,4f,12f,20f); line(4f,12f,20f,12f) }
                "delete" -> { rect(6f,7f,12f,15f); line(3f,6f,21f,6f); rect(9f,2f,6f,4f) }
                "logout" -> { line(9f,3f,3f,3f); line(3f,3f,3f,21f); line(3f,21f,9f,21f); line(9f,12f,22f,12f); line(17f,7f,22f,12f); line(17f,17f,22f,12f) }
                else -> { drawCircle(color,9f,Offset(12f,12f),style=stroke); line(12f,7f,12f,14f); drawCircle(color,1f,Offset(12f,18f)) }
            }
        }
    }
}

@Composable
internal fun Heading(text: String, subtitle: String? = null) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text, style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.Bold)
        subtitle?.let { Text(it, color=MaterialTheme.colorScheme.onSurfaceVariant, style=MaterialTheme.typography.bodyMedium) }
    }
}

@Composable
internal fun Panel(content: @Composable ColumnScope.() -> Unit) {
    OutlinedCard(Modifier.fillMaxWidth(), shape=RoundedCornerShape(12.dp),
        colors=CardDefaults.outlinedCardColors(containerColor=Color.White)) {
        Column(Modifier.padding(16.dp), verticalArrangement=Arrangement.spacedBy(12.dp), content=content)
    }
}

@Composable
internal fun Primary(text: String, onClick: () -> Unit) {
    Button(onClick=onClick, modifier=Modifier.fillMaxWidth().heightIn(min=48.dp), shape=RoundedCornerShape(8.dp)) { Text(text) }
}

@Composable
internal fun Secondary(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick=onClick, modifier=Modifier.fillMaxWidth().heightIn(min=48.dp), shape=RoundedCornerShape(8.dp)) { Text(text) }
}

@Composable
internal fun Field(label: String, initial: String = "", password: Boolean = false, keyboard: KeyboardType = KeyboardType.Text, enabled: Boolean = true) {
    var value by rememberSaveable { mutableStateOf(initial) }
    var visible by rememberSaveable { mutableStateOf(false) }
    OutlinedTextField(value=value, onValueChange={ value=it }, label={ Text(label) }, singleLine=true,
        enabled=enabled, modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(8.dp),
        keyboardOptions=KeyboardOptions(keyboardType=if(password) KeyboardType.Password else keyboard),
        visualTransformation=if(password && !visible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon=if(password) ({ TextButton(onClick={visible=!visible}) { Text(if(visible) "Ocultar" else "Ver") } }) else null)
}

@Composable
internal fun Choice(label: String, options: List<String>, initial: String = options.first()) {
    var value by rememberSaveable { mutableStateOf(initial) }
    var expanded by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxWidth()) {
        OutlinedTextField(value=value, onValueChange={}, readOnly=true, label={Text(label)},
            modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(8.dp),
            trailingIcon={ TextButton(onClick={expanded=true}) { Text("Elegir") } })
        Surface(onClick={expanded=true}, modifier=Modifier.matchParentSize(), color=Color.Transparent,
            shape=RoundedCornerShape(8.dp)) { Box(Modifier.semantics { contentDescription = "$label: $value. Elegir opción" }) }
        DropdownMenu(expanded=expanded, onDismissRequest={expanded=false}) {
            options.forEach { option -> DropdownMenuItem(text={Text(option)}, onClick={value=option; expanded=false}) }
        }
    }
}

@Composable
internal fun InfoRow(label: String, value: String) {
    Column(verticalArrangement=Arrangement.spacedBy(4.dp)) {
        Text(label, style=MaterialTheme.typography.labelMedium, color=MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style=MaterialTheme.typography.bodyLarge)
        HorizontalDivider(Modifier.padding(top=6.dp), color=MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
internal fun Status(text: String, positive: Boolean = true) {
    Surface(color=if(positive) Color(0xFFE7F4EC) else Color(0xFFFFF1D6), shape=RoundedCornerShape(6.dp)) {
        Text(text, Modifier.padding(horizontal=10.dp,vertical=5.dp), style=MaterialTheme.typography.labelMedium,
            color=if(positive) Color(0xFF216442) else Color(0xFF825900))
    }
}

@Composable
internal fun ConfirmDialog(title: String, message: String, confirm: String, dismiss: () -> Unit, action: () -> Unit) {
    AlertDialog(onDismissRequest=dismiss, title={Text(title)}, text={Text(message)},
        confirmButton={TextButton(onClick=action){Text(confirm)}},
        dismissButton={TextButton(onClick=dismiss){Text("Cancelar")}})
}

@Composable
internal fun FilePicker(label: String, notify: (String) -> Unit) {
    var selected by rememberSaveable { mutableStateOf(false) }
    Column {
        Text(label, style=MaterialTheme.typography.labelLarge)
        OutlinedButton(onClick={selected=true; notify("Documento de ejemplo seleccionado")}, modifier=Modifier.fillMaxWidth()) {
            Text(if(selected) "documento_ejemplo.pdf · Cambiar" else "Buscar documento")
        }
    }
}
