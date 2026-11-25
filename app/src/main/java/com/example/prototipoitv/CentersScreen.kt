package com.example.prototipoitv

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.util.Locale
import kotlin.math.*

private const val EPS_LAT = 38.38761
private const val EPS_LON = -0.51262
internal fun distance(s: Station): Double {
    val dLat = Math.toRadians(s.lat - EPS_LAT)
    val dLon = Math.toRadians(s.lon - EPS_LON)
    val a = sin(dLat / 2).pow(2) + cos(Math.toRadians(EPS_LAT)) * cos(Math.toRadians(s.lat)) * sin(dLon / 2).pow(2)
    return 12742.0 * asin(sqrt(a))
}

internal fun filterStations(query: String, radius: Int, minimum: Double, bestRated: Boolean): List<Station> {
    val filtered = stations.filter { station ->
        "${station.name} ${station.address} ${station.postcode}".contains(query.trim(), true) &&
            (radius == 0 || distance(station) <= radius) && station.rating >= minimum
    }
    return if (bestRated) filtered.sortedWith(compareByDescending<Station> { it.rating }.thenBy(::distance))
        else filtered.sortedBy(::distance)
}

private fun ratingText(rating: Double) = String.format(Locale.forLanguageTag("es-ES"), "%.1f", rating)

@Composable
internal fun CentersScreen(newVisit: (Station?) -> Unit) {
    var map by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var chosen by rememberSaveable { mutableStateOf("") }
    var zoom by rememberSaveable { mutableStateOf(false) }
    var radius by rememberSaveable { mutableIntStateOf(0) }
    var minimum by rememberSaveable { mutableDoubleStateOf(0.0) }
    var bestRated by rememberSaveable { mutableStateOf(false) }
    var filtersOpen by rememberSaveable { mutableStateOf(false) }
    val reset = { radius = 0; minimum = 0.0; bestRated = false; query = ""; zoom = false }
    val uri = LocalUriHandler.current
    val visible = filterStations(query, radius, minimum, bestRated)
    val selected = visible.find { it.name == chosen } ?: visible.firstOrNull()
    OutlinedTextField(query, { query = it; zoom = false }, label = { Text("Buscar centro o código postal") },
        singleLine = true, modifier = Modifier.fillMaxWidth())
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(!map, { map = false }, label = { Text("Lista") })
        FilterChip(map, { map = true }, label = { Text("Mapa") })
    }
    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text("${visible.size} de ${stations.size} centros")
        OutlinedButton(onClick = { filtersOpen = !filtersOpen }) {
            Text(if (filtersOpen) "Cerrar filtros" else if (radius > 0 || minimum > 0 || bestRated) "Filtros activos" else "Filtrar y ordenar")
        }
    }
    if (filtersOpen) {
        Panel {
            val orders = listOf("Más cercanos", "Mejor valorados")
            Selection("Ordenar por", orders, orders[if (bestRated) 1 else 0]) { bestRated = it == orders[1] }
            val radii = listOf(0, 10, 25, 50, 75)
            val radiusLabels = radii.map { if (it == 0) "Cualquier distancia" else "Hasta $it km" }
            Selection("Distancia desde EPS II", radiusLabels, radiusLabels[radii.indexOf(radius)]) {
                radius = radii[radiusLabels.indexOf(it)]
            }
            val ratings = listOf(0.0, 3.5, 4.0, 4.2)
            val ratingLabels = ratings.map { if (it == 0.0) "Todas las valoraciones" else "${ratingText(it)} ★ o más" }
            Selection("Valoración mínima", ratingLabels, ratingLabels[ratings.indexOf(minimum)]) {
                minimum = ratings[ratingLabels.indexOf(it)]
            }
            Text("Valoraciones de Guía Talleres · 05/10/2026", style = MaterialTheme.typography.bodySmall)
            TextButton(onClick = reset) { Text("Restablecer") }
        }
    }
    if (selected == null) {
        Text("No hay centros con estos filtros.")
        Secondary("Mostrar todos los centros", reset)
        return
    }
    Text("Origen simulado · EPS II, Universidad de Alicante")
    if (map) {
        val mapAnchor = remember { BringIntoViewRequester() }
        LaunchedEffect(Unit) { mapAnchor.bringIntoView() }
        Box(Modifier.bringIntoViewRequester(mapAnchor)) { StationMap(visible, selected, zoom) { chosen = it.name } }
        Text("Base local · Natural Earth | Cartografía © OpenStreetMap contributors", style = MaterialTheme.typography.labelSmall)
        Selection("Centro del mapa", visible.map { it.name }, selected.name) { chosen = it }
        TextButton(onClick = { zoom = !zoom }) { Text(if (zoom) "Ver toda la provincia" else "Acercar al centro") }
    }
    for (s in if (map) listOf(selected) else visible) {
        Panel {
            Heading(s.name, "${s.address} · ${s.postcode}")
            Text("★ ${ratingText(s.rating)} / 5", style = MaterialTheme.typography.titleSmall)
            Text(String.format(Locale.forLanguageTag("es-ES"), "%.1f km en línea recta desde EPS II", distance(s)))
            if (map) {
                Secondary("Cómo llegar desde EPS II") {
                    uri.openUri("https://www.google.com/maps/dir/?api=1&origin=$EPS_LAT,$EPS_LON&destination=${s.lat},${s.lon}&travelmode=driving")
                }
            } else TextButton(onClick = { chosen = s.name; zoom = true; map = true }) { Text("Ver en mapa") }
            Primary("Agendar cita") { newVisit(s) }
        }
    }
}

private fun tileX(lon: Double, n: Int) = (lon + 180) / 360 * n
private fun tileY(lat: Double, n: Int) = (1 - asinh(tan(Math.toRadians(lat))) / PI) / 2 * n

// Use a fixed SVG coordinate system: tile and pin heights must never depend on
// the intrinsic height of an HTML document inside a scrolling Compose screen.
internal fun mapHtml(visible: List<Station>, selected: Station, zoom: Boolean): String {
    val level = if (zoom) 11 else 8
    val n = 1 shl level
    val span = if (zoom) 2.4 else 1.12
    val left = tileX(if (zoom) selected.lon else -0.46, n) - span / 2
    val top = tileY(if (zoom) selected.lat else 38.36, n) - span / 2
    fun pixel(tile: Double, origin: Double) = (tile - origin) * 400 / span
    fun x(lon: Double) = pixel(tileX(lon, n), left)
    fun y(lat: Double) = pixel(tileY(lat, n), top)

    // Generalised Natural Earth land outline (world.geo.json/countries/ESP.geo.json).
    val coast = listOf(-2.0 to 40.0, -0.278711 to 39.309978, 0.111291 to 38.738514,
        -0.467124 to 38.292366, -0.683389 to 37.642354, -1.438382 to 37.443064, -2.0 to 37.0)
    val land = coast.joinToString(" ") { (lon, lat) -> "${x(lon)},${y(lat)}" }
    return buildString {
        append("""<!doctype html><html><head><meta name="viewport" content="width=device-width,initial-scale=1">
            <style>html,body{margin:0;width:100%;overflow:hidden}svg{display:block;width:100%;height:auto;aspect-ratio:1}
            text{font:13px sans-serif}a{cursor:pointer}.place{paint-order:stroke;stroke:white;stroke-width:3px;fill:#233c4c}</style>
            </head><body><svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 400 400" preserveAspectRatio="xMidYMid meet">
            <rect width="400" height="400" fill="#c9e6f2"/><polygon points="$land" fill="#edeedf" stroke="#91b4ac"/>
            <text x="${x(-0.13)}" y="${y(38.26)}" fill="#356a82" text-anchor="middle">Mediterráneo</text>""")
        val size = 400 / span
        for (col in floor(left).toInt()..floor(left + span).toInt()) {
            for (row in floor(top).toInt()..floor(top + span).toInt()) {
                append("""<foreignObject x="${pixel(col.toDouble(),left)}" y="${pixel(row.toDouble(),top)}" width="$size" height="$size">
                    <div xmlns="http://www.w3.org/1999/xhtml" style="height:100%;background:url(https://tile.openstreetmap.org/$level/$col/$row.png) center/100% 100%"></div>
                    </foreignObject>""")
            }
        }
        // Draw labels above the tiles so the same places are identifiable offline.
        val labels = if (zoom) listOf(selected) else stations.filter {
            it.name in listOf("ITV Alcoy", "ITV Benidorm", "ITV Villena", "ITV Elche", "ITV Ondara", "ITV Torrevieja")
        }
        labels.forEach { s ->
            append("""<text class="place" x="${x(s.lon)}" y="${y(s.lat)+22}" text-anchor="middle">${s.name.removePrefix("ITV ")}</text>""")
        }
        val ux = x(EPS_LON)
        val uy = y(EPS_LAT)
        append("""<path d="M $ux $uy l -10 -17" stroke="#176349" stroke-width="2"/>
            <rect x="${ux-56}" y="${uy-33}" width="49" height="19" rx="5" fill="#176349"/>
            <text x="${ux-32}" y="${uy-20}" text-anchor="middle" fill="white">EPS II</text>""")
        visible.sortedBy { it == selected }.forEach { s ->
            val i = stations.indexOf(s)
            val color = if (s == selected) "#a74208" else "#073cb9"
            append("""<a href="itv://station/$i" aria-label="${s.name}"><g transform="translate(${x(s.lon)} ${y(s.lat)})">
                <circle r="14" fill="transparent"/><circle r="9" fill="$color" stroke="white" stroke-width="2"/>
                <text y="4" text-anchor="middle" fill="white" style="font-size:10px">${i + 1}</text></g></a>""")
        }
        append("""</svg></body></html>""")
    }
}

@Composable
private fun StationMap(visible: List<Station>, selected: Station, zoom: Boolean, choose: (Station) -> Unit) {
    val html = remember(visible, selected, zoom) { mapHtml(visible, selected, zoom) }
    val chooseStation by rememberUpdatedState(choose)
    AndroidView(modifier = Modifier.fillMaxWidth().aspectRatio(1f), factory = { context ->
        WebView(context).apply {
            contentDescription = "Mapa de estaciones ITV desde EPS II"
            settings.allowFileAccess = false
            settings.allowContentAccess = false
            settings.userAgentString += " PrototipoITV-UA/1.0"
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val link: Uri = request.url
                    if (link.scheme == "itv" && link.host == "station") {
                        link.lastPathSegment?.toIntOrNull()?.let { stations.getOrNull(it) }?.let(chooseStation)
                    }
                    return true
                }
            }
        }
    }, update = { view ->
        if (view.tag != html) {
            view.tag = html
            view.loadDataWithBaseURL("https://map.prototipoitv.invalid/", html, "text/html", "UTF-8", null)
        }
    })
}
