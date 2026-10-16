# Dev8 – Fahrzeugsysteme, Tankstelle und Anhänger

Minecraft 1.20.2 · Forge 48.1.0 · Java 17 · Entwicklungsfassung · 16. Oktober 2026

Die zwölf freigegebenen Baupunkte erweitern die Dev7-Grundlage.

## Neue Funktionen

1. **Türen und Heck:** schwenkbare Türbleche und Scheiben; City/Family mit hochklappendem Heck, Transporter mit zwei Hecktüren. Öffnen nur bei angezogener Feststellbremse und ausgeschaltetem Motor. Motorstart schließt die Türen automatisch.
2. **Eigenes HUD:** Geschwindigkeit, Motor, Feststellbremse, Licht, Blinker, Tempomat, Tankbalken, Kilometer und Fahrzeugzustand in einem eigenen Anzeigefeld.
3. **Kilometerzähler:** tatsächliche zurückgelegte Strecke zählt; Gesamt- und Tageskilometer werden dauerhaft gespeichert. Zurücksetzen betrifft ausschließlich die Tagesstrecke.
4. **Kraftstoff:** unterschiedliche Tankgrößen und Verbrauchswerte; Leerlauf verbraucht ebenfalls Kraftstoff. Ein leerer Tank verhindert den Motorstart.
5. **Tankstelle:** herstellbare Zapfsäule mit bis zu 500 Litern gespeichertem Vorrat. Kraftstoffkanister dienen zum Befüllen und zum direkten Tanken am Fahrzeug.
6. **Schäden:** getrennte Zustände von Karosserie und Motor. Stärkere Zusammenstöße sowie zugefügter Schaden senken den Zustand. Ein schwer beschädigter Motor begrenzt die Leistung; ein defektes Fahrzeug startet nicht.
7. **Reparatur:** Reparaturschlüssel und Ersatzteile. Eine Reparatur verbraucht ein Ersatzteil und einen Haltbarkeitspunkt, stellt jeweils bis zu 25 Zustandspunkte an Karosserie und Motor wieder her.
8. **Haftung:** geringere Brems- und Lenkhaftung auf Erde/Schotter/Sand, Schnee und Eis. Auf Eis ist der Bremsweg deutlich länger. Dies ist ein einfaches materialabhängiges Fahrmodell.
9. **Tempomat:** speichert die aktuelle Vorwärtsgeschwindigkeit. Bremse, Rückwärtsfahrt, Feststellbremse, Motor aus, fehlende Steuereingaben und Kollisionen heben ihn auf.
10. **Kupplung:** Family und Transporter können einen eigenen oder freigegebenen nahen Lastenanhänger an-/abkuppeln. City hat keine Anhängerfreigabe. Verbindungen werden gespeichert, ohne entfernte Chunks zwangsweise zu laden.
11. **Lastenanhänger:** eigene platzierbare Entität mit 27 Inventarplätzen, Rädern, Deichsel und Rück-/Brems-/Blinkleuchten. Der Anhänger folgt dem Zugfahrzeug; ein blockierter Anhänger stoppt dessen Motor.
12. **Fahrzeugverwaltung:** Name ändern, Tageskilometer nullen, Türen bedienen, kuppeln, Schlüssel sperren und Besitzer an einen online anwesenden Spieler übertragen. Besitzerwechsel sperrt alte Fahrzeugschlüssel.

## Bedienung

**Wie in Dev7: I startet den Motor, H löst die Feststellbremse.** W/S fährt, A/D lenkt, Springen bremst, Schleichen steigt aus. L: Licht, Komma/Punkt: Blinker, R: Warnblinker, G: Hupe, B: Laderaum.

| Neue Aktion | Bedienung |
|---|---|
| Türen öffnen/schließen | O, bei Motor AUS und Feststellbremse AN |
| Heck öffnen/schließen | U, unter denselben Bedingungen |
| Tempomat | C, während der Vorwärtsfahrt |
| An-/Abkuppeln | J vom Fahrersitz oder Fahrzeugverwaltung |
| Tageskilometer nullen | Fahrzeugverwaltung; optional eigene Taste belegen |
| Fahrzeugverwaltung | Mit Fahrzeugpapieren auf das geparkte eigene Auto rechtsklicken |
| Reparatur | Mit Reparaturschlüssel rechtsklicken; Ersatzteile im Inventar |
| Direkt tanken | Mit gefülltem Kanister auf das geparkte eigene Auto rechtsklicken |
| Zapfsäule befüllen | Mit Kanister auf die Zapfsäule rechtsklicken |
| An der Zapfsäule tanken | Ohne Kanister rechtsklicken; nächstes berechtigtes Fahrzeug im Umkreis von 6 Blöcken |
| Anhängerinventar | Rechtsklick auf den Anhänger |
| Anhänger aufnehmen | Abgekuppelt, Schleichen + Rechtsklick mit leerer Hand |

Alle Tasten sind unter Optionen → Steuerung → NG Fahrzeuge registriert. Bei Konflikten mit anderen Mods eine freie Taste wählen. Der Motor-AUS-Hinweis im HUD zeigt die tatsächlich belegte Starttaste.

Wartung, Betanken, Inventare und Verwaltung erfordern ein stehendes Fahrzeug mit Motor AUS und Feststellbremse AN. Besitzer und Schlüsselinhaber können warten und tanken; Besitzerwechsel und Schlüsselsperre bleiben dem Besitzer vorbehalten. Für diese beiden Änderungen muss das Auto unbesetzt sein. Danach neue leere Schlüssel herstellen und binden.

Den Anhänger hinter dem Zugfahrzeug abstellen und im Stand kuppeln. Ein Anhänger mit geöffnetem Inventar wird nicht angekuppelt. Gekoppelte Fahrzeuge/Anhänger lassen sich nicht als Item aufnehmen. Zum manuellen Abkuppeln kann der Anhängerbesitzer außerdem mit Fahrzeugpapieren bei gedrückter Schleichentaste auf den Anhänger rechtsklicken. Der Anhänger bleibt ein separat zugeordnetes Fahrzeug.

## Tank und Verbrauch

| Fahrzeug | Tank | Fahrverbrauch |
|---|---:|---:|
| City | 40 l | 6,5 l / 100 km |
| Family | 55 l | 8 l / 100 km |
| Transporter | 80 l | 12 l / 100 km |

Ein Block Fahrstrecke entspricht einem Meter. Zusätzlich verbraucht ein laufender Motor 0,0001 l pro Tick. Neue und ältere Fahrzeuge ohne Tankdaten beginnen mit vollem Tank. Bestehende Werte bleiben beim Speichern und Aufnehmen erhalten. Ein Kanister enthält höchstens 10 Liter; Restmengen bleiben nach Teilbetankungen erhalten. Der Kraftstoff ist ein fiktiver Spielgegenstand.

## Neue Rezepte

Alle Gegenstände stehen auch im Kreativreiter NG Fahrzeuge bereit. Fahrzeuge werden weiterhin an der Fahrzeugwerkbank hergestellt.

- **Kraftstoffkanister:** oben mittig Eisen; mittlere Reihe Eisen, Kohle, Eisen; unten mittig Lohenstaub.
- **Kanister nachfüllen:** vorhandener Kanister + Kohle + Lohenstaub, formfrei. Das Rezept setzt ihn auf 10 l; möglichst einen leeren Kanister verwenden.
- **Vier Ersatzteile:** obere Reihe Eisen, Kupfer, Eisen; darunter mittig Redstone.
- **Reparaturschlüssel:** obere Reihe frei, Eisen, Eisen; mittlere Reihe frei, Stock, Eisen; unten links Stock.
- **Fahrzeugpapiere:** drei Papier nebeneinander, darunter mittig Redstone.
- **Zapfsäule:** oben Eisen, Glas, Eisen; mittig Kupfer, Redstone, Kupfer; unten drei Eisen.
- **Lastenanhänger:** oben Eisen, Truhe, Eisen; mittig drei Holzbretter; unten außen jeweils Eisen.

## Installation und Prüfstand

**Client und Server müssen beide Dev8 verwenden (Netzwerkprotokoll 4).** Frühere NG-Fahrzeuge-JAR ersetzen, nicht parallel laden; Welt vor einem Versionswechsel sichern. MC02 wurde in diesem Bauauftrag nicht verändert und läuft weiterhin mit Dev5.

**Build/Reobf erfolgreich und 56/56 erforderliche Forge-GameTests bestanden.** Darunter sind 15 neue Dev8-Prüfungen einschließlich eines 100-Tick-Anhängerlaufs, echter Kollisionsschäden und vollständigen Stillstands beim Bremsen auf Eis.

Im echten Minecraft-Client wurden Türen, Transporterheck, Anhänger/Zapfsäule, Fahrzeugverwaltung und HUD visuell geprüft. Das Umbenennen über die Verwaltung sowie ein durchgehender Gespannlauf mit Tempomat, Kilometerzählung und Verbrauch wurden über die echten Client-/Server-Pakete bestätigt. Fünf Aufnahmen liegen dem Download bei.

Prüfergebnisse, Client-Nachweise und JAR-Prüfsumme stehen in `docs/test-results/dev8.json`. Ein vollständiger Zwei-Client-Spieltest, Hörprobe am echten Ausgabegerät und erneute Integration mit NG Verkehr bleiben offen. Der Anhänger wurde noch nicht für gemeinsame Parkplatzzählung mit NG Verkehr freigegeben.

Die lokale Sichtprüfung nutzt `gradle runClient -PvehicleVisualTest -PvehicleVisualDev8` mit einer entbehrlichen vorbereiteten Welt `run/saves/visual-dev8`. Sie verändert diese Testwelt und endet automatisch. Testklassen gehören nicht zur ausgelieferten Mod.
