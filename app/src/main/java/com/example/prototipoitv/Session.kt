package com.example.prototipoitv

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

internal data class Station(val name: String, val address: String, val postcode: String, val lat: Double, val lon: Double, val rating: Double)

// Direcciones: Generalitat Valenciana / SITVAL, dadesobertes.gva.es/dataset/tmv-est-itv.
// Posiciones: mapas de las fichas de estación de guiatalleres.com/itv/alicante/.
internal val stations = listOf(
    Station("ITV Alcoy", "Carretera de la Font Roja, 21", "03801", 38.681341, -0.472048, 3.4),
    Station("ITV Alicante", "Polígono industrial Pla de la Vallonga, calle Nieve, 7", "03006", 38.347261, -0.550447, 3.5),
    Station("ITV Benidorm", "Avenida de la Comunidad Valenciana, s/n", "03503", 38.550058, -0.112662, 3.8),
    Station("ITV Calpe", "Depósito municipal de vehículos", "03710", 38.662971, 0.055396, 3.9),
    Station("ITV El Campello", "Av. Pla de Messell", "03560", 38.445563, -0.382389, 4.2),
    Station("ITV Elche", "Carretera de Aspe, s/n", "03206", 38.289215, -0.724815, 3.8),
    Station("ITV Novelda", "Calle Betíes, polígono industrial Fondonet", "03660", 38.389142, -0.749496, 4.2),
    Station("ITV Ondara", "Carretera CV-7260, 731", "03760", 38.822970, 0.012800, 3.6),
    Station("ITV Orihuela", "Carretera Orihuela–Almoradí, km 8,3", "03314", 38.089129, -0.848570, 4.1),
    Station("ITV Pilar de la Horadada", "Barrio \"Los Villenas\", nº 5 - frente al pozo de riego", "03190", 37.856324, -0.800186, 4.4),
    Station("ITV Redován", "N-340, km 690", "03370", 38.132913, -0.931776, 4.2),
    Station("ITV Santa Pola", "Calle Marineros, 9", "03130", 38.209120, -0.578454, 4.0),
    Station("ITV Torrevieja", "Urbanización Torreta II, CV-905, km 10,8", "03181", 37.996554, -0.690983, 4.0),
    Station("ITV Villena", "Autovía A-31, km 191,5 (Colonia Santa Eulalia)", "03400", 38.576825, -0.836870, 3.8)
)

internal data class Vehicle(
    val plate: String, val brand: String, val model: String, val year: String,
    val type: String = "Turismo", val id: String = UUID.randomUUID().toString(),
    val documents: Map<String, String> = emptyMap(), val sampleResult: Boolean = false
)
internal data class Visit(val vehicleId: String, val station: Station, val day: Long, val hour: String,
    val id: String = UUID.randomUUID().toString())

internal fun futureDay(offset: Int): Long = Calendar.getInstance().run {
    add(Calendar.DAY_OF_YEAR, offset)
    val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    utc.clear()
    utc.set(get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH))
    utc.timeInMillis
}
internal fun displayDay(day: Long): String = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es-ES")).run {
    timeZone = TimeZone.getTimeZone("UTC")
    format(Date(day))
}

// Activity-scoped: survives rotation and navigation, resets on a new session.
internal class ItvSession : ViewModel() {
    val vehicles = mutableStateListOf(
        Vehicle("1234 BCD", "SEAT", "León", "2020", id = "seat",
            documents = mapOf("Ficha técnica / tarjeta ITV" to "ficha-tecnica-ejemplo.pdf"), sampleResult = true),
        Vehicle("5070 KLM", "Toyota", "Corolla", "2019", id = "toyota", sampleResult = true),
        Vehicle("0082 MNP", "Renault", "Clio", "2023", id = "renault")
    )
    val visits = mutableStateListOf(*vehicles.mapIndexed { i, v ->
        Visit(v.id, stations[listOf(1,5,2)[i]], futureDay(2 + i * 4), listOf("10:30", "11:00", "16:00")[i], "visit-$i")
    }.toTypedArray())
    fun removeVehicle(id: String) {
        visits.removeAll { it.vehicleId == id }
        vehicles.removeAll { it.id == id }
    }
    fun saveVehicle(v: Vehicle) {
        val index = vehicles.indexOfFirst { it.id == v.id }
        if (index < 0) vehicles.add(v) else vehicles[index] = v
    }
    fun saveVisit(v: Visit) {
        val index = visits.indexOfFirst { it.id == v.id }
        if (index < 0) visits.add(v) else visits[index] = v
    }
    val upcoming get() = visits.filter { it.day >= futureDay(0) }.sortedWith(compareBy(Visit::day, Visit::hour))
}
