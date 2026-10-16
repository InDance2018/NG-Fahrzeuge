# NG-Fahrzeuge

**Aktueller Stand: 0.1.0-dev5 · 16. Oktober 2026 · Entwicklungsfassung**

**Eigene Fahrzeuge für Minecraft – ein Projekt von NeverGate.**

NG-Fahrzeuge ist eine eigenständige Fahrzeugmod für **Minecraft Java 1.20.2 mit Forge**. Drei Fahrzeuggruppen im Minecraft-Stil nutzen eine gemeinsame technische Grundlage.

**Projektstart: 16. Oktober 2026.** Die aktuelle Dev5 enthält erste Spielmodelle aller drei Gruppen samt Fahrsteuerung, Ausstattung und korrigierter Arclight-Fahrerzuordnung. Dev5 ist eine Entwicklungsfassung; eine stabile Veröffentlichung steht noch aus. Umgesetzte Funktionen und bestandene Serverprüfungen sind unten beschrieben, ausstehende Client-Spieltests ausdrücklich benannt.

## Aktueller Entwicklungsstand

**Dev5 – Fahrer-Erkennung auf Arclight korrigiert:** Die Fahrerzuordnung wird zusätzlich aus den tatsächlichen Mitfahrern abgeglichen. Arclight umgeht die mod-eigenen Ein-/Ausstiegsmethoden, wodurch Dev4 dort ohne erkannten Fahrer, Fahranzeige und Steuerung bleiben konnte. **30/30 Forge-Servertests bestanden**, einschließlich einer neuen Regression für fehlende Einstiegsmeldungen. Dev5 ist auf MC02 installiert. **Vom Projektinhaber bestätigt: Einsteigen, Fahren, Lenken, km/h-Anzeige und Aussteigen funktionieren.** Weitere Prüfungen bleiben offen. [Korrektur und Prüfstand](DEV5.md).

**Dev4 – drei Fahrzeuggruppen mit Ausstattung:** NG City (1 Sitz), NG Family (4 Sitze) und NG Transporter (2 Sitze) sind als erste Spielmodelle umgesetzt. Alle nutzen die serverseitige Fahrsteuerung, eine separat schaltbare Feststellbremse, Farbstoff-Lackierung sowie schaltbare Leuchtflächen mit Brems- und Rückfahrlicht. Die Fahrzeuggruppen behalten eigene Größen und Höhen.

**Prüfstand:** Build/Reobf erfolgreich; **29/29 eigenständige Forge-Servertests** einschließlich tatsächlicher Chunk-Entladung/-Rückkehr und eines Neustart-Nachweises. **38/38 Tests mit NG Verkehr Beta2-dev6** einschließlich neun zusätzlicher Integrationsfälle bestanden: Erkennung geparkter Fahrzeuge, Stellplätze, Mitfahrerfilter, Ein-/Ausfahrtszählung sowie Schranken und Höhenprüfung. Der grundlegende Fahr-/Bedienungstest mit Dev5 auf MC02 ist vom Projektinhaber bestätigt. Vollständige Sicht-/Ausstattungsprüfung und zwei echte verbundene Clients bleiben offen.

**Bedienung:** Rechtsklick steigt ein. W/S fährt, A/D lenkt, Springen bremst und Schleichen steigt aus. **H** schaltet die Feststellbremse, **L** die Leuchten; die Tasten sind umbelegbar. Neu platzierte Fahrzeuge stehen mit angezogener Feststellbremse. Mit Farbstoff rechtsklicken ändert die Lackierung. Ein unbesetztes Fahrzeug kann mit Schleichen + Rechtsklick mit leerer Hand aufgenommen werden.

Die Leuchten haben helle Leuchtflächen; es gibt noch keine auf die Straße projizierten Lichtkegel. Die Modelle sind erste Spielmodelle und noch nicht visuell im Minecraft-Client freigegeben.

[Dev5 und bestätigter MC02-Spieltest](DEV5.md) · [Dev4 und Prüfstand](DEV4.md) · [Entwicklung](CONTRIBUTING.md) · [Dev3](DEV3.md) · [Dev2](DEV2.md) · [Dev1](DEV1.md)

## Drei Fahrzeuggruppen

| Fahrzeug | Aussehen | Einsatz |
|---|---|---|
| **NG City** | Kompakter türkiser Zweitürer | Stadtverkehr und normale Parkplätze; erstes Entwicklungsfahrzeug |
| **NG Family** | Längerer türkiser Familienkombi | Mehrere Mitfahrer und größere Stellplätze |
| **NG Transporter** | Hoher weißer Kastenwagen mit türkisem Streifen | Transportaufgaben und Tests von Höhenbegrenzungen |

Die zuvor besprochenen Konzeptbilder geben die Designrichtung vor. Die ersten Spielmodelle sind im aktuellen Dev4-Paket enthalten; eine Client-Sichtprüfung steht noch aus.

## Funktionen und weitere Planung

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

Das Repository ist **öffentlich**. Quellcode und Entwicklungsstände sind einsehbar. Dev5 bleibt eine Entwicklungsfassung mit bestätigter Grundbedienung und weiteren ausstehenden Spieltests; eine stabile Veröffentlichung steht noch aus.

[Fehler und Vorschläge](https://github.com/InDance2018/NG-Fahrzeuge/issues) · [NG Verkehr](https://github.com/InDance2018/NG-Verkehrs-System)

## Nutzung und Rechte

Eine allgemeine Nutzungslizenz wurde noch nicht vergeben. Dieses Repository erteilt keine allgemeine Erlaubnis zur Weitergabe oder Veränderung von Code, Modellen, Texturen und Konzeptbildern. Bedingungen für spätere Downloads werden gesondert festgelegt.

---
**NeverGate · [InDance2018](https://github.com/InDance2018)**
