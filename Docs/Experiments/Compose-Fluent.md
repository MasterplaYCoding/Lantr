# Compose Fluent Integration

This project now includes `compose-fluent-ui` in `commonMain`, so you can adopt Fluent Design components in both Android and Desktop screens.

Dependencies added:

- `io.github.compose-fluent:fluent:v0.1.0`
- `io.github.compose-fluent:fluent-icons-extended:v0.1.0`

## What the library gives you

Compose Fluent is a Fluent Design UI toolkit for Compose Multiplatform.

Main categories exposed by the library include:

- app and window background materials such as Mica and Acrylic
- Fluent theme support with light and dark modes
- Windows-style buttons, toggles, checkboxes, text fields, combo boxes, sliders, progress controls
- richer navigation patterns such as side nav, top nav, breadcrumb bar, tab view, selector bar
- overlays and feedback such as dialogs, flyouts, tooltips, badges, info bars, command bars
- extra icon coverage through the Fluent icon pack

Important note:

- the project describes the library as experimental, and its APIs may change

## Best way to adopt it in Lantr

Do not replace the whole app at once.

Use it incrementally:

1. Keep `LantrTheme` as the default app theme.
2. Pick one surface at a time, such as Settings or Welcome.
3. Replace individual Material components with Fluent ones in that screen.
4. If you like the result, introduce Fluent backgrounds such as Mica on desktop-first surfaces.
5. Only after that consider wrapping larger sections in `FluentTheme`.

## Good places to start

These files are the safest first candidates:

- `composeApp/src/commonMain/kotlin/app/lantr/lantr/Views/mainMenu/mainScreen/screens/settings/SettingsScreen.kt`
- `composeApp/src/commonMain/kotlin/app/lantr/lantr/Views/WelcomeMenu/WelcomeScreen.kt`
- `composeApp/src/commonMain/kotlin/app/lantr/lantr/Views/Presentations/PresentationEditView.kt`

## Example adoption ideas

### 1. Fluent shell for desktop

Wrap a desktop-focused screen in Fluent theme and a Mica background.

Example shape:

```kotlin
FluentTheme {
    Mica(Modifier.fillMaxSize()) {
        ScreenContent()
    }
}
```

### 2. Replace Material buttons

Good for:

- toolbar actions
- settings actions
- welcome call-to-action buttons

Try switching:

- `Button` -> Fluent `Button`
- emphasized actions -> Fluent accent button
- subtle secondary actions -> Fluent subtle button

### 3. Upgrade settings controls

Your settings screen is a strong match for Fluent.

You can replace:

- `Switch` with Fluent toggle switch
- list rows with Fluent list item patterns
- section feedback with `InfoBar`
- segmented settings with Fluent segmented button

### 4. Better editor chrome

Your presentation editor could benefit from:

- Fluent command bar for top actions
- badge or info bar for editor status
- tab or selector bar if you want cleaner ARC vs SLIDES switching
- flyouts for theme and presentation info instead of simple dialogs

### 5. Use Fluent icons

If you want the app to feel more Windows-native on desktop, replace selected icons with the Fluent icon set instead of Material icons.

## Migration strategy

Recommended order:

1. Welcome screen visuals
2. Settings screen controls
3. Desktop editor top bar and popovers
4. Navigation surfaces
5. Theme/background layer experiments

## Source

Reference repo:

- https://github.com/compose-fluent/compose-fluent-ui
