# NG-Fahrzeuge Dev11

Minecraft Java 1.20.2 · Forge 48.1.0 · Java 17 · Netzwerkprotokoll 7.
Client und Server benötigen beide Dev11. Entwicklungsfassung; keine Installation auf MC02 durch diesen Bauauftrag.

## Die 20 Bauten

1. **NG Stadtbus:** eigener langer Busaufbau, Fahrerplatz und zwölf getrennte Fahrgastsitze.
2. **Zwei Bustüren:** `O` vorn, `U` hinten; getrennte Öffnung und Animation. Bei angezogener Parkbremse und Stillstand auch mit laufendem Motor bedienbar. Offene Türen sperren die Fahrt.
3. **Haltewunsch:** Fahrgäste drücken `F9`; der Fahrer sieht die Meldung im HUD. Öffnen einer Bustür bestätigt und löscht den Wunsch. Fahrgäste erhalten keine Fahr- oder Türsteuerung.
4. **Linie und Ziel:** Fahrzeugpapiere am geparkten Bus, Register „Sitz / Linie“. Acht Zeichen Liniennummer, 32 Zeichen Fahrtziel; Anzeigen vorn, rechts und hinten. Einstellungen bleiben gespeichert.
5. **NG Motorrad:** eigenes schmales Zweiradmodell, stärkere Beschleunigung, höhere Höchstgeschwindigkeit und direktere Lenkung. Kein Anhänger und kein Dachträger.
6. **Sozius:** zweiter Sitz hinter dem Fahrer; maximal zwei Personen.
7. **Seitenständer:** `F8` bei Stillstand, Parkbremse und ausgeschaltetem Motor. Abgestellt senkt sich der Ständer automatisch, normales Einsteigen hebt ihn an. Ausgeklappt sperrt er den Motorstart.
8. **Schräglage:** das Motorrad neigt sich beim Lenken und steht auf dem Seitenständer sichtbar geneigt.
9. **NG Pickup:** eigener Aufbau mit offener Ladefläche, zwei Sitzen und 27 Frachtplätzen.
10. **Heckklappe:** `U` klappt die Pickup-Klappe nach unten. `B` öffnet das zugehörige Frachtinventar bei geparktem Fahrzeug und abgeschaltetem Motor.
11. **Sichtbare Ladung:** Kisten auf der Pickup-Ladefläche und auf montierten Dachträgern folgen der Zahl belegter Frachtplätze. Leer bedeutet keine Kisten. Die Anzeige verrät keine Item-Arten und erzeugt keine zusätzlichen Items.
12. **Blinkerhebel:** sichtbar bewegter Hebel; Links-/Rechtsblinker gehen nach einer erkannten Kurve und anschließender Geradeausfahrt zurück. Warnblinker bleibt an, bis er abgeschaltet wird.
13. **Fahrersitz:** Fahrzeugpapiere → „Sitz / Linie“ → vor/zurück und höher/tiefer. Die gespeicherte Einstellung verschiebt den tatsächlichen Fahrerplatz und damit die Fahrerperspektive.
14. **Gurte:** `F7` für Fahrer und Fahrgäste in Autos und Bus. Angelegte Gurte sind am Sitz sichtbar; HUD zeigt eigenen Gurt und Anzahl angelegter Gurte. Beim Aussteigen wird der Gurt gelöst. Motorräder besitzen keine Sicherheitsgurte. Es wird kein zusätzlicher Insassen-Unfallschutz behauptet.
15. **Sichtbarer Schaden:** beschädigte Stoßfänger und Kratz-/Dellenflächen erscheinen abhängig vom vorhandenen Karosseriezustand; Reparaturen verwenden weiterhin die vorhandenen Ersatzteile.
16. **Lackierstation:** Farbe durch gehaltenen Farbstoff wählen, Station rechtsklicken. Eigenes Fahrzeug innerhalb sechs Blöcken bereitstellen, parken und Motor abschalten. Pro erfolgreicher Umlackierung ein Farbstoff und ein Eisenbarren; identische Farbe kostet nichts. Die vorhandene direkte Farbstoff-Lackierung bleibt verfügbar.
17. **Lichtstufen:** `L` schaltet Aus → Standlicht → Abblendlicht → Aus. `F10` wechselt bei eingeschaltetem Abblendlicht zum Fernlicht und zurück. Eigene sichtbare Leuchtflächen, kein dynamisches Ausleuchten von Weltblöcken.
18. **Nebelscheinwerfer:** `F12` schaltet die zusätzlichen Frontlampen getrennt. Lampen nutzen die vorhandene Starterbatterie.
19. **Meine Fahrzeuge:** neues Übersichts-Item rechtsklicken. Nur eigene erfasste Fahrzeuge, mit Name, Typ und letztem gespeicherten Standort; Garagenfahrzeuge bleiben enthalten. Neue Fahrzeuge werden nach Besitzzuordnung erfasst, bestehende nach dem nächsten geladenen Tick. Seitenweise Darstellung ohne feste Gesamtbegrenzung.
20. **Ortung:** Fahrzeug in der Übersicht anklicken. Richtung und Entfernung erscheinen unten links; andere Dimensionen werden benannt. „Ortung aus“ beendet die Anzeige. Die Ortung verwendet den beim Öffnen erfassten letzten Standort und lädt keine Chunks; bei fahrenden Fahrzeugen Übersicht für einen neuen Stand erneut öffnen.

## Tasten sind vorbelegt

Keine manuelle Erstbelegung erforderlich. `I` Motor, `H` Parkbremse, `WASD` fahren/lenken, Leertaste bremsen, Umschalt aussteigen. `F6` Außenkamera, `F7` Gurt, `F8` Seitenständer, `F9` Haltewunsch, `F10` Fernlicht, `F12` Nebelscheinwerfer. Alle Belegungen stehen unter Optionen → Steuerung → NG Fahrzeuge; andere Mods können Konflikte verursachen.

Die Fahrzeugverwaltung hat drei Register: Fahrzeug, Bedienung, Sitz / Linie. Händler und Werkbank enthalten alle sieben Fahrzeugtypen. Der Händler zeigt zwei Seiten; Preise und Ankauf bleiben konfigurierbare Item-Transaktionen. Euro-Zahlungen gehören zum NG-Wirtschaftssystem.

## Herstellung

Alle drei neuen Fahrzeuge entstehen in der Fahrzeugwerkbank; Materialkosten werden vor dem Bau angezeigt und vollständig geprüft. Alternativ beim Fahrzeughändler oder im Kreativinventar.

Lackierstation: `RGB / III / IRI`, R roter Farbstoff, G grüner Farbstoff, B blauer Farbstoff, I Eisenbarren.

Fahrzeugübersicht: ` P  / ICI /  R `, P Papier, I Eisenbarren, C Kompass, R Redstone.

## Öffentliche Fahrzeugansicht

`VehicleSnapshot.of(NgCityEntity)` liefert für eine bereits geladene Serverinstanz eine unveränderliche, rein lesende Ansicht: UUID, Dimension, Position, tatsächliche AABB, Typ, Besitzer, Fahrer und Parkzustand. Besitzer und Fahrer bleiben getrennt. Entfernte Ziele und Clientinstanzen liefern keine Ansicht. Es gibt keine Chunk-Suche und keine Welt-/Fahrzeugmutation. Der Bus trägt `ng_bus`; die drei neuen Typen stehen in den Fahrzeug- und Parkfahrzeug-Tags.

Ein aktueller gemeinsamer Test mit NG-Verkehr ist dadurch nicht nachgewiesen. Straßen, Haltestellenanlagen und Euro-Zahlungen sind kein Teil dieses Fahrzeugbaus.

## Prüfstand

**Build/Reobf und 100/100 erforderliche Forge-GameTests bestanden.** Die 20 neuen Tests decken Sitze, Türen/Haltewunsch, Rechte, Linien, Motorrad/Seitenständer/Fahrverhalten, Pickup-Ladung und Speicherung, sichtbare Ladungszähler ohne Inventarweitergabe, Blinker-Rückstellung, Sitzverstellung, individuelle Gurte, Lackierkosten/Rechte, getrennte Lampen, Besitzerfilter/Wechsel, Übersicht/Garage, neue Händler-/Werkbanktypen, getrennte Besitzer-/Fahrerdaten und ungültige Speicherwerte ab.

In zwei getrennten Serverprozessen wurden alle sieben Fahrzeugtypen mit gleicher UUID und Position wiederhergestellt. Fahrersitz, Buslinie/Ziel, Nebellicht und die Fahrzeugübersicht wurden zusätzlich von der Festplatte zurückgelesen. Der bestehende echte Chunk-Entladungs-/Rückkehrtest besteht weiterhin.

Zehn Aufnahmen des echten Minecraft-Clients dokumentieren Modelle, Türen, Ladung/Schäden, Ständer, Lampen, Verwaltung, Händler, Übersicht/Ortung und Fahranzeigen. Verwaltungspakete, Motorradfahren/Lenken, die Blickrichtung der Außenkamera in Kurven und das Anschnallen im Bus sind im Clientlauf bestätigt. Die Klangausgabe lief mit OpenAL No Output; es wird kein menschlicher Hörtest behauptet.

JAR-SHA256: `ee8e463506766783cf1a331e2ec5a70601a04d45d81a1fa5bb3e2839aa28f8ee`. Prüfbericht: `docs/test-results/dev11.json`. Ein Test mit zwei echten verbundenen Dev11-Clients, MC02-Spieltest und ein aktueller gemeinsamer Test mit NG-Verkehr bleiben offen.

Die isolierte, automatisch endende Vorschau läuft mit `gradle runClient -PvehicleVisualTest -PvehicleVisualDev11` in der vorbereiteten entbehrlichen Welt `run/saves/visual-dev11`. Prüfklassen gelangen nicht in die Mod-JAR.
