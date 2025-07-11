# 🎭 Staatstheater Augsburg App - Offizielle Version 1.0.0 🎭

Herzlich willkommen im GitHub-Repository der inoffiziellen Android-App für das Staatstheater Augsburg! Diese Anwendung bietet eine bequeme Möglichkeit, aktuelle Termine und Nachrichten des Staatstheaters direkt auf Ihrem Android-Gerät abzurufen.

---

## ✨ Features auf einen Blick

Diese App wurde entwickelt, um Ihnen einen schnellen und einfachen Zugang zu den wichtigsten Informationen des Staatstheaters Augsburg zu ermöglichen:

* **📅 Aktueller Spielplan:** Erhalten Sie einen Überblick über bevorstehende Aufführungen, Konzerte und Veranstaltungen. Die App ruft die neuesten Termine dynamisch ab und präsentiert sie übersichtlich.
* **📰 Neueste Nachrichten:** Bleiben Sie informiert über Ankündigungen, Pressemitteilungen und interessante Artikel rund um das Staatstheater.
* **🌐 Web-Integration:** Durch den Einsatz von Bibliotheken wie Retrofit, Moshi und Jsoup werden Inhalte direkt von der offiziellen Webseite des Staatstheaters abgerufen und für die mobile Anzeige aufbereitet.
* **Intuitive Navigation:** Wechseln Sie nahtlos zwischen den verschiedenen Bereichen der App (Termine, News etc.) dank einer benutzerfreundlichen Navigationsstruktur.
* **Modernes Design:** Die App ist mit Jetpack Compose entwickelt und bietet ein modernes, ansprechendes Benutzerinterface, das sich an den aktuellen Android-Designrichtlinien orientiert.

---

## 📱 Installation der App

Die App kann direkt als APK-Datei auf Ihrem Android-Gerät installiert werden. Bitte folgen Sie diesen Schritten:

1.  **APK-Datei herunterladen:**
    * Navigieren Sie zum [Releases-Bereich dieses GitHub-Repositories](https://github.com/paalwie/staatstheater-augsburg-app/releases).
    * Suchen Sie den neuesten Release (z.B. "Version 1.0.0 - Initial Release").
    * Laden Sie die Datei `app-release.apk` herunter.

2.  **Installation aus unbekannten Quellen erlauben:**
    * Aus Sicherheitsgründen blockiert Android standardmäßig die Installation von Apps, die nicht aus dem Google Play Store stammen. Sie müssen diese Einstellung einmalig für die Installation der APK aktivieren.
    * **Je nach Android-Version und Gerätehersteller variieren die Schritte leicht:**
        * **Android 8.0 (Oreo) und neuer:**
            * Öffnen Sie die `Einstellungen` Ihres Geräts.
            * Gehen Sie zu `Apps & Benachrichtigungen` (oder ähnlich, z.B. `Apps` oder `Anwendungen`).
            * Tippen Sie auf `Spezieller App-Zugriff` oder `Besondere Zugriffsrechte`.
            * Wählen Sie `Apps installieren aus unbekannten Quellen` (oder `Unbekannte Apps installieren`).
            * Finden Sie Ihren Webbrowser (z.B. Chrome, Firefox) oder Ihren Dateimanager, über den Sie die APK heruntergeladen haben, und aktivieren Sie die Option "Installation aus dieser Quelle zulassen" (oder "Dieser Quelle vertrauen").
        * **Android 7.0 (Nougat) und älter:**
            * Öffnen Sie die `Einstellungen` Ihres Geräts.
            * Gehen Sie zu `Sicherheit`.
            * Aktivieren Sie die Option `Unbekannte Quellen` (oder `Unbekannte Herkunft`). Bestätigen Sie die Warnmeldung.

3.  **APK-Datei öffnen und installieren:**
    * Nachdem Sie die Installation aus unbekannten Quellen erlaubt haben, öffnen Sie die heruntergeladene `app-release.apk`-Datei (Sie finden diese normalerweise in Ihrem `Downloads`-Ordner).
    * Folgen Sie den Anweisungen auf dem Bildschirm, um die App zu installieren.
    * Nach der Installation können Sie die Einstellung für "Unbekannte Quellen" aus Sicherheitsgründen wieder deaktivieren, falls gewünscht.

---

## 🛠️ Entwicklungsinformationen

Diese App wurde mit den neuesten Android-Entwicklungstechnologien erstellt:

* **Sprache:** Kotlin
* **UI-Toolkit:** Jetpack Compose
* **Minimum API Level:** 24 (Android 7.0 Nougat)
* **Target API Level:** 35 (Android 15)
* **Build System:** Gradle (Kotlin DSL)
* **Wichtige Bibliotheken:**
    * Retrofit & Moshi: Für den sicheren und effizienten Datenaustausch über HTTP und JSON-Parsing.
    * OkHttp Logging Interceptor: Für detaillierte Netzwerk-Logs während der Entwicklung.
    * Jsoup: Zum Parsen und Extrahieren von Daten aus HTML-Webseiten.
    * AndroidX Lifecycle Components: Für eine robuste und reaktive Anwendungsarchitektur.
    * AndroidX Navigation Compose: Für die Verwaltung der Navigation innerhalb der App.
    * Core Library Desugaring: Ermöglicht die Nutzung moderner Java 8+ APIs (z.B. `java.time` für Datum und Zeit) auch auf älteren Android-Geräten.

---

## 🧪 Getestete Geräte

Die App wurde erfolgreich auf einem **Samsung Galaxy A35** getestet und sollte auf den meisten Android-Geräten mit **Android 7.0 (API Level 24)** oder höher stabil laufen.

---

## 🤝 Mitwirken

Dieses Projekt ist als Beispiel und zur Nutzung gedacht. Wenn Sie Ideen für Verbesserungen oder Funktionen haben, können Sie gerne ein Issue eröffnen oder sich mit mir in Verbindung setzen.

---

Viel Spaß mit der Staatstheater Augsburg App!

---
