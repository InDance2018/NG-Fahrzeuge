# Entwicklungsplan – NG-Fahrzeuge

Stand: 16. Oktober 2026 · Dev3-Fahrtest

## Dev1 – NG City als technische Grundlage

- [x] Gradle-Projekt, Java-17-Toolchain, gemeinsamer Mod-Einstieg und Forge-Metadaten angelegt.
- [x] Metadaten und Ressourcenformat geprüft; nur Minecraft/Forge als Pflichtabhängigkeiten.
- [ ] Gradle-Wrapper ergänzen.

Details: [Dev3](DEV3.md) · [Dev2](DEV2.md) · [Dev1](DEV1.md) · [Build-Anleitung](CONTRIBUTING.md)

- [x] Eigenständiger Forge-Build für Minecraft 1.20.2 / Forge 48.1.0 / Java 17.
- [x] NG City als eigene Fahrzeugentität und Platzierungsgegenstand.
- [x] Erstes einfaches türkisfarbenes Spielmodell erstellt; Client-Sichtprüfung offen.
- [x] Einsteigen, Aussteigen und serverseitig geprüfte Fahrfreigabe umgesetzt; echte Client-Bedienprüfung offen.
- [x] Vorwärts, rückwärts, lenken und bremsen; automatisches Parken beim Aussteigen/fehlendem Fahrer.
- [ ] Separat schaltbare, gespeicherte Feststellbremse.
- [x] Speichern/Wiederladen von Position, Ausrichtung, UUID und Name per NBT-Rundlauf geprüft; echter Neustarttest offen.
- [x] Achtzehn automatisierte Forge-Serverprüfungen bestanden (Dev3).
- [ ] Client-Sicht-/Bedienprüfung von Dev3 inklusive Sitzposition und Kamera.
- [x] Nutzer bestätigt gemeinsamen Start von Dev1 und NG Verkehr; Dev3-Integration weiterhin offen.

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
