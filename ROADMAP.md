# Entwicklungsplan – NG-Fahrzeuge

Stand: 16. Oktober 2026 · Projektstart

## Dev1 – NG City als technische Grundlage

- [x] Gradle-Projekt, Java-17-Toolchain, gemeinsamer Mod-Einstieg und Forge-Metadaten angelegt.
- [x] Metadaten und Ressourcenformat geprüft; nur Minecraft/Forge als Pflichtabhängigkeiten.
- [ ] Gradle-Wrapper ergänzen.

Details: [Dev1](DEV1.md) · [Build-Anleitung](CONTRIBUTING.md)

- [x] Eigenständiger Forge-Build für Minecraft 1.20.2 / Forge 48.1.0 / Java 17.
- [ ] NG City als eigene Fahrzeugentität und Platzierungsgegenstand.
- [ ] Erstes einfaches Spielmodell in der vereinbarten Designrichtung.
- [ ] Einsteigen, Aussteigen und serverseitig geprüfte Fahrsteuerung.
- [ ] Vorwärts, rückwärts, lenken, bremsen und Feststellbremse.
- [ ] Speichern und Wiederladen der Fahrzeugdaten.
- [ ] Automatisierte Serverprüfungen; Client-Spieltest gesondert dokumentiert.

## Danach

- [ ] Echte Neustart-, Chunk- und Mehrspielertests.
- [ ] Optionale Integration mit NG Verkehr: Erkennung ohne Fahrer, keine Mitfahrer-Doppelzählung, Schranken und Höhe.
- [ ] NG Family mit mehreren Sitzplätzen.
- [ ] NG Transporter mit größerer Fahrzeughöhe; Transportinventar gesondert festlegen.
- [ ] Licht und Farben.
- [ ] Rezepte und Bedienungsanleitung.
- [ ] Erste gemeinsam erprobte Testfassung.

## Grenzen

Keine Pflichtabhängigkeit zwischen den beiden Mods. Keine Fahrzeug-KI im ersten Stand. Konzeptbilder sind keine Spielmodelle. Aufgaben gelten erst als geprüft, wenn der konkrete Prüfumfang dokumentiert ist. Kein automatisches Ausrollen auf bestehende Minecraft-Server.
