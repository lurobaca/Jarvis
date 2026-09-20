# Jarvis para Android

MVP de un asistente personal que escucha la palabra **“Jarvis”** en el dispositivo, responde **“Sí, señor”** y abre la aplicación oficial de ChatGPT. Si ChatGPT tiene habilitado `Settings → Voice → Advanced → Start with Voice`, una conversación nueva o vacía comenzará en voz automáticamente.

## Estado

Versión inicial `0.1.0`. Requiere validación en un teléfono Android real. Android puede impedir que una aplicación abra otra desde segundo plano; cuando ocurra, el siguiente incremento añadirá una notificación de apertura como alternativa segura.

## Privacidad

- La detección se ejecuta localmente con Vosk.
- La aplicación no almacena grabaciones.
- No requiere una cuenta, una clave externa ni conexión a Internet para escuchar.
- La voz de confirmación es una voz TTS instalada en Android. No clona ni imita a Paul Bettany.

## Requisitos

- Android Studio con JDK 17.
- Android SDK 35.
- Android 8.0/API 26 o posterior.
- ChatGPT para Android instalado.

## Configuración

1. Clona el repositorio.
2. Copia `local.properties.example` como `local.properties` y ajusta `sdk.dir`.
3. Abre el proyecto en Android Studio y ejecuta `app`.
4. En ChatGPT, selecciona `Settings → Voice → Advanced` y activa `Start with Voice`.
5. Abre Jarvis, concede micrófono/notificaciones y pulsa **Activar Jarvis**.

## Flujo

1. Un servicio visible escucha la palabra “Jarvis”.
2. Vosk procesa el audio localmente con un modelo incluido en el APK.
3. Un control de tres segundos evita activaciones duplicadas.
4. Android TTS usa una voz masculina en español, cuando el motor instalado la ofrece, y dice “Sí, señor. ¿Qué necesita?”.
5. Al finalizar, Android invoca el asistente digital predeterminado.

## Estructura

- `domain`: reglas puras y estados.
- `wakeword`: abstracción y adaptador de Vosk.
- `speech`: confirmación mediante Android TTS.
- `launcher`: apertura segura de ChatGPT.
- `service`: coordinación del servicio de micrófono.

Consulta [Arquitectura](docs/ARCHITECTURE.md), [Instalación](docs/INSTALLATION.md) y [Pruebas](docs/TESTING.md).

## Limitaciones

- ChatGPT no publica un Intent para seleccionar Advanced Voice desde otra aplicación. El usuario debe configurarlo una vez.
- Para usar ChatGPT, debe estar disponible y seleccionado como asistente digital predeterminado de Android; si el fabricante no lo ofrece, Android abrirá el asistente disponible.
- `Start with Voice` depende de que ChatGPT abra una conversación nueva o vacía.
- Android puede restringir la apertura automática de actividades desde segundo plano.
- El APK es más grande porque incluye el modelo de reconocimiento sin conexión.
- Las voces TTS instaladas cambian según el fabricante del teléfono.

## Licencia

Código del proyecto para uso personal. Las dependencias conservan sus propias licencias y condiciones.
