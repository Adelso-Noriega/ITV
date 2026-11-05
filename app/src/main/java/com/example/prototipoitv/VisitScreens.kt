package com.example.prototipoitv

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
internal fun HomeScreen(navigate: (Screen) -> Unit) {
    Heading("Hola, Alberto", "Todo listo para tu próxima ITV.")
    Panel {
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
            Text("Próxima cita", style=MaterialTheme.typography.titleMedium, fontWeight=FontWeight.SemiBold)
            Status("Cita confirmada")
        }
        Row(horizontalArrangement=Arrangement.spacedBy(12.dp), verticalAlignment=Alignment.CenterVertically) {
            ItvIcon("vehicles")
            Column {
                Text("1234 ABC", style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold)
                Text("ITV Alicante 1")
                Text("12/11/2026 · 10:30", color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Primary("Realizar check-in") { navigate(Screen.CHECK_IN) }
        TextButton(onClick={navigate(Screen.APPOINTMENT_EDIT)}, modifier=Modifier.align(Alignment.End)) { Text("Modificar cita") }
    }
    Text("Accesos rápidos", style=MaterialTheme.typography.titleMedium, fontWeight=FontWeight.SemiBold)
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        QuickAccess("appointments", "Reservar cita", Modifier.weight(1f)) { navigate(Screen.APPOINTMENT_NEW) }
        QuickAccess("vehicles", "Mis vehículos", Modifier.weight(1f)) { navigate(Screen.VEHICLES) }
    }
    Row(horizontalArrangement=Arrangement.spacedBy(12.dp)) {
        QuickAccess("results", "Resultados ITV", Modifier.weight(1f)) { navigate(Screen.RESULTS) }
        QuickAccess("centers", "Centros ITV", Modifier.weight(1f)) { navigate(Screen.CENTERS) }
    }
}

@Composable
private fun QuickAccess(icon: String, label: String, modifier: Modifier, action: () -> Unit) {
    OutlinedCard(onClick=action, modifier=modifier, colors=CardDefaults.outlinedCardColors(containerColor=Color.White)) {
        Column(Modifier.fillMaxWidth().padding(vertical=22.dp,horizontal=8.dp), horizontalAlignment=Alignment.CenterHorizontally,
            verticalArrangement=Arrangement.spacedBy(12.dp)) {
            ItvIcon(icon)
            Text(label, style=MaterialTheme.typography.labelLarge, textAlign=TextAlign.Center)
        }
    }
}

@Composable
internal fun CentersScreen(navigate: (Screen) -> Unit, notify: (String) -> Unit) {
    var map by rememberSaveable { mutableStateOf(false) }
    Field("Buscar por nombre, ciudad o código postal")
    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        FilterChip(selected=!map, onClick={map=false}, label={Text("Lista")}, modifier=Modifier.weight(1f))
        FilterChip(selected=map, onClick={map=true}, label={Text("Mapa")}, modifier=Modifier.weight(1f))
    }
    if(map) {
        MapIllustration()
        Text("Mapa ilustrativo de los centros de ejemplo.", style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
    }
    listOf("ITV Alicante 1" to "Calle Pino, 1 · Alicante", "ITV Alicante 2" to "Calle Olivo, 2 · Alicante").forEachIndexed { index, (name, address) ->
        Panel {
            Row(horizontalArrangement=Arrangement.spacedBy(12.dp), verticalAlignment=Alignment.CenterVertically) {
                ItvIcon("centers")
                Column {
                    Text(name, style=MaterialTheme.typography.titleMedium, fontWeight=FontWeight.SemiBold)
                    Text(address, style=MaterialTheme.typography.bodyMedium)
                }
            }
            Status(if(index==0) "Abierto hasta las 20:00" else "Abierto hasta las 18:00")
            Primary("Agendar cita") { navigate(Screen.APPOINTMENT_NEW) }
            if(map) TextButton(onClick={notify("Centro de ejemplo: $name")}) { Text("Ver ubicación") }
        }
    }
}

@Composable
private fun MapIllustration() {
    Box(Modifier.fillMaxWidth().height(250.dp).background(Color(0xFFEAF0E5),RoundedCornerShape(12.dp))) {
        Canvas(Modifier.fillMaxSize().semantics { contentDescription="Mapa de ejemplo con dos centros ITV en Alicante" }) {
            val w=size.width; val h=size.height
            drawRect(Color(0xFFC8E2B6), Offset(w*.1f,h*.1f),Size(w*.24f,h*.28f))
            drawRect(Color(0xFFC8E2B6), Offset(w*.62f,h*.59f),Size(w*.3f,h*.3f))
            val road=Path().apply { moveTo(0f,h*.73f); lineTo(w*.4f,h*.52f); lineTo(w*.65f,h*.12f); lineTo(w,h*.2f) }
            drawPath(road,Color.White,style=androidx.compose.ui.graphics.drawscope.Stroke(22.dp.toPx()))
            drawLine(Color.White,Offset(w*.14f,0f),Offset(w*.46f,h),18.dp.toPx())
            drawLine(Color(0xFFE8CF83),Offset(0f,h*.38f),Offset(w,h*.63f),9.dp.toPx())
            listOf(Offset(w*.38f,h*.49f),Offset(w*.69f,h*.25f)).forEach { point ->
                drawCircle(Color(0xFF2166BA),12.dp.toPx(),point)
                drawCircle(Color.White,4.dp.toPx(),point)
            }
        }
        Surface(Modifier.align(Alignment.BottomStart).padding(12.dp), shape=RoundedCornerShape(6.dp),color=Color.White) {
            Text("Alicante · 2 centros",Modifier.padding(8.dp),style=MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
internal fun AppointmentsScreen(navigate: (Screen) -> Unit, notify: (String) -> Unit) {
    var deleting by rememberSaveable { mutableStateOf(false) }
    Heading("Tus próximas citas")
    Primary("+ Agendar cita") { navigate(Screen.APPOINTMENT_NEW) }
    Panel {
        Status("Cita confirmada")
        Text("1234 ABC · SEAT León", style=MaterialTheme.typography.titleMedium, fontWeight=FontWeight.Bold)
        InfoRow("Centro ITV", "ITV Alicante 1")
        InfoRow("Fecha", "12/11/2026")
        InfoRow("Hora", "10:30")
        Primary("Realizar check-in") { navigate(Screen.CHECK_IN) }
        Secondary("Modificar cita") { navigate(Screen.APPOINTMENT_EDIT) }
        TextButton(onClick={deleting=true}, modifier=Modifier.align(Alignment.End)) { Text("Eliminar cita",color=MaterialTheme.colorScheme.error) }
    }
    if(deleting) ConfirmDialog("Eliminar cita", "¿Quieres simular la cancelación de esta cita? La cita de ejemplo se mantendrá disponible.",
        "Eliminar", {deleting=false}) { deleting=false; notify("Cancelación de cita simulada") }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppointmentFormScreen(editing: Boolean, notify: (String) -> Unit, done: () -> Unit) {
    var calendar by rememberSaveable { mutableStateOf(false) }
    var date by rememberSaveable { mutableStateOf("12/11/2026") }
    val picker=rememberDatePickerState(initialSelectedDateMillis=1794441600000L)
    Heading(if(editing) "Modifica tu reserva" else "Prepara tu próxima ITV")
    Panel {
        if(editing) InfoRow("Matrícula", "1234 ABC")
        else Choice("Matrícula",listOf("1234 ABC · SEAT León"))
        // All options are fixtures; no availability checks or remote booking occurs.
        Choice("Centro ITV",listOf("ITV Alicante 1", "ITV Alicante 2"))
        Text("Fecha disponible", style=MaterialTheme.typography.labelLarge)
        OutlinedButton(onClick={calendar=true}, modifier=Modifier.fillMaxWidth().heightIn(min=52.dp)) {
            Text(date, Modifier.weight(1f), textAlign=TextAlign.Start)
            ItvIcon("appointments", "Abrir calendario")
        }
        Choice("Hora disponible",listOf("10:30", "11:00", "11:30", "12:00", "16:00"))
        Primary(if(editing) "Guardar" else "Agendar") { notify(if(editing) "Modificación de cita simulada" else "Reserva simulada: se muestra la cita de ejemplo"); done() }
        Secondary("Cancelar", done)
    }
    if(calendar) DatePickerDialog(onDismissRequest={calendar=false},
        confirmButton={TextButton(onClick={
            picker.selectedDateMillis?.let { millis ->
                date=SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-ES")).apply {
                    timeZone=TimeZone.getTimeZone("UTC")
                }.format(Date(millis))
            }
            calendar=false
        }) {Text("Aceptar")}}, dismissButton={TextButton(onClick={calendar=false}){Text("Cancelar")}}) { DatePicker(state=picker) }
}

@Composable
internal fun CheckInScreen() {
    Heading("Tu turno está preparado", "Escanea el código en el centro para identificar tu cita.")
    Panel {
        Text("Código QR", style=MaterialTheme.typography.titleMedium)
        QrIllustration(Modifier.align(Alignment.CenterHorizontally))
        Text("Código ilustrativo · sin validez", Modifier.fillMaxWidth(), textAlign=TextAlign.Center,
            style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
        Status("Turno de ejemplo: A-001")
        InfoRow("Vehículo", "1234 ABC · SEAT León")
        InfoRow("Centro", "ITV Alicante 1")
        InfoRow("Fecha de reserva", "12/11/2026 · 10:30")
    }
}

@Composable
private fun QrIllustration(modifier: Modifier) {
    Canvas(modifier.size(200.dp).background(Color.White).padding(12.dp)
        .semantics { contentDescription="Código QR ilustrativo, sin validez para realizar un check-in real" }) {
        val cell=size.minDimension/25f
        fun finder(x: Int, y: Int) {
            drawRect(Color(0xFF182C42),Offset(x*cell,y*cell),Size(7*cell,7*cell))
            drawRect(Color.White,Offset((x+1)*cell,(y+1)*cell),Size(5*cell,5*cell))
            drawRect(Color(0xFF182C42),Offset((x+2)*cell,(y+2)*cell),Size(3*cell,3*cell))
        }
        for(y in 0..24) for(x in 0..24) {
            val reserved=(x<8&&y<8)||(x>16&&y<8)||(x<8&&y>16)
            if(!reserved && (x*3+y*5+x*y)%7<3) drawRect(Color(0xFF182C42),Offset(x*cell,y*cell),Size(cell,cell))
        }
        finder(0,0); finder(18,0); finder(0,18)
    }
}
