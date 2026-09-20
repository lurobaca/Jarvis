# Arquitectura

La solución usa Clean Architecture de forma deliberadamente pequeña. Las interfaces existen solamente en las fronteras sustituibles: detección, voz y apertura de ChatGPT.

`WakeWordListenerService` coordina el caso de uso sin conocer detalles internos de Vosk o TTS. `WakeWordGate` contiene la regla pura del periodo de enfriamiento y se prueba sin Android.

## Decisiones

- Una sola app module evita complejidad prematura.
- Vosk ofrece reconocimiento local sin cuentas, claves ni servicios externos.
- Foreground Service hace visible el uso continuo del micrófono.
- Android TTS evita distribuir archivos de una voz protegida o clonada.
- La app oficial de ChatGPT se abre mediante su paquete público; no se usan Activities internas.
