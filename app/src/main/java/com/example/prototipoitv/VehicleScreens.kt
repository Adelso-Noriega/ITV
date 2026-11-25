package com.example.prototipoitv

import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
internal fun VehiclesScreen(session: ItvSession, add: () -> Unit, open: (Vehicle, Boolean) -> Unit) {
    var removing by rememberSaveable { mutableStateOf<String?>(null) }
    Heading("Tus vehículos")
    Primary("+ Añadir vehículo", add)
    if (session.vehicles.isEmpty()) Text("Aquí aparecerán los vehículos que añadas.")
    session.vehicles.forEach { v ->
        Panel {
            Heading(v.plate, "${v.brand} ${v.model} · ${v.year}")
            Secondary("Detalle") { open(v, false) }
            Secondary("Documentación") { open(v, true) }
            TextButton(onClick = { removing = v.id }) { Text("Eliminar vehículo", color = MaterialTheme.colorScheme.error) }
        }
    }
    session.vehicles.find { it.id == removing }?.let { v ->
        ConfirmDialog("Eliminar vehículo", "Se eliminará ${v.plate} y se cancelarán sus citas pendientes.", "Eliminar",
            { removing = null }) { session.removeVehicle(v.id); removing = null }
    }
}

@Composable
internal fun VehicleDetailScreen(session: ItvSession, vehicle: Vehicle, initialDocuments: Boolean, notify: (String) -> Unit, edit: () -> Unit) {
    var docs by rememberSaveable(vehicle.id) { mutableStateOf(initialDocuments) }
    Heading(vehicle.plate, "${vehicle.brand} ${vehicle.model}")
    TabRow(selectedTabIndex = if (docs) 1 else 0, containerColor = MaterialTheme.colorScheme.surface) {
        listOf("Detalle", "Documentación").forEachIndexed { index, label ->
            val selected = (index == 1) == docs
            Tab(selected = selected, onClick = { docs = index == 1 },
                modifier = Modifier.heightIn(min = 68.dp),
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                text = { Text(label, style = MaterialTheme.typography.titleSmall) })
        }
    }
    Panel {
        if (docs) {
            Text("Selecciona un documento del teléfono. Solo se guardará su nombre durante esta sesión.")
            for (label in listOf("Permiso de circulación", "Ficha técnica / tarjeta ITV")) {
                DocumentPicker(label, vehicle.documents[label], notify) { name ->
                    session.saveVehicle(vehicle.copy(documents = vehicle.documents + (label to name)))
                }
            }
        } else {
            InfoRow("Año de matrícula", vehicle.year)
            InfoRow("Marca", vehicle.brand)
            InfoRow("Modelo", vehicle.model)
            InfoRow("Tipo de vehículo", vehicle.type)
            Primary("Modificar vehículo", edit)
        }
    }
}

@Composable
private fun DocumentPicker(label: String, filename: String?, notify: (String) -> Unit, save: (String) -> Unit) {
    val resolver = LocalContext.current.contentResolver
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            val name = runCatching {
                resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                    if (cursor.moveToFirst()) cursor.getString(0) else null
                }
            }.getOrNull()
            if (name.isNullOrBlank()) notify("No se pudo leer el nombre del archivo") else save(name)
        }
    }
    InfoRow(label, filename ?: "Sin documento")
    Secondary(if (filename == null) "Seleccionar archivo" else "Cambiar archivo") {
        picker.launch(arrayOf("*/*"))
    }
}

@Composable
internal fun VehicleFormScreen(session: ItvSession, original: Vehicle?, done: () -> Unit) {
    var numbers by rememberSaveable { mutableStateOf(original?.plate?.take(4).orEmpty()) }
    var letters by rememberSaveable { mutableStateOf(original?.plate?.takeLast(3).orEmpty()) }
    var brand by rememberSaveable { mutableStateOf(original?.brand.orEmpty()) }
    var model by rememberSaveable { mutableStateOf(original?.model.orEmpty()) }
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    var year by rememberSaveable { mutableStateOf(original?.year ?: currentYear.toString()) }
    var type by rememberSaveable { mutableStateOf(original?.type ?: "Turismo") }
    var documents by rememberSaveable { mutableStateOf(HashMap(original?.documents.orEmpty())) }
    var error by rememberSaveable { mutableStateOf("") }
    Heading(if (original == null) "Nuevo vehículo" else "Modificar vehículo")
    Panel {
        PlateFields(numbers, letters, { numbers = it }, { letters = it })
        OutlinedTextField(brand, { brand = it }, label = { Text("Marca") }, singleLine = true)
        OutlinedTextField(model, { model = it }, label = { Text("Modelo") }, singleLine = true)
        Selection("Año de matrícula", (currentYear downTo 1900).map(Int::toString), year) { year = it }
        Selection("Tipo de vehículo", listOf("Turismo", "Motocicleta", "Furgoneta"), type) { type = it }
        for (label in listOf("Permiso de circulación", "Ficha técnica / tarjeta ITV")) {
            DocumentPicker(label, documents[label], { error = it }) { filename ->
                documents = HashMap(documents).apply { put(label, filename) }
            }
        }
        if (error.isNotEmpty()) Text(error, color = MaterialTheme.colorScheme.error)
        Primary(if (original == null) "Registrar" else "Guardar") {
            val plate = "$numbers $letters"
            error = when {
                !plate.matches(Regex("[0-9]{4} [BCDFGHJKLMNPRSTVWXYZ]{3}")) -> "Completa las cuatro cifras y las tres consonantes."
                session.vehicles.any { it.plate == plate && it.id != original?.id } -> "Ya existe un vehículo con esa matrícula."
                brand.isBlank() || model.isBlank() -> "Introduce la marca y el modelo."
                else -> ""
            }
            if (error.isEmpty()) {
                val v = original ?: Vehicle(plate, brand, model, year)
                session.saveVehicle(v.copy(plate = plate, brand = brand.trim(), model = model.trim(), year = year, type = type, documents = documents))
                done()
            }
        }
        Secondary("Cancelar", done)
    }
}
