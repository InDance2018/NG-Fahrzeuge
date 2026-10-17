# Beta 1 · HUD-Fix 1 · 17. Oktober 2026

Behebt [Issue #2](https://github.com/InDance2018/NG-Fahrzeuge/issues/2): Das bisherige feste 220×191-GUI-HUD verdeckte bei großer Minecraft-GUI-Skalierung die Fahrersicht.

Das Standard-HUD sitzt jetzt unten rechts oberhalb der Schnellleiste. Es zeigt Fahrzeug, Tempo, Gang, Tank und die wichtigste Warnung. Kilometer, Zustand, Blinker, Licht, Gurte, Drehzahl, Kühlung und weitere Daten bleiben in der zuschaltbaren Detailansicht erreichbar. F1 blendet das gesamte HUD aus; in geöffneten Menüs wird das Fahr-HUD ausgeblendet.

- **F4:** Details ein/aus. Nach dem Aussteigen startet die nächste Fahrt wieder kompakt.
- **Bild runter / Bild hoch:** HUD kleiner/größer, in Schritten von 10 % zwischen 60 und 140 %. Umbelegbar in Minecraft → Optionen → Steuerung → NG-Fahrzeuge.
- Größe wird lokal in `config/ng_fahrzeuge-hud.properties` gespeichert (`scale=1.0` als Standard).
- Die Darstellung rechnet in Bildschirmpixeln statt mit der Minecraft-Menügröße. Das kompakte HUD ist höchstens 28 % der Fensterbreite und 16 % der Fensterhöhe groß; Details höchstens 46 % der Fensterhöhe. Zusätzlich bleibt die Schnellleiste frei. Bei 1920×1008 Pixeln beträgt die Standardgröße 400×148 Pixel.

## Datei und Kompatibilität

`ng_fahrzeuge-0.1.0-beta1-hudfix1.jar` · Forge-Modversion `0.1.0-beta1` · Manifest-Build `beta1-hudfix1`.

Dies ist der korrigierte, noch unveröffentlichte Beta-1-Kandidat. Eindeutiger Dateiname und Manifest-Build unterscheiden ihn von der vorläufigen Beta 1. Minecraft 1.20.2, Forge 48.1.0, Java 17. Die vorhandene NG-Verkehr Beta 2 erkennt die unveränderte Modversion weiterhin. Spielmechanik-Klassen und Netzwerkcode sind bytegleich zur bisherigen Beta 1; der laufende MC02 wurde bei dieser HUD-Korrektur nicht verändert.

Minecraft schließen, die bisherige Fahrzeug-JAR aus dem aktiven mods-Ordner entfernen und durch die HUD-Fix-JAR ersetzen. Nur eine aktive NG-Fahrzeuge-JAR verwenden. NG-Verkehr Beta 2 bleibt verwendbar.

SHA-256: `59a0f6d7830aee7e7a78050d4c1051dcd20708b8bc436b66d59c7e3d4cc97f3b` · 355813 Bytes.

## Prüfung

- Java-17-/Forge-Produktionsbuild einschließlich Reobf und `checkHudLayout`: erfolgreich.
- 560 Kombinationen aus Auflösung, GUI-Skalierung, HUD-Größe und Detailansicht: innerhalb der Fenstergrenzen, Größenlimits eingehalten, Schnellleiste frei. Gleiche Pixelgröße bei unterschiedlichen Minecraft-GUI-Skalierungen geprüft.
- Echter isolierter Forge-Client: Cockpit mit kompaktem HUD bei GUI 4; Details über F4 bei GUI 4 und 2; Verkleinerung per Bild runter; F1 blendet aus. Automatische Szene abgeschlossen (`HUD_VISUAL_KEYS_AND_SCREENSHOTS_PASS`), Aufnahmen visuell kontrolliert.
- JAR-Integrität, Java-17-Klassen und Metadaten geprüft; keine Testklassen in der Produktions-JAR. Spielmechanik-Klassen, Modelle, Texturen und Rezepte unverändert gegenüber Beta 1.
- Ein erneuter gemeinsamer Mehrspieler-Praxistest dieser HUD-Fassung wurde noch nicht durchgeführt. Vorherige dev12-Praxistests bleiben historische Nachweise ihrer jeweiligen Builds.

Reproduzieren: Gradle 8.1.1 mit Java 17, `gradle --offline build`. `gradle -PhudVisual runClient` startet die isolierte HUD-Szene, wenn eine Testwelt unter `run/saves/hudfix` und ein Grafikdisplay vorhanden sind. Testwelten und Screenshots gehören nicht in die Produktions-JAR.

Stand: Korrektur gebaut und geprüft, keine öffentliche Release-Veröffentlichung oder Website-Veröffentlichung.
