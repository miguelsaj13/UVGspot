# UVGspot — Alcance congelado (Entrega 4)

**Estado:** acuerdo propuesto para la Entrega 4; se confirma al aprobarlo el equipo.  
**Fuente de requisitos:** `Acuerdo_de_Requisitos - Los camotes (2).xlsx`, hojas **4. Priorizar funciones** y **5. Historias Must**, más observaciones escritas del docente.  
**Documentos relacionados:** `docs/delivery-4-requirements.md`, `docs/architecture.md`, `AGENTS.md`.

## 1. Propósito y problema que se atiende

UVGspot ayuda a estudiantes de la UVG que pierden tiempo buscando un lugar apropiado para estudiar entre clases. El estudiante indica sus necesidades y consulta **recomendaciones basadas en características registradas y disponibilidad estimada**, no disponibilidad exacta ni en tiempo real.

**Flujo principal comprometido:** Login → Selección de necesidades → Recomendaciones → Detalle del espacio. El flujo debe funcionar con datos de Firestore a través de Repository y permitir que un usuario externo lo complete sin ayuda.

**Restricción:** después de la Entrega 4 el alcance funcional queda congelado; solo se corrige y estabiliza. Los requisitos técnicos de la rúbrica (incluidos M4, M9 y M11) son obligatorios aunque no se correspondan uno a uno con los cuatro Must originales.

## 2. Registro histórico: priorización de la Entrega 2

Esta tabla conserva las decisiones de la hoja **4. Priorizar funciones**. No sustituir retroactivamente su contenido.

| ID | Funcionalidad priorizada en Entrega 2 | MoSCoW original | Decisión para Entrega 4 |
|---|---|---|---|
| F1 | Indicar qué necesita el estudiante del espacio en ese momento. | Must | **Dentro**: formulario de necesidades. |
| F2 | Indicar la disponibilidad **estimada** de los espacios. | Must | **Dentro**: estimación con origen y vigencia identificables. |
| F3 | Consultar las características de un espacio. | Must | **Dentro**: ficha de características y ubicación textual. |
| F4 | Recomendar espacio según necesidad, características y disponibilidad. | Must | **Dentro**: filtrado/ordenamiento reproducible. |
| F5 | Guardar espacios como favoritos para consultarlos fácilmente. | Could | **Dentro como ampliación explícita**: favoritos locales mediante Room, por M11. |
| F6 | Mostrar historial de espacios visitados recientemente. | Won't | **Fuera**: no hay evidencia directa de necesidad. |

**Nota de trazabilidad:** F5 no se convierte retrospectivamente en Must; se incorpora de forma consciente como función secundaria y evidencia de persistencia Room. Los reportes de disponibilidad tampoco eran un Must explícito: se agregan como mecanismo acotado de actualización para cumplir la escritura real de Firestore (M9) y alimentar la estimación F2. Su inclusión debe constar en la lista de alcance aprobado.

## 3. Desajuste de las historias entregadas y corrección

La hoja **5. Historias Must** no coincide con la priorización. Las observaciones del docente piden conservar la intención de los Must, distinguir **disponibilidad estimada** de tiempo real y usar filtros como mecanismo de recomendación. Este documento conserva el registro original y define las historias implementables.

| ID histórico de historia | Lo que solicitaba la historia original | Decisión / corrección |
|---|---|---|
| F1 | Disponibilidad **en tiempo real** y ocupación actual inmediata. | **Fuera esa promesa**. Se implementa consulta de disponibilidad **estimada**, no medición instantánea. |
| F2 | Filtrar por ruido, capacidad, enchufes y “comodidad” subjetiva. | **Dentro parcialmente**: preferencias y filtros de atributos objetivos registrados; comodidad no es criterio verificable sin definición. |
| F3 | Mapa y ubicación exacta. | **Fuera el mapa y las rutas**. **Dentro** la ubicación textual por edificio y nivel, que era lo verificable en el criterio original. |
| F4 | Conocer aforo y concurrencia actual, con criterio que aceptaba solo uno. | **Fuera** el conteo actual de personas y aforo exacto no verificado. **Dentro** el indicador de concurrencia/disponibilidad **estimada** cuando existan datos. |

Las historias originales no se borran del Excel. Las historias siguientes son **reformulaciones propuestas para el alcance congelado**, no citas literales de la entrega anterior.

## 4. Historias de usuario finales y criterios verificables

### US-01 — Indicar necesidades (Must F1)

**Como** estudiante de la UVG, **quiero** indicar las características que necesito para estudiar, **para** encontrar opciones adecuadas a mi actividad.

**Criterios de aceptación:**
- Puedo seleccionar un ambiente y una actividad, y activar o desactivar necesidades opcionales como WiFi o enchufes.
- Las preferencias se conservan al avanzar a recomendaciones y al regresar para editarlas.
- La validación y el estado del botón siguen la definición de M4 sin convertir necesidades opcionales en obligatorias artificialmente. El formulario de tres campos del Login puede utilizarse para M4 si cumple íntegramente la rúbrica.

### US-02 — Consultar disponibilidad estimada (Must F2)

**Como** estudiante de la UVG, **quiero** consultar la disponibilidad estimada de los espacios, **para** evitar recorridos innecesarios.

**Criterios de aceptación:**
- Cada ficha presenta una estimación **alta, media, baja o desconocida**, según la evidencia disponible.
- No se muestra una estimación como dato en tiempo real ni se confunde una lectura de caché con una confirmación reciente del servidor.
- Cuando no hay reportes vigentes, se muestra “Sin información reciente” o equivalente; no se inventa ocupación actual.

### US-03 — Consultar características (Must F3)

**Como** estudiante de la UVG, **quiero** consultar las características registradas de un espacio, **para** decidir si se ajusta a lo que necesito.

**Criterios de aceptación:**
- Al pulsar “Ver detalles”, se abre el espacio correcto mediante su ID.
- La ficha muestra características disponibles (por ejemplo, ruido, actividad permitida, WiFi y ubicación por edificio/nivel) sin prometer sensores ni precisión instantánea.
- El botón Atrás vuelve a la pantalla anterior.

### US-04 — Recibir recomendaciones (Must F4)

**Como** estudiante de la UVG, **quiero** recibir recomendaciones según mis preferencias y la disponibilidad estimada, **para** reducir el tiempo buscando dónde estudiar.

**Criterios de aceptación:**
- Las recomendaciones provienen de los espacios consultados mediante Repository/Firestore, no de una lista fija en la UI.
- El resultado cambia de forma verificable cuando cambian preferencias relevantes; el orden o exclusión sigue reglas documentadas y comprobables.
- La lista utiliza una colección dinámica y `LazyColumn`/`LazyVerticalGrid` con `key` estable.

## 5. Funcionalidades secundarias incluidas y su justificación

### SC-01 — Favoritos locales (F5, originalmente Could)

**Dentro:** guardar/quitar favoritos desde el detalle y consultarlos desde la navegación existente, reutilizando el diseño de lista. Room conserva al menos el ID del espacio después de cerrar y reabrir la aplicación, también sin conexión. Firestore sigue siendo la fuente de información compartida; Room no replica todos los documentos.

**Criterio:** guardar un favorito, cerrar completamente la app, reabrir sin red y verificar que el ID sigue guardado y el estado de favorito se muestra correctamente. Si no hay ficha completa en caché, mostrar una indicación apropiada.

### SC-02 — Reportar disponibilidad estimada (mecanismo para F2 y M9)

**Dentro como ampliación controlada:** desde la ficha de un espacio, un usuario identificado mediante sesión anónima de Firebase puede enviar una valoración sencilla de disponibilidad. La acción debe ser explícita; abrir una ficha no escribe datos. Los reportes se guardan en `availabilityReports` y no modifican directamente los datos oficiales de `studySpaces`.

**Criterios:**
- Una acción visible envía un reporte a Firestore por Repository, con ID de espacio, UID de sesión, valor permitido y timestamp del servidor.
- La interfaz diferencia **pendiente**, **confirmado** y **fallido**; una escritura local pendiente no se anuncia como confirmada en el servidor.
- El usuario no puede editar arbitrariamente documentos oficiales; las reglas de seguridad deben verificarse.

**Advertencia M9:** la rúbrica dice que la app lee una colección principal y escribe/actualiza documentos. La propuesta usa `studySpaces` para lectura y `availabilityReports` para escritura; verificar con el docente si exige ambas operaciones en una misma colección. No afirmar que M9 está aprobado sin esa confirmación y una prueba real.

## 6. Política de estimación y recomendaciones

**Política propuesta, no medición institucional:**
1. Considerar reportes de disponibilidad con timestamp del servidor dentro de los **últimos 30 minutos**.
2. Para cada par usuario–espacio, considerar solo su reporte vigente más reciente.
3. Determinar alta/media/baja por mayoría; en empate, usar el reporte más reciente.
4. Sin reportes válidos recientes: **desconocida**. La app no debe presentar datos de ejemplo como información actual confirmada.
5. Mostrar claramente si los datos proceden de caché y pueden estar desactualizados.

La recomendación compara ambiente, tipo de actividad y necesidades opcionales con los atributos registrados, y toma en cuenta la disponibilidad estimada. Los pesos, filtros obligatorios y desempates deben fijarse en pruebas unitarias documentadas; **no se autoriza a Codex a inventar nuevas condiciones de producto**.

## 7. Lista Dentro / Fuera para congelar alcance

| Dentro de Entrega 4 | Fuera de Entrega 4 | Justificación |
|---|---|---|
| Cuatro pantallas existentes y navegación | Rediseño completo o nuevas pantallas ajenas al flujo | Conservar prototipo validado. |
| Necesidades y recomendaciones | IA entrenada o personalización predictiva | Reglas simples verificables bastan para el Must F4. |
| Características y ubicación textual | Mapas, GPS, rutas y geolocalización precisa | No son necesarios para resolver el flujo central. |
| Disponibilidad **estimada** y reportes recientes | Sensores, ocupación exacta, número actual de personas y disponibilidad en tiempo real | No existe fuente institucional ni hardware para medirlo. |
| Favoritos locales (Room) | Historial automático de visitas | F5 es útil y M11 lo exige como persistencia; F6 fue Won't. |
| Firestore y sesión anónima para reportes | Autenticación institucional UVG, verificación de carnet o almacenamiento de contraseñas | No se dispone de integración institucional. |
| Validación de formulario, estados, Edge-to-Edge | Crashlytics, pagos, reservas, redes sociales | No son requisitos de esta entrega ni esenciales al flujo. |
| Navegación Spaces/Favorites reutilizando lista existente | Pantallas adicionales sin justificación | Evitar crecimiento innecesario del alcance. |

## 8. Restricciones de experiencia y diseño

- **Preservar** las pantallas Login, Needs, Recommendations y Detail, la paleta verde `#00B81C`, Material 3, tipografía, espaciados y componentes existentes.
- Añadir únicamente las interacciones mínimas de reportes, favoritos, validación, estados de carga/error/vacío y correcciones de Edge-to-Edge.
- Los textos visibles al usuario van en español; los identificadores Kotlin y campos Firestore en inglés.
- No mostrar botones sin función: si Spaces/Favorites aparecen en la navegación, deben operar sobre la lista existente o eliminarse del flujo aprobado con justificación.
- No usar información falsa de disponibilidad en tiempo real; distinguir estimación, caché y estado desconocido.

## 9. Condiciones de cierre y evidencias

| Evidencia | Verificación necesaria |
|---|---|
| Flujo principal | Persona externa completa Login → Needs → Recommendations → Detail sin ayuda. |
| M4 | Formulario de tres campos, errores junto al campo, validación en tiempo real y botón condicionado. |
| M5–M8 | UiState/StateFlow, Repository como única puerta, inyección manual y coroutines; inspección del código. |
| M9 ★ | Lectura y escritura real verificadas en Firebase, con mapeo a dominio y reglas comprobadas. |
| M10 ★ | Loading/Error/Empty y modo avión sin cierre ni espera infinita. |
| M11 | Favorito persiste tras cerrar/reabrir sin conexión. |
| M12 | Revisión de convenciones en todo el repositorio. |
| Task test ★ | Registro de tarea, resultado, dudas observadas como hechos y cambios propuestos; no inventar resultados. |
| Alcance | Entregar las listas Dentro/Fuera vinculadas a la Entrega 2 y sus razones. |

## 10. Reglas para Codex

- Leer este documento junto con `docs/delivery-4-requirements.md`, `docs/architecture.md` y `AGENTS.md` antes de editar.
- No confundir la **priorización original** con las **historias originales** ni con las **reformulaciones aprobadas**.
- No ampliar funcionalidades fuera de las listas anteriores. Si un requisito técnico exige una excepción, documentarla antes de implementar.
- No declarar cumplido ningún requisito sin evidencia de compilación, ejecución o prueba según corresponda.
- No realizar commits, push, despliegue de reglas de Firebase ni cambios de ramas sin autorización explícita.

**Aprobación de alcance:** pendiente de confirmación del equipo antes del cierre de la Entrega 4.
