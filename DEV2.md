# Dev2 – NG City als platzierbarer Modellprototyp

Stand: 16. Oktober 2026 · Minecraft 1.20.2 / Forge 48.1.0 / Java 17

## Neu

- NG City als eigene Fahrzeugentität und Platzierungsgegenstand mit eigenem NG-Fahrzeuge-Inventarreiter.
- Eigenes einfaches türkises Minecraft-Modell mit Rädern, dunklen Fenstern, Griffen und Leuchten. Die Leuchten sind noch nicht schaltbar.
- Rechtsklick auf die Oberseite des Bodens zum Platzieren, Ausrichtung in 90-Grad-Schritten nach Blickrichtung.
- Fahrzeugfläche ungefähr 1,8 × 2,6 Blöcke, Höhe 1,4 Blöcke; für Platzierung entsprechend freie Fläche bereithalten.
- Prüfung von Hindernissen, Weltgrenze und Baurechten. Survival verbraucht ein Fahrzeugitem; Kreativ verbraucht keines.
- Schleichen + Rechtsklick mit leerer Haupthand nimmt das Fahrzeug wieder auf. Survival erhält genau einen Gegenstand zurück. Kreativ entfernt das Modell ohne zusätzlichen Gegenstand.
- Schwerkraft und Bodenkollision; keine horizontale Eigenbewegung. Kein Einsteigen oder Fahren in diesem Stand.
- Position, Ausrichtung, UUID und benutzerdefinierter Name werden über Minecraft-Entitätsdaten gespeichert.
- Deutsche und englische Texte; keine Pflichtabhängigkeit zu NG Verkehr.

## Geprüft

Build und Forge-Reobf erfolgreich. Sechs Tests auf einem eigenständigen Forge-GameTestServer ohne NG Verkehr bestanden:

1. Gültige Platzierung verbraucht genau ein Item; zweite Platzierung im selben Raum wird abgewiesen.
2. Hindernis und fehlende Baurechte verhindern Platzierung ohne Itemverlust.
3. Kreativplatzierung behält den Gegenstand.
4. Wiederaufnahme respektiert Baurechte und dupliziert bei Wiederholung keinen Gegenstand.
5. NBT-Speichern und Wiederladen erhält Identität, Namen, Position, Ausrichtung und die gedrehte Fahrzeugfläche.
6. Schwerkraft setzt das Fahrzeug auf festen Boden, ohne horizontal abzudriften.

JAR-Inhalt, ZIP-Integrität, JSON-Ressourcen, expandierte Metadaten und fehlende Pflichtabhängigkeit zu NG Verkehr geprüft. Die automatisierten Tests sind nicht in der ausgelieferten Mod-JAR enthalten.

Der Nutzer hat den gemeinsamen Start der vorherigen Dev1 mit NG Verkehr bestätigt. Diese Rückmeldung bestätigt weder die Dev2-Darstellung noch die Fahrzeugerkennung im Parksystem.

## Kurzer Spieltest

1. Minecraft schließen. Alte NG-Fahrzeuge-JAR entfernen, Dev2-JAR in den mods-Ordner legen. NG Verkehr darf installiert bleiben.
2. Im Kreativinventar den Reiter **NG Fahrzeuge** öffnen und **NG City** nehmen. Alternativ: `/give @s ng_fahrzeuge:ng_city`.
3. Auf einer freien ebenen Fläche den Boden rechtsklicken. Fahrzeug von vorne, hinten und beiden Seiten ansehen.
4. Eine zweite Platzierung im selben Fahrzeug versuchen: sie muss abgewiesen werden.
5. Mit leerer Hand schleichen und das Fahrzeug rechtsklicken: es wird entfernt.
6. Ein Fahrzeug erneut platzieren, die Welt verlassen und wieder betreten: Position und Ausrichtung prüfen.

## Offen

Client-Sicht-/Bedienprüfung, Test mit freigegebener Forge-Laufzeit und der reobfuskierten JAR,
echter Welt-/Serverneustart, Chunkwechsel, Mehrspieler und konkrete NG-Verkehr-Erkennung sind noch offen.
Kein Fahrzeug-KI-Verkehr, keine Mitfahrer, kein Inventar und kein Schadenssystem.
Einsteigen/Fahrsteuerung, Feststellbremse, Licht/Farben sowie NG Family/NG Transporter folgen.
Keine Installation auf MC02 durchgeführt.

Datei: `ng_fahrzeuge-0.1.0-dev2.jar` (17710 Bytes).
SHA-256: `2157f86d20aceba60679e055b4772c434774a3615a4116bfbdd1e2f786876b11`.
