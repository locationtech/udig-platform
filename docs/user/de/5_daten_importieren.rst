5 Daten importieren
===================

Daten importieren
~~~~~~~~~~~~~~~~~

Daten können wie folgt in uDig importiert werden:

-  Durch Drag und Drop von außerhalb nach uDig, auch aus einem Browser.
-  Durch Drag und Drop aus einem anderen uDig-Fenster
-  Durch Hinzufügen von Layern in eine Karte mit dem Importassistenten: **Layer > Hinzufügen** oder
   **Datei > Neu > Layer**

Die "Drag und Drop"-Funktionen sind alledings betriebssystemabhängig und funktionieren nicht auf
allen Computern. Wenn Ihr Computer dies nicht unterstützt, können Sie immer noch den Assistenten
(über **Layer > Hinzufügen**) verwenden.

Verwenden von "Drag und drop" oder "Kopieren und Einfügen"
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Auf den gebräuchlichsten Computersystemen wie Macintosh und Macintosh kann der jeweilige
Dateisystembrowser (z.B. der "Explorer") verwendet werden, um Verzeichnisse und Dateien aus dem
Dateisystem in uDig zu laden.

Dazu müssen die entsprechende Ressources (Daten) in die korrekte Position in uDig's Projektsystem
eingefügt werden. Es reicht nicht, die Daten einfach auf eine leere Fläche in uDig zu ziehen.

Durch folgende Schritte werden neue Layer in der Karte erzeugt:

#. Öffnen Sie ihren Dateisystembrowser
#. Wählen Sie die Datei "countries.shp" aus dem
   `Demodatenpaket <http://udig.refractions.net/docs/data-v1_1.zip>`__ und ziehen Sie sie auf den
   Eintrag einer existierenden Karte im Projektfenster.
    Bei diesem Drag und Drop wird die Karte, der Daten hinzugefügt werden, automatisch ausgewählt.

Sie können mit den Daten auch eine neue Karte erstellen:

-  Ziehen Sie die Ressouce dazu aus dem Dateisystembrowser auf ein Projekt (nicht eine Karte) ODER
-  Kopieren Sie die Datei im Dateisystembrowser, markieren Sie dann das Projekt und wählen Sie
   anschließend **Bearbeiten > Einfügen**.
    |image0|

Auch durch Drag und Drop von Informationen auf ein Projekt können neue Karten erstellt werden:

#. Ziehen Sie den folgenden Link auf ein Projekt:
   [|image1|\ ]
#. Wählen Sie den Layer "bathymetry"
    |image2|
#. Dieser Layer ist als Hintergrund geeignet. Ordnen Sie deshalb ihre Layer im Layerfenster durch
   Drag und Drop um. Sie können auch den betreffenden Layer auswählen und die Pfeiltasten
   hoch/runter verwendet.
    |image3|

Verwenden des Importassistenten
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

Der Importassistent wird ebenso dazu verwendet, Ressourcen für die Arbeitsfläche verfügbar zu
machen.

#. Wählen Sie **Datei > Neu > Karte** zum Erstellen einer neuen, leeren Karte.
#. Öffnen Sie über **Layer > Hinzufügen** oder **Datei > Neu > Layer** den Importassistenten.
#. Wählen Sie im Importassistenten den gewünschten Datentyp, z.B. "Web Map Server" und klicken Sie
   dann auf "Weiter". Sie können auch auf "Web Map Server" doppelklicken.
    |image4|
#. Geben Sie folgende GetCapabilities-URL ein und wählen Sie "Weiter":
   http://atlas.gc.ca/cgi-bin/atlaswms_en?VERSION=1.1.1&Request=GetCapabilities&Service=WMS
    |image5|
#. Wählen Sie die Karte für den Maßstab 1:50Mio aus der angezeigten Liste ("15 Million Subset").
    |image6|
#. Bestätigen Sie mit "Fertigstellen". Es wird nun eine Karte mit dem gewählten Layer erzeugt.
   Eventuell sollten Sie noch den Bildschirmausschnitt mittels `Zoom <6.1%20Zoomen.html>`__
   verändern.
    |image7|

Weiter im Tutorial: `6 Werkzeuge <6%20Werkzeuge.html>`__

(c) Copyright (c) 2004-2008 Refractions Research Inc. and others.

.. |image0| image:: download/thumbnails/3768/Karte%20mit%20countries_shp.png
   :target: http://udig.refractions.net/confluence//download/attachments/3768/Karte%20mit%20countries_shp.png
.. |image1| image:: download/thumbnails/3768/Karte%20laedt%20DMSolutions.png
   :target: http://udig.refractions.net/confluence//download/attachments/3768/Karte%20laedt%20DMSolutions.png
.. |image2| image:: download/thumbnails/3768/Karte%20laedt%20DMSolutions.png
   :target: http://udig.refractions.net/confluence//download/attachments/3768/Karte%20laedt%20DMSolutions.png
.. |image3| image:: download/thumbnails/3768/Bathymetry%20verschieben.png
   :target: http://udig.refractions.net/confluence//download/attachments/3768/Bathymetry%20verschieben.png
.. |image4| image:: download/thumbnails/3768/Importassistent%20WMS.png
   :target: http://udig.refractions.net/confluence//download/attachments/3768/Importassistent%20WMS.png
.. |image5| image:: download/attachments/3768/Importassistent%20WMS_2.png
.. |image6| image:: download/attachments/3768/Importassistent%20WMS_3.png
.. |image7| image:: download/attachments/3768/Importassistent%20WMS_4.png
