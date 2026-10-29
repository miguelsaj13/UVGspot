# UVGspot — Entrega 4: requisitos y criterios de evaluación

> **Propósito:** referencia de requisitos para el equipo y Codex. Este documento distingue entre **requisitos del docente** (obligatorios), **rúbrica visible** (evidencia de evaluación) y **decisiones del equipo** (implementación propuesta). En caso de conflicto, prevalecen las instrucciones y la rúbrica originales del docente.
>
> **Estado:** especificación previa a la implementación. Ningún criterio se considera cumplido por el solo hecho de estar descrito aquí.

## 1. Objetivo y condiciones generales — docente

> El flujo principal funciona de principio a fin con Firebase Firestore, pasando por un Repository, y alguien ajeno al equipo puede completarlo sin ayuda. Con esta entrega se congela el alcance: después solo se corrige y se estabiliza. Aporta **10 % de la nota final (Competencia 2)**. Esta entrega no tiene techo de jurado.

**Condición crítica:** los requisitos señalados con **★** son críticos. Si alguno queda en nivel **Ausente**, la entrega se considera **no evaluable**.

## 2. Requisitos técnicos — docente

### UI y navegación

| ID | Requisito |
|---|---|
| **M1** | Contiene las pantallas para generar una aplicación funcional y un tema propio aplicado de forma consistente (colores y tipografía). |
| **M2** | Navegación entre pantallas con Navigation 3 o Navigation Compose. Al menos una pantalla recibe un argumento (por ejemplo, el ID del elemento en el detalle) y el botón Atrás regresa a la pantalla anterior. |
| **M3** | Al menos una lista con `LazyColumn` o `LazyVerticalGrid` que usa `key`. |
| **M4** | Un formulario con al menos **3 campos validados en tiempo real**, mensajes de error junto al campo y botón habilitado **solo cuando el formulario es válido**. |

### Arquitectura

| ID | Requisito |
|---|---|
| **M5** | MVVM: cada pantalla con lógica tiene un ViewModel que expone un estado inmutable con `StateFlow` y sobrevive a la rotación. Los Composables de contenido no conocen el ViewModel: reciben el estado y avisan los eventos con callbacks. |
| **M6 ★** | Repository es la **única puerta a los datos**. Ningún Composable ni ViewModel importa `com.google.firebase` ni `androidx.room`. |
| **M7** | Inyección manual: el ViewModel recibe el Repository por constructor; no lo crea él mismo. |
| **M8** | Coroutines: las operaciones de datos son `suspend` o `Flow` y se lanzan desde `viewModelScope`; nada bloquea el hilo principal. |

### Datos

| ID | Requisito |
|---|---|
| **M9 ★** | Firestore: al menos una colección principal. La app **lee de ella y escribe o actualiza documentos desde una acción real del usuario**. Cada documento se convierte a una `data class` de dominio antes de salir del Repository. |
| **M10 ★** | Estados visibles: **cargando, error y vacío** (cuando no hay elementos). Sin conexión, la app **no se cierra ni se queda cargando indefinidamente**. |
| **M11** | Room: **Entity, DAO y Database** funcionando; algún dato sobrevive a cerrar y reabrir la app. |

### Código

| ID | Requisito |
|---|---|
| **M12** | Todo el código cumple las convenciones del curso, recogidas en la sección 4. |

### Proceso y alcance

| Criterio | Requisito |
|---|---|
| **Task test ★** | Una persona ajena al equipo intenta completar el flujo principal **sin ayuda**, mientras el equipo observa sin intervenir. |
| **Alcance** | El equipo decide qué user stories de la Entrega 2 quedan **Dentro** y cuáles **Fuera**. Después de esta entrega no se agregan funcionalidades. |
| **Observaciones** | Cada observación escrita del docente sobre el código del proyecto está corregida o justificada. |

## 3. Consideraciones especiales — docente

- **Firestore y desconexión:** Firestore conserva caché y puede devolver datos guardados en lugar de fallar. Una escritura con `await()` puede permanecer pendiente hasta que vuelva la red. Definir qué verá el usuario: caché, error o aviso de sincronización pendiente. **Nunca** dejar un indicador de carga infinito.
- **Room no compite con Firestore:** utilizar Room para datos locales del usuario, por ejemplo favoritos, borradores o historial.

## 4. Convenciones de código M12 — docente

| Regla | Exigencia |
|---|---|
| Idioma | Clases, funciones, variables, paquetes, entidades Room, colecciones y campos Firestore **en inglés**. Los textos visibles al usuario **en español**. |
| Nombres | `PascalCase` para clases, interfaces, objetos y Composables (`TripListScreen`); `lowerCamelCase` para funciones, variables y propiedades (`loadTrips()`); `UPPER_SNAKE_CASE` para constantes (`MAX_SEATS`). Paquetes en minúsculas, sin guiones bajos. Evitar nombres vagos como `data`, `list2`, `temp` y `MainActivity2`. |
| Estado y callbacks | El ViewModel guarda `private val _uiState = MutableStateFlow(...)` y expone `val uiState = _uiState.asStateFlow()`. Los callbacks comienzan con `on` (`onTripClick`, `onQueryChange`). |
| Composables reutilizables | Reciben `modifier: Modifier = Modifier` y lo aplican a su raíz. |
| Null safety | No utilizar `!!`; preferir `?.`, `?:`, `let` o validar antes. |
| Código limpio | Sin bloques de código comentado, `println` ni `Log` de depuración; eliminar restos de plantillas y archivos no utilizados. |
| Formato | Sangría de **4 espacios** e imports explícitos, sin `*`. Antes de entregar: **Reformat Code** y **Optimize Imports** sobre el paquete principal. |

> **Regla adicional del equipo, no literal de M12:** preferir funciones cortas y de una sola responsabilidad (objetivo orientativo: máximo 30 líneas cuando sea razonable). No fragmentar código artificialmente solo para cumplir una cifra.

## 5. Instrucciones de entrega — docente

1. Crear la rama `entrega-4` desde `main` según el enunciado. **Nota del repositorio UVGspot:** la rama principal existente se llama `master`; el equipo creó `entrega-4` desde ella. Conservar esta diferencia documentada y aclararla con el docente si exige literalmente `main`.
2. Reemplazar los datos mock del flujo principal por **lecturas y escrituras en Firestore**, siempre dentro del Repository.
3. Completar los requisitos.
4. Probar la app en **modo avión**: no debe cerrarse ni quedarse cargando.
5. Recorrer todo el repositorio con la tabla de convenciones y corregir observaciones del docente.
6. Realizar el **task test** con una persona de otro equipo (o ajena al curso si no se puede ese día): **decir únicamente cuál es la tarea, sin explicar la app ni ayudar**. Anotar lo que sucede.
7. Usar las user stories de la Entrega 2 para decidir y documentar **Dentro/Fuera**.

## 6. Rúbrica visible — docente

La información siguiente transcribe los niveles y puntajes **visibles** en las capturas compartidas; no inventar niveles, puntajes ni descripciones que no aparecen.

| Categoría | Completo | Parcial | Puntos visibles de Completo / Parcial |
|---|---|---|---|
| Firestore (**M9 ★**) | Lee de la colección principal y escribe o actualiza desde una acción real del usuario; Repository convierte cada documento a `data class` de dominio. | Lee y escribe, pero falta mapeo a dominio o se hace fuera del Repository. | **2 / 1.4** |
| Arquitectura (**M5–M8; M6 ★**) | Se cumplen M5, M6, M7 y M8. | Falla uno de M5, M7 o M8. | **2 / 1.4** |
| Estados y errores (**M10 ★**) | Cargando, error y vacío visibles; en modo avión muestra qué pasa y sigue abierta. | Falta uno de los tres estados. | No visibles en la captura |
| UI y navegación (**M1–M4**) | Se cumplen M1, M2, M3 y M4. | Falla uno de M1 a M4. | **1.5 / 1** |
| Convenciones (**M12**) | Todo el repositorio cumple la tabla de convenciones. | Incumplimientos aislados: algunos nombres, `!!` o código comentado. | **1 / 0.7** |
| Task test **★** | Persona externa real; registro indica tarea, resultado, dónde dudó según hechos observados y qué cambiarían. | Registro incompleto o con opiniones en lugar de hechos. | **1 / 0.7** |
| Room (**M11**) | El dato guardado en Room sigue visible al reabrir la app **sin conexión**. | Room implementado, pero no se demuestra persistencia. | **0.5 / 0.3** |
| Alcance congelado | Listas Dentro y Fuera con las user stories de Entrega 2 y el porqué de cada exclusión. | Listas sin justificación o sin relación con Entrega 2. | **0.5 / 0.3** |

**No disponible en las capturas:** texto completo de columnas/niveles adicionales (p. ej. Inicial/Ausente), algunos puntajes y detalles expandidos tras “ver una descripción más detallada”. Consultar la rúbrica original para esos extremos; no asumirlos.

## 7. Decisiones del equipo para UVGspot — no son requisitos literales del docente

Estas decisiones definen **cómo** se propone cumplir la rúbrica. Deben mantenerse separadas de las obligaciones oficiales.

### 7.1 Alcance y diseño

- Flujo principal: **Login → Needs → Recommendations → Space Detail**.
- Mantener el diseño validado en Stitch/Figma, el tema Material 3 y el color principal **`#00B81C`**. Agregar estados y controles nuevos con el mismo lenguaje visual, sin rediseñar las pantallas.
- Dentro: selección de necesidades, filtros/recomendaciones, disponibilidad **estimada**, características, detalle por ID, reportes de disponibilidad y favoritos locales.
- Fuera: mapas/rutas, GPS, sensores, conteos exactos de personas, disponibilidad garantizada en tiempo real, pagos, reservaciones y funciones sociales.
- **Observaciones previas del docente:** incorporar placeholders en formularios y revisar Edge-to-Edge y contraste de las barras de estado/navegación.

### 7.2 Arquitectura acordada

- **MVVM + Repository + `StateFlow` + inyección manual por constructor**, sin Hilt/Koin.
- Paquetes de responsabilidad: `domain` (modelos, contratos y cálculo puro), `data` (implementaciones Firestore/Room), `di` (contenedor y factories), `ui` (Routes, Screens, ViewModels, componentes y tema), `navigation`.
- Los Composables de contenido muestran estado y emiten callbacks; los ViewModels no importan Firebase ni Room; los Repositories encapsulan acceso y excepciones de datos.
- `viewModelScope` para trabajo asíncrono; recolección de `StateFlow` consciente del ciclo de vida.
- No introducir Use Cases ni frameworks de DI sin necesidad.

### 7.3 Firestore, autenticación y disponibilidad

- Colección principal: **`studySpaces`**; colección de reportes: **`availabilityReports`**.
- Firestore almacena datos compartidos; cada documento se mapea a un modelo de dominio dentro de la capa de datos.
- Firebase Authentication **anónima** para autorizar reportes. El formulario Login existente **no debe afirmar que verifica credenciales institucionales**, ni enviar o almacenar carnet/correo/contraseña como si lo hiciera.
- Los clientes **no pueden modificar directamente** documentos oficiales de `studySpaces`.
- Cada reporte guarda espacio, UID autenticado, opción reportada y timestamp del servidor, con reglas que validan estructura y autoría.
- **Política de estimación propuesta y aprobada por el equipo:** considerar reportes de los últimos **30 minutos**; contar solo el más reciente por UID y espacio; usar la opción más frecuente y desempatar por el reporte más reciente. Si no hay evidencia reciente, mostrar **“Sin información reciente”**. Esta es una estimación de usuarios, **no ocupación en tiempo real**.
- Manejar caché, pendientes, errores y desconexión sin indicadores de carga infinitos. No afirmar que una escritura pendiente ya se confirmó en el servidor.
- **Pendiente de verificación técnica:** cómo aplicar límites y reglas contra abuso de reportes, y cómo sincronizar una estimación sin backend adicional; no fingir garantías de seguridad que no existen.

### 7.4 Room y favoritos

- Room almacena **IDs de espacios favoritos** (y opcionalmente fecha de guardado), no copias completas de la colección Firestore.
- El usuario puede guardar y quitar favoritos; el estado debe sobrevivir al cierre/reapertura y ser visible sin conexión.
- Las pestañas Spaces y Favorites pueden reutilizar la lista existente con distintos filtros, evitando rediseños y pantallas innecesarias.

### 7.5 Formulario M4

- Codex debe **verificar el Login existente**, que según la inspección preliminar ya tiene tres campos (carnet, correo y contraseña) y validación reactiva.
- Confirmar **errores junto a cada campo**, validación en tiempo real y botón habilitado solo con formulario válido.
- No obligar al estudiante a seleccionar WiFi/enchufes artificialmente para cumplir M4.
- No representar una autenticación institucional real si solo se usa Firebase Auth anónima.

### 7.6 Crashlytics y permisos

- Crashlytics **no aparece como requisito de esta entrega**; no añadirlo sin aprobación.
- El flujo acordado no requiere permisos peligrosos como ubicación, cámara o contactos. `INTERNET` es un permiso normal de manifiesto.
- Si en el futuro se usan recursos protegidos, el acceso a los datos del recurso debe abstraerse detrás de una interfaz de datos; las solicitudes de permisos de ejecución requieren coordinación compatible con el ciclo de vida Android.

## 8. Matriz de verificación — no marcar sin evidencia

| Criterio | Evidencia mínima a producir | Estado inicial |
|---|---|---|
| M1 | Comparación de las cuatro pantallas con el diseño y tema | Pendiente de verificar |
| M2 | Recorrido, Atrás y argumento `spaceId` | Pendiente de verificar |
| M3 | `LazyColumn`/`LazyVerticalGrid` con `key` | Pendiente de verificar |
| M4 | Tres campos, errores cercanos, botón condicionado, casos válidos/inválidos | Pendiente de verificar |
| M5 | `UiState` inmutable + `StateFlow`, rotación y callbacks | Pendiente de verificar |
| M6 ★ | Búsqueda de imports Firebase/Room fuera de data y flujo a través de Repository | Pendiente de verificar |
| M7 | Constructores con interfaces y factory manual | Pendiente de verificar |
| M8 | `suspend`/`Flow`, `viewModelScope`, ausencia de bloqueos | Pendiente de verificar |
| M9 ★ | Lectura real y reporte escrito desde UI; mapeo documento → dominio | Pendiente de verificar |
| M10 ★ | Loading/Empty/Error y pruebas modo avión con/sin caché y escrituras pendientes | Pendiente de verificar |
| M11 | Guardar favorito, cerrar app, reabrir sin red y comprobar estado | Pendiente de verificar |
| M12 | Inspección de convenciones en **todo** el repositorio | Pendiente de verificar |
| Task test ★ | Registro factual de tarea, resultado, dudas observadas y cambios | Pendiente de realizar por el equipo |
| Alcance | Listas Dentro/Fuera relacionadas con Entrega 2 y razones | Pendiente de documentar |
| Observaciones | Placeholders y Edge-to-Edge corregidos o justificados | Pendiente de verificar |

## 9. Instrucciones para Codex al usar este documento

1. Leer `AGENTS.md`, este documento y, cuando existan, `docs/architecture.md` y `docs/scope.md` **antes de implementar**.
2. Tratar la sección 2, 3, 4 y 5 como **requisitos docentes**, la sección 6 como **rúbrica visible** y la sección 7 como **decisiones del equipo**.
3. No marcar requisitos como cumplidos sin pruebas ejecutadas. Reportar comandos, resultados, cambios y verificaciones manuales pendientes.
4. Preservar el diseño actual; cambios visuales solo para requisitos funcionales, validación, estados, accesibilidad y Edge-to-Edge.
5. No publicar reglas de Firestore, cambiar ramas, hacer push ni manipular recursos remotos sin autorización explícita.
6. Si una ambigüedad impide cumplir un criterio crítico, detener esa decisión y solicitar confirmación; no inventar requisitos ni declarar éxito.

## 10. Fuentes proporcionadas por el equipo

- Instrucciones y capturas de **Entrega 4**, incluyendo M1–M12, proceso, convenciones y detalles offline.
- Capturas de la **rúbrica de Entrega 4** (solo fragmentos visibles).
- Archivo de la Entrega 2: `Acuerdo_de_Requisitos - Los camotes (2).xlsx` y comentarios escritos del docente.
- Lecturas de referencia (no sustituyen la rúbrica):
  - https://kotlinlang.org/docs/coding-conventions.html
  - https://developer.android.com/topic/architecture?hl=es-419
  - https://developer.android.com/training/data-storage/room
  - https://firebase.google.com/docs/firestore
  - https://firebase.google.com/docs/firestore/manage-data/enable-offline
  - https://firebase.google.com/docs/auth/android/anonymous-auth
