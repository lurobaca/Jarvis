# Arquitectura

La solución usa Clean Architecture de forma deliberadamente pequeña. Las interfaces existen solamente en las fronteras sustituibles: detección, voz y apertura de ChatGPT.

`WakeWordListenerService` coordina el caso de uso sin conocer detalles internos de Porcupine o TTS. `WakeWordGate` contiene la regla pura del periodo de enfriamiento y se prueba sin Android.

## Decisiones

- Una sola app module evita complejidad prematura.
- Porcupine ofrece detección local y una palabra integrada `JARVIS`.
- Foreground Service hace visible el uso continuo del micrófono.
- Android TTS evita distribuir archivos de una voz protegida o clonada.
- La app oficial de ChatGPT se abre mediante su paquete público; no se usan Activities internas.
