# Cámara Inteligente

Aplicación Android nativa para detectar objetos localmente con la cámara o archivos de la galería. Usa CameraX y MediaPipe Tasks con modelos EfficientDet Lite en formato TensorFlow Lite; las imágenes y videos no se envían a una API externa.

## Funciones

- Detección múltiple en tiempo real con cajas, categoría y confianza.
- Umbral de confianza, máximo de resultados, modelo y delegado configurables.
- Cámara frontal/trasera, fotografía etiquetada y grabación de video con audio.
- Selección de fotografías y videos mediante el selector seguro de Android.
- Reproducción, pausa y análisis de fotogramas de video.
- Conteo por categoría en vivo.
- Historial local de análisis y resultados compartibles.
- Persistencia de fotografías con sus cajas y etiquetas.
- Solicitud en tiempo de ejecución de cámara y micrófono; la galería usa `OpenDocument` y no requiere acceso global al almacenamiento.

## Arquitectura

- Kotlin + Android Views y Navigation.
- CameraX: `Preview`, `ImageAnalysis` y `VideoCapture`.
- MediaPipe Tasks Vision + EfficientDet Lite0/Lite2.
- `SharedPreferences` + JSON para el índice del historial; medios anotados en almacenamiento privado y videos capturados en MediaStore.
- Procesamiento 100 % local.

## Ejecutar en GitHub Codespaces

1. En GitHub, abre **Code → Codespaces → Create codespace on main**.
2. Espera a que el contenedor instale JDK 17 y Android SDK 34.
3. Compila con `./gradlew assembleDebug`.
4. Descarga `app/build/outputs/apk/debug/app-debug.apk` e instálalo en un dispositivo Android 7.0 o posterior.

La cámara física no está disponible dentro del Codespace. Para probar cámara y grabación instala el APK en un teléfono; las imágenes de galería pueden probarse en un emulador conectado.

## Desarrollo local

Requisitos: JDK 17, Android SDK 34 y un dispositivo Android API 24+. Abre la raíz en Android Studio o ejecuta:

```bash
./gradlew testDebugUnitTest assembleDebug
```

Los modelos se descargan desde el almacenamiento oficial de MediaPipe durante la primera compilación y se empaquetan dentro del APK. En ejecución no se necesita internet.

## Privacidad

La inferencia ocurre en el dispositivo. Los resultados se conservan localmente hasta que la aplicación se desinstala o se eliminan sus datos. Compartir un resultado siempre requiere una acción explícita del usuario mediante el selector de Android.

La implementación de detección parte del ejemplo oficial de MediaPipe bajo licencia Apache 2.0; los encabezados originales se conservan en los archivos derivados.
