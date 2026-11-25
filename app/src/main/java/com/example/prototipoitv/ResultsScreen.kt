package com.example.prototipoitv

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
internal fun ResultsScreen(session: ItvSession, notify: (String) -> Unit) {
    var history by rememberSaveable { mutableStateOf(false) }
    var selected by rememberSaveable { mutableStateOf("") }
    val vehicle = session.vehicles.firstOrNull { it.id == selected } ?: session.vehicles.firstOrNull()
    if (vehicle == null) {
        Heading("Sin vehículos", "Añade un vehículo desde Vehículos para consultar sus resultados.")
        return
    }
    Selection("Vehículo", session.vehicles.map { it.plate }, vehicle.plate) { plate ->
        selected = session.vehicles.first { it.plate == plate }.id
    }
    if (!vehicle.sampleResult) {
        Heading("Sin inspecciones registradas", vehicle.plate)
        return
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        FilterChip(selected=!history, onClick={history=false}, label={Text("Último resultado")}, modifier=Modifier.weight(1f))
        FilterChip(selected=history, onClick={history=true}, label={Text("Histórico")}, modifier=Modifier.weight(1f))
    }
    if(!history) {
        Panel {
            Text("Inspección · 01/01/2026", style=MaterialTheme.typography.titleMedium)
            Status("Favorable")
            Text("Válida hasta el 01/01/2027")
            Text(stations[1].name, color=MaterialTheme.colorScheme.onSurfaceVariant)
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
                Text(stations[1].name)
                Secondary("Descargar informe") { notify("Prototipo: no se descarga un informe real") }
            }
        }
    }
}
