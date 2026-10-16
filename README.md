# NG-Fahrzeuge

**Aktueller Stand: 0.1.0-dev11 · 17. Oktober 2026 · Entwicklungsfassung**

**Eigene Fahrzeuge für Minecraft – ein Projekt von NeverGate.**

NG-Fahrzeuge ist eine eigenständige Fahrzeugmod für **Minecraft Java 1.20.2 mit Forge**. Sieben Fahrzeuggruppen im Minecraft-Stil nutzen eine gemeinsame technische Grundlage.

**Projektstart: 16. Oktober 2026.** Dev10 ergänzt Fahrzeugkamera, Gang-/Fahrtrichtungsanzeige, Rückfahrwarner, Handbremshebel, Motorhaube und Motorraum, Heckausbau, Dachstauraum, Anhängervarianten und -beleuchtung, Warnbalken, Seilwinde, Reifenschäden/Radwechsel, Verschmutzung/Waschplatz sowie Fahrzeugverkauf. Die Euro-Währung bleibt im NG-Wirtschaftssystem. Entwicklungsfassung; eine stabile Veröffentlichung steht noch aus.

## Aktueller Entwicklungsstand

**Auf MC02 freigegeben: Dev11 am 17. Oktober 2026.** Die vorherige Fahrzeugversion ist deaktiviert. Für MC02 benötigt auch der Client Dev11.

**Dev11 – 20 neue Baupunkte:** Stadtbus mit 13 Sitzen, getrennte Türen, Haltewunsch und Linienanzeigen; Motorrad mit Sozius, Ständer und Schräglage; Pickup mit Ladefläche und Heckklappe; sichtbare Ladung, Blinkerhebel/Rückstellung, Fahrersitz/Gurte, Karosserieschäden, Lackierstation, Lichtstufen/Nebellicht und eigene Fahrzeugübersicht mit Standortortung. **100/100 Forge-Servertests, echter Neustart aller sieben Fahrzeugtypen samt Dev11-Einstellungen und Fahrzeugübersicht sowie Client-Prüflauf bestanden.** Client und Server benötigen Dev11 (Netzwerkprotokoll 7). [Alle 20 Bauten, Bedienung und Prüfstand](DEV11.md).

**Dev10 – 15 neue Baupunkte:** Kamera, D/N/R-Anzeige, Rückfahrwarner, bewegter Handbremshebel, Motorhaube/Motorraum, Heckausbau, Dachgepäckträger mit 18 Plätzen, Kasten-/Planenanhänger, Anhänger-Rückfahrlicht, gelber Warnbalken, Seilwinde, Reifen, Radwechsel, Schmutz/Waschplatz und Händlerverkauf. **80/80 Forge-Servertests bestanden; Kamera, Verkauf und Dachinventar zusätzlich im echten Client geprüft.** Client und Server benötigen Dev10 (Netzwerkprotokoll 6). [Bedienung, Rezepte und Prüfstand](DEV10.md).

**Dev9 – zwölf neue Baupunkte:** Kennzeichen vorn/hinten, Händler mit Item-Bezahlung, Garage, Abschleppseil, Abschleppwagen mit Ladefläche/Rampe, Scheibenwischer, elektrische Fenster, Innenlicht, Batterie, Starthilfekabel, Werkstatt-Hebebühne und Family-/Transporter-Varianten. **67/67 Forge-Servertests einschließlich echtem Garagen-Festplattenlauf bestanden; Händler, Garage und Kennzeichenverwaltung im echten Client geprüft.** Client und Server benötigen Dev9 (Netzwerkprotokoll 5). [Bedienung, Rezepte und Prüfstand](DEV9.md).

**Dev8 – zwölf neue Baupunkte:** Türen und Heck animiert, eigenes HUD, Gesamt-/Tageskilometer, Tank und Verbrauch, Zapfsäule, Karosserie-/Motorschäden, Reparatur, Untergrundhaftung, Tempomat, Kupplung, Lastenanhänger mit 27 Plätzen und Fahrzeugverwaltung mit Besitzer-/Schlüsselwechsel. **56/56 Forge-Servertests bestanden; echter Client-Gespannlauf mit Tempomat, Tank und Kilometerzähler sowie Verwaltung geprüft.** Client und Server benötigen Dev8 (Netzwerkprotokoll 4). [Bedienung, Rezepte und Prüfstand](DEV8.md).

**Dev7 – acht neue Erweiterungen:** Motor starten/abschalten, eigene Motor-/Fahrklänge, Blinker und Warnblinker, Hupe, Spiegelgestaltung und dritte Bremsleuchte, Laderaum (9/27/54 Plätze), Besitzer/Schlüssel/Abschließen sowie Fahrzeugwerkbank mit Überlebensrezepten. **41/41 Forge-Servertests bestanden; Werkbankbau sowie Motorstart/Fahren zusätzlich über den echten Minecraft-Client geprüft.** Client und Server benötigen beide Dev7 (Netzwerkprotokoll 3). MC02 wurde inzwischen auf Dev8 aktualisiert. [Bedienung, Rezepte und Prüfstand](DEV7.md).

**Dev6 – Optik nach der Projektzentrale:** City und Family in Mint, Family mit Dachreling, weißer Transporter mit mintfarbenem Streifen. Neue Karosserien, Scheiben, Innenräume, Räder, NG-Embleme und Inventarmodelle. Alle drei Modelle wurden im echten Minecraft-Client von vorn und hinten geprüft. **Build/Reobf und 30/30 Forge-Servertests bestanden.** Dev6 wurde nicht separat auf MC02 installiert; der aktuelle Serverstand ist Dev8. [Modelle und Prüfstand](DEV6.md).

**Dev5 – Fahrer-Erkennung auf Arclight korrigiert:** Die Fahrerzuordnung wird zusätzlich aus den tatsächlichen Mitfahrern abgeglichen. Arclight umgeht die mod-eigenen Ein-/Ausstiegsmethoden, wodurch Dev4 dort ohne erkannten Fahrer, Fahranzeige und Steuerung bleiben konnte. **30/30 Forge-Servertests bestanden**, einschließlich einer neuen Regression für fehlende Einstiegsmeldungen. Dev5 war auf MC02 installiert und wurde inzwischen durch Dev8 ersetzt. **Vom Projektinhaber bestätigt: Einsteigen, Fahren, Lenken, km/h-Anzeige und Aussteigen funktionieren.** Weitere Prüfungen bleiben offen. [Korrektur und Prüfstand](DEV5.md).

**Dev4 – drei Fahrzeuggruppen mit Ausstattung:** NG City (1 Sitz), NG Family (4 Sitze) und NG Transporter (2 Sitze) sind als erste Spielmodelle umgesetzt. Alle nutzen die serverseitige Fahrsteuerung, eine separat schaltbare Feststellbremse, Farbstoff-Lackierung sowie schaltbare Leuchtflächen mit Brems- und Rückfahrlicht. Die Fahrzeuggruppen behalten eigene Größen und Höhen.

**Historischer Prüfstand Dev4:** Build/Reobf erfolgreich; **29/29 eigenständige Forge-Servertests** einschließlich tatsächlicher Chunk-Entladung/-Rückkehr und eines Neustart-Nachweises. **38/38 Tests mit NG Verkehr Beta2-dev6** einschließlich neun zusätzlicher Integrationsfälle bestanden: Erkennung geparkter Fahrzeuge, Stellplätze, Mitfahrerfilter, Ein-/Ausfahrtszählung sowie Schranken und Höhenprüfung. Der grundlegende Fahr-/Bedienungstest mit Dev5 auf MC02 ist vom Projektinhaber bestätigt. Vollständige Sicht-/Ausstattungsprüfung und zwei echte verbundene Clients bleiben offen.

**Bedienung ab Dev11:** Rechtsklick steigt ein. **I startet den Motor; H löst die Feststellbremse.** W/S fährt, A/D lenkt, Springen bremst, Schleichen steigt aus. L: Licht, Komma/Punkt: Blinker, R: Warnblinker, G: Hupe, B: Laderaum, O/U: Türen/Heck, C: Tempomat, J: Anhänger, K: Wischer, V: Fenster, N: Innenlicht, M: Motorhaube, Y: Warnbalken, Z: Dachstauraum, F6: Fahrzeugkamera, F7: Gurt, F8: Seitenständer, F9: Haltewunsch, F10: Fernlicht, F12: Nebelscheinwerfer. Die neuen Tasten sind bereits vorbelegt. L durchläuft Aus/Stand-/Abblendlicht. Das neue Übersichts-Item zeigt eigene Fahrzeuge und startet deren Standortortung. Fahrzeugpapiere öffnen im Stand die Verwaltung. Alle Tasten sind unter Optionen → Steuerung → NG Fahrzeuge umbelegbar. Neu platzierte Fahrzeuge stehen mit Motor AUS und Feststellbremse AN. Besitzer und Schlüsselinhaber können lackieren und auf den Laderaum zugreifen; nur der Besitzer kann ein unbesetztes Fahrzeug mit Schleichen + Rechtsklick mit leerer Hand aufnehmen. [Schlüssel, Laderaum und Baukosten](DEV7.md).

Die Leuchten haben helle Leuchtflächen; es gibt noch keine auf die Straße projizierten Lichtkegel. Die Dev6-Modelle wurden im Minecraft-Client ohne Shader visuell geprüft; Konzeptbeleuchtung und Umgebung können abweichen.

[Dev11: Bus, Motorrad, Pickup und 20 Bauten](DEV11.md) · [Dev10: Kamera, Ausstattung und Werkstatt](DEV10.md) · [Dev9: Garage, Pannenhilfe und Komfort](DEV9.md) · [Dev8: Fahrzeugsysteme und Anhänger](DEV8.md) · [Dev7: neue Funktionen und Bedienung](DEV7.md) · [Dev6: neue Fahrzeugoptik](DEV6.md) · [Dev5 und bestätigter MC02-Spieltest](DEV5.md) · [Dev4 und Prüfstand](DEV4.md) · [Entwicklung](CONTRIBUTING.md) · [Dev3](DEV3.md) · [Dev2](DEV2.md) · [Dev1](DEV1.md)

## Sieben Fahrzeuggruppen

| Fahrzeug | Aussehen | Einsatz |
|---|---|---|
| **NG City** | Kompakter türkiser Zweitürer | Stadtverkehr und normale Parkplätze; erstes Entwicklungsfahrzeug |
| **NG Family** | Längerer türkiser Familienkombi | Mehrere Mitfahrer und größere Stellplätze |
| **NG Transporter** | Hoher weißer Kastenwagen mit türkisem Streifen; auch mit Fenstern | Transportaufgaben und Tests von Höhenbegrenzungen |
| **NG Abschleppwagen** | Gelber Zweitsitzer mit Ladefläche und Rampe | Fahrzeuge bergen und transportieren |
| **NG Stadtbus** | Roter Bus mit zwei Türen und drei Linienanzeigen | Fahrer und zwölf Fahrgäste |
| **NG Motorrad** | Schmales schwarzes Zweirad mit Seitenständer | Fahrer und Sozius |
| **NG Pickup** | Blauer Zweitsitzer mit offener Ladefläche | 27 Frachtplätze und klappbare Heckklappe |

Dev6 setzt die Originalvorlagen der Projektzentrale als detaillierte Minecraft-Modelle um. Sechs Aufnahmen aus dem echten Client dokumentieren die Sichtprüfung im Downloadpaket.

## Funktionen und weitere Planung

- Vorwärtsfahren, Lenken, Rückwärtsfahren, Bremsen und Feststellbremse.
- Einsteigen, Aussteigen und je nach Fahrzeug mehrere Sitzplätze.
- Beleuchtung und auswählbare Fahrzeugfarben.
- Abgestellte Fahrzeuge bleiben über Welt- und Serverneustarts erhalten.
- Zum Modell passende Fahrzeuggrößen und Kollisionen.
- Einzelspieler und Mehrspieler mit serverseitiger Prüfung der Steuerung.

Laderaum, Schlüssel und Fahrzeugherstellung sind seit Dev7 umgesetzt; Dev8 ergänzt Tankstelle, Reparatur und Lastenanhänger. Autonomer Verkehr gehört nicht zum aktuellen Entwicklungsumfang.

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

Das Repository ist **öffentlich**. Quellcode und Entwicklungsstände sind einsehbar. Dev11 bleibt eine Entwicklungsfassung mit erweitertem Fahrzeugsystem und weiteren ausstehenden Spieltests; eine stabile Veröffentlichung steht noch aus.

[Fehler und Vorschläge](https://github.com/InDance2018/NG-Fahrzeuge/issues) · [NG Verkehr](https://github.com/InDance2018/NG-Verkehrs-System)

## Nutzung und Rechte

Eine allgemeine Nutzungslizenz wurde noch nicht vergeben. Dieses Repository erteilt keine allgemeine Erlaubnis zur Weitergabe oder Veränderung von Code, Modellen, Texturen und Konzeptbildern. Bedingungen für spätere Downloads werden gesondert festgelegt.

---
**NeverGate · [InDance2018](https://github.com/InDance2018)**
