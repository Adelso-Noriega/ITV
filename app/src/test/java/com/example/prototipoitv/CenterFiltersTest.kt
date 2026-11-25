package com.example.prototipoitv

import org.junit.Assert.*
import org.junit.Test

class CenterFiltersTest {
    @Test fun nearestAndBestRatedUseTheSameFilteredStations() {
        val nearest = filterStations("", 0, 0.0, false)
        assertEquals("ITV Alicante", nearest.first().name)
        assertEquals(nearest.map(::distance).sorted(), nearest.map(::distance))
        val best = filterStations("", 50, 4.0, true)
        assertTrue(best.isNotEmpty())
        assertTrue(best.all { distance(it) <= 50 && it.rating >= 4.0 })
        assertEquals(best.map { it.rating }.sortedDescending(), best.map { it.rating })
        assertTrue(filterStations("03006", 0, 4.2, true).isEmpty())
        assertEquals(stations.size, nearest.size)
    }

    @Test fun offlineOverviewPlacesEveryStationInsideTheViewport() {
        val html = mapHtml(stations, stations.first(), false)
        val markers = Regex("""translate\(([-\d.]+) ([-\d.]+)\)""").findAll(html).toList()
        assertEquals(stations.size, markers.size)
        markers.forEach {
            assertTrue(it.groupValues[1].toDouble() in 9.0..391.0)
            assertTrue(it.groupValues[2].toDouble() in 9.0..391.0)
        }
        assertTrue(html.contains("<polygon"))
        assertTrue(html.contains("EPS II"))
        val zoomed = mapHtml(listOf(stations.first()), stations.first(), true)
        val center = Regex("""translate\(([-\d.]+) ([-\d.]+)\)""").find(zoomed)!!
        assertEquals(200.0, center.groupValues[1].toDouble(), 0.01)
        assertEquals(200.0, center.groupValues[2].toDouble(), 0.01)
    }
}
