# Vivix

Vivix is an English-language adaptation of [GC-Tune](https://github.com/Bebrazui/GC-Tune) (upstream v1.2.2) for vivo OriginOS devices.

The original project includes a Game Cube command channel, icon-pack tinting, themes, fonts, and system customization. This project is not affiliated with vivo.

> **Device safety:** advanced commands and system-theme operations may not work on every OriginOS release. Back up your settings and only use features you understand.

## Build

The GitHub Actions **Android debug APK** workflow compiles the Android app using JDK 17 and Gradle 8.11.1. Its unsigned production keystore is not included, so CI produces only a standard **debug-signed** APK.

Download the APK from the workflow run's Artifacts section.


## Backup and recovery (OriginOS icon themes)

Vivix now creates a **mandatory backup before applying any new icon pack**. If that backup cannot be completed and checked, the icon installation stops before changing active theme resources. In **Customization → Icons → Backup & recovery**, you can:

- **Back up current icon theme:** save the current Vivo icon/mask directories.
- **Restore saved icon theme:** restore either the latest or the first saved snapshot, after backing up the current state again.
- **Disable Vivix icon overrides:** move the active custom-icon and launcher-mask directories aside without deleting their contents, allowing OriginOS to try its default theme icons.
- **View saved backups:** inspect saved snapshot identifiers.

Backups are stored in `/data/bbkcore/theme/.vivix_recovery/`, outside app-private storage. They survive an app reinstall but **not a factory reset**. Backups are **not automatically deleted**; each backup occupies storage, and the app must still be able to run the authorized Game Cube system channel to restore files.

**A snapshot is never guaranteed to be factory-original.** If Vivix changed the theme before this feature was installed, the first snapshot captures that already-modified state. For corrupted icons, the **Disable Vivix icon overrides** option is a reversible recovery attempt, not a guaranteed replacement for a full firmware repair. If access to the Game Cube channel fails, operations will not be reported as successful.

Restoring does not clear `com.bbk.launcher2` data, rearrange apps, reset widgets, or reinstall firmware. Keep a backup of your phone before any system customization.

## Credits

Source adapted from [Bebrazui/GC-Tune](https://github.com/Bebrazui/GC-Tune); preserve upstream attributions and applicable licensing. This is an independent source-code adaptation in an existing repository, not a GitHub-native fork.

## Translation

User-facing Russian interface text and status/error messages are being translated to English. Translation mappings are kept under `tools/ru-en.json` for review.
