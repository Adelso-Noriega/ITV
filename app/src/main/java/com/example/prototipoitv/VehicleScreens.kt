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
internal fun VehiclesScreen(navigate: (Screen) -> Unit, notify: (String) -> Unit) {
    var deleting by rememberSaveable { mutableStateOf(false) }
    Heading("Tus vehículos", "Consulta y prepara la documentación de tu vehículo.")
    Primary("+ Registrar vehículo") { navigate(Screen.VEHICLE_NEW) }
    Panel {
        Row(verticalAlignment=Alignment.CenterVertically, horizontalArrangement=Arrangement.spacedBy(12.dp)) {
            ItvIcon("vehicles")
            Column {
                Text("1234 ABC", style=MaterialTheme.typography.titleLarge, fontWeight=FontWeight.Bold)
                Text("SEAT León · 2020", color=MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Status("ITV vigente")
        InfoRow("Próxima inspección", "01/01/2027")
        Secondary("Detalle / Modificar") { navigate(Screen.VEHICLE_DETAIL) }
        TextButton(onClick={deleting=true}, modifier=Modifier.align(Alignment.End)) { Text("Eliminar vehículo", color=MaterialTheme.colorScheme.error) }
    }
    if(deleting) ConfirmDialog("Eliminar vehículo", "¿Quieres simular la eliminación de 1234 ABC? El vehículo de ejemplo se mantendrá para poder recorrer el prototipo.",
        "Eliminar", {deleting=false}) { deleting=false; notify("Eliminación simulada") }
}

@Composable
internal fun VehicleFormScreen(editing: Boolean, notify: (String) -> Unit, done: () -> Unit) {
    Heading(if(editing) "Modificar vehículo" else "Nuevo vehículo")
    Panel {
        Field("Matrícula", if(editing) "1234 ABC" else "")
        Choice("Año de matrícula", listOf("2020", "2021", "2022", "2023", "2024", "2025", "2026"))
        Choice("Marca", listOf("SEAT", "Toyota", "Renault", "Peugeot", "Volkswagen"))
        Choice("Modelo", listOf("León", "Ibiza", "Corolla", "Clio", "Golf", "Otro"))
        Choice("Tipo de vehículo", listOf("Turismo", "Motocicleta", "Furgoneta"))
        Field("VIN / Número de bastidor", if(editing) "VSSZZZ5FZLR123456" else "")
        Text("Documentación", style=MaterialTheme.typography.titleMedium)
        FilePicker("Ficha técnica", notify)
        FilePicker("Permiso de circulación", notify)
        Primary(if(editing) "Guardar" else "Registrar") {
            notify(if(editing) "Modificación simulada" else "Registro simulado: se conserva el vehículo de ejemplo")
            done()
        }
        Secondary("Cancelar", done)
    }
}

@Composable
internal fun VehicleDetailScreen(notify: (String) -> Unit) {
    var editing by rememberSaveable { mutableStateOf(false) }
    if(editing) {
        VehicleFormScreen(true, notify) { editing=false }
    } else {
        Panel {
            Row(Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
                Text("1234 ABC", Modifier.weight(1f), style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.Bold)
                ItvIcon("vehicles")
            }
            InfoRow("Año de matrícula", "2020")
            InfoRow("Marca", "SEAT")
            InfoRow("Modelo", "León")
            InfoRow("Tipo de vehículo", "Turismo")
            InfoRow("VIN", "VSSZZZ5FZLR123456")
            Primary("Modificar vehículo") { editing=true }
        }
        Panel {
            Text("Actualizar documentos", style=MaterialTheme.typography.titleMedium)
            FilePicker("Ficha técnica", notify)
            FilePicker("Permiso de circulación", notify)
            Primary("Guardar documentos") { notify("Documentación de ejemplo guardada") }
        }
    }
}

@Composable
internal fun ResultsScreen(notify: (String) -> Unit) {
    var history by rememberSaveable { mutableStateOf(false) }
    Choice("Vehículo", listOf("1234 ABC · SEAT León"))
    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        FilterChip(selected=!history, onClick={history=false}, label={Text("Último resultado")}, modifier=Modifier.weight(1f))
        FilterChip(selected=history, onClick={history=true}, label={Text("Histórico")}, modifier=Modifier.weight(1f))
    }
    if(!history) {
        Panel {
            Text("Inspección · 01/01/2026", style=MaterialTheme.typography.titleMedium)
            Status("Favorable")
            Text("Válida hasta el 01/01/2027")
            Text("ITV Alicante 1", color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Panel {
            Text("Detalle de la inspección", style=MaterialTheme.typography.titleMedium)
            InfoRow("Identificación del vehículo", "Sin defectos")
            InfoRow("Frenos", "2 defectos leves")
            InfoRow("Alumbrado y señalización", "Sin defectos")
            InfoRow("Emisiones", "Sin defectos")
            Primary("Descargar informe") { notify("Prototipo: no se descarga un informe real") }
        }
    } else {
        Panel {
            Text("Filtrar por año", style=MaterialTheme.typography.titleMedium)
            Field("Desde", "2020", keyboard=KeyboardType.Number)
            Field("Hasta", "2026", keyboard=KeyboardType.Number)
            Secondary("Buscar") { notify("Se muestran las inspecciones de ejemplo") }
        }
        listOf("01/01/2026", "01/01/2024").forEach { date ->
            Panel {
                Text(date, style=MaterialTheme.typography.titleMedium)
                Status("Favorable")
                Text("ITV Alicante 1")
                Secondary("Descargar informe") { notify("Prototipo: no se descarga un informe real") }
            }
        }
    }
}
