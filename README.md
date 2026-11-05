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
| 7. Cuenta | Barra inferior: Cuenta |
| 8. Perfil | Cuenta > Perfil |
| 9. Cambio de contraseña | Cuenta > Seguridad |
| 10. Gestión de vehículos | Barra inferior: Vehículos |
| 11. Registrar vehículo | Vehículos > Registrar vehículo |
| 12. Detalle / Modificar vehículo | Vehículos > Detalle / Modificar |
| 13. Centros ITV | Inicio > Centros ITV; pestañas Lista y Mapa |
| 14. Resultados ITV | Inicio > Resultados ITV; Último resultado e Histórico |
| 15. Gestión de citas | Barra inferior: Citas |
| 16. Agendar cita | Inicio > Reservar cita, o Citas > Agendar cita |
| 17. Modificar cita | Citas > Modificar cita |
| 18. Realizar check-in | Inicio o Citas > Realizar check-in |

Los formularios admiten cambios visuales; guardar muestra una confirmación y permite continuar la navegación. Los registros de ejemplo permanecen disponibles. Buscar, seleccionar documentos y descargar informes muestran una respuesta de demostración. El mapa y el QR son ilustrativos. Google se representa mediante un diálogo local y no solicita ninguna cuenta real.

Las pantallas se organizan en `AuthScreens.kt`, `VehicleScreens.kt` y `VisitScreens.kt`. `ItvApp.kt` contiene el tema y la navegación; `Components.kt`, los componentes visuales compartidos.

## Comprobaciones

Desde la terminal del proyecto, con el JDK de Android Studio configurado:

```powershell
.\gradlew.bat :app:assembleDebug :app:lintDebug
.\gradlew.bat :app:connectedDebugAndroidTest
```

La segunda orden necesita un emulador o dispositivo conectado. `NavigationTest.kt` comprueba acceso, recuperación, registro, cuenta, vehículos, centros, resultados, citas, check-in y conservación de la pantalla al recrear la actividad.

El APK de prueba se genera en `app/build/outputs/apk/debug/app-debug.apk`.
