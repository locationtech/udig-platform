Neuheiten in Version 1.2
========================

Was ist neu in uDig 1.2
=======================

-  `Was ist neu in uDig 1.2 <#NeuheiteninVersion1.2-WasistneuinuDig1.2>`__
-  `Weitere Rasterdatenformate <#NeuheiteninVersion1.2-WeitereRasterdatenformate>`__
-  `Unterstützung für Tile Server <#NeuheiteninVersion1.2-Unterst%C3%BCtzungf%C3%BCrTileServer>`__
-  `WFS 1.1.0 Support <#NeuheiteninVersion1.2-WFS1.1.0Support>`__
-  `Experimenteller Kachelrenderer <#NeuheiteninVersion1.2-ExperimentellerKachelrenderer>`__
-  `ArcSDE <#NeuheiteninVersion1.2-ArcSDE>`__
-  `Fortgeschrittene Symboliken <#NeuheiteninVersion1.2-FortgeschritteneSymboliken>`__
-  `Verbesserungen für Entwickler <#NeuheiteninVersion1.2-Verbesserungenf%C3%BCrEntwickler>`__

|image0|

`Neuheiten in Version 1.1 <Neuheiten%20in%20Version%201.1.html>`__

Weitere Rasterdatenformate
==========================

Die Anzahl der unterstützen Rasterdatenformate wurde weiter erhöht. Rasterbilder können nun auch
größer sein als der verfügbare Hauptspeicher. uDig unterstützt u.a. ...

-  GeoTiff
-  Standardbildformate (JPEG, GIF, PNG, etc...) mit Worldfile (TFW, JPW, ...)

... sowie unter bestimmten Betriebssystemen:

-  ECW
-  MrSID

Unterstützung für Tile Server
=============================

Die neue Unterstützung für Web Map Server Caching (WMS-C) erlaubt den Umgang mit Servern, die
gekachelte Daten liefern, bspw. GeoWebCache und TileCache.

WFS 1.1.0 Support
=================

| Herzlicher Dank an OpenGeo für die Implementierung der WFS 1.1.0-Schnittstelle. Sie können
"WFS=1.1.0" in Ihren "Capabilities"-URLs angeben, wenn Sie Server ansprechen, die diesen Standard
unterstützen.
|  WFS-T wird dabei derzeit noch nicht unterstützt.

Experimenteller Kachelrenderer
==============================

In den Voreinstellungen von uDig können Sie den "Tiled Renderer" (Kachel-Renderer) einschalten und
testen. Wird dieser zusammen mit dem neuen, nicht mehr flackernden, Werkzeug zum Verschieben des
Ausschnitts eingesetzt, so wird die Navigation auf der Karte zum Genuß.

ArcSDE
======

Die ArcSDE-Unterstützung wurde in Zusammenarbeit von OpenGeo und Refractions komplett
neugeschrieben. Die neue Implementierung nutzt Multithreading und erlaubt damit ein flüssigeres
Arbeiten ohne Wartezeiten.

Fortgeschrittene Symboliken
===========================

Das Kartenrendern wurde verbessert:

-  Es werden nun Schraffurmuster und True-Type-Fonts unterstützt.
-  Das Styled Layer Descriptor (SLD)-Dokument läßt Werte weg, die sowieso nur den Standardwerten
   entsprechen. Dadurch ergeben sich kleinere Dokumente bei gleichem Aussehen.

Fortgeschrittene Funktionen für Vektorgrafiken:

-  Textfluß entlang einer gebogenen Linie.
-  Geometriefunktionen

Verbesserungen für Entwickler
=============================

-  Mit Geotools 2.6. wird nun endlich eine aktuelle Version dieser GIS-Bibliothek eingesetzt. Damit
   werden fünf Jahre Entwicklungsarbeit auch in uDig nutzbar, u.a. auch die neuen
   Rasterdatenformate.
-  Durch Nutzung der Bibliothek "ImageIO-ext" werden die von GDAL unterstützten Rasterdatenformate
   für uDig zugänglich gemacht.

(c) Copyright (c) 2004-2008 Refractions Research Inc. and others.

.. |image0| image:: http://udig.refractions.net/image/DE/ngrelr.gif
