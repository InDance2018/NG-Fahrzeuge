# Dev5 – Fahrer-Erkennung für Arclight

**Testnachtrag vom 17.10.2026:** Der Projektinhaber bestätigt alle bisherigen DEV-Stände auf MC02 sowie den bestandenen Drei-Spieler-/Chunk-/Neustarttest. [Aktueller Testbericht](PRAXISTEST-2026-10-17.md). Ältere Testangaben im folgenden Dokument beschreiben den damaligen Prüfstand.


Stand: 16. Oktober 2026 · Minecraft 1.20.2 · Forge 48.1.0 · Java 17

## Fehler und Korrektur

Auf MC02 wurde für Dev4 gemeldet: Einsteigen möglich, keine Fahranzeige und keine
Bewegung trotz gelöster beziehungsweise neu belegter Feststellbrems-Taste.
Die gelieferten Client-Protokolle bestätigen Dev4, die Registrierung von
DrivingClient am Forge-Bus und einen akzeptierten Fahr-Netzwerkkanal.
Sie enthalten keinen eindeutigen Laufzeitfehler für diesen Vorgang.

Im Arclight-Quellcode für 1.20.2 wird startRiding auf bridge$addPassenger
umgeleitet. Dadurch wird der mod-eigene addPassenger-Override umgangen, in dem
Dev4 den Fahrer zuwies. Beim Aussteigen gibt es denselben abweichenden Aufrufweg.

Dev5 gleicht daher die tatsächlichen Mitfahrer auf dem Server ab. Ein neu
hinzugekommener Spieler erhält einen freien Fahrerplatz. Ein ausgeschiedener
Fahrer wird entfernt und das Fahrzeug geparkt. Bereits sitzende Mitfahrer
übernehmen den Fahrerplatz weiterhin nicht automatisch. Die normalen Forge-
Ein-/Ausstiegsmethoden bleiben für unmittelbare Aktualisierungen erhalten.
Alle Steuerpakete bleiben auf den tatsächlich sitzenden Fahrer beschränkt.

Technische Referenz:
https://github.com/IzzelAliz/Arclight/blob/Net/arclight-common/src/main/java/io/izzel/arclight/common/mixin/core/world/entity/EntityMixin.java

## Prüfung

- Build und Reobf erfolgreich.
- 30/30 Forge-Servertests bestanden: 29 bestehende Prüfungen und eine neue
  Regression für fehlende mod-eigene Einstiegsmeldungen.
- Die neue Regression entfernt gezielt den vom normalen Einstieg gesetzten
  Fahrerzustand, erhält die tatsächliche Sitzbelegung und prüft Erkennung,
  freigeschaltete Bremse, angenommene Fahrpakete, Bewegung, Ausstieg und
  Fahrerwechsel ohne Übernahme durch vorhandene Mitfahrer.
- Keine neue Behauptung über einen tatsächlichen Serverneustart-Test oder einen
  erneuten Verkehrs-Integrationslauf mit Dev5; diese Nachweise stammen aus Dev4.
- Vor der Korrektur wurde Dev4 in einem isolierten Forge-Client mit integriertem
  Server geprüft: Fahrer erkannt, H-Aktion übertragen, Fahrpakete angenommen und
  Fahrzeugbewegung server- sowie clientseitig beobachtet. Tasteneingaben wurden
  über die Minecraft-KeyMapping-Schicht eingespeist. Das war kein MC02-Test.
- **Grundlegender Arclight-/MC02-Spieltest mit Dev5 bestanden laut Rückmeldung von InDance2018 am 16. Oktober 2026:** Einsteigen, Fahren, Lenken, km/h-Anzeige und Aussteigen.
- Weitere Fahrzustände, vollständige Sicht-/Ausstattungsprüfungen aller Fahrzeuggruppen, das Parksystem im Spiel und zwei echte Clients bleiben offen.

## Installation und Bedienung

Datei: ng_fahrzeuge-0.1.0-dev5.jar. Nur eine Fahrzeug-JAR gleichzeitig installieren.
Für eine einheitliche Testfassung Dev5 auf Client und Server einsetzen.
Netzwerkprotokoll 2 und gespeicherte Fahrzeugdaten bleiben unverändert.

Optionen → Steuerung → Tastenbelegung → NG Fahrzeuge: Feststellbremse und Licht
auf freie Tasten ohne Konflikte legen. Vorgaben: H und L. Einsteigen per
Rechtsklick, Feststellbremse lösen, W/S fahren und A/D lenken. Leertaste bremst,
Schleichen steigt aus. Die Fahranzeige erscheint für den erkannten Fahrer.

## Installation auf MC02 · 16. Oktober 2026

Dev5 ist nach ausdrücklicher Freigabe auf MC02 installiert und aktiviert.
Vor dem Austausch wurde die vollständige, gestoppte Serverinstanz gesichert;
Integrität und vollständiger Sicherungsumfang wurden bestätigt. Dev4 ist als
deaktivierte Rückfallversion erhalten. Der anschließende Serverstart ist
abgeschlossen; der Dienst läuft und die Bridge meldet einen frischen Online-Status.
Die Mod-Inventur führt ausschließlich Dev5 als aktive Fahrzeugfassung.
Am 16. Oktober 2026 hat InDance2018 den grundlegenden Dev5-Spieltest auf MC02 bestätigt: Einsteigen, Fahren, Lenken, km/h-Anzeige und Aussteigen funktionieren. Licht, Lackierung, sämtliche Fahrzeuggruppen, weitere Fahrzustände und Zwei-Client-Tests sind durch diese Rückmeldung noch nicht vollständig geprüft.

SHA-256: 1b93fb27a3b23ab7911249966aa2797547bf1e8cf7d0624dbf3c4e6b4fd3f8a4
