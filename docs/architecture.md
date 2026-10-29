# UVGspot — Arquitectura técnica (Entrega 4)

**Estado:** especificación de arquitectura aprobada como objetivo; no acredita implementación ni pruebas.  
**Paquete Android:** `gt.uvg.uvgspot`  
**Documentos relacionados:** `docs/delivery-4-requirements.md`, `docs/scope.md` (pendiente), `AGENTS.md`.

## 1. Objetivo y restricciones

UVGspot recomienda espacios de estudio de la UVG según preferencias y **disponibilidad estimada**, permite consultar detalles, enviar reportes de disponibilidad y guardar favoritos locales. Flujo principal: **Login → Needs → Recommendations → Space Detail**. Se conserva el diseño existente de Stitch/Figma y el tema Material 3 (verde principal `#00B81C`).

La Entrega 4 exige MVVM, `StateFlow`, Repository como única puerta a los datos (**M6 ★**), inyección manual, coroutines, Firestore con lectura y escritura real (**M9 ★**), estados visibles y modo avión (**M10 ★**), Room, formulario validado y prueba externa. La fuente normativa es `docs/delivery-4-requirements.md`; ante contradicción, prevalece el enunciado del docente.

**Fuera de alcance:** mapas, GPS, sensores, aforo en tiempo real, reservas, pagos, Crashlytics y autenticación institucional. No añadir permisos peligrosos de Android sin una funcionalidad aprobada. `INTERNET` se declara en el manifiesto y no requiere diálogo de permiso en tiempo de ejecución.

## 2. Dirección de dependencias

```text
MainActivity / AppNavigation / Route
            |
            v
Content Screen <--- UiState inmutable + callbacks --- ViewModel
                                                    |
                                              interfaces Repository
                                                    ^
                                                    |
                                 implementaciones en data/repository
                                      /             |             \
                              Firestore       Firebase Auth        Room
```

- **UI (`ui/`)**: los `Screen` de contenido dibujan estado y emiten callbacks; no conocen ViewModels ni fuentes de datos. Los `Route` obtienen ViewModels mediante fábrica, recolectan `StateFlow` con `collectAsStateWithLifecycle()` y conectan eventos. No acceder a Firebase, Room, repositorios ni red desde un Composable.
- **ViewModel**: recibe interfaces Repository por constructor; mantiene `private val _uiState = MutableStateFlow(...)` y expone `val uiState = _uiState.asStateFlow()`. Lanza operaciones con `viewModelScope`; prepara estado de presentación, valida y coordina acciones. No mantiene `Activity`, `View`, `Context` ni importa `com.google.firebase`/`androidx.room`.
- **Domain (`domain/`)**: modelos, interfaces Repository y algoritmo puro de recomendaciones. No depende de Android, Firebase ni Room.
- **Data (`data/`)**: implementa contratos, convierte documentos/entidades en modelos de dominio, traduce errores y encapsula Firestore/Auth/Room. Los DAO y clientes SDK nunca salen de esta capa.
- **DI (`di/`)**: `AppContainer` crea instancias y una `ViewModelProvider.Factory` entrega dependencias y `SavedStateHandle` cuando corresponda. Sin Hilt/Koin.
- **Navigation (`navigation/`)**: define destinos y argumentos, no consulta bases de datos ni calcula recomendaciones.

La capa `domain` es **organización ligera**, no implica agregar Use Cases. `RecommendationCalculator` es una función/clase pura de cálculo, no una capa adicional.

## 3. Estructura objetivo

```text
app/src/main/java/gt/uvg/uvgspot/
├── UVGspotApplication.kt
├── MainActivity.kt
├── domain/
│   ├── model/
│   │   ├── StudySpace.kt
│   │   ├── StudyPreferences.kt
│   │   ├── AvailabilityReport.kt
│   │   ├── Recommendation.kt
│   │   └── DataResult.kt
│   ├── repository/
│   │   ├── StudySpaceRepository.kt
│   │   ├── AvailabilityRepository.kt
│   │   ├── FavoritesRepository.kt
│   │   └── SessionRepository.kt
│   └── recommendation/
│       └── RecommendationCalculator.kt
├── data/
│   ├── remote/
│   │   ├── StudySpaceDocument.kt
│   │   ├── AvailabilityReportDocument.kt
│   │   └── FirestoreMappers.kt
│   ├── local/
│   │   ├── FavoriteEntity.kt
│   │   ├── FavoritesDao.kt
│   │   └── UVGspotDatabase.kt
│   └── repository/
│       ├── FirestoreStudySpaceRepository.kt
│       ├── FirestoreAvailabilityRepository.kt
│       ├── FirebaseSessionRepository.kt
│       └── RoomFavoritesRepository.kt
├── di/
│   ├── AppContainer.kt
│   └── AppViewModelFactory.kt
├── navigation/
│   └── AppNavigation.kt
└── ui/
    ├── components/                 (reutilizar componentes actuales)
    ├── screens/
    │   ├── login/                  LoginScreen, LoginViewModel, LoginUiState
    │   ├── needs/                  NeedsScreen, NeedsViewModel, NeedsUiState
    │   ├── recommendations/        RecommendationsScreen, RecommendationsViewModel, RecommendationsUiState
    │   └── detail/                 SpaceDetailScreen, SpaceDetailViewModel, SpaceDetailUiState
    └── theme/                      Color, Theme, Type
```

Esta estructura es **orientativa**, no una orden de crear archivos vacíos. Codex debe conservar y refactorizar los existentes, sin duplicar nombres, componentes o responsabilidades. No mover archivos únicamente por estética si implica riesgo innecesario. El archivo `firestore.rules` puede vivir en la raíz, sujeto a aprobación antes de desplegarlo.

## 4. Contratos y modelos de dominio

### Modelos

- `StudySpace`: `id: String` (ID del documento), nombre, ubicación, descripción breve y detallada, características tipadas (ruido, modalidad, WiFi, enchufes), URL opcional de imagen y datos de referencia disponibles. No anotaciones `@Entity` ni tipos `DocumentSnapshot`.
- `StudyPreferences`: ambiente y actividad seleccionados, WiFi y enchufes opcionales. No obligar a marcar WiFi/enchufes artificialmente.
- `AvailabilityReport`: ID, `spaceId`, estado reportado, `userId`, marca de tiempo y estado de sincronización cuando aplique. No almacenar carnet, correo ni contraseña en reportes.
- `Recommendation`: espacio, puntuación y razones calculadas a partir de preferencias. `matchReason` no es una propiedad estática del espacio.
- `DataResult`: éxito/datos y errores tipados, con metadatos suficientes para caché, origen y pendientes. Evitar un resultado genérico que esconda el estado de sincronización.

### Interfaces (firmas conceptuales; Codex debe ajustarlas al código real)

```kotlin
interface StudySpaceRepository {
    fun observeStudySpaces(): Flow<DataResult<List<StudySpace>>>
    fun observeStudySpace(spaceId: String): Flow<DataResult<StudySpace?>>
}

interface AvailabilityRepository {
    fun observeRecentReports(spaceId: String): Flow<DataResult<List<AvailabilityReport>>>
    suspend fun submitReport(spaceId: String, availability: ReportedAvailability): DataResult<ReportSubmission>
}

interface FavoritesRepository {
    fun observeFavoriteIds(): Flow<Set<String>>
    suspend fun setFavorite(spaceId: String, isFavorite: Boolean): DataResult<Unit>
}

interface SessionRepository {
    suspend fun ensureAnonymousSession(): DataResult<String>
}
```

Las firmas anteriores son **contratos de intención**, no código para copiar sin verificar. Si `submitReport` puede quedar pendiente offline, su resultado no debe confundir **aceptado localmente** con **confirmado por servidor**; diseñar estado observable y persistente apropiado. Manejar cancelación de coroutines sin convertir `CancellationException` en error normal.

### Firestore: colecciones

- `studySpaces/{spaceId}`: fuente de espacios y características. Solo lectura desde clientes; **no permitir que clientes editen datos oficiales**.
- `availabilityReports/{reportId}`: reportes enviados por usuarios autenticados anónimamente, con campos validados, `userId == request.auth.uid`, `spaceId` válido y fecha generada por servidor. Denegar actualizaciones/borrados no autorizados.

**Punto a confirmar con el docente (M9 ★):** la rúbrica dice que la app lee una colección principal y escribe/actualiza documentos desde una acción real. Nuestro diseño **lee `studySpaces` y escribe `availabilityReports`**. Aunque es una operación real en Firestore, podría interpretarse que la escritura debe ocurrir en la misma colección principal. Antes de dar M9 por aprobado, confirmar esta interpretación o documentar una alternativa segura que cumpla el requisito sin permitir editar espacios oficiales.

La app no debe publicar reglas abiertas (`allow read, write: if true`). Reglas versionadas y probadas; despliegue manual con aprobación. Auth anónima **no verifica pertenencia a UVG**. Considerar limitación de frecuencia/abuso y no mostrar reportes como datos verificados.

### Room

`FavoriteEntity(spaceId: String, savedAt: Long)` con `spaceId` como clave primaria; `FavoritesDao` expone `Flow` y operaciones suspendibles; `UVGspotDatabase` configura la base. `RoomFavoritesRepository` implementa el contrato de dominio. Room **solo guarda IDs de favoritos** y no duplica los documentos completos de Firestore. Guardar un ID no garantiza mostrar la ficha completa offline si no existe caché de Firestore.

## 5. Disponibilidad estimada (regla acordada)

1. Se consideran únicamente reportes con marca de tiempo **confirmada por servidor** dentro de los últimos **30 minutos** respecto del momento de evaluación.
2. Para cada pareja (`userId`, `spaceId`), cuenta solo el reporte más reciente dentro de la ventana.
3. Gana la categoría de disponibilidad más frecuente entre los reportes válidos.
4. Si hay empate, gana la categoría del reporte más reciente entre las empatadas.
5. Si no hay reportes válidos recientes, mostrar **«Sin información reciente» / UNKNOWN**, no afirmar disponibilidad actual.
6. La UI debe identificar el dato como **estimación**, nunca como ocupación en tiempo real.

Usar categorías controladas (`HIGH`, `MEDIUM`, `LOW`, `UNKNOWN`) y pruebas para empate, reportes caducados, timestamp pendiente, duplicados y reloj. **No usar la hora del dispositivo como autoridad para registrar `createdAt`**; se usa timestamp del servidor. Una implementación puramente cliente puede tener limitaciones de confianza temporal y consultas; documentarlas.

**Actualización temporal:** el paso de “vigente” a “caducado” no ocurre automáticamente si no llegan eventos nuevos. La app debe recalcular al volver a primer plano o mediante un mecanismo acotado compatible con el ciclo de vida; evitar temporizadores permanentes.

## 6. Recomendaciones y preferencias

- Needs captura preferencias en un `UiState` y las mantiene al navegar/editar; el usuario debe poder cambiarlas.
- Recommendations recibe preferencias mediante un mecanismo de navegación/estado explícito y restaurable. Evitar duplicar estados incompatibles entre ViewModels.
- El algoritmo puro compara ambiente, actividad, WiFi y enchufes; usa disponibilidad **estimada** sin afirmar tiempo real. La selección de WiFi/enchufes es opcional, pero si se solicita debe influir realmente en el resultado.
- Orden y reglas de coincidencia deben ser **deterministas y documentados** antes de fijar pesos; definir qué ocurre si no hay coincidencias y cómo tratar `UNKNOWN`. No inventar una fórmula opaca.
- Recommendations y Detail deben obtener datos por Repository, no `SampleData` directamente. Los mocks pueden permanecer solo como fakes de pruebas o durante una migración controlada; el flujo entregado debe leer Firestore.

## 7. Pantallas, estado y navegación

- **Login:** conservar diseño y tres campos existentes (carnet, correo, contraseña). Validación reactiva con mensajes junto a cada campo, placeholders y botón solo habilitado con formulario válido (M4). **No enviar, guardar ni validar realmente la contraseña contra UVG**. No afirmar inicio de sesión institucional: la autorización de reportes utiliza Firebase Auth anónima y se obtiene cuando se necesita.
- **Needs:** `NeedsUiState` inmutable; ambiente y actividad seleccionables, esenciales opcionales. Mantener selecciones al editar y al rotar.
- **Recommendations:** lista con `LazyColumn` y `key` estable; chips de preferencias reales; ranking determinista; estados Loading/Empty/Error; botones Spaces/Favorites con acciones reales (se puede reutilizar la lista con filtro).
- **Detail:** recibe `spaceId: String`, consulta Repository, muestra datos y ausencia/error; permite guardar favorito en Room y reportar disponibilidad en Firestore desde una acción explícita.
- **Back:** `popBackStack()` vuelve al destino anterior. Validar argumento ausente o inválido sin pantalla en blanco.

Patrón:

```kotlin
data class RecommendationsUiState(
    val isLoading: Boolean = false,
    val spaces: List<Recommendation> = emptyList(),
    val errorMessage: String? = null
)

class RecommendationsViewModel(
    private val repository: StudySpaceRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecommendationsUiState())
    val uiState = _uiState.asStateFlow()
}
```

Los ejemplos son ilustrativos: el ViewModel real recibirá también las dependencias necesarias para disponibilidad y preferencias. Los `Route` utilizan `collectAsStateWithLifecycle()`; los `Screen` reciben `uiState` y callbacks sin conocer el ViewModel.

## 8. DI manual y ciclo de vida

- `UVGspotApplication` mantiene un `AppContainer` por proceso; registra la clase en el Manifest.
- `AppContainer` crea y comparte los clientes/repositorios; no inicializar bases en cada recomposición.
- `AppViewModelFactory` proporciona ViewModels con sus interfaces por constructor y `SavedStateHandle` cuando aplique.
- La navegación no instancia repositorios; las Routes reciben las fábricas desde el punto de composición.
- `SavedStateHandle` conserva argumentos/selecciones restaurables; `ViewModel` sobrevive a rotación. No asumir que `ViewModel` por sí solo sobrevive a muerte del proceso.

## 9. Estados de carga, errores y modo avión (M10 ★)

- Diferenciar **Loading**, **Content**, **Empty**, **Error** y **Cached/Stale**; también **Pending/Confirmed/Rejected** para reportes.
- Firestore puede devolver caché. Mostrar contenido cacheado con indicación de posible desactualización; caché vacía sin conexión **no es necesariamente una colección vacía**.
- Sin caché y sin conexión, mostrar estado recuperable y opción de reintentar; nunca spinner infinito.
- Las escrituras pueden quedar pendientes de confirmación. No presentar éxito definitivo antes de confirmación del servidor.
- Evitar bloquear la UI con `await()` indefinido. Gestionar desconexión, timeout/cancelación de espera de interfaz y reintento sin duplicar reportes.
- El Repository traduce errores de SDK a errores de dominio. La UI muestra mensajes claros en español. No capturar excepciones silenciosamente.
- Room debe permitir observar favoritos al reabrir la app en modo avión.

## 10. Diseño y permisos

**Diseño protegido:** no reemplazar layouts de Stitch/Figma, tema Material 3, verde `#00B81C`, tipografía, formas, iconos ni jerarquía visual. Reutilizar `AvailabilityBadge`, `StudySpaceCard`, `SpaceCharacteristic`, `BottomNavigationBar` y demás componentes existentes. Añadir solo elementos imprescindibles: validaciones, estados, reporte, favorito y correcciones del profesor.

**Edge-to-Edge:** revisar `MainActivity`, `WindowInsets`, barras de estado/navegación, teclado y contraste. No ocultar controles bajo barras del sistema. Probar en emulador; no dar por verificado por inspección del código.

**Permisos:** UVGspot no requiere GPS, contactos, cámara ni permisos peligrosos para el flujo aprobado. Si en el futuro se incorpora un recurso protegido, el acceso se abstraerá tras Repository; el diálogo de permiso de Android debe coordinarse desde un componente con ciclo de vida, sin guardar `Activity` en ViewModel. No implementar un sistema de permisos sin necesidad.

## 11. Reglas de implementación y verificación

- Kotlin en inglés (identificadores y campos de Firestore/Room), textos visibles en español. PascalCase para clases/Composables, camelCase para funciones/variables, UPPER_SNAKE_CASE para constantes.
- Composables reutilizables con `modifier: Modifier = Modifier` aplicado a su raíz. Imports explícitos; sangría 4 espacios; sin `!!`, `println`, logs de depuración, código comentado ni archivos sobrantes.
- Funciones pequeñas y de una responsabilidad; objetivo orientativo de 30 líneas cuando sea razonable, no límite absoluto de Kotlin.
- No usar Hilt, Koin, capas Use Case, Crashlytics ni dependencias ajenas a requisitos.
- Implementar con checkpoints internos: **arquitectura → compilación → Firestore → Room → UI/estados → pruebas**. Una tarea de Codex puede abarcar varias etapas, pero no continuar ignorando fallos de compilación.
- Reportar archivos cambiados, comandos ejecutados, resultados reales y pasos manuales pendientes. No afirmar que se comprobó Firebase Console, reglas desplegadas, emulador o task test si no se verificó.
- No publicar reglas, crear commits, hacer push, borrar datos ni modificar ramas sin autorización.

### Matriz mínima de pruebas

| Requisito | Evidencia verificable |
|---|---|
| M1–M3 | Cuatro pantallas con tema consistente; navegación con ID y Atrás; `LazyColumn` con `key`. |
| M4 | Tres campos del Login, errores junto a campos, validación inmediata y botón condicionado. |
| M5–M8 | `StateFlow` inmutable, Screen sin ViewModel, SDKs solo en data, inyección por constructor, coroutines. |
| M9 ★ | Lectura real de Firestore, escritura real por acción del usuario, mapeo a dominio, reglas verificadas. |
| M10 ★ | Loading/Empty/Error visibles; modo avión con y sin caché; ninguna carga infinita. |
| M11 | Guardar favorito, cerrar app, reabrir en modo avión y comprobar persistencia. |
| M12 | Auditoría de convenciones en todo el repositorio. |
| Task test ★ | Persona externa, tarea sin ayuda, hechos observados, resultado, dudas y cambios propuestos. |
| Alcance | Lista Dentro/Fuera vinculada a user stories de Entrega 2 y justificaciones. |

## 12. Decisiones abiertas que no debe inventar Codex

1. **M9:** confirmar si la escritura en `availabilityReports` satisface el requisito de colección principal leído desde `studySpaces`.
2. **M4:** comprobar que los tres campos existentes de Login y sus errores cumplen el criterio; no agregar requisitos artificiales a Needs.
3. **Ranking:** definir/documentar reglas exactas de puntuación y desempate antes de codificarlas.
4. **Datos iniciales:** verificar que `studySpaces` tenga documentos reales; el cliente no debe sembrar datos oficiales automáticamente.
5. **Seguridad:** probar reglas Firestore con Auth anónima; no desplegarlas automáticamente.
6. **Git:** el docente solicita `entrega-4` desde `main`; el repositorio usa `master` como base. No renombrar ramas automáticamente.
7. **Task test:** requiere una persona real externa; Codex solo puede preparar la guía y el formato de registro.

## 13. Lecturas de referencia

- [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- [Android Guide to app architecture](https://developer.android.com/topic/architecture)
- [Android Room](https://developer.android.com/training/data-storage/room)
- [Firebase Firestore offline](https://firebase.google.com/docs/firestore/manage-data/enable-offline)
- [Firebase anonymous authentication](https://firebase.google.com/docs/auth/android/anonymous-auth)
- [Android ViewModel SavedStateHandle](https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-savedstate)

> Este documento especifica el diseño previsto. Codex debe contrastarlo con `docs/delivery-4-requirements.md` y el código real antes de modificar archivos, y registrar cualquier desviación justificada.
