Neuheiten in Version 1.1
========================

Was ist neu in uDig 1.1
=======================

Im Folgenden erfahren Sie einige der Änderungen seit uDig 1.0.5:

Komplett erneuerte Bearbeitungswerkzeuge
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

|image0|

|image1|

|image2|

|image3|

 

|image4|

|image5|

|image6|

Die alten Bearbeitungswerkzeuge wurden verworfen und neue Werkzeuge mit besserem Nutzer-Feeedback
wurden hinzugefügt. Neben besserer Benutzerführung existiert nun auch ein neues Werkzeug-Framework,
mit dem das Erstellen neuer Werkzeuge in wenigen Minuten möglich ist.

+------------------+------------------+------------------+------------------+------------------+------------------+
| |image13|        |
| |image14|        |
| |image15|        |
| |image16|        |
| |image17|        |
| |image18|        |
+------------------+------------------+------------------+------------------+------------------+------------------+

Dahingehend existiert ein öffentliches Tutorial auf `Edit Tool
Tutorial <http://udig.refractions.net/confluence//display/DEV/Edit+Tool+Example>`__.

Erzeugen leerer Layer
~~~~~~~~~~~~~~~~~~~~~

Es ist nun möglich, einen neuen Kartenlayer zu erzeugen, indem man den Menüpunkt **Layer >
Erzeugen** auswählt. Dieser Layer befindet sich allerdings nur im Hauptspeicher und wird nicht
automatisch gespeichert.

Erzeugen neuer Featuretypen
~~~~~~~~~~~~~~~~~~~~~~~~~~~

| Wurde ein Service in den Katalog importiert, kann man diesen Service anwählen und "Operationen >
Bearbeiten > Featuretyp erzugen" wählen, um so einen neuen Featuretyp in diesem Service zu
defininieren.
|  Beispiel: Importieren Sie einen Shapefile. Wählen Sie den Service im Katalog. Wählen Sie
anschließend "Operationen > Bearbeiten > Featuretyp erzugen" und definieren Sie den Featuretyp. Nach
dem Anklicken von "OK" wird ein neuer Shapefile-Kartenlayer erstellt, in den Sie nun aus anderen
Layern kopierte Feature einfügen oder neue Feautures dieses Typs mittels der Bearbeitunswerkzeuge
erzeugen können.

Thematische Stile
~~~~~~~~~~~~~~~~~

Die Stilauswahl geschieht nun über einen Dialog, der es u.a. erlaubt, ein Thema nach den Werten
eines Attributs einzufärben. Der ColorBrewer der Pennsylvania State University wurde dabei
integriert.

|image19|

Mylar
~~~~~

Der Mylar-Effekt entstammt ursprünglich der GIS-Community. UDig erlaubt es damit, den aktiven
Kartenlayer hervorzuheben und alle anderen Informationen auf der Karte in den Hintergrund treten zu
lassen.

+----------------------------------------------------+----------------------------------------------------+
| Cannot resolve external resource into attachment.  |
| Cannot resolve external resource into attachment.  |
+----------------------------------------------------+----------------------------------------------------+

Umformen
~~~~~~~~

Die Sachdaten von Objekten können in GISX verändert werden, sowohl namentlich als auch inhaltlich.
Dabei wird mit Hilfe einer in CQL (`Constraint Query
Language <http://udig.refractions.net/confluence//display/EN/Constraint+Query+Language>`__)
erstellten Definition aus den bestehenden Attributen eine neue Sammlung von Attributen erstellt.

-  Soll ein Attribut auch im Ergebnis erscheinen, listen Sie es einfach auf - mit dem gleichen
   Namen:
   BESCHREIBUNG=BESCHREIBUNG
-  Zum Umbenennen einer Datenspalte verwenden Sie den Ausdruck <NeuerName>=<AlterSpaltenname>:
   BESCHREIBUNG=NOTE
-  Es können Spalten mit konstantem Wert hinzugefügt werden:
   JAHR=2008
-  Auch berechnungen aus anderen Spalten sind mit Hilfe von Ausdrücken möglich:
   BEVÖLKERUNGSDICHTE=BEVÖLKERUNG/area(THE\_GEOM) (mit THE\_GEOM als Geometriespalte)
-  Durch Weglassen einer Spalte in der CQL-Definition erscheint diese auch nicht in den umgeformeten
   Daten.

Tabellenfenster
~~~~~~~~~~~~~~~

Ein Tabellenfenster wurde hinzugefügt, um damit die Features der aktuellen Auswahl zu betrachten. In
der Standardperspektive versteckt sich das Tabellenfenster hinter dem Katalog.

|image20|

Validierung
~~~~~~~~~~~

Es ist nun möglich, verschiedene Validierungen an einem Layer durchzuführen. Diese Testserie kann
angepaßt werden, und nicht bestandene Test werden in einer Problemliste verwaltet.

Verbesserte Drag-und-Drop-Fähigkeiten
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

An mehreren neuen Stellen steht nun Drag und Drop zur Verfügung. Versuchen Sie einmal folgende
Tricks:

-  Wählen Sie einige Features mit Hilfe des alten Auswahlbox-Werkzeugs. Wählen Sie nun das Werkzeug
   "Featureauswahl" und ziehen Sie die Auswahl in den Textbereich.
-  Erzeugen Sie einen neuen Kartenlayer. Ziehen Sie einfach bestehende Features auf den neuen
   Kartenlayer.

Kopieren und Einfügen (Copy und Paste) wurden hinzugefügt.
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Hier dazu einige Ideen zm Probieren:

-  Wählen Sie einige Features mittels der Auswahlbox. Kopieren Sie die Auswahl und fügen Sie sie in
   einen Texteditor ein.
    Wählen Sie ein Feature mit der "Featureauswahl" und kopieren Sie es in einen Texteditor.

Import und Export von "Open Web Services Context"
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Es ist nun möglich OWS context documents zu importieren und auch, bestehende Karten als OSW-Kontext
zu exportieren.

Browserfenster
~~~~~~~~~~~~~~

Ein neues Fenster mit einem Browser darun wurde hinzugefügt.

Suchfenster
~~~~~~~~~~~

Ebenfalls neu ist das Suchfenster. Dieses Fenster erlaubt die Eingabe einer Adresse und zeigt eine
Liste der entsprechenden Funde an. Ein Klick auf eines dieser Ergebnisse zeigt das Element im
Kartenausschnitt und läßt es blinken,

Neue Perspektive
~~~~~~~~~~~~~~~~

Für all dienjenigen, die schon immer etwas anders waren und/oder dachten gibt es nun eine
alternative Kartenperspektive, zu erreichen im Menü unter "Fenster > Perspektive öffnen >
Alternative Kartenperspektive.

Gespeicherte Orte
~~~~~~~~~~~~~~~~~

Ein weiteres neues Fenster ermöglocht es nun, geographische Orte (also Koordinaten) als Lesezeichen
abzuspeichern und später auf Knopfdruck zu diesen Orten zurückzukehren. (Dank, Cole!)

Export
~~~~~~

Es stehen nun mehrere Exportfunktionen für Karten zur Verfügung:

-  Export als Bild. Flexibler als das Arbeiten mit Screenshots.
-  PDF Export: Funktioniert genauso einfach. Der aktuelle Kartenausschnitt landet in einem
   PDF-Dokument.
-  Shapefile Export: Erfaßte Karten oder Vektordaten von der anderen Seite der Erde lassen sich nun
   bei Bedarf auch als lokale Datei (Shapefile) speichert. So kann man sie schneller wieder laden.

Beseitigte Fehler für den Mac
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Einige der größeren Fehler für den Macintosh wurden beseitigt. Am auffallendsten waren dabei die
Probleme beim Neustarten von uDig und Probleme mit Drag und Drop. Weiterhin bleiben aber einige
Einschränkungen am Mac: Ist keine Karte offen, kann man keine Objekte in den Editorbereich ziehen.
Das Ziehen auf Karten ist jedoch möglich, genauso wie das Ziehen auf das Kartenlayerfenster (was den
gleichen Effekt hat).

Neue Benutzervorgaben
~~~~~~~~~~~~~~~~~~~~~

Einige neue Optionen, u.a. für die Erstellung von Karten, wurden hinzugefügt. Gehen Sie dazu unter
**Fenster > Benutzervorgaben**. Einige der neuen Optionen sind:

-  Kartenhintergrundfarbe
-  Transparenzen (langsamer, aber gutaussehend)
-  Anti-aliasing (langsamer, aber gutaussehend)
-  Ein Standard-Koordinatenreferenzsystem (CRS) für neue Karten

Neue Druck-API
~~~~~~~~~~~~~~

Die API für die Druckfunktionen wurde überarbeitet, und das Programmieren von Erweiterungen verlangt
nicht länger die Kenntnis von Details von EMF. Des weiteren kam ein Erweiterungspunkt für
Druckrahmen hinzu.

Weitere Änderungen
~~~~~~~~~~~~~~~~~~

-  Distanzmeßwerkzeug
-  Selbstdefinierte CRS werden nun für die spätere Wiederverwendung gespeichert.
-  Nutzerdefinierte Stichworte können zu einem CRS hinzugefügt werden und erlauben das leichtere
   Wiederfinden.
-  Ein neuer Dialog ermöglicht es, bei Fehlern einen Fehlerbericht an das uDig-Entwicklungsteam zu
   senden.

(c) Copyright (c) 2004-2008 Refractions Research Inc. and others.

.. |image0| image:: /images/neuheiten_in_version_1.1/add_vertext_mode.gif
.. |image1| image:: /images/neuheiten_in_version_1.1/remove_vertext_mode.gif
.. |image2| image:: /images/neuheiten_in_version_1.1/edit_vertex_mode.gif
.. |image3| image:: /images/neuheiten_in_version_1.1/hole_vertex_mode.gif
.. |image4| image:: /images/neuheiten_in_version_1.1/delete_feature_mode.gif
.. |image5| image:: /images/neuheiten_in_version_1.1/difference_feature_mode.gif
.. |image6| image:: /images/neuheiten_in_version_1.1/split_feature_mode.gif
.. |image7| image:: /images/neuheiten_in_version_1.1/new_point_mode.gif
.. |image8| image:: /images/neuheiten_in_version_1.1/new_line_mode.gif
.. |image9| image:: /images/neuheiten_in_version_1.1/new_polygon_mode.gif
.. |image10| image:: /images/neuheiten_in_version_1.1/new_rectangle_mode.gif
.. |image11| image:: /images/neuheiten_in_version_1.1/new_circle_mode.gif
.. |image12| image:: /images/neuheiten_in_version_1.1/new_freehand_mode.gif
.. |image13| image:: /images/neuheiten_in_version_1.1/new_point_mode.gif
.. |image14| image:: /images/neuheiten_in_version_1.1/new_line_mode.gif
.. |image15| image:: /images/neuheiten_in_version_1.1/new_polygon_mode.gif
.. |image16| image:: /images/neuheiten_in_version_1.1/new_rectangle_mode.gif
.. |image17| image:: /images/neuheiten_in_version_1.1/new_circle_mode.gif
.. |image18| image:: /images/neuheiten_in_version_1.1/new_freehand_mode.gif
.. |image19| image:: /images/neuheiten_in_version_1.1/themed_pop_density.gif
.. |image20| image:: /images/neuheiten_in_version_1.1/selection_view.jpg
