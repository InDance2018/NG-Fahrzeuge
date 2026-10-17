# Dev3 – NG City: Einsteigen und Fahren

**Testnachtrag vom 17.10.2026:** Der Projektinhaber bestätigt alle bisherigen DEV-Stände auf MC02 sowie den bestandenen Drei-Spieler-/Chunk-/Neustarttest. [Aktueller Testbericht](PRAXISTEST-2026-10-17.md). Ältere Testangaben im folgenden Dokument beschreiben den damaligen Prüfstand.


Stand: 16. Oktober 2026 · Minecraft 1.20.2 / Forge 48.1.0 / Java 17

## Neue Funktionen

- Ein Fahrerplatz. Normaler Rechtsklick steigt ein; Schleichen steigt aus.
- Vorwärts- und Rückwärtsfahrt, geschwindigkeitsabhängiges Lenken und Bremsen.
- Gegenläufiges Gas bremst erst auf null, bevor die Fahrtrichtung wechselt.
- Automatisches Parken ohne Fahrer, beim Aussteigen und beim Wiederladen. Bei ausbleibender Eingabe wird nach zehn Ticks gebremst. Es gibt noch keine separat umschaltbare Feststellbremse.
- Serverberechnete Bewegung; das neue Netzwerkpaket übermittelt nur Tasten und Fahrzeug-ID. Der Server akzeptiert Eingaben ausschließlich vom aktuellen Fahrer dieses Fahrzeugs. Vanilla-Fahrzeugpositionspakete erhalten keine Steuerungsfreigabe.
- Kollision mit Hindernissen; halbe Blockstufen können überwunden werden. Seitliche Ausstiegspositionen werden geprüft, bei blockierten Seiten weitere Positionen versucht. Wenn keine geeignet ist, gilt Minecrafts Rückfallposition.
- Durchsichtige Fenster, eine Fahrersitzposition und geglättete Positionsübertragung für den Client. Geschwindigkeitsanzeige in der Aktionsleiste.
- Zunächst ungefähr 32 km/h vorwärts und 13 km/h rückwärts bei 20 TPS; Testwerte, kein abschließendes Fahrverhalten.

## Installation und Bedienung

Minecraft schließen. Alle alten NG-Fahrzeuge-JARs aus dem mods-Ordner entfernen,
ZIP entpacken und `ng_fahrzeuge-0.1.0-dev3.jar` einsetzen. Client und Server benötigen
bei einem Mehrspielertest dieselbe Fassung. NG Verkehr ist weiterhin optional.

Kreativinventar: **NG Fahrzeuge → NG City**. Alternativ: `/give @s ng_fahrzeuge:ng_city`.

| Aktion | Standardbedienung |
| --- | --- |
| Fahrzeug platzieren | Boden auf einer freien Fläche rechtsklicken |
| Einsteigen | Fahrzeug normal rechtsklicken |
| Vorwärts | W |
| Bremsen, danach rückwärts | S |
| Lenken | A / D |
| Bremse halten | Leertaste (Springen) |
| Aussteigen | Linke Umschalttaste (Schleichen) |
| Unbesetztes Fahrzeug aufnehmen | Schleichen + Rechtsklick mit leerer Haupthand |
| Außenansicht | F5 |

Die Fahrsteuerung verwendet die aktuellen Minecraft-Tastenbelegungen für Bewegung,
Springen und Schleichen. Beim Öffnen eines Menüs oder inaktivem Fenster wird gebremst.
Ein besetztes Fahrzeug kann weder aufgenommen noch von einem zweiten Fahrer übernommen werden.

## Prüfstand

Build und Forge-Reobf erfolgreich. 18 automatisierte Tests auf einem separaten
Forge-GameTestServer ohne NG Verkehr bestanden. Die sechs Tests aus Dev2 wurden
um elf Fahrtests und einen Bodentest erweitert:

- Platzierung, Hindernisse, Baurechte, Survival-/Kreativ-Verbrauch und Wiederaufnahme ohne Duplizierung.
- Speicherung von Position, Ausrichtung, Name und UUID sowie Schwerkraft.
- Einsteigen, ein einzelner Fahrerplatz und Schutz vor Aufnahme eines besetzten Fahrzeugs.
- Fahrerfreigabe, falsche Fahrzeug-ID, ungültige Eingaben und Eingaben nach dem Aussteigen.
- Geschwindigkeitsgrenzen, Richtungswechsel, Vorwärts-/Rückwärtsbewegung und Lenkung ohne Drehen im Stand.
- Tatsächliches Anhalten mit Bremse, Wandkollision, Eingabe-Zeitüberschreitung, Parken beim Aussteigen und erneutes Einsteigen ohne alte Gasstellung.
- Wiederladen eines zuvor bewegten Fahrzeugs mit zurückgesetzter Bewegung, freie Ausstiegsseite und Paketserialisierung.
- Fahrt auf festem Boden und Überwinden einer halben Blockstufe.

Ein Teil der Bewegungsprüfungen verwendet eine hindernisfreie Testspur ohne Schwerkraft;
Boden/Schwerkraft/Stufe werden gesondert mit echter Blockkollision geprüft.
GameTests prüfen kompilierten Entwicklungscode, nicht die Installation der reobfuskierten JAR.
Die Testklassen sind nicht in der ausgelieferten Mod-JAR enthalten.

## Gemeinsamer Spieltest – noch offen

Darstellung und Fenster, Fahrerpose und Kamera in erster/dritter Person, reale Tasteneingaben
über eine Client-Verbindung, Aussteigen in unterschiedlichen Aufbauten, zwei gleichzeitig
verbundene Clients sowie Welt-/Serverneustart und Chunkwechsel sind noch zu prüfen.
Die konkrete Erkennung durch NG Verkehr bleibt offen. Es wurde nichts auf MC02 installiert.
Der Nutzer hatte den gemeinsamen Start von Dev1 und NG Verkehr bestätigt; Dev2/Dev3
wurden bewusst für einen gemeinsamen Modell- und Fahrtest zurückgestellt.

Lichtschaltung, Farben, Mitfahrer, Rezepte, Transportinventar, Schadens-/Kraftstoffsystem,
Fahrzeug-KI sowie NG Family und NG Transporter sind nicht Teil dieser Testfassung.

Datei: `ng_fahrzeuge-0.1.0-dev3.jar` (26426 Bytes).
SHA-256: `56e427c43708afa814abff6740d31ea883c2571e3226aa3176f57aae9ebdfe07`.
