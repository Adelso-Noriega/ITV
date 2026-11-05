package com.example.prototipoitv

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** End-to-end checks of the prototype's navigation, without real credentials or services. */
@RunWith(AndroidJUnit4::class)
class NavigationTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()

    private fun tap(text: String) {
        val node = ui.onNodeWithText(text)
        try { node.assertIsDisplayed() } catch (_: AssertionError) { node.performScrollTo() }
        node.performClick()
    }
    private fun back() { ui.onNodeWithContentDescription("Volver").performClick() }
    private fun enter() { tap("Iniciar sesión"); tap("Entrar") }

    @Test fun publicScreensAndRegistration() {
        tap("Registro con Google")
        ui.onNodeWithText("Permiso de acceso a Google").assertIsDisplayed()
        tap("Denegar")
        tap("Registrarme")
        tap("Cancelar")
        tap("Iniciar sesión")
        tap("¿Olvidaste tu contraseña?")
        tap("Guardar")
        ui.onNodeWithText("Bienvenido de nuevo").assertIsDisplayed()
        back()
        tap("Registro con Google")
        tap("Conceder permiso")
        tap("Aceptar")
        ui.onNodeWithText("Hola, Alberto").assertIsDisplayed()
        ui.activityRule.scenario.recreate()
        ui.onNodeWithText("Hola, Alberto").assertIsDisplayed()
    }

    @Test fun accountScreensAndLogout() {
        enter()
        tap("Cuenta")
        tap("Perfil")
        tap("Modificar")
        tap("Guardar")
        ui.onNodeWithText("Tu cuenta").assertIsDisplayed()
        tap("Seguridad")
        tap("Guardar")
        tap("Eliminar cuenta")
        tap("Cancelar")
        tap("Cerrar sesión")
        ui.onNodeWithText("Tu ITV, más sencilla").assertIsDisplayed()
    }

    @Test fun vehiclesCentersAndResults() {
        enter()
        tap("Mis vehículos")
        tap("+ Registrar vehículo")
        tap("Registrar")
        tap("Detalle / Modificar")
        tap("Modificar vehículo")
        tap("Guardar")
        ui.onNodeWithText("VSSZZZ5FZLR123456").assertExists()
        tap("Inicio")
        tap("Centros ITV")
        tap("Mapa")
        ui.onNodeWithContentDescription("Mapa de ejemplo con dos centros ITV en Alicante").assertIsDisplayed()
        tap("Lista")
        back()
        tap("Resultados ITV")
        tap("Histórico")
        tap("Buscar")
        tap("Último resultado")
        ui.onNodeWithText("Favorable").assertIsDisplayed()
    }

    @Test fun appointmentsAndCheckIn() {
        enter()
        tap("Reservar cita")
        tap("Agendar")
        tap("Modificar cita")
        tap("Guardar")
        tap("Realizar check-in")
        ui.onNodeWithText("Turno de ejemplo: A-001").assertIsDisplayed()
        back()
        tap("Eliminar cita")
        tap("Cancelar")
        ui.onNodeWithText("Cita confirmada").assertIsDisplayed()
    }
}
