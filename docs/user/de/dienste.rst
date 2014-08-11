Dienste
=======

Dienste
~~~~~~~

Dienste stellen Daten oder Dienstleistungen bereit. In diesem konkreten Fall von Internetdiensten
(`Webservices <http://de.wikipedia.org/wiki/Web_Service>`__) wird vom Client (z.B. uDig) eine
Anforderung für Informationen, Daten oder eine Datenverarbeitung an den Dienst auf einem Server
gesendet, welcher dann (hoffentlich) durch die Übersendung der angefordeten Informationen oder Daten
antwortet.

Etliche wichtigen Dienste und Schnittstellen wie WFT, WMS, WCS oder CAD wurden durch das `Open
Geospatial Consortium <http://de.wikipedia.org/wiki/Open_Geospatial_Consortium>`__ spezifiziert.

Data store
^^^^^^^^^^

Unter einem "Data store" (siehe auch `Data
warehouse <http://de.wikipedia.org/wiki/Data-Warehouse>`__ versteht man bei GeoTools ganz generell
eine Datenquelle, sei es nun eine Datei, eine Datenbank oder ein Webservice. In unserem Fall liefert
diese zumeinst räumliche Daten, also
`Features <http://udig.refractions.net/confluence//display/EN/Feature>`__.

Web Feature Server (WFS)
^^^^^^^^^^^^^^^^^^^^^^^^

Ein `Web Feature Server <http://de.wikipedia.org/wiki/Web_Feature_Server>`__ ist ein Webservice,
welcher auf Anfrage Features liefert. Diese Features lassen sich vom Client weiterverarbeiten,
mittels Stilen dekorieren, als Karte ausgeben, verändern und z.B. als Shapefile abspeichern.

Web Map Server (WMS)
^^^^^^^^^^^^^^^^^^^^

Im Gegensatz zum Web Feature Server liefert ein `Web Map
Server <http://de.wikipedia.org/wiki/Web_Map_Server>`__ fertig dekorierte Karten als Bilder, so
bspw. als .jpg oder .png. Zumeist kann der Client einen bestimmen Kartenausschnitt/Zoom und eine
bestimmte Dekorierung anfragen und erhält dann die darstellungsfertige Karte. Viele
Routenplanungsdienste und frei verfügbare Kartendienste (z.B. Google Maps) liefern so ihre Karten an
den Browser.

Web Catalog Service (CAT)
^^^^^^^^^^^^^^^^^^^^^^^^^

Dieser von OGC spezifizierte Dienst soll das Auffinden von Geodaten im Internet vereinfachen. Er
fungiert quasi als eine Bibliothek für Geodaten und andere Dienste. Momentan (Stand Anfang 2008) ist
er noch nicht verbreitet.

(c) Copyright (c) 2004-2008 Refractions Research Inc. and others.
