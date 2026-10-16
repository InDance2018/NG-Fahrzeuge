# NG-Fahrzeuge

**Eigene Fahrzeuge für Minecraft – ein Projekt von NeverGate.**

NG-Fahrzeuge wird eine eigenständige Fahrzeugmod für **Minecraft Java 1.20.2 mit Forge**. Geplant sind drei Fahrzeuggruppen im Minecraft-Stil.

**Projektstart: 16. Oktober 2026.** Konzept und Entwürfe stehen. Wir beginnen die technische Entwicklung mit NG City. Es gibt noch keine freigegebene spielbare Version. Alle nachstehenden Funktionen sind geplant, sofern nicht ausdrücklich als geprüft gekennzeichnet.

## Aktueller Entwicklungsstand

**Dev3 – NG City mit Einsteigen und Fahren:** Ein Fahrerplatz, Vorwärts-/Rückwärtsfahrt, Lenken, Bremse, automatisches Parken beim Aussteigen und eine Geschwindigkeitsanzeige sind umgesetzt. Die Fenster sind durchsichtig. Build/Reobf und **18/18 Forge-Servertests** sind bestanden. Client-Darstellung, Sitz/Kamera, tatsächliche Tasteneingaben über eine Client-Verbindung und NG-Verkehr-Erkennung werden noch im Spiel erprobt.

**Bedienung:** Rechtsklick am Fahrzeug zum Einsteigen, W/S zum Fahren, A/D zum Lenken, Springen (Standard: Leertaste) zum Bremsen und Schleichen (Standard: linke Umschalttaste) zum Aussteigen. Die Tasten folgen den Minecraft-Belegungen. Ein unbesetztes Fahrzeug lässt sich weiter mit Schleichen + Rechtsklick mit leerer Hand aufnehmen.

**Dev1-Spielstart vom Nutzer bestätigt:** Die Grundgerüstfassung startet laut Rückmeldung gemeinsam mit NG Verkehr. Dev2/Dev3 sind noch nicht durch den Nutzer im Spiel geprüft.

[Entwicklung und Build](CONTRIBUTING.md) · [Dev3 und Testanleitung](DEV3.md) · [Dev2](DEV2.md) · [Dev1](DEV1.md)

## Drei Fahrzeuggruppen

| Fahrzeug | Aussehen | Einsatz |
|---|---|---|
| **NG City** | Kompakter türkiser Zweitürer | Stadtverkehr und normale Parkplätze; erstes Entwicklungsfahrzeug |
| **NG Family** | Längerer türkiser Familienkombi | Mehrere Mitfahrer und größere Stellplätze |
| **NG Transporter** | Hoher weißer Kastenwagen mit türkisem Streifen | Transportaufgaben und Tests von Höhenbegrenzungen |

Die Konzeptbilder zeigen die Designrichtung, keine fertigen Spielmodelle.

![NG City – Konzept](docs/images/ng-city.png)
![NG Family – Konzept](docs/images/ng-family.png)
![NG Transporter – Konzept](docs/images/ng-transporter.png)

## Geplante Funktionen

- Vorwärtsfahren, Lenken, Rückwärtsfahren, Bremsen und Feststellbremse.
- Einsteigen, Aussteigen und je nach Fahrzeug mehrere Sitzplätze.
- Beleuchtung und auswählbare Fahrzeugfarben.
- Abgestellte Fahrzeuge bleiben über Welt- und Serverneustarts erhalten.
- Zum Modell passende Fahrzeuggrößen und Kollisionen.
- Einzelspieler und Mehrspieler mit serverseitiger Prüfung der Steuerung.

Transportinventar, Rezepte und weitere Ausstattung legen wir im Projektverlauf fest. Autonomer Verkehr gehört nicht zum ersten Entwicklungsstand.

## Eigenständig und gemeinsam nutzbar

**NG-Fahrzeuge benötigt NG Verkehr nicht.** Beide Projekte behalten eigene Versionen, eigene JARs und eigene Repositories.

Mit dem [NG-Verkehrs-System](https://github.com/InDance2018/NG-Verkehrs-System) ist eine optionale Zusammenarbeit vorgesehen:

- Stellplätze und Schranken erkennen Fahrzeuge auch ohne Fahrer.
- Mitfahrer werden nicht als zusätzliche Fahrzeuge gezählt.
- Höhenkontrollen berücksichtigen die Fahrzeughöhe.
- Parkplatzzählung, Schranken und Parkleitsystem werden gemeinsam getestet.

Die Anbindung ist noch nicht als funktionierend bestätigt. NG Verkehr bleibt auch für andere Fahrzeugmods offen.

## Entwicklungsplan

1. **Grundlage und NG City:** Modstruktur, Registrierung, erstes Spielmodell, Ein-/Aussteigen und Fahrsteuerung.
2. **Parken und Speicherung:** Feststellbremse, dauerhaftes Abstellen und Neustarttests.
3. **NG-Verkehr-Anbindung:** Fahrzeugerkennung, Zählung, Schranken und Höhenkontrolle.
4. **NG Family und NG Transporter:** Weitere Größen und Sitzplätze auf gemeinsamer Fahrzeugtechnik.
5. **Ausstattung und Erprobung:** Licht, Farben, Mehrspieler, Chunkwechsel und erste Testfassung.

Wir arbeiten in nachvollziehbaren Dev-Ständen. Ein erfolgreicher Build ersetzt keinen Spieltest. [Aufgaben und Prüfstände](ROADMAP.md).

## Technische Basis

| Bereich | Grundlage |
|---|---|
| Minecraft | Java Edition 1.20.2 |
| Modloader | Forge 48.1.0 als erste Testbasis |
| Java | 17 |
| Mod-ID | `ng_fahrzeuge` |
| NG Verkehr | Optional; keine Pflichtinstallation |
| Installation | Bei Veröffentlichung auf Client und Server |

## Entwicklung und Rückmeldungen

Das Repository ist derzeit **privat**. Ankündigungen sehen nur Personen mit Repository-Zugriff. Ein Termin für die erste spielbare Testfassung steht noch nicht fest.

[Fehler und Vorschläge](https://github.com/InDance2018/NG-Fahrzeuge/issues) · [NG Verkehr](https://github.com/InDance2018/NG-Verkehrs-System)

## Nutzung und Rechte

Eine allgemeine Nutzungslizenz wurde noch nicht vergeben. Dieses Repository erteilt keine allgemeine Erlaubnis zur Weitergabe oder Veränderung von Code, Modellen, Texturen und Konzeptbildern. Bedingungen für spätere Downloads werden gesondert festgelegt.

---
**NeverGate · [InDance2018](https://github.com/InDance2018)**
