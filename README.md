# Vivix

Vivix is an English-language adaptation of [GC-Tune](https://github.com/Bebrazui/GC-Tune) (upstream v1.2.2) for vivo OriginOS devices.

The original project includes a Game Cube command channel, icon-pack tinting, themes, fonts, and system customization. This project is not affiliated with vivo.

> **Device safety:** advanced commands and system-theme operations may not work on every OriginOS release. Back up your settings and only use features you understand.

## Build

The GitHub Actions **Android debug APK** workflow compiles the Android app using JDK 17 and Gradle 8.11.1. Its unsigned production keystore is not included, so CI produces only a standard **debug-signed** APK.

Download the APK from the workflow run's Artifacts section.

## Credits

Source adapted from [Bebrazui/GC-Tune](https://github.com/Bebrazui/GC-Tune); preserve upstream attributions and applicable licensing. This is an independent source-code adaptation in an existing repository, not a GitHub-native fork.

## Translation

User-facing Russian interface text and status/error messages are being translated to English. Translation mappings are kept under `tools/ru-en.json` for review.
