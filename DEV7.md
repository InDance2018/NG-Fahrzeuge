# Dev7 – Motor, Ausstattung, Laderaum und Fahrzeugbau

**Testnachtrag vom 17.10.2026:** Der Projektinhaber bestätigt alle bisherigen DEV-Stände auf MC02 sowie den bestandenen Drei-Spieler-/Chunk-/Neustarttest. [Aktueller Testbericht](PRAXISTEST-2026-10-17.md). Ältere Testangaben im folgenden Dokument beschreiben den damaligen Prüfstand.


Minecraft 1.20.2 · Forge 48.1.0 · Java 17 · Entwicklungsfassung · 16. Oktober 2026

Die acht freigegebenen Erweiterungen bauen auf der Dev6-Optik auf.

1. **Motor:** explizit starten und abschalten. Ohne laufenden Motor fährt das Fahrzeug nicht an. Abschalten bremst ab; beim Aussteigen des Fahrers und beim Laden der Welt bleibt der Motor aus.
2. **Klänge:** eigene synthetische Start-, Leerlauf- und Fahrgeräusche. City, Family und Transporter haben unterschiedliche Motorgrundklänge; Tonhöhe und Lautstärke folgen der synchronisierten Geschwindigkeit.
3. **Blinker:** links, rechts und Warnblinker mit sichtbaren vorderen, seitlichen und hinteren Leuchtflächen und Klickgeräusch. Erneutes Betätigen schaltet aus; ein anderer Blinker ersetzt den bisherigen.
4. **Hupe:** räumlicher Klang für Spieler in der Nähe, beim Halten wiederholt; serverseitig begrenzte Wiederholrate.
5. **Spiegel und Bremslicht:** gestaltete Außenspiegel, zusätzlicher Innenspiegel und dritte Bremsleuchte bei City und Family. Spiegel sind Modellflächen, keine Livekameras.
6. **Laderaum:** City 9, Family 27, Transporter 54 Inventarplätze. Inhalte und Slotpositionen bleiben in der Welt und beim Aufnehmen als Fahrzeuggegenstand erhalten.
7. **Schlüssel:** Besitzer beim Platzieren; bei alten unzugeordneten Fahrzeugen übernimmt der erste regulär interagierende Spieler. Besitzer können mehrere leere Schlüssel binden und weitergeben. Ein passender Schlüssel erlaubt zusätzliche Fahrer und Laderaumzugriff; fremde Spieler können ein abgeschlossenes Auto nicht betreten. Nur der Besitzer kann es aufnehmen oder zusätzliche Schlüssel binden.
8. **Werkbank:** herstellbarer Fahrzeugwerkbank-Block mit Auswahl der drei Fahrzeuge, Baukostenanzeige und serverseitiger Materialentnahme. Alle Materialien und ein freier Inventarplatz werden vor dem Bauen geprüft.

## Bedienung – neu ab Dev7

**Nach dem Einsteigen zuerst I zum Motorstart und H zum Lösen der Feststellbremse drücken.** Sämtliche Tasten sind unter Optionen → Steuerung → NG Fahrzeuge registriert und umbelegbar. Konflikte mit anderen Mods dort auf eine freie Taste legen.

| Aktion | Standardtaste |
|---|---|
| Motor starten/abschalten | I |
| Feststellbremse | H |
| Fahren / Lenken | W/S / A/D |
| Betriebsbremse / Aussteigen | Springen / Schleichen |
| Licht | L |
| Blinker links / rechts | Komma / Punkt |
| Warnblinker | R |
| Hupe | G |
| Laderaum vom Fahrersitz | B |

Leeren Schlüssel auf das eigene Fahrzeug rechtsklicken: binden. Gebundenen Schlüssel rechtsklicken: auf-/abschließen (unbesetzt). Zum Einsteigen den Schlüssel im Inventar lassen und eine andere Handbelegung wählen. Schleichen + Rechtsklick mit passendem gebundenem Schlüssel öffnet den Laderaum auch von außen. Laderaumzugriff erfordert Feststellbremse und ausgeschalteten Motor.

Schleichen + Rechtsklick mit leerer Hand nimmt ein unbesetztes Fahrzeug weiterhin auf. Es darf dabei kein Laderaumfenster geöffnet sein. Das Fahrzeugitem bewahrt Besitzer, Schlüsselzuordnung und Laderauminhalt. Auch im Kreativmodus wird der aufgenommene Gegenstand zurückgegeben, damit Ladung erhalten bleibt.

## Überlebensrezepte

**Schlüssel:** 2 Eisennuggets nebeneinander; 1 Goldnugget über dem rechten Eisennugget. Weitere leere Schlüssel genauso herstellen und als Besitzer einzeln binden.

**Fahrzeugwerkbank:** obere Reihe 3 Eisenbarren; mittlere Reihe Redstone, Werkbank, Redstone; untere Reihe 3 Holzbretter. Rechtsklick öffnet die Fahrzeugauswahl. Material wird dem Spielerinventar entnommen; Kreativspieler können ohne Material bauen.

| Material | City | Family | Transporter |
|---|---:|---:|---:|
| Eisenbarren | 24 | 36 | 48 |
| Kupferbarren | 8 | 12 | 16 |
| Glas | 6 | 8 | 10 |
| Schwarze Wolle | 4 | 4 | 4 |
| Redstone | 4 | 6 | 8 |

## Installation und Prüfstand

**Client und Server müssen Dev7 verwenden.** Das Netzwerkprotokoll ist jetzt 3; Dev5/Dev6 sind keine passenden Gegenstellen. Alte NG-Fahrzeuge-JAR ersetzen, nicht parallel laden. Vor einem Versionswechsel die Welt sichern.

Build/Reobf und **41/41 Forge-GameTests** bestanden. Die elf neuen Tests behandeln Motorzustände, Abschalten/Laden, Blinker/Hupe, Besitzer- und Schlüsselrechte, Laderaumgröße und Zugriffsbedingungen, persistente Inventarpositionen, Aufnehmen ohne Verdopplung, atomare Baukosten, Werkbankzugriff und Rezeptregistrierung.

Im echten Minecraft-Client wurden Werkbank, Laderaum und Warnblinker visuell geprüft. Ein Transporter wurde über die reale Client-Menüaktion im Überlebensmodus aus den angezeigten Materialien gebaut; danach wurden Motorstart und Fahren über Client-/Server-Pakete bestätigt. Die Sound-Engine startete über OpenAL mit einem stummen Testausgabegerät; das ist keine menschliche Hörprobe. Drei Screenshots liegen im Downloadpaket.

Der ergänzende Client-Prüfstand und die JAR-Prüfsumme stehen in `docs/test-results/dev7.json`. Akustische Feinabstimmung am echten Ausgabegerät sowie ein Spieltest mit zwei verbundenen Clients bleiben offen. Die bisherigen NG-Verkehr-Integrationstests wurden für Dev7 nicht erneut ausgeführt.

**MC02 läuft weiterhin mit Dev5.** Dev7 wurde in diesem Bauschritt nicht dort installiert.

## Entwicklung

`gradle build runGameTestServer` baut und prüft die Mod. Die Klangquellen liegen als UTF-8-Base64 unter `src/main/sound-assets`; Gradle dekodiert sie ohne zusätzliche Audio-Werkzeuge. `python3 tools/generate_vehicle_sounds.py` erzeugt die originalen synthetischen Quellen neu (Python und ffmpeg erforderlich).

Die optionale Sichtprüfung läuft mit `gradle runClient -PvehicleVisualTest -PvehicleVisualDev7` in einer vorher vorbereiteten, entbehrlichen Welt `run/saves/visual-dev7`. Sie verändert ausschließlich diese lokale Testwelt. Testklassen werden nicht in die Mod-JAR aufgenommen.
