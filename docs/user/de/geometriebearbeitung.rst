Geometriebearbeitung
====================

Geometriebearbeitung |image0|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Das Geometriebearbeitungswerkzeug kann Eckpunkte hinzufügen, auswählen, verschieben und entfernen.
Es kann auch genutzt werden, um ganze Geometrien zu verschieben. Die hier gezeigte Funktionalität
werden teilweise auch durch die Werkzeuge Polygonerstellung, Linienerstellung und Punkerstellung
bereitgestellt.

Anwendungsbeispiel:

#. Erzeugen Sie aus den Beispieldaten eine Karte
#. Fügen Sie **bc\_border.shp**, **bc\_hospitals.shp** und **bc\_municipality.shp** zur Karte hinzu.
#. Zoom Sie zu einem kleinen Ausschnitt der Karte.
    |image1|
#. Wählen Sie die zu bearbeitenden Kartenlayer aus, z.B. "bc\_municipality" (die Gemeinden).
#. Verwenden Sie das Werkzeug "Geometrie bearbeiten" und wählen Sie eine Gemeinde.
    |image2|
#. Sie können das Werkzeug "Geometrie bearbeiten" nun zum Verschieben von Punkten verwenden. Bewegen
   Sie Ihre Maus über einen Eckpunkt. Klicken Sie und ziehen Sie den Punkt an eine andere Stelle.
    **Tip:** Der Kreis um den Punkt zeigt den aktuellen "Einrastbereich". Um weitere Information zum
   "Einrasten" zu erhalten, klicken Sie hier.
    |image3|
#. Sie können mit dem Werkzeug "Geometrie bearbeiten" auch neue Eckpunkte anlegen: Klicken Sie auf
   eine der geraden Linien zwischen zwei bestehenden Eckpunkten. Es erscheint dort ein neuer
   Eckpunkt, welcher nun ebenfalls verschoben werden kann.
    |image4|
#. Zum Löschen eines Eckpunkts wählen Sie einen Eckpunkt aus und drücken Sie die Taste "Entf" oder
   wählen Sie im Menü **Bearbeiten > Löschen**.
    |image5|
#. Indem Sie einen Auswahlrahmen um mehrere Eckpunkte ziehen, können Sie mehrere Eckpunkte
   gleichzeitig auswählen.
    |image6|
#. Alle derartig gewählten Eckpunkte können zusammen verschoben oder gelöscht werden.
    |image7|
#. Wollen Sie die ganze Geometrie verschieben, so drücken Sie **Strg+Alt** und ziehen Sie die
   betreffende Geometrie.
    |image8|
#. Nachdem die Bearbeitung abgeschlossen ist werden diese Änderungen übernommen, wenn Sie:

   #. einen Doppelklick mit der linken Maustaste ausführen.
   #. ein anderes Werkzeug wählen oder
   #. die Schaltfläche "Änderungen endgültig übernehmen" aus der Symbolleiste betätigen.
       |image9|

Jede der durchgeführten Änderungen kann mit **Strg+Z** oder **Bearbeiten > Rückgängig** widerrufen
werden. Widerrufene Änderungen können mittels **Strg+Y** oder **Bearbeiten > Wiederherstellen**
erneut ausgeführt werden.

Wollen Sie Attributinformationen (Sachdaten) eines Objekts ändern, so verwenden Sie dazu den
Featureeditor.

Tips
~~~~

-  Erweiterte Bearbeitungsfunktionen können helfen, das Umschalten zwischen verschiedenen Werkzeugen
   zu reduzieren. Ist diese Option eingeschaltet, lassen sich bspw. mit dem Werkzeug "Geometrie
   bearbeiten" wie beschrieben Eckpunkte hinzufügen.
-  Mehrere Geometrien können im `EditBlackboard <EditBlackboard.html>`__ (einer Art Zwischenablage)
   abgelegt werden und zusammen bearbeitet werden.

   -  **Shift** beim Anklicken fügt das gewählte Feature zum
      `EditBlackboard <EditBlackboard.html>`__ hinzu
   -  **Alt** beim Anklicken fügt Features hinzu oder entfernt diese.
   -  **Esc** (oder **Esc-Esc**, je nach Betriebssystem) löscht das
      `EditBlackboard <EditBlackboard.html>`__.

-  Verwenden mehrere Geometrien eine oder mehrere gemeinsame Eckpunkte, so wirkt sich das
   Verschieben dieser Eckpunkte auf alle betroffenen Geometrien aus.
-  Verwenden Sie das Einrasten, um Eckpunkte exakt an anderen Geometrien auszurichten.
-  Eine Linie kann mittels folgende Schritte geteilt werden:

   #. Wählen Sie die Linie aus, z.B. vom Kartenlayer "bc\_borders". Dazu muß dieser Layer ausgewählt
      sein!
       |image10|
   #. Fügen Sie an dem Punkt, an dem die Linie geteilt werden soll mit dem Werkzeug `Eckpunkt
      hinzufügen <8957.html>`__ einen Eckpunkt ein.
       |image11|
   #. Die Linie kann nun geteilt werden: Wählen Sie entweder im Menü "Bearbeiten > Linie teilen"
      oder im Kontextmenü **Operationen > Linie teilen**
       |image12|
       |image13|

|image14|

-  `6.6 Eckpunkt-Werkzeuge <6.6%20Eckpunkt-Werkzeuge.html>`__
-  `6.7 Geometrieerstellungswerkzeuge <6.7%20Geometrieerstellungswerkzeuge.html>`__
-  `6.8 Objektbearbeitungswerkzeuge <6.8%20Objektbearbeitungswerkzeuge.html>`__

(c) Copyright (c) 2004-2008 Refractions Research Inc. and others.

.. |image0| image:: /images/geometriebearbeitung/edit_vertex_mode.gif
.. |image1| image:: /images/geometriebearbeitung/tooledit1.png
.. |image2| image:: /images/geometriebearbeitung/tooleditfeature.png
.. |image3| image:: /images/geometriebearbeitung/movevertex.png
.. |image4| image:: /images/geometriebearbeitung/addvertex.png
.. |image5| image:: /images/geometriebearbeitung/removevertex.png
.. |image6| image:: /images/geometriebearbeitung/selectmanyvertex.png
.. |image7| image:: /images/geometriebearbeitung/movemany.png
.. |image8| image:: /images/geometriebearbeitung/movegeometry.png
.. |image9| image:: /images/geometriebearbeitung/accept.png
.. |image10| image:: /images/geometriebearbeitung/selectline.png
.. |image11| image:: /images/geometriebearbeitung/addlinevertex.png
.. |image12| image:: /images/geometriebearbeitung/editmenu.png
.. |image13| image:: /images/geometriebearbeitung/contextmenu.png
.. |image14| image:: http://udig.refractions.net/image/DE/ngrelr.gif
