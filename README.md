# Prototipo ITV

Prototipo académico en Kotlin y Jetpack Compose basado en los 18 apartados de `WireFrames ITV.pdf`. Incluye pantallas, diálogos y navegación. No tiene servidor, autenticación real, base de datos ni reservas reales.

## Ejecutar en Android Studio

1. Abrir esta carpeta como proyecto.
2. Esperar a que termine la sincronización de Gradle.
3. Seleccionar la configuración `app` y un emulador o dispositivo Android (API 24 o superior).
4. Pulsar Run.
5. Elegir **Iniciar sesión > Entrar**, sin necesidad de introducir credenciales, o **Registrarme > Aceptar**.

## Pantallas del documento

| Apartado | Acceso en el prototipo |
| --- | --- |
| 1. Home registro | Pantalla inicial |
| 2. Autorizar cuenta Google | Registro con Google |
| 3. Términos y condiciones | Registrarme, o conceder permiso de Google |
| 4. Inicio de sesión | Iniciar sesión |
| 5. Recuperar contraseña | ¿Olvidaste tu contraseña? |
| 6. Home de usuario | Entrar, o aceptar términos |
| 7. Cuenta | Botón de usuario en la barra superior |
| 8. Perfil | Cuenta > Perfil |
| 9. Cambio de contraseña | Cuenta > Seguridad |
| 10. Gestión de vehículos | Barra inferior: Vehículos |
| 11. Registrar vehículo | Vehículos > Registrar vehículo |
| 12. Detalle / Modificar vehículo | Vehículos > Detalle / Modificar |
| 13. Centros ITV | Barra inferior > Centros; pestañas Lista y Mapa |
| 14. Resultados ITV | Barra inferior > Resultados; Último resultado e Histórico |
| 15. Gestión de citas | Barra inferior: Citas |
| 16. Agendar cita | Citas > Agendar nueva cita |
| 17. Modificar cita | Citas > Modificar cita |
| 18. Realizar check-in | Inicio o Citas > Realizar check-in |

La sesión arranca con tres vehículos y tres citas. Las altas, modificaciones, cancelaciones y nombres de documentos permanecen en memoria al navegar o girar el móvil. Eliminar un vehículo también elimina sus citas. Para repetir la prueba con los datos iniciales, detén y vuelve a ejecutar la aplicación.

Centros muestra las 14 estaciones con dirección para vehículos del catálogo de la Generalitat en la provincia de Alicante. El mapa parte de la EPS II y calcula distancias en línea recta. La costa y los marcadores se dibujan también sin conexión. Con internet se añade la cartografía de OpenStreetMap. Lista y Mapa comparten la búsqueda, el radio desde EPS II, la valoración mínima y la ordenación. Las valoraciones corresponden al directorio de Guía Talleres del 5 de octubre de 2026. «Cómo llegar» abre la ruta por carretera con ese mismo origen. Las reservas y resultados son de demostración.

Las pantallas se organizan en `AuthScreens.kt`, `VehicleScreens.kt` y `VisitScreens.kt`. `ItvApp.kt` contiene el tema y la navegación; `Components.kt`, los componentes visuales compartidos.

## Comprobaciones

Desde la terminal del proyecto, con el JDK de Android Studio configurado:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

La segunda orden necesita un emulador o dispositivo conectado. `NavigationTest.kt` comprueba acceso, recuperación, registro, cuenta, vehículos, centros, resultados, citas, check-in y conservación de la pantalla al recrear la actividad.

El APK de prueba se genera en `app/build/outputs/apk/debug/app-debug.apk`.
