# Instalación

## Android Studio

Configura `local.properties`, sincroniza Gradle, conecta el teléfono con depuración USB y ejecuta la variante `debug`.

## ADB

Después de generar el APK:

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

En el teléfono, desactiva la optimización de batería para Jarvis si el fabricante interrumpe el servicio. La notificación de escucha debe permanecer visible.
