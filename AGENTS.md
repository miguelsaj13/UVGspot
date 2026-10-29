## Existing UI protection

The existing Login, Needs, Recommendations, and Space Detail screens are the visual source of truth.

- Do not redesign, replace, or recreate existing screens.
- Preserve the existing layouts, spacing, typography, colors, icons, shapes, and navigation structure.
- The primary brand color is #00B81C.
- Reuse existing Material 3 components and design tokens.
- New screens and components must follow the existing visual language.
- Visual modifications are allowed only when required for Delivery 4 functionality, validation, accessibility, loading/error states, placeholders, or Edge-to-Edge corrections.
- Do not modify unrelated UI files.
- Never introduce a new design system.

## Approval and verification workflow

- Inspect relevant existing code before proposing changes.
- Before each implementation phase, present a file-by-file plan.
- Wait for explicit approval before modifying code.
- Implement only the approved phase.
- Preserve working behavior and existing navigation.
- Run compilation and relevant tests whenever the environment supports them.
- Never claim a test passed without executing it.
- Report modified files, architectural decisions, test results, and remaining manual steps.
- Do not commit, push, merge, or modify Git branches without explicit approval.
