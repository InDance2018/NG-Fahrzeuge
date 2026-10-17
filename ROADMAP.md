# Entwicklungsplan – NG-Fahrzeuge

Stand: **17. Oktober 2026 · 0.1.0-dev12 · Entwicklungs-/Testfassung**.

## Aktueller Projekt- und Serverstand

Dev12 ist auf MC02 installiert, in der Modliste aktiviert und nach Neustart live als geladen bestätigt. Frühere Fahrzeugfassungen sind deaktiviert. Client und Server benötigen Dev12 (Netzwerkprotokoll 8).

[Umfang und Bedienung](DEV12.md) · [MC02-Installationsnachweis und aktuelle Lieferdatei](MC02-2026-10-17.md).

| Entwicklungsstand | Umgesetzt |
|---|---|
| Dev4–Dev6 | City, Family, Transporter, Fahrsteuerung, Arclight-Fahrererkennung und überarbeitete Modelle |
| Dev7 | Motor/Klang, Blinker, Hupe, Spiegel, Laderaum, Schlüssel/Besitzer und Fahrzeugwerkbank |
| Dev8 | Türen/HUD/Kilometer, Tankstelle, Schäden/Reparatur, Haftung, Tempomat und Lastenanhänger |
| Dev9 | Kennzeichen, Händler/Garage, Abschleppen, Wischer/Fenster/Licht, Batterie/Starthilfe und Hebebühne |
| Dev10 | Kamera, Heck-/Dachausbau, Anhängervarianten, Warnbalken/Seilwinde, Reifen, Waschen und Verkauf |
| Dev11 | Stadtbus, Motorrad, Pickup, Linienanzeigen, Lackierstation und Fahrzeugübersicht |
| Dev12 | Geländewagen, Sportwagen, Taxi, Sattelzug, Allrad/Untersetzung, Getriebe, Einzeltüren, Fernschlüssel, Kühlung und Diagnose |

## Belegter Prüfstand

- [x] Aktuellen Dev12-Quellstand gebaut; 129 erforderliche Forge-Servertests erneut bestanden.
- [x] Zusammen mit NG-Verkehr beta2-dev12: 16 gezielte Integrationstests bestanden, einschließlich Zustandszugriff auf alle elf Fahrzeugtypen.
- [x] Native Fahrzeug-/Servicebelege über getrennte Serverprozesse und tatsächliche Chunk-Entladung/Rückkehr geprüft.
- [x] Wiederholte Serviceanfragen führen keine zweite Leistung aus.
- [x] MC02-Installation nach vollständiger Sicherung; JARs bytegenau zurückgelesen und Laufzeitversionen geprüft.

Die historischen Dev12-Client-/Neustartprüfungen sind in DEV12.md beschrieben. Die damalige Build-Datei und die neu gebaute MC02-Lieferdatei besitzen getrennte Prüfsummen. Frühere Zwei-Client-Nachweise gelten nur für die jeweils dokumentierten Versionspaare.

## Noch offen

- [ ] Vollständiger Dev12-Spieltest mit zwei echten Fahrern und allen Fahrzeugfunktionen.
- [ ] Sämtliche Sattelzug-Parkgeometrien, Höhen-/Breitenkontrollen und Rangiersituationen gemeinsam erproben.
- [ ] Vollständiger MC02-Spieltest des neuen Umfangs.
- [ ] Akustische Feinprüfung am echten Ausgabegerät.
- [ ] Elektroantrieb mit Traktionsakku und passender Ladeoperation.
- [ ] Euro-Anbindung durch das NG-Wirtschaftssystem.
- [ ] Stabile Gesamtveröffentlichung nach weiterer Erprobung.

Keine Fahrzeug-KI im aktuellen Umfang. Die Mods bleiben eigenständig; eine gemeinsame Versionsfreigabe gilt nur für den dokumentierten Prüfumfang.

<details>
<summary>Historischer Entwicklungsplan vom 16. Oktober 2026 (Dev4)</summary>

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

</details>
