# Dev4 – City, Family, Transporter und Parksystem

Stand: 16. Oktober 2026 · Minecraft 1.20.2 / Forge 48.1.0 / Java 17

Die fünf besprochenen Punkte sind technisch weiterbearbeitet. Für den fehlenden
eigenen Spieltestserver wurden lokale Forge-Prüfungen durchgeführt. Sicht- und
Bedienprüfungen mit echten Minecraft-Clients bleiben ausdrücklich offen.

## Drei Fahrzeuggruppen – eine Mod-JAR

| Gruppe | Sitze | Breite × Länge × Höhe in Blöcken | Ausgangsfarbe |
| --- | ---: | --- | --- |
| NG City | 1 | 1,8 × 2,6 × 1,4 | Cyan/Türkis |
| NG Family | 4 | 1,9 × 3,6 × 1,55 | Cyan/Türkis |
| NG Transporter | 2 | 2,2 × 4,2 × 2,5 | Weiß mit türkisfarbenem Streifen |

Alle Gruppen besitzen eigene Entitätstypen, Gegenstände, erste Blockmodelle und
Inventarmodelle. Die Fläche folgt der Ausrichtung. Ein Fahrzeug hat genau einen
Fahrer; weitere Personen sitzen als Mitfahrer. Fahrerwechsel: Steigt der Fahrer
aus, wird geparkt und kein vorhandener Mitfahrer übernimmt automatisch. Eine neu
einsteigende Person erhält den freien Fahrerplatz. Überzählige Personen werden
abgewiesen. Der Transporter enthält noch kein Frachtinventar.

## Bedienung

Minecraft schließen und alle vorherigen NG-Fahrzeuge-JARs aus dem mods-Ordner
entfernen. `ng_fahrzeuge-0.1.0-dev4.jar` aus der ZIP einsetzen. Bei einem
Mehrspielertest müssen Client und Server dieselbe Fahrzeugfassung verwenden.
NG Verkehr bleibt optional und wird nicht in diese JAR eingebaut.

Kreativinventar: **NG Fahrzeuge**. Befehle:
`/give @s ng_fahrzeuge:ng_city`, `/give @s ng_fahrzeuge:ng_family` oder
`/give @s ng_fahrzeuge:ng_transporter`.

| Aktion | Standardbelegung |
| --- | --- |
| Platzieren | Boden auf ausreichend freier Fläche rechtsklicken |
| Einsteigen / Mitfahren | Fahrzeug normal rechtsklicken |
| Vorwärts / bremsen und rückwärts | W / S |
| Lenken | A / D |
| Fußbremse halten | Leertaste (Springen) |
| Feststellbremse umschalten | H |
| Scheinwerfer/Rückleuchten umschalten | L |
| Aussteigen | Linke Umschalttaste (Schleichen) |
| Farbe ändern | Unbesetztes Fahrzeug mit Farbstoff rechtsklicken |
| Fahrzeug aufnehmen | Unbesetztes Fahrzeug mit leerer Hand schleichend rechtsklicken |
| Außenansicht | F5 |

**Vor dem Losfahren H drücken:** Neu platzierte Fahrzeuge stehen mit angezogener
Feststellbremse. H und L lassen sich in den Minecraft-Tastenbelegungen unter
**NG Fahrzeuge** umstellen. Die Fahrsteuerung folgt den normalen Bewegungstasten.

Nur der Fahrer darf lenken, Feststellbremse oder Licht umschalten. Menüöffnung,
inaktives Fenster und ausbleibende Steuerpakete führen zum Bremsen. Ohne Fahrer
wird automatisch geparkt. Lackieren verbraucht im Survival einen Farbstoff nur,
wenn die Farbe geändert wird; im Kreativmodus wird keiner verbraucht.
Farben und Ausstattung bleiben beim Aufnehmen und erneuten Platzieren erhalten.

## Licht und Speicherung

Scheinwerfer und Rückleuchten besitzen schaltbare helle Leuchtflächen. Brems- und
Rückfahrlicht reagieren auf Fahrzustände. Es gibt noch keine auf die Straße
projizierten Lichtkegel oder dynamische Weltbeleuchtung durch die Scheinwerfer.

Fahrzeugtyp, UUID, Name, Position, Ausrichtung, Farbe, Lichtzustand und Feststellbremse
werden gespeichert. Nach Laden steht die Bewegung zunächst still; ohne Fahrer wird
die Feststellbremse automatisch angezogen. Ältere Fahrzeuge ohne neue
Ausstattungsfelder erhalten sichere Ausgangswerte.

## Optionale Zusammenarbeit mit NG Verkehr

Die Mod ergänzt den bereits vorhandenen Entity-Type-Tag
`ng_verkehr:parking_vehicles` mit ihren drei Fahrzeugtypen, ohne Einträge anderer
Mods zu ersetzen. Dadurch werden auch unbesetzte Fahrzeuge erkannt. Es gibt keine
Pflichtabhängigkeit, keinen gemeinsamen Quellcode und keine eingebettete Verkehrs-JAR.

Mit **NG Verkehr 0.1.0-beta2-dev6** lokal geprüft:

- Alle drei Fahrzeuggruppen werden geparkt und mit Mitfahrern als Fahrzeug erkannt; Mitfahrer werden separat ausgeschlossen.
- NG City und NG Transporter belegen einen Stellplatz und geben ihn beim Entfernen frei.
- Ein NG Family mit vier Personen belegt genau einen Stellplatz.
- Tatsächliche Ein- und Ausfahrtssensoren zählen NG City korrekt.
- Die 2-Meter-Schranke öffnet für NG City. Sie bleibt für den 2,5 Meter hohen Transporter geschlossen und öffnet nach Erhöhung auf 2,60 Meter.
- Eine volle Parkzone öffnet die Schranke nicht für ein weiteres passendes Fahrzeug.

Das sind automatisierte Integrationsfälle auf dem Forge-Server. Sie ersetzen
keinen echten Client-Spieltest sämtlicher Parkplatzaufbauten oder anderer
NG-Verkehr-Versionen.

## Prüfung

- Build und Forge-Reobf erfolgreich; nur Minecraft/Forge als Pflichtabhängigkeiten.
- **29/29 eigenständige Servertests bestanden.** Grundlage sind die 18 Tests aus Dev3 plus neun Ausstattungs-/Fahrzeuggruppenprüfungen und zwei Speicherprüfungen.
- **Tatsächlicher Server-Neustart:** Ein Serverprozess legte drei markierte Fahrzeuge an und wurde beendet. Ein neuer Prozess fand dieselben UUIDs, Gruppen, Namen, Positionen, Farben, Leuchten- und Parkzustände wieder.
- **Tatsächliche Chunk-Entladung/-Rückkehr:** Der Chunk und die ursprüngliche Fahrzeuginstanz wurden nachweislich entfernt; nach Laden wurde eine andere Instanz mit derselben UUID und Ausstattung gefunden.
- **38/38 Tests mit NG Verkehr Beta2-dev6 bestanden:** die Basis plus neun zusätzliche Integrationsfälle. Der separate Neustart-Nachweis stammt aus dem eigenständigen Lese-Lauf; im Integrationslauf wurde kein weiterer Server-Neustart behauptet.
- JAR-Integrität, expandierte Metadaten, Ressourcen-JSON, Modellpfade und Trennung von Produktions-/Testklassen geprüft.

[Maschinenlesbarer Prüfstand](docs/test-results/dev4.json). Die Testklassen und die
fremde Verkehrs-JAR sind nicht in der ausgelieferten Fahrzeug-JAR enthalten.

## Weiterhin offen

Client-Sicht-/Bedienprüfung aller Modelle, Inventarfarben, Leuchten, Sitzpositionen
und Kameraperspektiven; tatsächliche Tasteneingaben über die Client-Verbindung;
zwei echte gleichzeitig verbundene Minecraft-Clients und weitere Sondergeometrien.
Dafür muss später ein geeigneter Spieltest stattfinden. Es wurde nichts auf MC02
oder anderen produktiven Minecraft-Servern installiert.

Rezepte, Transportinventar, Fahrzeug-KI, Kraftstoff, Schaden und weitere Ausstattung
sind noch keine fertigen Funktionen. Es gibt noch keine stabile Gesamtveröffentlichung.

Datei: `ng_fahrzeuge-0.1.0-dev4.jar`.
SHA-256: `583e8137f892e5a8d491901a781e1215045d88b0d4ab09ae0e09e342303fef33`.
