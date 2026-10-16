# NG-Fahrzeuge

**Aktueller Stand: 0.1.0-dev7 · 16. Oktober 2026 · Entwicklungsfassung**

**Eigene Fahrzeuge für Minecraft – ein Projekt von NeverGate.**

NG-Fahrzeuge ist eine eigenständige Fahrzeugmod für **Minecraft Java 1.20.2 mit Forge**. Drei Fahrzeuggruppen im Minecraft-Stil nutzen eine gemeinsame technische Grundlage.

**Projektstart: 16. Oktober 2026.** Die aktuelle Dev7 erweitert die Dev6-Modelle um Motorsteuerung, Fahrzeugklänge, Blinker, Hupe, Laderaum, Schlüssel und eine Fahrzeugwerkbank. Dev7 ist eine Entwicklungsfassung; eine stabile Veröffentlichung steht noch aus. Umgesetzte Funktionen und bestandene Serverprüfungen sind unten beschrieben, ausstehende Client-Spieltests ausdrücklich benannt.

## Aktueller Entwicklungsstand

**Dev7 – acht neue Erweiterungen:** Motor starten/abschalten, eigene Motor-/Fahrklänge, Blinker und Warnblinker, Hupe, Spiegelgestaltung und dritte Bremsleuchte, Laderaum (9/27/54 Plätze), Besitzer/Schlüssel/Abschließen sowie Fahrzeugwerkbank mit Überlebensrezepten. **41/41 Forge-Servertests bestanden; Werkbankbau sowie Motorstart/Fahren zusätzlich über den echten Minecraft-Client geprüft.** Client und Server benötigen beide Dev7 (Netzwerkprotokoll 3). MC02 bleibt auf Dev5. [Bedienung, Rezepte und Prüfstand](DEV7.md).

**Dev6 – Optik nach der Projektzentrale:** City und Family in Mint, Family mit Dachreling, weißer Transporter mit mintfarbenem Streifen. Neue Karosserien, Scheiben, Innenräume, Räder, NG-Embleme und Inventarmodelle. Alle drei Modelle wurden im echten Minecraft-Client von vorn und hinten geprüft. **Build/Reobf und 30/30 Forge-Servertests bestanden.** MC02 bleibt auf der bestätigten Dev5; Dev6 ist dort noch nicht installiert. [Modelle und Prüfstand](DEV6.md).

**Dev5 – Fahrer-Erkennung auf Arclight korrigiert:** Die Fahrerzuordnung wird zusätzlich aus den tatsächlichen Mitfahrern abgeglichen. Arclight umgeht die mod-eigenen Ein-/Ausstiegsmethoden, wodurch Dev4 dort ohne erkannten Fahrer, Fahranzeige und Steuerung bleiben konnte. **30/30 Forge-Servertests bestanden**, einschließlich einer neuen Regression für fehlende Einstiegsmeldungen. Dev5 ist auf MC02 installiert. **Vom Projektinhaber bestätigt: Einsteigen, Fahren, Lenken, km/h-Anzeige und Aussteigen funktionieren.** Weitere Prüfungen bleiben offen. [Korrektur und Prüfstand](DEV5.md).

**Dev4 – drei Fahrzeuggruppen mit Ausstattung:** NG City (1 Sitz), NG Family (4 Sitze) und NG Transporter (2 Sitze) sind als erste Spielmodelle umgesetzt. Alle nutzen die serverseitige Fahrsteuerung, eine separat schaltbare Feststellbremse, Farbstoff-Lackierung sowie schaltbare Leuchtflächen mit Brems- und Rückfahrlicht. Die Fahrzeuggruppen behalten eigene Größen und Höhen.

**Historischer Prüfstand Dev4:** Build/Reobf erfolgreich; **29/29 eigenständige Forge-Servertests** einschließlich tatsächlicher Chunk-Entladung/-Rückkehr und eines Neustart-Nachweises. **38/38 Tests mit NG Verkehr Beta2-dev6** einschließlich neun zusätzlicher Integrationsfälle bestanden: Erkennung geparkter Fahrzeuge, Stellplätze, Mitfahrerfilter, Ein-/Ausfahrtszählung sowie Schranken und Höhenprüfung. Der grundlegende Fahr-/Bedienungstest mit Dev5 auf MC02 ist vom Projektinhaber bestätigt. Vollständige Sicht-/Ausstattungsprüfung und zwei echte verbundene Clients bleiben offen.

**Bedienung ab Dev7:** Rechtsklick steigt ein. **I startet den Motor; H löst die Feststellbremse.** W/S fährt, A/D lenkt, Springen bremst, Schleichen steigt aus. L: Licht, Komma/Punkt: Blinker, R: Warnblinker, G: Hupe, B: Laderaum. Alle Tasten sind unter Optionen → Steuerung → NG Fahrzeuge umbelegbar. Neu platzierte Fahrzeuge stehen mit Motor AUS und Feststellbremse AN. Besitzer und Schlüsselinhaber können lackieren und auf den Laderaum zugreifen; nur der Besitzer kann ein unbesetztes Fahrzeug mit Schleichen + Rechtsklick mit leerer Hand aufnehmen. [Schlüssel, Laderaum und Baukosten](DEV7.md).

Die Leuchten haben helle Leuchtflächen; es gibt noch keine auf die Straße projizierten Lichtkegel. Die Dev6-Modelle wurden im Minecraft-Client ohne Shader visuell geprüft; Konzeptbeleuchtung und Umgebung können abweichen.

[Dev7: neue Funktionen und Bedienung](DEV7.md) · [Dev6: neue Fahrzeugoptik](DEV6.md) · [Dev5 und bestätigter MC02-Spieltest](DEV5.md) · [Dev4 und Prüfstand](DEV4.md) · [Entwicklung](CONTRIBUTING.md) · [Dev3](DEV3.md) · [Dev2](DEV2.md) · [Dev1](DEV1.md)

## Drei Fahrzeuggruppen

| Fahrzeug | Aussehen | Einsatz |
|---|---|---|
| **NG City** | Kompakter türkiser Zweitürer | Stadtverkehr und normale Parkplätze; erstes Entwicklungsfahrzeug |
| **NG Family** | Längerer türkiser Familienkombi | Mehrere Mitfahrer und größere Stellplätze |
| **NG Transporter** | Hoher weißer Kastenwagen mit türkisem Streifen | Transportaufgaben und Tests von Höhenbegrenzungen |

Dev6 setzt die Originalvorlagen der Projektzentrale als detaillierte Minecraft-Modelle um. Sechs Aufnahmen aus dem echten Client dokumentieren die Sichtprüfung im Downloadpaket.

## Funktionen und weitere Planung

- Vorwärtsfahren, Lenken, Rückwärtsfahren, Bremsen und Feststellbremse.
- Einsteigen, Aussteigen und je nach Fahrzeug mehrere Sitzplätze.
- Beleuchtung und auswählbare Fahrzeugfarben.
- Abgestellte Fahrzeuge bleiben über Welt- und Serverneustarts erhalten.
- Zum Modell passende Fahrzeuggrößen und Kollisionen.
- Einzelspieler und Mehrspieler mit serverseitiger Prüfung der Steuerung.

Laderaum, Schlüssel und Fahrzeugherstellung sind seit Dev7 umgesetzt. Autonomer Verkehr gehört nicht zum aktuellen Entwicklungsumfang.

## Eigenständig und gemeinsam nutzbar

**NG-Fahrzeuge benötigt NG Verkehr nicht.** Beide Projekte behalten eigene Versionen, eigene JARs und eigene Repositories.

Mit dem [NG-Verkehrs-System](https://github.com/InDance2018/NG-Verkehrs-System) ist eine optionale Zusammenarbeit vorgesehen:

- Stellplätze und Schranken erkennen Fahrzeuge auch ohne Fahrer.
- Mitfahrer werden nicht als zusätzliche Fahrzeuge gezählt.
- Höhenkontrollen berücksichtigen die Fahrzeughöhe.
- Parkplatzzählung, Schranken und Parkleitsystem werden gemeinsam getestet.

Die Anbindung über den bestehenden Fahrzeug-Tag wurde mit NG Verkehr Beta2-dev6 auf dem Forge-Testserver geprüft. Ein realer Zwei-Client-Test steht noch aus. NG Verkehr bleibt auch für andere Fahrzeugmods offen.

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

Das Repository ist **öffentlich**. Quellcode und Entwicklungsstände sind einsehbar. Dev7 bleibt eine Entwicklungsfassung mit neuer Ausstattung auf der Dev6-Optik und weiteren ausstehenden Spieltests; eine stabile Veröffentlichung steht noch aus.

[Fehler und Vorschläge](https://github.com/InDance2018/NG-Fahrzeuge/issues) · [NG Verkehr](https://github.com/InDance2018/NG-Verkehrs-System)

## Nutzung und Rechte

Eine allgemeine Nutzungslizenz wurde noch nicht vergeben. Dieses Repository erteilt keine allgemeine Erlaubnis zur Weitergabe oder Veränderung von Code, Modellen, Texturen und Konzeptbildern. Bedingungen für spätere Downloads werden gesondert festgelegt.

---
**NeverGate · [InDance2018](https://github.com/InDance2018)**
