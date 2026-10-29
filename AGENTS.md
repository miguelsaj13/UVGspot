# UVGspot — Codex project instructions

## Required reading and precedence
Before making changes, read:
1. `docs/delivery-4-requirements.md` (professor's requirements and rubric).
2. `docs/scope.md` (approved product scope and user stories).
3. `docs/architecture.md` (agreed architecture and contracts).

Follow the professor's explicit requirements first, then the approved scope, then architecture, then this file's implementation guidance. If documents conflict or a requirement is ambiguous, report the conflict and ask before implementing that part. Never silently invent requirements.

## Project and scope
UVGspot is a Kotlin / Jetpack Compose / Material 3 Android app recommending university study spaces based on user needs, characteristics, and **estimated**, not real-time, availability.

Preserve the existing primary flow: Login -> Needs -> Recommendations -> Space Detail. Keep the existing UI and navigation structure. The approved delivery scope includes Firestore-backed study spaces and user availability reports, Room-backed local favorites, and a deterministic recommendation policy. Do not add maps, GPS, sensors, payments, institutional authentication, or unrelated features.

## Mandatory architecture
- Use MVVM, immutable `UiState` exposed as read-only `StateFlow`, and unidirectional UI state/events.
- Content Composables receive state and callbacks only. Routes connect ViewModels to UI and collect state lifecycle-aware.
- ViewModels receive repository interfaces via constructor injection, use `viewModelScope` for async work, and do not hold `Context`, `Activity`, or views.
- Repository interfaces define data access; concrete implementations are the only gateway to Firestore, Firebase Auth, Room, and other data/system resources.
- No Composable or ViewModel imports `com.google.firebase` or `androidx.room`.
- Use manual dependency injection through `AppContainer` and `ViewModelProvider.Factory`. Do not add Hilt, Koin, or unnecessary use-case layers.
- Use `suspend` / `Flow` for data operations; do not block the main thread. Preserve coroutine cancellation; map relevant failures to explicit results.
- Keep domain models independent of Firebase documents and Room entities. Convert data-source objects to domain data classes inside the data layer before exposing them.
- Avoid Android runtime permissions unless genuinely required. `INTERNET` is a manifest permission, not a runtime permission. If a future feature needs runtime permission, separate the lifecycle-bound request UI from protected-resource access via a repository contract; do not introduce one for this delivery.

## Data and offline behavior
- Firestore: `studySpaces` for official space data and `availabilityReports` for user reports; do not let clients directly edit official space documents.
- Use Firebase Anonymous Authentication only for the report flow; do not pretend the existing login verifies institutional credentials or persist its password.
- Availability is an **estimate** based on reports within the agreed 30-minute window, not a live sensor measurement. Follow the policy in `docs/architecture.md` and `docs/scope.md`; do not invent a competing algorithm.
- Room persists favorite space IDs locally; do not duplicate Firestore's full collection into Room.
- Expose loading, empty, error, cached/stale, and pending-write states where applicable. Never leave an infinite spinner when offline; never present a pending Firestore write as server-confirmed.
- Never deploy permissive security rules, embed admin credentials, or log personal credentials. Propose rule changes for review before deployment.

## Existing design is protected
- The current four screens and their Stitch/Figma-derived visual structure are the source of truth.
- Preserve UVG green `#00B81C`, Material Design 3 theme, typography, spacing, shapes, icons, and existing reusable components.
- Do not redesign or replace screens. New controls (reports, favorites, validation, loading/error/empty states) must match the existing design system.
- Make only necessary UI corrections for Delivery 4, including placeholders, Edge-to-Edge, system bar contrast, accessibility, and safe insets.
- Keep user-visible text in Spanish and code identifiers in English.

## Kotlin and code hygiene
- PascalCase for classes, interfaces, objects, and Composable functions; camelCase for ordinary functions, properties, and variables; UPPER_SNAKE_CASE for constants.
- Use meaningful names, four-space indentation, explicit imports, and `on...` callback names.
- Reusable Composables accept `modifier: Modifier = Modifier` and apply it at their root.
- Prefer small single-purpose functions (target <=30 lines when practical), without fragmenting logically cohesive code needlessly.
- Do not use `!!`, `GlobalScope`, blocking data calls, debug `println`/`Log`, obsolete template code, or commented-out dead code.
- Preserve working behavior and use existing components instead of creating duplicates.

## Execution and verification
- Work on the existing project, not a generated replacement. Verify the active Git branch before editing; do not change branches, commit, push, merge, or force-push without explicit authorization.
- The user may authorize one complete implementation task. If so, implement in small internal stages, compile and test after each meaningful stage where tooling allows, and fix regressions before continuing.
- Do not claim builds, emulator tests, Firestore connectivity, offline behavior, or Room persistence passed unless actually verified.
- Produce a final M1–M12 traceability table with file locations and evidence, and explicitly mark unverified items.
- Human-only work (external-user task test, Firebase Console setup/rules deployment where unavailable, manual device tests) must be listed as pending, not claimed complete.
- Report changed files, architectural decisions, executed commands, test outcomes, and manual follow-ups.

## References
- https://kotlinlang.org/docs/coding-conventions.html
- https://developer.android.com/topic/architecture
- https://developer.android.com/training/data-storage/room
- https://firebase.google.com/docs/firestore
- https://firebase.google.com/docs/auth/android/anonymous-auth

Use official documentation when needed, but the professor's provided rubric and the approved project documents determine delivery scope.
