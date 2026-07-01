# Notice

**I've started doing Minecraft commissions in my [Discord server](https://discord.gg/Ce6Vty6khA)!**

# Crosshair Enhancements

This mod overhauls vanilla crosshair and attack indicator rendering.
It offers various customizations, quality-of-life improvements, and bug fixes.

## Features

- **Blending & Opacity**: Applies custom blending and opacity settings to both the crosshair and the attack indicator separately.
- **Visibility Rules**: Renders the crosshair in Third-Person mode, Spectator mode, or in Debug Overlay (via F3 key).
- **Full Indicator Visibility**: Renders or hides the full indicator based on set state.
- **Full Indicator for All Items**: Forces the full attack indicator to render for all items (not for weapons only).
- **Responsive Indicator**: Renders the indicator as fully charged the exact moment a max-damage hit is possible (when the weapon's charge is at least 90%).
- **Smooth Indicator**: Interpolates the attack indicator's charging animation to match your client's framerate.
- **Centralized Config**: Vanilla options related to either the crosshair or the attack indicator are integrated into the config menu for easier access.

## Bug fixes // QoL changes

- **Centered Crosshair Fix**: Resolves the vanilla bug where the crosshair renders slightly off-center.
  To fix an off-center attack indicator, use my [Centered Full Indicator Fix](https://modrinth.com/resourcepack/centered-full-indicator-fix) pack.
- **Full Indicator Rendering Fix**: Resolves the vanilla logic bug with full attack indicator being rendered in situations where it shouldn't or the other way around.
  The full indicator was intended to be rendered for weapons (items with attack speed and attack damage attributes) exclusively.
- **Mining Indicator Fix**: Stops rendering the attack indicator while mining.
  Seeing one is very misleading.

## Planned features

I will go through these later:

- **Show Crosshair In Hidden Menus**: Keeps the crosshair visible when HUD is hidden (via F1 key).

Stay tuned!

## Important

- The only attack indicator supported is the crosshair one.
  I don't think I'll ever support an inventory indicator.
- I encourage you not to use this mod alongside other crosshair-related mods.
  This action might result in a visual bug or even a crash.
  I try my best to implement relevant tweaks.
