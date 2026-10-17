# NG Fahrzeuge · Dev9

**Testnachtrag vom 17.10.2026:** Der Projektinhaber bestätigt alle bisherigen DEV-Stände auf MC02 sowie den bestandenen Drei-Spieler-/Chunk-/Neustarttest. [Aktueller Testbericht](PRAXISTEST-2026-10-17.md). Ältere Testangaben im folgenden Dokument beschreiben den damaligen Prüfstand.


Entwicklungsfassung für Minecraft Java 1.20.2 / Forge 48.1.0. Dev9 umfasst ausschließlich die zwölf angekündigten Fahrzeugpunkte. Eine Euro-Währung oder Parkticket-Bezahlung gehört nicht zu diesem Paket.

## Die zwölf Baupunkte

| Nr. | Funktion | Bedienung und Umfang |
|---|---|---|
| 1 | Kennzeichen | Fahrzeugpapiere öffnen die Verwaltung. Bis zu zwölf Buchstaben, Ziffern, Leerzeichen und Bindestriche eingeben; das Kennzeichen erscheint vorn und hinten. |
| 2 | Fahrzeughändler | Händlerblock rechtsklicken. Vier Fahrzeuggruppen mit Inventarmodell-Vorschau und Preis; ein Klick kauft das Fahrzeug als Item. Standardpreise: 24/40/56/96 Smaragde. |
| 3 | Garage | Garagenblock rechtsklicken. Bis zu neun eigene Fahrzeuge pro Spieler serverweit einlagern/ausparken. UUID, Schlüsselbindung, Lackierung, Laderaum und Fahrzeugzustand bleiben erhalten. |
| 4 | Abschleppseil | Mit dem Seil zuerst Zugwagen, dann Pannenfahrzeug rechtsklicken; beide müssen stehen, Motor aus und Feststellbremse an. Schleichen + Rechtsklick löst die Verbindung. |
| 5 | Abschleppwagen | Neue vierte Fahrzeuggruppe mit zwei Sitzen, Ladefläche und Rampe. Fernbedienung + Schleichen/Rechtsklick schaltet die Rampe. Normaler Rechtsklick auf den Abschlepper lädt das nächste berechtigte Fahrzeug; ein weiterer lädt hinter ihm ab. |
| 6 | Scheibenwischer | K schaltet sichtbar animierte Wischer an/aus. |
| 7 | Elektrische Fenster | V fährt die seitlichen Fahrerhausfenster sichtbar herunter/hoch. Benötigt Batterieladung. |
| 8 | Innenlicht | N schaltet die Innenleuchte; geöffnete Türen schalten sie automatisch ein. Die Leuchtfläche ist emissiv, ohne dynamische Beleuchtung der Umgebung. |
| 9 | Batterie | Ladezustand im HUD. Motorstart benötigt mindestens 5 % und verbraucht 2 Prozentpunkte. Laufender Motor lädt; abgestelltes Licht, Innenlicht und Wischer verbrauchen Ladung. |
| 10 | Starthilfekabel | Zuerst Spender, dann Empfänger rechtsklicken, höchstens acht Blöcke auseinander. 10 Prozentpunkte werden übertragen, wenn der Spender mindestens 20 % und der Empfänger unter 20 % hat. Beide Fahrzeuge müssen zugänglich sein. |
| 11 | Werkstatt-Hebebühne | Block neben einem berechtigten abgestellten Fahrzeug rechtsklicken: Fahrzeug wird auf die Bühne gesetzt und angehoben. Mit Reparaturschlüssel und Ersatzteilen reparieren, mit anderem Gegenstand absenken. Bei blockiertem Absenkplatz bleibt es oben. |
| 12 | Varianten | NG Family mit Dachbox sowie NG Transporter mit seitlicher Laderaumverglasung. Eigene Items und Herstellungsrezepte; Variante bleibt beim Aufnehmen/Einlagern gespeichert. |

Neue Komfortfunktionen sind zusätzlich als Schaltflächen in der Fahrzeugverwaltung erreichbar. Alle Tasten sind unter **Optionen → Steuerung → NG Fahrzeuge** änderbar. Grundbedienung: **I Motor, H Feststellbremse, W/S Fahren, A/D Lenken, Leertaste Bremse, Schleichen Aussteigen**.

## Bedingungen

Händler und Garage sind bedienbare Blöcke. Zum Ausparken muss der Bereich vor der Vorderseite des Garagenblocks frei und geladen sein. Ein fehlgeschlagenes Ausparken behält den Datensatz. Nur eigene Fahrzeuge können eingelagert werden. Insassen, offene Laderäume, Anhänger und Abschleppverbindungen verhindern das Einlagern. Besitzer-/Schlüsselwechsel sind während einer Bergungsverbindung gesperrt.

Seil und Ladefläche verlangen Zugang zu beiden Fahrzeugen. Ein besetztes Pannenfahrzeug wird nicht aufgenommen. Während des Schleppens oder auf der Bühne ist sein eigener Motor gesperrt. Bergungsverbindungen und Bühnenposition werden gespeichert; das System lädt keine entfernten Chunks. Beim Abbau einer Bühne wird ein geladenes Fahrzeug freigegeben; ein später geladenes Fahrzeug erkennt die fehlende Bühne ebenfalls.

Das Werkstattfahrzeug muss beim Hochsetzen kollisionsfrei auf die Bühne passen. Ein blockierter Abladeplatz verhindert das Abladen vom Abschlepper. Große Fahrzeuge können über die Ladefläche hinausragen; die Ladefunktion prüft den freien Platz.

## Herstellung und Konfiguration

Alle neuen Gegenstände und Blöcke stehen im Kreativreiter **NG Fahrzeuge**. Der Abschleppwagen ist zusätzlich in der Fahrzeugwerkbank als vierte Auswahl baubar.

Rezepte an der normalen Werkbank (Zeilen von oben nach unten; `.` = leer):

| Gegenstand | Muster | Zutaten |
|---|---|---|
| Abschleppseil | `S.S / .S. / S.S` | S = Faden |
| Starthilfekabel | `IRI / .C.` | I = Eisenbarren, R = Redstone, C = Kupferbarren |
| Fernbedienung | `.R. / ICI / .I.` | I = Eisenbarren, R = Redstone, C = Kupferbarren |
| Fahrzeughändler | `GGG / IRI / III` | G = Glas, I = Eisenbarren, R = Smaragd |
| Garage | `III / ICI / III` | I = Eisenbarren, C = Truhe |
| Hebebühne | `I.I / IPI / IRI` | I = Eisenbarren, P = Kolben, R = Redstone |
| Family mit Dachbox | `III / GEG / WRW` | I = Eisenblock, G = Glas, E = Truhe, W = schwarze Wolle, R = Redstoneblock |
| Fenster-Transporter | `III / GEG / WRW` | I = Eisenblock, G/E = Glas, W = schwarze Wolle, R = Redstoneblock |

Der Händler verwendet die Forge-Serverkonfiguration `ng_fahrzeuge-server.toml` im jeweiligen Weltordner `serverconfig`. Unter `dealer` sind `payment_item` sowie `ng_city_price`, `ng_family_price`, `ng_transporter_price` und `ng_tow_truck_price` einstellbar. Standard: `minecraft:emerald` und 24/40/56/96. Ungültige Item-IDs fallen auf Smaragde zurück. Dies ist eine Item-Bezahlung, kein neues Geldsystem.

## Installation und Prüfstand

Client und Server benötigen beide **Dev9, Netzwerkprotokoll 5**. Dev9 wurde am 16. Oktober 2026 auf MC02 installiert und aktiviert; der Serverstart mit allen vier Welten wurde bestätigt. Ein neuer Spieltest des Projektinhabers mit Dev9 wurde noch nicht bestätigt.

**Build/Reobf erfolgreich; 67/67 erforderliche Forge-GameTests bestanden.** Elf neue Dev9-Prüfungen decken Komfort/Batterie/Kennzeichen, sichere Händlerkäufe, Garagenidentität und blockierte Ausfahrten, einen echten Festplatten-Speicher-/Ladelauf, Seil/Ladefläche, Hebebühne und Variantenplatzierung ab.

Im echten Minecraft-Client wurden Kennzeichen vorn/hinten, Wischer/Fenster, Dachbox, Fenster-Transporter, beladener Abschlepper mit abgesenkter Rampe, mittig angehobenes Fahrzeug und die Bedienfenster geprüft. Händlerkauf, Einlagern/Ausparken und Kennzeichenänderung wurden über echte Client-/Server-Pakete bestätigt. Acht Aufnahmen liegen dem Paket bei.

Prüfbericht: `docs/test-results/dev9.json`. JAR-SHA256: `8f887e4070d3ce675bb18fc80152422b859341edc44a77a035315e1dcfdc50fe`. Ein Test mit zwei echten verbundenen Clients, ein MC02-Spieltest mit Dev9 und eine erneute NG-Verkehr-Integration für den Abschlepper bleiben offen.

Die separate Prüfszene läuft mit `gradle runClient -PvehicleVisualTest -PvehicleVisualDev9` in einer entbehrlichen vorbereiteten Welt `run/saves/visual-dev9`. Sie verändert diese Prüfwelt und endet automatisch. Prüfklassen sind nicht in der Mod-JAR enthalten.
