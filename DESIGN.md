# SmartCooking — DESIGN.md

A design-system reference for **SmartCooking**, a health-diet Android app (Jetpack
Compose, Material 3) that shows recipes, recommends dishes by BMI, and calculates
calories. Drop this file into the project and point an AI coding agent at it to
generate UI that matches the "Fresh Energy" direction below.

**Design language:** *Fresh Energy* (元气餐桌). Healthy eating should look
appetizing and lively — not clinical. The palette comes from fresh produce
(kiwi / leaf greens, mango orange, berry red) and the neutrals carry a green
tint so nothing reads as dead white or pure black.

---

## 1. Visual theme & atmosphere

- Bright, friendly, energetic — like a well-shot food app, not a hospital chart.
- One signature moment: **the energy figure** — a large, bold number (BMI or
  calories) that anchors the screen. Everything else stays quiet around it.
- Rounded, organic shapes and generous whitespace; no thin hairline "dashboard"
  density and no uniform card shadows.

## 2. Color palette

### Light mode

| Token | Hex | Role |
|---|---|---|
| Primary / DeepGreen | `#1F7A3D` | buttons, selected states, links |
| PrimaryContainer / MintTint | `#C9F0D6` | chips, selection backgrounds |
| KiwiGreen (brand accent) | `#2BA24A` | hero / energy surfaces, large fills |
| Secondary / Tangerine | `#D96A1F` | energy accent, secondary actions |
| SecondaryContainer / CreamPeach | `#FFE3CC` | highlighted tags |
| Tertiary / BerryDeep | `#C22E4F` | highlights, "recommended" badges |
| Error | `#BA1A1A` | destructive / validation |
| Background / MintWhite | `#F6FBF7` | app background |
| Surface | `#FFFFFF` | cards, sheets |
| SurfaceVariant / FieldTint | `#EAF3EC` | inputs, dividers |
| OnSurface / InkGreen | `#1C2A21` | primary text |
| OnSurfaceVariant / InkSoft | `#52655A` | secondary text |
| Outline | `#C7D5CA` | borders |

### Dark mode

| Token | Hex | Role |
|---|---|---|
| Primary / Mint | `#57C97B` | buttons, selected states |
| PrimaryContainer | `#1E7A37` | chips, selection backgrounds |
| Secondary | `#FFB267` | energy accent |
| Tertiary | `#FF8BA0` | highlights, badges |
| Background / ForestBlack | `#101B14` | app background |
| Surface / ForestSurface | `#17241B` | cards, sheets |
| SurfaceVariant | `#23332A` | inputs, dividers |
| OnSurface | `#EDF6EF` | primary text |
| OnSurfaceVariant | `#A9BDAF` | secondary text |
| Outline | `#3D4A41` | borders |

### Rules

- Prefer semantic slots (`MaterialTheme.colorScheme.*`) over raw hex in screens.
- Use `KiwiGreen` only for large hero/energy surfaces, never for small text on
  white — `DeepGreen` is the legible primary.
- Contrast targets: body text ≥ 4.5:1, large text ≥ 3:1.
- Never use pure white/black for surfaces; use `MintWhite` / `ForestBlack`.

## 3. Typography

One sans family (system Roboto by default). Strong weight contrast and tight
tracking on display sizes; numbers inherit the bold display treatment.

| Style | Size / weight | Use |
|---|---|---|
| displayLarge | 57sp / Bold, -1.5sp | the BMI / calorie "energy figure" |
| headlineMedium | 28sp / Bold, -0.5sp | page titles ("Recipe Library") |
| headlineSmall | 24sp / Bold | section headers |
| titleMedium | 16sp / SemiBold | card / recipe titles |
| titleSmall | 14sp / SemiBold | list item titles |
| bodyMedium | 14sp / Regular | descriptions, meta text |
| bodySmall | 12sp / Regular | captions |
| labelLarge | 14sp / SemiBold | buttons |
| labelMedium / Small | 12 / 11sp | chips, badges, overlines |

- Sentence case everywhere; no ALL-CAPS labels.
- For a rounder, friendlier feel, add a rounded face (e.g. **Nunito** or
  **Quicksand**) as a bundled font and set it as the `FontFamily` in
  `AppTypography`. This is optional polish.

## 4. Shape & radius

Layered radii — not one radius on everything.

| Slot | Radius | Use |
|---|---|---|
| extraSmall | 8dp | tiny chips, dots |
| small | 12dp | small controls |
| medium | 16dp | buttons, text fields |
| large | 24dp | cards |
| extraLarge | 32dp | sheets, dialogs |
| pill | 999dp | primary CTA, filter chips |

## 5. Spacing & layout

- 4dp base grid; common steps: 4 / 8 / 12 / 16 / 24 / 32.
- Screen padding: 16dp horizontal.
- List gaps: 8–12dp; section gaps: 24dp.
- Left-aligned content; center only for empty/loading states and dialogs.

## 6. Component notes

- **Recipe cards**: `Card` with `shape = large (24dp)`, 12dp inner padding,
  title in `titleMedium`, meta line ("18 min · Easy · 386 kcal") in `bodySmall`
  with `onSurfaceVariant`. The "Recommended" badge uses `tertiary`.
- **Filter chips / difficulty / time**: pill shape, `primaryContainer` when
  selected, `onPrimaryContainer` text.
- **Bottom navigation**: `NavigationBar` (Profile / Recipes / Calculator);
  selected item uses `primary`, unselected uses `onSurfaceVariant`.
- **Energy figure (hero)**: `displayLarge` in `onPrimary` on a `KiwiGreen`
  fill, with a one-line conclusion chip below.
- **Buttons**: primary = `primary` fill, pill shape, `labelLarge`. Secondary =
  outlined with `outline` border.
- **Errors / empty states**: explain what happened and how to fix it in plain
  language (e.g. "No matching recipes — try clearing the filters"), never a
  bare "Error".

## 7. Content rules

- All UI copy is **English**.
- Do **not** change recipe/dish data, titles, or seed content.
- Difficulty values are stored in Chinese (`简单` / `中等` / `困难`) but
  **displayed** in English (`Easy` / `Medium` / `Hard`).

## 8. Dos & Don'ts

**Do**

- Use semantic color slots and theme typography/shapes.
- Make the energy figure (BMI / calories) the loudest element on its screen.
- Keep one green as the anchor; use mango/berry sparingly for emphasis.

**Don't**

- Hardcode hex colors or font sizes in screens.
- Use gradient washes as decoration or identical shadows on every card.
- Use all-caps labels or centered paragraphs for body text.
- Introduce new accent colors beyond green / mango / berry.

## 9. Agent prompt guide

To have an AI agent build UI in this style, include a line like:

> "Follow DESIGN.md. Use `MaterialTheme.colorScheme`, `typography`, and `shapes`
> only — no hardcoded colors. Keep UI copy in English and do not modify recipe
> content. Lead each screen with a large energy figure (BMI or calories) in
> `displayLarge` on a `KiwiGreen` surface."

---

*Implementation: theme in `ui/theme/` (Color/Type/Shape/Theme, incl. `LocalEnergySurface` for hero cards), shared components in `ui/components/` (`EnergyCard`, `RecipeCard`), and the four screens (Profile, RecipeList, RecipeDetail, CalorieCalculator).*
