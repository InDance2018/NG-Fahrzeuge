# NG Fahrzeuge · Dev10

15 Fahrzeug-Baupunkte auf Grundlage von Dev9. Minecraft Java 1.20.2, Forge 48.1.0, Java 17. Die Ankündigung wurde am 16. Oktober 2026 zuerst auf GitHub veröffentlicht (`b3863ea`). Euro-Währung bleibt im separaten NG-Wirtschaftssystem.

## Funktionen und Bedienung

| Nr. | Baupunkt | Umsetzung |
|---|---|---|
| 1 | Fahrzeugkamera | **F6** wechselt am Fahrzeug zwischen normaler Ansicht und einer mitfahrenden Außenkamera. Die Außenkamera prüft feste Blöcke zwischen Fahrzeug und Kameraposition. Beim Aussteigen wird die vorherige Ansicht wiederhergestellt. |
| 2 | Gang/Fahrtrichtung | HUD zeigt **D**, **N** oder **R** nach tatsächlicher Bewegung. |
| 3 | Rückfahrwarner | Eigener Piepton am Transporter und Abschleppwagen bei laufendem Motor und tatsächlicher Rückwärtsfahrt. |
| 4 | Handbremshebel | Im Innenraum sichtbar bewegter Hebel; folgt der Feststellbremse. |
| 5 | Motorhaube | **M** oder Verwaltung: Haube im gesicherten Stand öffnen/schließen; darunter sichtbarer Motorblock. Offene Haube sperrt den Motorstart. |
| 6 | Kofferraumgestaltung | Zusätzlicher Ladeboden und Innenverkleidung im Heck der Personenwagen und des Transporters. |
| 7 | Dachgepäckträger | Träger mit Rechtsklick montieren. **Z** öffnet 18 zusätzliche Plätze. Mit einem Träger in der Hand und Schleichen/Rechtsklick leer abbauen. Auf Abschlepper und Family mit Dachbox nicht montierbar. |
| 8 | Anhängervarianten | Kleiner geschlossener Kastenanhänger mit 27 und großer Planenanhänger mit 54 Plätzen. Eigene Items und Rezepte; aufnehmen/platzieren erhält Variante und Inhalt. |
| 9 | Anhängerbeleuchtung | Rücklicht, Bremslicht und Blinker folgen dem Zugwagen; zusätzliche weiße Rückfahrleuchten. Abkuppeln setzt die Signale zurück. |
| 10 | Gelber Warnbalken | **Y** oder Verwaltung schaltet den Warnbalken des Abschleppwagens. Zwei abwechselnd leuchtende gelbe Segmente; benötigt Batterieladung. |
| 11 | Elektrische Seilwinde | Rampe mit Fernbedienung + Schleichen/Rechtsklick öffnen. Normaler Rechtsklick am Abschlepper startet das schrittweise Hochziehen des nächsten berechtigten unbesetzten Fahrzeugs. Wieder rechtsklicken lädt ab, wenn dahinter Platz ist. |
| 12 | Reifenschäden | Vier gespeicherte Reifenzustände. Verschleiß durch Strecke, stärkere Beschädigung durch Kollisionen/Schaden. Stark beschädigte Reifen werden sichtbar flacher und begrenzen Geschwindigkeit sowie Lenkung. |
| 13 | Radwechsel | Wagenheber am unbesetzten, gesicherten Fahrzeug rechtsklicken. Ersatzrad in die Hand, Reparaturschlüssel ins Inventar; Rechtsklick ersetzt den schlechtesten Reifen und verbraucht das Ersatzrad. Wagenheber erneut rechtsklicken, um wieder fahren zu können. |
| 14 | Schmutz und Waschplatz | Fahrt erzeugt sichtbare Schmutzflecken, auf losem Untergrund schneller. Mit Wassereimer den Waschplatz neben einem berechtigten abgestellten Fahrzeug bedienen: Fahrzeug sauber, leerer Eimer zurück. |
| 15 | Fahrzeugverkauf | Beim Händler **Verkauf** neben dem Fahrzeugtyp anklicken. Eigenes Fahrzeug daneben parken und vollständig leeren. Tooltip zeigt den Rücknahmewert anhand von Karosserie, Motor und schlechtestem Reifen. |

Alle neuen Tasten sind vorbelegt und unter **Optionen → Steuerung → NG Fahrzeuge** änderbar. Grundbedienung bleibt: I Motor, H Feststellbremse, W/S Fahren, A/D Lenken, Leertaste Bremse, Schleichen Aussteigen.

## Sicherung und Speicherung

Dachinventar, Reifen, Schmutz und Ausstattung bleiben beim Speichern, Aufnehmen und Einlagern erhalten. Offene Dachinventare verhindern Aufnahme, Einlagerung und Besitzerwechsel. Ein nicht leerer Dachträger kann nicht abgebaut werden. Wagenheber, Haube und aktive Seilwinde verhindern den Motorstart. Die Seilwinde bewegt pro Tick höchstens 0,12 Blöcke, benötigt Batterieladung und pausiert bei blockiertem Weg oder geschlossener Rampe. Nach dem Hochziehen bleibt das Fahrzeug auf der Ladefläche gesichert.

Der Verkauf verlangt Besitzerrechte, leeres Fahrzeug und leeres Dachinventar, keine Insassen, keine offenen Inventare und keine Anhänger-/Bergungsverbindung. Die Auszahlung wird vollständig auf freien Inventarplatz geprüft. Erst dann wird das Fahrzeug einmalig entfernt und die Item-Auszahlung ausgeführt.

## Rezepte und Händlerkonfiguration

Neue Items stehen ebenfalls im Kreativreiter NG Fahrzeuge. Normale Werkbank, Reihen von oben nach unten, `.` bedeutet leer:

| Gegenstand | Muster | Zutaten |
|---|---|---|
| Dachgepäckträger | `III / I.I` | I = Eisengitter |
| Wagenheber | `.I. / IPI / .I.` | I = Eisenbarren, P = Kolben |
| Ersatzrad | `WWW / WIW / WWW` | W = schwarze Wolle, I = Eisenbarren |
| Waschplatz | `III / IBI / IRI` | I = Eisenbarren, B = Wassereimer, R = Redstone |
| Kastenanhänger | `III / ICI / W.W` | I = Eisenbarren, C = Truhe, W = schwarze Wolle |
| Planenanhänger | `WWW / ICI / B.B` | W = grüne Wolle, I = Eisenblock, C = Truhe, B = schwarze Wolle |

In `ng_fahrzeuge-server.toml`, Abschnitt `dealer`, ergänzt `sell_percent` den Rücknahmeprozentsatz (Standard 50, einstellbar 1–100). Er bezieht sich auf den konfigurierten Neupreis und wird mit dem Fahrzeugzustand gewichtet. Auszahlungsitem bleibt das eingestellte `payment_item`, standardmäßig Smaragde. Mindestrücknahme: ein Item. Dies ist kein Euro-Geldsystem.

## Prüfstand und Installation

Client und Server benötigen beide **Dev10, Netzwerkprotokoll 6**. MC02 wurde im vorherigen Schritt auf Dev9 aktualisiert; eine Installation von Dev10 gehört nicht zu diesem Bauauftrag.

**Build/Reobf erfolgreich und 80/80 erforderliche Forge-GameTests bestanden.** Die 13 neuen Tests prüfen Dachausstattung und beide Inventare, Schutz offener Dachinventare, Motorhaube/Startsperre, Reifen/Radwechsel, Wasserverbrauch am Waschplatz, zustandsabhängigen Verkauf samt Einmalzahlung, Eigentum/Inhalte/Verbindungen, Seilwindenbewegung und Startsperre, Anhänger-Kapazität und Speicherung, Beleuchtungssignale, Rechteprüfung, volles Inventar sowie Schutz privater Dachinhalte.

Im echten Minecraft-Client wurden die neuen Modelle und Bedienfenster anhand von acht Aufnahmen geprüft. Fahrzeugverkauf und Dachinventar wurden über das echte Menü bedient. F6-Umschaltung zur Außenkamera und zurück, Fahren mit aktiver Kamera sowie automatische Wiederherstellung beim Aussteigen sind bestätigt.

Prüfbericht: `docs/test-results/dev10.json`. JAR-SHA256: `c5f954e5125df3f4c83f92ea1bd8159ae129b35dccee06f512821caf9fe87b83`. Ein Test mit zwei echten verbundenen Clients, ein MC02-Spieltest mit Dev10 und eine erneute NG-Verkehr-Integration der neuen Anhänger bleiben offen. Der Rückfahrwarner ist als eigener Mono-OGG enthalten; die Prüfung nutzte OpenAL No Output, ohne menschliche Hörprobe.

Die separate Prüfszene läuft mit `gradle runClient -PvehicleVisualTest -PvehicleVisualDev10` in der entbehrlichen vorbereiteten Welt `run/saves/visual-dev10`. Sie verändert die Prüfwelt und endet automatisch. Prüfklassen sind nicht in der ausgelieferten JAR enthalten.
