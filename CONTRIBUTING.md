# Entwicklung

Basis: JDK 17, Gradle 8.1.1, Minecraft 1.20.2, Forge 48.1.0 und ForgeGradle 6.0.24.
Gradle muss vorerst lokal installiert sein; ein Gradle-Wrapper ist noch nicht enthalten.

```sh
gradle --version
gradle build
```

Das erste Kommando muss JDK 17 und Gradle 8.1.1 anzeigen. Der erste Build benötigt
Netzzugriff auf die offiziellen Gradle-, Forge-, Mojang- und Maven-Repositories.
Die Ausgabe nach erfolgreichem Build/Reobf liegt unter
`build/libs/ng_fahrzeuge-0.1.0-dev3.jar`.

Entwicklungsstart: `gradle runClient` oder `gradle runServer`.
Ein Server muss nach dem ersten Start dessen Minecraft-EULA anzeigen; vor einer
bewussten Zustimmung bleibt er gestoppt. Für spätere Installationen benötigen
Client und Server dieselbe Modfassung.

Die Mod startet unabhängig von NG Verkehr. Gemeinsame Klassen dürfen keine
Clientklassen importieren. Rendercode gehört später nach
`de.nevergate.fahrzeuge.client` und wird nur auf dem Client registriert.
NG City, Platzierung, Einsteigen und Fahrsteuerung sind implementiert. Ein eigenes
Serverpaket übermittelt ausschließlich die gedrückten Tasten. Der Server akzeptiert
sie nur vom aktuellen Fahrer des referenzierten Fahrzeugs und berechnet Bewegung,
Geschwindigkeit und Kollisionen selbst. Bei mehr als zehn Ticks ohne neue Eingabe
wird gebremst; ohne Fahrer sowie beim Wiederladen wird geparkt. Clientklassen
liegen ausschließlich im Client-Paket. Basisspeicherung nutzt Minecraft-Entitätsdaten.

Automatisierte Tests: `gradle runGameTestServer`. Die Tests liegen separat unter
`src/gametest/java` und sind nicht Teil der Mod-JAR. In einer reinen Serverumgebung
können unnötige Client-Downloads mit
`gradle runGameTestServer -x downloadAssets -x extractNatives` ausgelassen werden.

Vor einer Freigabe: Build/Reobf, Start ohne NG Verkehr auf dediziertem Server und
Client sowie die jeweils neuen Spielfunktionen prüfen. Ein Build bestätigt keinen
Spieltest. Keine automatische Installation auf MC02 oder anderen Spielservern.
