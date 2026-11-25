package com.example.prototipoitv

import org.junit.Assert.*
import org.junit.Test

class ItvSessionTest {
    @Test fun removingVehicleCancelsOnlyItsAppointments() {
        val session = ItvSession()
        assertEquals(3, session.vehicles.size)
        assertEquals(3, session.upcoming.size)
        val removed = session.vehicles.first()
        session.removeVehicle(removed.id)
        assertEquals(2, session.vehicles.size)
        assertEquals(2, session.upcoming.size)
        assertTrue(session.visits.none { it.vehicleId == removed.id })
    }

    @Test fun editsRetainIdentityAndNewSessionReseedsData() {
        val session = ItvSession()
        val vehicle = session.vehicles.first()
        session.saveVehicle(vehicle.copy(plate = "0000 BBB", documents = mapOf("Permiso de circulación" to "mi-permiso.pdf")))
        val visit = session.upcoming.first()
        session.saveVisit(visit.copy(day = futureDay(30), station = stations.last()))
        assertEquals(3, session.vehicles.size)
        assertEquals(3, session.visits.size)
        assertEquals(visit.id, session.upcoming.last().id)
        assertEquals("mi-permiso.pdf", session.vehicles.first().documents["Permiso de circulación"])
        session.vehicles.toList().forEach { session.removeVehicle(it.id) }
        assertTrue(session.upcoming.isEmpty())
        assertTrue(session.vehicles.isEmpty())
        assertEquals(3, ItvSession().vehicles.size)
    }

    @Test fun catalogueHasDistinctLocationsAndValidSeedPlates() {
        assertEquals(14, stations.size)
        assertEquals(stations.size, stations.distinctBy { it.lat to it.lon }.size)
        assertTrue(stations.all { it.lat in 37.8..38.9 && it.lon in -1.1..0.3 && it.postcode.startsWith("03") })
        assertTrue(ItvSession().vehicles.all { it.plate.matches(Regex("[0-9]{4} [BCDFGHJKLMNPRSTVWXYZ]{3}")) })
    }
}
