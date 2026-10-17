# Dev6 – Fahrzeugoptik nach der Projektzentrale

**Testnachtrag vom 17.10.2026:** Der Projektinhaber bestätigt alle bisherigen DEV-Stände auf MC02 sowie den bestandenen Drei-Spieler-/Chunk-/Neustarttest. [Aktueller Testbericht](PRAXISTEST-2026-10-17.md). Ältere Testangaben im folgenden Dokument beschreiben den damaligen Prüfstand.


Stand: 16. Oktober 2026 · Minecraft 1.20.2 · Forge 48.1.0 · Entwicklungsfassung

Die drei Fahrzeugmodelle wurden anhand der Originalbilder auf der [NG-Fahrzeuge-Projektseite](https://chatgpt.com/space/page_e10909c287ec81919116d20fe502ac97) neu gestaltet.

- **NG City:** kompakter mintfarbener Zweitürer, schräge Front- und Heckscheibe, schwarze Stoßfänger und Fensterrahmen, NG-Kühlergrill, helle Scheinwerfer und orange Blinker.
- **NG Family:** längerer mintfarbener Kombi mit vier Türen, hinteren Seitenfenstern, schwarzen Seitenleisten und Dachreling.
- **NG Transporter:** hoher weißer Kastenwagen mit mintfarbenem Seitenstreifen, gestuftem Hochdach, großen Spiegeln, Schiebetürkonturen und zweigeteilten Hecktüren.

Alle drei erhalten Reifen mit abgestufter Kontur, silberne Felgen, transparente Scheiben, sichtbare Sitze mit Kopfstützen, Armaturenbrett und Lenkrad. Räder drehen beim Fahren; die Vorderräder berücksichtigen den Lenkeinschlag. Lack, Gummi, Metall, Stoff und Leuchten verwenden eigene reproduzierbare Pixelmaterialien. Die Inventarmodelle zeigen die drei unterschiedlichen Fahrzeugformen.

Die Grundfarbe Cyan wird entsprechend den Vorlagen als Mint dargestellt. Andere Farbstoffe bleiben nutzbar. Bedienung, Fahrphysik, Fahrerzuordnung, Speicherung und Netzwerkprotokoll bleiben gegenüber Dev5 unverändert. Leuchten sind weiterhin helle Flächen ohne projizierte Lichtkegel.

## Prüfung

- Build einschließlich Reobf erfolgreich.
- **30/30 erforderliche Forge-GameTests bestanden**, darunter die Arclight-Fahrerzuordnungsregression.
- Echter Minecraft-Client mit den neuen Modellen gestartet und sauber beendet. Alle drei Fahrzeuge von vorn und hinten visuell geprüft; sechs Screenshots im Downloadpaket.
- JAR enthält die Produktionsklassen, keine GameTest- oder Visualtest-Klassen.

Die Screenshots zeigen Minecraft ohne Shader. Die Beleuchtung und Umgebung der Konzeptbilder werden dadurch nicht identisch reproduziert. Ein vollständiger Sicht-/Ausstattungs- und Zwei-Client-Spieltest bleibt offen. Die früheren 38 Integrationstests mit NG Verkehr wurden für Dev6 nicht erneut ausgeführt.

**MC02 läuft weiterhin mit Dev5.** Dessen Grundbedienung hat der Projektinhaber bestätigt. Dev6 wurde dort noch nicht installiert oder im laufenden Mehrspielbetrieb bestätigt.

## Reproduzierbare Sichtprüfung

Die optionale Quelle `src/visualtest` gehört nicht zur ausgelieferten Mod. In einer lokalen Testinstallation eine entbehrliche Einzelspielerwelt `run/saves/visual-dev6` vorbereiten und `gradle runClient -PvehicleVisualTest` ausführen. Die Testszene verändert diese Welt (Fahrzeugaufstellung, Boden und Spielregeln), erstellt sechs Aufnahmen unter `run/screenshots/dev6-*.png` und beendet den Client. Ausschließlich eine Testwelt verwenden.

Inventarmodelle lassen sich mit `python3 tools/generate_vehicle_items.py` reproduzieren. Die Prüfdetails und JAR-Prüfsumme stehen in `docs/test-results/dev6.json`.
