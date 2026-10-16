# Dev1 – technisches Grundgerüst

Stand: 16. Oktober 2026

## Angelegt

- Eigenes Gradle-Projekt mit Java-17-Toolchain, ForgeGradle 6.0.24 und Forge 48.1.0 für Minecraft 1.20.2.
- Mod-Einstieg `de.nevergate.fahrzeuge.NgFahrzeuge`, Mod-ID `ng_fahrzeuge`, Entwicklungskennung `0.1.0-dev1`.
- Gemeinsames Setup ohne Clientklassen im Mod-Einstieg.
- Forge-Metadaten, Ressourcenpaket und Build-/Startanleitung in CONTRIBUTING.md.
- Nur Forge und Minecraft als Pflichtabhängigkeiten; keine NG-Verkehr-Abhängigkeit.

## Noch offen

NG City als Fahrzeugentität, Platzierung, Modell, Ein-/Aussteigen, Fahrsteuerung,
Netzwerksynchronisierung und Speicherung sind noch nicht implementiert. Dieses
Grundgerüst ist keine spielbare Fahrzeugfassung. Dev1 ist noch nicht abgeschlossen.
Ein Gradle-Wrapper und automatisierte Spieltests sind noch nicht enthalten.

## Prüfung

Metadaten nach Ersetzung der Gradle-Variablen als TOML eingelesen, Pflichtabhängigkeiten
geprüft und pack.mcmeta als JSON geprüft. Build/Reobf mit Java 17 und Gradle 8.1.1 erfolgreich. Die erzeugte JAR wurde auf
ZIP-Integrität, enthaltenen Mod-Einstieg, expandierte Metadaten und Ressourcen geprüft.
Es sind noch keine automatisierten Spieltests vorhanden; Gradle meldet `test NO-SOURCE`.
Clientstart und dedizierter Serverstart wurden noch nicht geprüft. Ein erfolgreicher Build ersetzt weder einen Client- noch
einen dedizierten Servertest.

Build-Ausgabe: `ng_fahrzeuge-0.1.0-dev1.jar` (2682 Bytes).
SHA-256: `9eaa51d0bda72757f97306fdf626414c0d82530ad1bb46abe1e40b4f3bc04455`.
