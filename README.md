# Jarvis para Android

MVP de un asistente personal que escucha la palabra **“Jarvis”** en el dispositivo, responde **“Sí, señor”** y abre la aplicación oficial de ChatGPT. Si ChatGPT tiene habilitado `Settings → Voice → Advanced → Start with Voice`, una conversación nueva o vacía comenzará en voz automáticamente.

## Estado

Versión inicial `0.1.0`. Requiere validación en un teléfono Android real. Android puede impedir que una aplicación abra otra desde segundo plano; cuando ocurra, el siguiente incremento añadirá una notificación de apertura como alternativa segura.

## Privacidad

- La detección se ejecuta localmente con Porcupine.
- La aplicación no almacena grabaciones.
- La clave de Picovoice se guarda solamente en las preferencias privadas del teléfono y no se sube a Git.
- La voz de confirmación es una voz TTS instalada en Android. No clona ni imita a Paul Bettany.

## Requisitos

- Android Studio con JDK 17.
- Android SDK 35.
- Android 8.0/API 26 o posterior.
- ChatGPT para Android instalado.
- Cuenta y AccessKey de [Picovoice Console](https://console.picovoice.ai/).

## Configuración

1. Clona el repositorio.
2. Copia `local.properties.example` como `local.properties` y ajusta `sdk.dir`.
3. Abre el proyecto en Android Studio y ejecuta `app`.
4. En ChatGPT, selecciona `Settings → Voice → Advanced` y activa `Start with Voice`.
5. Abre Jarvis, pega tu AccessKey de Picovoice y pulsa **Guardar AccessKey**.
6. Concede micrófono/notificaciones y pulsa **Activar Jarvis**.

## Flujo

1. Un servicio visible escucha la palabra “Jarvis”.
2. Porcupine procesa el audio localmente.
3. Un control de tres segundos evita activaciones duplicadas.
4. Android TTS dice “Sí, señor”.
5. Al finalizar, se abre ChatGPT.

## Estructura

- `domain`: reglas puras y estados.
- `wakeword`: abstracción y adaptador de Porcupine.
- `speech`: confirmación mediante Android TTS.
- `launcher`: apertura segura de ChatGPT.
- `service`: coordinación del servicio de micrófono.

Consulta [Arquitectura](docs/ARCHITECTURE.md), [Instalación](docs/INSTALLATION.md) y [Pruebas](docs/TESTING.md).

## Limitaciones

- ChatGPT no publica un Intent para seleccionar Advanced Voice desde otra aplicación. El usuario debe configurarlo una vez.
- `Start with Voice` depende de que ChatGPT abra una conversación nueva o vacía.
- Android puede restringir la apertura automática de actividades desde segundo plano.
- Porcupine necesita una AccessKey, aunque la detección de la palabra se realiza localmente.
- Las voces TTS instaladas cambian según el fabricante del teléfono.

## Licencia

Código del proyecto para uso personal. Las dependencias conservan sus propias licencias y condiciones.
