package com.example.prototipoitv

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun HomeScreen(session: ItvSession, navigate: (Screen) -> Unit, newVisit: (Station?) -> Unit, open: (Visit, Screen) -> Unit) {
    Heading("Hola, Usuario")
    val next = session.upcoming.firstOrNull()
    if (next != null) {
        Heading("Próxima cita")
        VisitCard(session, next, open)
    } else {
        val needsCar = session.vehicles.isEmpty()
        Panel {
            Heading(if (needsCar) "Añade tu primer coche" else "Prepara tu próxima ITV",
                if (needsCar) "Registra tu vehículo para poder agendar su inspección." else "Todavía no tienes citas pendientes.")
            Button(onClick = { if (needsCar) navigate(Screen.VEHICLE_NEW) else newVisit(null) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 72.dp)) {
                ItvIcon("plus", modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(12.dp))
                Text(if (needsCar) "Añadir coche" else "Agendar nueva cita")
            }
        }
    }
}

@Composable
private fun VisitCard(session: ItvSession, visit: Visit, open: (Visit, Screen) -> Unit, cancel: (() -> Unit)? = null) {
    val v = session.vehicles.firstOrNull { it.id == visit.vehicleId } ?: return
    Panel {
        Status("Cita confirmada")
        Heading(visit.station.name, "${displayDay(visit.day)} · ${visit.hour}")
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            if (v.id == "seat") Image(painterResource(R.drawable.vehiculo_ejemplo), null,
                Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
            Column { InfoRow(v.plate, "${v.brand} ${v.model}") }
        }
        Primary("Realizar check-in") { open(visit, Screen.CHECK_IN) }
        Secondary("Modificar cita") { open(visit, Screen.APPOINTMENT_EDIT) }
        if (cancel != null) TextButton(onClick = cancel, modifier = Modifier.align(Alignment.End)) {
            Text("Cancelar cita", color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
internal fun AppointmentsScreen(session: ItvSession, newVisit: (Station?) -> Unit, open: (Visit, Screen) -> Unit) {
    var removing by rememberSaveable { mutableStateOf<String?>(null) }
    Heading("Tus próximas citas")
    val upcoming = session.upcoming
    if (upcoming.isEmpty()) {
        Text("Todavía no tienes citas pendientes.")
        Primary("+ Agendar nueva cita") { newVisit(null) }
    } else Secondary("+ Agendar nueva cita") { newVisit(null) }
    upcoming.forEach { visit -> VisitCard(session, visit, open) { removing = visit.id } }
    session.visits.find { it.id == removing }?.let { v ->
        ConfirmDialog("Cancelar cita", "¿Cancelar la cita en ${v.station.name} del ${displayDay(v.day)} a las ${v.hour}?",
            "Sí, cancelar cita", { removing = null }) { session.visits.remove(v); removing = null }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppointmentFormScreen(session: ItvSession, original: Visit?, stationName: String, back: () -> Unit,
    addCar: () -> Unit, saved: () -> Unit) {
    if (session.vehicles.isEmpty()) {
        Heading("Añade un vehículo para reservar")
        Primary("+ Añadir coche", addCar)
        return
    }
    var vehicleId by rememberSaveable { mutableStateOf(original?.vehicleId ?: session.vehicles.first().id) }
    val vehicle = session.vehicles.find { it.id == vehicleId } ?: session.vehicles.first()
    var selectedStation by rememberSaveable { mutableStateOf(stationName) }
    val center = stations.firstOrNull { it.name == selectedStation } ?: stations[1]
    var day by rememberSaveable { mutableLongStateOf(original?.day ?: futureDay(1)) }
    var hour by rememberSaveable { mutableStateOf(original?.hour ?: "10:30") }
    var calendar by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf("") }
    Heading(if (original == null) "Prepara tu próxima ITV" else "Modifica tu reserva")
    Panel {
        Selection("Matrícula", session.vehicles.map { it.plate }, vehicle.plate) { plate ->
            vehicleId = session.vehicles.first { it.plate == plate }.id
        }
        Selection("Centro ITV", stations.map { it.name }, center.name) { name -> selectedStation = name }
        Secondary("Fecha · ${displayDay(day)}") { calendar = true }
        Selection("Hora disponible", listOf("10:30", "11:00", "11:30", "12:00", "16:00"), hour) { hour = it }
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        Primary(if (original == null) "Agendar" else "Guardar") {
            error = when {
                day < futureDay(0) -> "Elige una fecha de hoy en adelante."
                session.visits.any { it.id != original?.id && it.vehicleId == vehicle.id && it.day == day && it.hour == hour } ->
                    "Este vehículo ya tiene una cita a esa hora."
                else -> ""
            }
            if (error.isEmpty()) {
                val visit = original ?: Visit(vehicle.id, center, day, hour)
                session.saveVisit(visit.copy(vehicleId = vehicle.id, station = center, day = day, hour = hour))
                saved()
            }
        }
        Secondary("Cancelar", back)
    }
    if (calendar) {
        val picker = rememberDatePickerState(initialSelectedDateMillis = day)
        DatePickerDialog(onDismissRequest = { calendar = false }, confirmButton = {
            TextButton(onClick = { picker.selectedDateMillis?.let { day = it }; calendar = false }) { Text("Aceptar") }
        }, dismissButton = { TextButton(onClick = { calendar = false }) { Text("Cancelar") } }) {
            DatePicker(state = picker)
        }
    }
}
