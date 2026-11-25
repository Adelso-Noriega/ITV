package com.example.prototipoitv

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
internal fun CheckInScreen(session: ItvSession, visit: Visit) {
    val vehicle = session.vehicles.first { it.id == visit.vehicleId }
    Heading("Tu turno está preparado", "Escanea el código en el centro para identificar tu cita.")
    Panel {
        Text("Código QR", style=MaterialTheme.typography.titleMedium)
        QrIllustration(Modifier.align(Alignment.CenterHorizontally))
        Text("Código ilustrativo · sin validez", Modifier.fillMaxWidth(), textAlign=TextAlign.Center,
            style=MaterialTheme.typography.bodySmall, color=MaterialTheme.colorScheme.onSurfaceVariant)
        Status("Turno de ejemplo: A-001")
        InfoRow("Vehículo", "${vehicle.plate} · ${vehicle.brand} ${vehicle.model}")
        InfoRow("Centro", visit.station.name)
        InfoRow("Fecha de reserva", "${displayDay(visit.day)} · ${visit.hour}")
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
