# Dev12 – Gelände, Sportwagen, Taxi und Sattelzug

**0.1.0-dev12 · Minecraft 1.20.2 · Forge 48.1.0 · Java 17**

Entwicklungsfassung mit den 20 freigegebenen Bauten. Client und Server benötigen beide Dev12 (Netzwerkprotokoll 8). MC02 bleibt auf dem bereits freigegebenen Dev11; Dev12 wurde dort nicht installiert.

## Die 20 Bauten

1. **NG Geländewagen:** eigenes Modell, vier Sitze, höhere Bodenfreiheit.
2. **Allrad:** zuschaltbar, verbessert den Antrieb auf losem Untergrund.
3. **Untersetzung:** nur mit Allrad; mehr Zugkraft bei niedriger Höchstgeschwindigkeit.
4. **Berganfahrhilfe:** hält nach dem Bremsen bis zu zwei Sekunden, bevor das Fahrzeug wieder rollen kann.
5. **NG Sportwagen:** eigenes flaches Modell, zwei Sitze, stärkerer Antrieb.
6. **Mehrganggetriebe:** Automatik oder Handschaltung mit sechs Vorwärtsgängen, Neutral und Rückwärtsgang; Gangwechsel verändern die Fahrleistung.
7. **Drehzahlmesser:** Gang, Schaltmodus und Umdrehungen im Fahr-HUD.
8. **NG Taxi:** eigenes gelbes Modell, vier Sitze und Dachzeichen.
9. **Taxischild:** frei bei leerem Fahrzeug oder nur Fahrer, besetzt mit Fahrgästen; separat abschaltbar.
10. **Einzeltüren:** vorn links/rechts sowie hinten bei Viertürern. Bus behält seine getrennten Bustüren; Motorrad hat keine Türen.
11. **NG Sattelzugmaschine:** eigene Kabine, zwei Sitze, sechs Räder und Sattelplatte.
12. **Kofferauflieger:** 108 echte Frachtplätze auf zwei Seiten mit je 54 Plätzen.
13. **Sattelkupplung:** Königszapfen, sichtbarer Riegel und nachlaufende Verbindung mit begrenztem Knickwinkel.
14. **Aufliegerstützen:** mit der Kurbel aus- und einfahrbar; ausgefahrene Stützen sperren das Losfahren.
15. **Fernschlüssel:** vorhandener gültiger Fahrzeugschlüssel schließt aus bis zu 32 Blöcken Entfernung auf oder zu.
16. **Separates Frachtschloss:** Kofferraum und Heckzugang unabhängig von der Fahrzeugverriegelung sichern.
17. **Unfallwarnblinker:** schwerer Aufprall schaltet automatisch die Warnblinkanlage ein.
18. **Kühlsystem:** Kühlerzustand, Kühlmittelverlust, Motortemperatur, Leistungsbegrenzung und Motorabschaltung bei Überhitzung.
19. **Kühlerwartung:** Kühlmittelkanister und Reparatur mit den vorhandenen Ersatzteilen/Werkzeugen.
20. **Diagnosegerät:** Werkstattansicht mit Karosserie-, Motor-, Kühler- und Reifenschäden sowie benötigten Teilen und Werkzeugen.

## Bedienung

**Einsteigen → I: Motor an → H: Feststellbremse lösen → W/S und A/D fahren/lenken.** Springen bremst, Schleichen steigt aus. Neue Fahrzeuge starten mit Motor aus und Feststellbremse an. Alle Tasten sind vorbelegt und unter Optionen → Steuerung → NG Fahrzeuge umbelegbar.

| Neue Funktion | Vorbelegte Taste |
|---|---|
| Allrad / Untersetzung / Berganfahrhilfe | Nummernblock 4 / 5 / 6 |
| Automatik/Handschaltung | Nummernblock 7 |
| Hoch-/Herunterschalten | Nummernblock + / − |
| Taxischild / Frachtschloss | Nummernblock 8 / 9 |
| Tür vorn links / rechts | Nummernblock 0 / 1 |
| Tür hinten links / rechts | Nummernblock 2 / 3 |

**Ohne Nummernblock:** Fahrzeugpapiere öffnen die Verwaltung. Im Reiter **Technik** stehen dieselben Funktionen als Schaltflächen bereit. Allrad, Untersetzung und Getriebemodus im Stillstand mit Feststellbremse wechseln. Vorwärts/Rückwärts über Neutral nur bei annähernd stehendem Fahrzeug wechseln. Beim manuellen Fahren rechtzeitig hochschalten; ein zu niedriger Gang begrenzt die Geschwindigkeit. Berganfahrhilfe ist standardmäßig eingeschaltet.

Türen und Frachtschloss im sicheren Stand mit ausgeschaltetem Motor bedienen. Bus verwendet weiterhin seine vorhandenen Türfunktionen. Zum Fahren offene Türen schließen.

**Fernschlüssel:** vorhandenen Schlüssel wie bisher mit dem Fahrzeug verbinden. Danach Rechtsklick in die Luft innerhalb 32 Blöcken; Schleichen + Rechtsklick schaltet stattdessen das Frachtschloss. Das Fahrzeug muss geladen sein. Widerrufene Schlüssel funktionieren auch per Funk nicht. Mehrdeutig passende Schlüssel schalten kein Fahrzeug.

## Sattelzug und Fracht

Auflieger mit ausgefahrenen Stützen hinter die Zugmaschine stellen, Königszapfen über die Sattelplatte bringen und im sicheren Stand **J** drücken. Danach mit der **Aufliegerkurbel** rechtsklicken und die Stützen einfahren. Erst dann Motor starten und losfahren. Andere Fahrzeuge können nur die bisherigen Anhänger ziehen; die Zugmaschine koppelt den neuen Sattelauflieger.

Zum Abstellen: Motor aus, Feststellbremse an, Stützen ausfahren und mit J abkuppeln. Auch die bisherige Papierbedienung beachtet die Stützen. Während jemand auf die Fracht zugreift, bleiben Stützen und Abkuppeln gesperrt. Der Auflieger folgt dem Königszapfen; bei Hindernissen wird die Zugmaschine angehalten. Enge Rangiermanöver benötigen Platz.

Rechtsklick öffnet die Fracht. Die Seiten-Schaltfläche wechselt zwischen Plätzen 1–54 und 55–108; beide Seiten greifen auf dasselbe gespeicherte Inventar zu. Nur berechtigte Spieler in Reichweite erhalten Zugriff. Ein abgekuppelter Auflieger lässt sich mit Schleichen + leerer Hand aufnehmen; Fracht und Stützenzustand bleiben erhalten.

## Kühlung und Werkstatt

Der Kühlkreislauf fasst **5 Liter**, ein neuer Kanister **4 Liter**. Mit geöffneter Motorhaube, ausgeschaltetem Motor, Feststellbremse und Temperatur unter **60 °C** den Kanister am Fahrzeug benutzen. Pro Klick werden höchstens 1 Liter und nur die tatsächlich fehlende Menge entnommen; Restmenge steht am Item.

Ein beschädigter Kühler verliert bei laufendem Motor Kühlmittel. Über **105 °C** sinkt die Leistung, über **115 °C** nimmt der Motor Schaden, ab **130 °C** schaltet er ab. Abkühlen lassen und Schaden beheben. Das Diagnosegerät per Rechtsklick am abgestellten Fahrzeug öffnen. Die vorhandene Reparaturfunktion bessert auch den Kühler aus; Werkzeug und Ersatzteile bleiben dieselben. Reifen benötigen weiterhin das Reifen-/Werkzeugsystem aus Dev10.

## Herstellung

Die Fahrzeugwerkbank bietet jetzt alle **elf Fahrzeugtypen**. Neue Baukosten im Überleben:

| Fahrzeug | Eisenbarren | Kupferbarren | Glas | Schwarze Wolle | Redstone |
|---|---:|---:|---:|---:|---:|
| Geländewagen | 108 | 36 | 20 | 4 | 18 |
| Sportwagen | 120 | 40 | 22 | 4 | 20 |
| Taxi | 132 | 44 | 24 | 4 | 22 |
| Sattelzugmaschine | 144 | 48 | 26 | 4 | 24 |

Werkbankrezepte, jeweils drei Reihen von oben nach unten:

- **Kofferauflieger:** `III / CGC / WWW`; I = Eisenblock, C = Truhe, G = Glas, W = schwarze Wolle.
- **Kurbel:** ` II /  I /  I` (Drei-Spalten-Raster: oben Mitte/Rechts, darunter jeweils Mitte); vier Eisenbarren.
- **Kühlmittelkanister:** `IBI / IWI /  I `; I = Eisenbarren, B = blauer Farbstoff, W = Wassereimer.
- **Diagnosegerät:** `IRI / GCG / III`; I = Eisenbarren, R = Redstone, G = Glas, C = Komparator.

Händler und Werkbank bieten die vier neuen Fahrzeuge; es wird keine neue Geldwährung eingeführt.

## Prüfstand und Grenzen

**129/129 Servertests bestanden**, darunter 29 neue Dev12-Fälle. Separater Schreib-/Lese-Neustart mit elf Typen und beladenem Sattelzug bestanden. Echter Client: Handschaltung/Fahren, Allrad/Untersetzung, Fernschlüssel, Werkbankbau und synchronisierte zweite Frachtseite bestanden; zwölf Spielaufnahmen visuell geprüft.

Der genaue Build-, Neustart- und Client-Prüfstand steht in [docs/test-results/dev12.json](docs/test-results/dev12.json). Die Testszenen und GameTests sind vom Produktions-JAR getrennt.

Dev12 bleibt eine Entwicklungsfassung. Ein vollständiger Spieltest mit zwei echten verbundenen Clients steht aus. Die neue Fahrzeugversion und die größeren Sattelzug-Abmessungen benötigen einen eigenen gemeinsamen Prüfstand mit NG Verkehr; bestehende Nachweise älterer Versionspaare gelten dafür nicht automatisch. Die bisherige öffentliche Snapshot-/Tank-/Wasch-Schnittstelle bleibt erhalten. Ein erfolgreicher Einzelbuild bestätigt keine neue gemeinsame Versionsfreigabe.
