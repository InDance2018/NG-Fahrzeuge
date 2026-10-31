# NG-Fahrzeuge · aktueller Stand

Stand: **31.10.2026** · Minecraft Java 1.20.2 · Forge 48.1.0 · Java 17.

| Bereich | Stand |
|---|---|
| Dokumentierte Entwicklungsgrundlage | 0.1.0-dev12; elf Fahrzeugtypen und 20 Dev12-Baupunkte |
| Öffentliche GitHub-Releases | Noch kein Release vorhanden |
| Beta-1-HUD-Korrektur | Im Branch [fix/beta1-hud-size](https://github.com/InDance2018/NG-Fahrzeuge/tree/fix/beta1-hud-size), Commit 3a0e4a735e49363474c87dbd3c64b6296b4a24d3; geprüfter Kandidat, kein veröffentlichter Release |
| Weiterer erfasster Versuch | Beta1-Kamera2; nicht mit dev12 oder HUD-Fix1 gleichsetzen, vollständige Quell-/Buildzuordnung offen |
| Verkehrsanbindung | dev12 mit Verkehr beta2-dev12: 16 gezielte gemeinsame Tests |

## Vorhandene Funktionen und Prüfungen

[DEV12.md](DEV12.md) dokumentiert elf Fahrzeugtypen bis zum Sattelzug: City, Family, Transporter, Abschleppwagen, Bus, Motorrad, Pickup, Geländewagen, Sportwagen, Taxi und Sattelzugmaschine. Unter anderem Türen, Sitze, Beleuchtung, Tank/Verbrauch, Schäden/Reparatur, Schlüssel, Anhänger/Fracht, Kennzeichen, Getriebe, Kühlung und Diagnose.

Für dev12 sind 129 erforderliche Forge-Servertests, separate Neustart-/Chunkprüfungen und ein grafischer Clientlauf dokumentiert. [Eigentümer-Praxistest 17.10.](PRAXISTEST-2026-10-17.md): drei Spieler, 15 Minuten Chunkwechsel und vier Neustarts. Das sind datierte Nachweise und keine neue Abnahme aller späteren Beta-Builds.

## Beta 1 · HUD-Fix1

[Technischer Bericht im zugehörigen Branch](https://github.com/InDance2018/NG-Fahrzeuge/blob/fix/beta1-hud-size/HUD-FIX1.md), [Fehlermeldung #2](https://github.com/InDance2018/NG-Fahrzeuge/issues/2), [Kandidatenbericht #3](https://github.com/InDance2018/NG-Fahrzeuge/issues/3).

Kompaktes Fahr-HUD unten rechts; F4 schaltet Details, Bild runter/hoch passt die lokal gespeicherte Größe an. Pixelbasierte Skalierung statt Vergrößerung mit dem Minecraft-GUI. 560 Layoutkombinationen, Java-/Forge-Build und tatsächlicher Client-Sicht-/Bedienlauf sind dokumentiert. Spielmechanik und Netzwerk blieben gegenüber der verwendeten Beta1-Grundlage unverändert; kein neuer gemeinsamer Mehrspieler-Praxistest.

Kandidat: ng_fahrzeuge-0.1.0-beta1-hudfix1.jar, 355.813 Bytes, SHA-256 59a0f6d7830aee7e7a78050d4c1051dcd20708b8bc436b66d59c7e3d4cc97f3b. Die Datei wird durch diese Dokumentationspflege nicht als Download oder Release veröffentlicht. HUD-Fix1 und Kamera2 sind unterschiedliche Stände.

## Offene Arbeit

- Aktuelle Beta-/Kamerakandidaten samt Quellen, Artefakten und passenden Clients eindeutig zuordnen und abnehmen.
- Sonderfälle für Auflieger-/Parkgeometrie und akustische Feinprüfung fortführen.
- Echter Elektroantrieb/Traktionsakku, Lade-Gegenstelle und optionale City-Wirtschaft bleiben offen.
- Stable-Release erst nach eigenständiger Freigabe; vorhandene Kandidaten sind keine stabile Gesamtveröffentlichung.

NG-Fahrzeuge bleibt eigenständig ohne Verkehrs-/City-Pflichtinstallation. NG-City dev-0.2.10 ist am 21.10. vom Eigentümer im Einzel-/Mehrspieler bestätigt; gemeinsame Wirtschaft ist damit nicht implementiert oder abgenommen.

[Roadmap](ROADMAP.md) · [Änderungen](CHANGELOG.md) · [Webkatalog](https://nevergate.de/ng-fahrzeuge/).

## Einordnung

Dokumentationsabgleich vom 31.10.2026. Versionsnachweise, berichtete Versuche, Nutzerabnahmen und öffentliche Releases werden getrennt geführt. Bestehende Tests werden mit ihrem ursprünglichen Datum übernommen; diese Aktualisierung führt keinen neuen Build oder Spieltest aus und veröffentlicht keine neue Binärdatei.
