# Entwicklungsplan – NG-Fahrzeuge

Stand: 16. Oktober 2026 · Dev4 – drei Fahrzeuggruppen und Parksystem

## Dev1 – NG City als technische Grundlage

- [x] Gradle-Projekt, Java-17-Toolchain, gemeinsamer Mod-Einstieg und Forge-Metadaten angelegt.
- [x] Metadaten und Ressourcenformat geprüft; nur Minecraft/Forge als Pflichtabhängigkeiten.
- [ ] Gradle-Wrapper ergänzen.

Details: [Dev4](DEV4.md) · [Dev3](DEV3.md) · [Dev2](DEV2.md) · [Dev1](DEV1.md) · [Build-Anleitung](CONTRIBUTING.md)

- [x] Eigenständiger Forge-Build für Minecraft 1.20.2 / Forge 48.1.0 / Java 17.
- [x] NG City als eigene Fahrzeugentität und Platzierungsgegenstand.
- [x] Erstes einfaches türkisfarbenes Spielmodell erstellt; Client-Sichtprüfung offen.
- [x] Einsteigen, Aussteigen und serverseitig geprüfte Fahrfreigabe umgesetzt; echte Client-Bedienprüfung offen.
- [x] Vorwärts, rückwärts, lenken und bremsen; automatisches Parken beim Aussteigen/fehlendem Fahrer.
- [x] Separat schaltbare, gespeicherte Feststellbremse; automatisches Parken ohne Fahrer.
- [x] Speichern/Wiederladen von Position, Ausrichtung, UUID und Name per NBT-Rundlauf und tatsächlichem Server-Neustart geprüft.
- [x] 29/29 eigenständige Forge-Serverprüfungen; 38/38 mit NG Verkehr Beta2-dev6 (neun zusätzliche Integrationsfälle).
- [ ] Client-Sicht-/Bedienprüfung von Dev4 inklusive aller Modelle, Sitzpositionen, Farben, Leuchten und Kamera.
- [x] Nutzer bestätigt gemeinsamen Start von Dev1 und NG Verkehr; Dev4-Integration lokal auf dem Forge-Server geprüft; reale Client-Erprobung offen.

## Danach

- [x] Tatsächliche Chunk-Entladung/-Rückkehr und Fahrzeugzustände nach einem getrennten Server-Neustart geprüft.
- [ ] Zwei echte verbundene Minecraft-Clients und weitere Mehrspieler-Sonderfälle.
- [x] Optionale Integration mit NG Verkehr Beta2-dev6: Erkennung ohne Fahrer, Mitfahrerfilter, Stellplätze, Ein-/Ausfahrt sowie Schranken/Höhe.
- [x] NG Family mit vier Sitzplätzen und eindeutigem Fahrer.
- [x] NG Transporter mit 2,5 m Höhe und zwei Sitzen.
- [ ] Transportinventar gesondert festlegen.
- [x] Schaltbare Leuchtflächen, Brems-/Rückfahrlicht und 16 Farbstofffarben; Client-Sichtprüfung offen.
- [ ] Rezepte und Bedienungsanleitung.
- [ ] Erste gemeinsam erprobte Testfassung.

## Grenzen

Keine Pflichtabhängigkeit zwischen den beiden Mods. Keine Fahrzeug-KI im ersten Stand. Konzeptbilder sind keine Spielmodelle. Aufgaben gelten erst als geprüft, wenn der konkrete Prüfumfang dokumentiert ist. Kein automatisches Ausrollen auf bestehende Minecraft-Server.
