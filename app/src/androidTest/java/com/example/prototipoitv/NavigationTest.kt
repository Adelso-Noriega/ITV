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
        val node = ui.onAllNodesWithText(text).onFirst()
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
        ui.onNodeWithText("Entrar").assertIsDisplayed()
        tap("Crear cuenta")
        tap("Registro con Google")
        tap("Conceder permiso")
        tap("Aceptar")
        ui.onNodeWithText("Hola, Usuario").assertIsDisplayed()
        ui.activityRule.scenario.recreate()
        ui.onNodeWithText("Hola, Usuario").assertIsDisplayed()
    }

    @Test fun accountScreensAndLogout() {
        enter()
        ui.onNodeWithContentDescription("Abrir cuenta").performClick()
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
        tap("Vehículos")
        tap("Documentación")
        ui.onNodeWithText("Permiso de circulación").assertExists()
        tap("Detalle")
        tap("Modificar vehículo")
        tap("Guardar")
        ui.onNodeWithText("Tipo de vehículo").assertExists()
        ui.onNodeWithText("VIN", substring = true).assertDoesNotExist()
        tap("Centros")
        tap("Mapa")
        ui.onNodeWithText("Centro del mapa").assertExists()
        tap("Lista")
        tap("Resultados")
        tap("Histórico")
        tap("Buscar")
        tap("Último resultado")
        ui.onNodeWithText("Favorable").assertExists()
    }

    @Test fun appointmentsAndCheckIn() {
        enter()
        tap("Citas")
        tap("+ Agendar nueva cita")
        tap("Agendar")
        ui.onAllNodesWithText("Cita confirmada").assertCountEquals(4)
        tap("Modificar cita")
        tap("Guardar")
        tap("Realizar check-in")
        ui.onNodeWithText("Turno de ejemplo: A-001").assertExists()
        back()
        repeat(4) {
            tap("Cancelar cita")
            tap("Sí, cancelar cita")
        }
        tap("Inicio")
        ui.onNodeWithText("Agendar nueva cita").assertExists()
        ui.activityRule.scenario.recreate()
        ui.onNodeWithText("Agendar nueva cita").assertExists()
        tap("Vehículos")
        repeat(3) {
            tap("Eliminar vehículo")
            tap("Eliminar")
        }
        tap("Inicio")
        tap("Añadir coche")
        ui.onNodeWithText("Nuevo vehículo").assertExists()
    }
}
