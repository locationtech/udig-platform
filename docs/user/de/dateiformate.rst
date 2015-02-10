Dateiformate
============

Dateiformate
~~~~~~~~~~~~

uDig kann Geodaten unter anderem aus Dateien lesen und in diese schreiben. Das wohl bekannteste
Format für Geodaten ist der Shapefile.

Standardmäßig unterstützt uDig folgende dateibasierten Geodatenformate:

+----------------------------------------------------+----------------------------------------------------+
| shp                                                | jpeg mit jgw                                       |
| Shapefile                                          | JPEG-Bild mit Georeferenzierungsdaten              |
+----------------------------------------------------+----------------------------------------------------+

Durch Plugins lassen sich weitere Datenformate unterstützen.

Shapefile
^^^^^^^^^

`Shapefiles <http://de.wikipedia.org/wiki/Shapefile>`__ sind das wohl bekannteste Geodatenformat für
Features, also Einzelobjekte. Ein Shapefile ist genaugenommen stets eine Sammlung mehrerer Dateien
mit gleichem Namen aber unterschiedlicher Dateiendung (siehe unten).

 

Die drei klassischen Dateien

filename.shp

Geometrien

filename.shx

Index zwischen Geometrien und Attributen

filename.dbf

Attribute

 

Übliche Metadaten

filename.prj

Projektion

 

Open-Source-Erweiterungen

filename.qix

Räumlicher `Quadtree <http://de.wikipedia.org/wiki/Quadtree>`__-Index

filename.fix

Index für Feature-IDs

filename.sld

Zeichenstil im `SLD-Format <http://www.opengeospatial.org/standards/sld>`__ (XML-Datei)

 

ESRI-spezifische Erweiterungen

filename.sbn

Attributindex

filename.sbx

Räumlicher Index

filename.lyr

Stildaten für ArcMap

filename.avl

Stildaten für ArcView

filename.shp.xml

Metadaten nach `FGDC-Standard <http://en.wikipedia.org/wiki/Geospatial_metadata>`__

Ein Shapefile enthält immer nur Daten eines Geometrietyps (üblicherweise Punkte, Linien oder
Flächen). Flächen können Löcher enthalten. Es gibt weitere Geometrietypen, bspw. Punktwolken.

Genaugenommen enthält ein Shapefile sogar nur Features eines Featuretyps (wenn die Anwendungen
dieses Konzept verwenden). Beispielsweise ist es unüblich, in einem Shapefile Flüsse, Straßen,
Ackerrandstreifen und andere völlig verschiedene Linien gemeinsam zu speichern. Das ist praktisch
darin begründet, daß viele Programme alle Features eines Shapefiles mit dem gleichen Stil dekorieren
(was dann unübersichtlich wirkt) und außerdem verschiedene Featuretypen wie Flüsse, Straßen oder
Ackerrandstreifen verschiedene Sachdaten (Attribute) beinhalten.

Die meisten Shapefiles kommen ohne Stildaten, können aber in Anwendungen wie uDig mit Stilen
versehen werden. Diese Stile können dann als .sld-Datei im `Styled Layer
Descriptor-Format <http://www.opengeospatial.org/standards/sld>`__ gespeichert werden.

+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+
| |image1|                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    |
| **Tip**                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     |
| Sollten Sie Probleme haben, mehrere Shapefiles gemeinsam oder ein Shapefile gemeinsam mit anderen Daten darzustellen, obwohl diese im gleichen Kartenausschnitt sichtbar sein sollten, so kann das an einer fehlenden oder fehlerhaften Projektionsdatei liegen. Öffnen Sie zu einer Karte im Kontextmenü den Dialog **Eigenschaften** und dort die Seite **Koordinatenreferenzsystem**. Wählen Sie unter "Standard-CRS" die korrekte Projektion, kopieren Sie anschließend den Inhalt der Seite "Benutzerdefiniertes CRS" in eine Datei **<IhrDateiname>.prj**. Versuchen Sie nun erneut, das Shapefile **<IhrDateiname>.shp** zu laden.   |
+---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------+

(c) Copyright (c) 2004-2008 Refractions Research Inc. and others.

.. |image0| image:: images/icons/emoticons/check.gif
.. |image1| image:: images/icons/emoticons/check.gif
