Data Weather Analysis And Forecast
##################################

Community Plugins : Data Weather Analysis and Forecast

This page last changed on Nov 01, 2012 by mauricio.pazos.

`Motivation <#DataWeatherAnalysisandForecast-Motivation>`__

`Current Desktop Solutions <#DataWeatherAnalysisandForecast-CurrentDesktopSolutions>`__

`Goals <#DataWeatherAnalysisandForecast-Goals>`__

`Standards <#DataWeatherAnalysisandForecast-Standards>`__

-  `Grib <#DataWeatherAnalysisandForecast-Grib>`__
-  `NetCDF Climate and Forecast (CF) Metadata
   Convention <#DataWeatherAnalysisandForecast-NetCDFClimateandForecast%28CF%29MetadataConvention>`__
-  `Unidata's Common Data Model
   (CDM) <#DataWeatherAnalysisandForecast-Unidata%27sCommonDataModel%28CDM%29>`__
-  `OGC Standards <#DataWeatherAnalysisandForecast-OGCStandards>`__
-  `2nd workshop on the use of GIS/OGC standards in
   meteorology <#DataWeatherAnalysisandForecast-2ndworkshopontheuseofGIS%2FOGCstandardsinmeteorology>`__
-  `OGC Web Services - Phase 7 <#DataWeatherAnalysisandForecast-OGCWebServicesPhase7>`__

`Related Works <#DataWeatherAnalysisandForecast-RelatedWorks>`__

`Gribs vs NetCDF <#DataWeatherAnalysisandForecast-GribsvsNetCDF>`__

`GUI - Weather Desktop <#DataWeatherAnalysisandForecast-GUIWeatherDesktop>`__

`Product Backlog <#DataWeatherAnalysisandForecast-ProductBacklog>`__

Motivation
==========

There is not an open source weather desktop with edition capability which allows to draw the frontal
zones and their possible evolution. Additionally, it should be useful some tools to identify and
delimit risk zones.

The development process is thinking as **Open Development**. The final product will have
**GNU/LGPL** licence. Thus your feedback will be welcome!.

Current Desktop Solutions
=========================

Popular free and open source desktop solutions:

-  `zyGrib <http://www.zygrib.org/index.php?page=abstract_en>`__ allows an easy access to weather
   data sources available on-line. Available for Linux, Mac and Windows

-  `UGrib <http://www.grib.us/>`__ available for Windows

-  `Unidata IDV <http://www.unidata.ucar.edu/software/idv/docs/workshop/overview/index.html>`__

-  ...

Goals
=====

-  Visualization of meteo data (Gribs, netCDF, ...)

-  Display satellite imagery

-  Meteo Data Access: internet, local, download from online resources (common use)

-  Edition of the frontal zones

-  Edition of the frontal zone evolution

-  Frontal zone notes

-  Drawing of the risk zones

-  Animation
    ...

Standards
=========

In order to get a product useful to analysis of weather data it is very important the access to the
scientific information available in many scattered sources around the world. This section resumes
the initiatives of the organizations in order to share climate and forecast data.

The `WMO <http://www.wmo.int/pages/index_en.html>`__ is an international specialized agency of
Unated Nations that promotes the cooperation and the standardization of meteorological,
oceanographical, and hydrological data.

The WMO have published and adopted the following meteorological codes:

-  SYNOP, CLIMAT and TEMP (Character Oriented)

-  BUFR, CREX

-  Grib (gridded geo-positioned data)

Grib
~~~~

Grib is a bit-oriented data format, it was approved by
`WMO <http://www.wmo.int/pages/index_en.html>`__ in 1985. It es a efficient vehicle to transmit
large volumes of gridded data. This offers a more compact data format than the character oriented
bulletins.

`NetCDF Climate and Forecast (CF) Metadata Convention <http://cf-pcmdi.llnl.gov/>`__
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

"The conventions for climate and forecast (CF) metadata are designed to promote the processing and
sharing of files created with the NetCDF API. The CF conventions are increasingly gaining acceptance
and have been adopted by a number of `projects and
groups <http://cf-pcmdi.llnl.gov/projects-and-groups-adopting-the-cf-conventions-as-their-standard>`__
"

`Unidata's Common Data Model (CDM) <http://www.unidata.ucar.edu/software/netcdf-java/CDM/index.html>`__
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

"Unidata's Common Data Model (CDM) is an abstract data model for scientific datasets. It merges the
netCDF, OPeNDAP, and HDF5 data models to create a common API for many types of scientific data. The
**NetCDF Java library is an implementation of the CDM which can read many file formats besides
netCDF**. We call these CDM files, a shorthand for files that can be read by the NetCDF Java library
and accessed through the CDM data model. ..."

OGC Standards
~~~~~~~~~~~~~

| The European Agencies Use `OGC Standards in
Meteorology <http://www.opengeospatial.org/pressroom/pressreleases/1111>`__
|  `ECMWF Workshop on the use of GIS/OGC standards in meteorology
(2008) <http://www.ecmwf.int/newsevents/meetings/workshops/2008/OGC_workshop/Presentations/>`__

`2nd workshop on the use of GIS/OGC standards in meteorology <http://www.meteo.fr/cic/meetings/gis-ogc/>`__
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

`OGC Web Services - Phase 7 <http://www.opengeospatial.org/projects/initiatives/ows-7>`__
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Related Works
=============

-  `GRIB API <http://www.ecmwf.int/products/data/software/grib_api.html>`__

-  `NetCDF Java library <http://www.unidata.ucar.edu/software/netcdf-java/>`__

-  `OGC Standards NetCDF <http://www.opengeospatial.org/standards/netcdf>`__

-  `Grib2NetCDF <http://www.ncl.ucar.edu/Applications/grib2netCDF.shtml>`__

-  `WKT and weather symbols for
   marks <http://docs.codehaus.org/display/GEOTOOLS/WKT+and+weather+symbols+for+marks>`__

-  `NOAA - Connecting UDIG to NDFD Web
   Services <http://www.weather.gov/mdl/XML/Design/UDIG_example.php>`__

-  `Network Common Data Form netCDF <http://www.unidata.ucar.edu/software/netcdf/docs/>`__

-  `JGrass hydro-geomorphologic plugins for
   uDig <http://code.google.com/p/jgrass/wiki/SummerOfCode2009>`__

-  `Netcdf in JGrass, the non spatial
   part <http://jgrasstechtips.blogspot.com/2010/01/netcdf-in-jgrass-non-spatial-part.html>`__

-  `ncWMS <http://www.resc.rdg.ac.uk/trac/ncWMS/>`__. It is an `extension of OGC
   WMS <http://www.resc.rdg.ac.uk/trac/ncWMS/wiki/WmsExtensions>`__.

-  `National Huracane Center - Irene Report <http://www.nhc.noaa.gov/gis/>`__. This is an example of
   how the organizations report the risk zones.

-  ...

Gribs vs NetCDF
===============

In this section is evaluated two libraries which are capable to handle the meteorology data, Gribs
and NetCDF, taking into account the goals of this project and the user vision. Taking as base the
comparison table done by `Met
Office <http://www.wmo.int/pages/prog/www/WDM/ET-ADRS-1/ET-ADRS-GRIB2vsNetCDF.ppt>`__, I have made
the following analysis that includes some features relevant for this project.

Criteria

Grib

NetCDF

Standard

WMO Standard

De facto Standard

Self describing

No - Interpretation require: WMO tables and Local tables

Yes - low level definition of dimensions, variables and attributes

Multiplatform

yes

yes

Cost support

WMO supported

More extensive: OGC Web Service Standard, ESRI ArcView

lossless compression

Proposal to include LZW, Weather-huffman, JPEG2000 Fraunhofer Institute developed compression
(factor 2.5 on 16 bit data)

| No for NetCDF3, but Cornell developed extension
|  NetCDF4 (HDF5) supports various

License

There are LGPL implementations like the following provided by ECMWF `Grib
API <http://www.ecmwf.int/products/data/software/grib_api.html>`__

The present version use the following `licence
(MIT-Style) <http://www.unidata.ucar.edu/software/netcdf/copyright.html>`__. Previous versions were
GNU/LGPL

Compatibility

I did not find any tool

The Unidata library includes decoders for GRIB 1 and GRIB 2

Previous Expreiences

There is not uDig experience

Andrea Anonello (Hydrologis)

| The user would like access to all information available, independently its format. In the other
hand, we can see there are many implementation of different format (gribs, netCDF, others), it is
necessary an effort to reuse those library that provide the input/output for different format
formats. NetCDF allows to cover the IO requirements for the most spread formats.
|  Thus, taking into account the user and developer view point looks like the NetCDF library is the
better option.

GUI - Weather Desktop
=====================

In this section is analyzed the GUI requirements.

|image0|

Edit tools associated to different kind of layers

-  Fronal Zones:cold front; warm front; stationary front; occluded front; surface trough;
   squall/shear line; dry line; tropical wave
-  Risk Zones: create/edit/remove point,line and polygon.
-  Report Zones: a geometry with a detailed weather analysis (templates could be great).

Product Backlog
===============

Sprint

Description

End (yyyy/mm/dd)

Deliverable

Status

1

Inception

2011/09/16

vision

|image1|

2

Desktop weather layout

2011//

uw-1.0.0-m1

|image2|

3

First Frontal tool

 

uw-1.0.0-m2

 

4

More Frontal tools

 

uw-1.0.0-m3

 

...

...

...

...

...

+---------------------------+
| |image6|                  |
| **Status**                |
|                           |
| -  |image7| In progress   |
| -  |image8| Released      |
                           
+---------------------------+

| 

Attachments:

| |image9|
`udig-weather-desktop-03.png <download/attachments/13533344/udig-weather-desktop-03.png>`__
(image/png)

+-------------+----------------------------------------------------------+
| |image11|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+-------------+----------------------------------------------------------+

.. |image0| image:: /images/data_weather_analysis_and_forecast/udig-weather-desktop-03.png
.. |image1| image:: images/icons/emoticons/check.gif
.. |image2| image:: images/icons/emoticons/star_green.gif
.. |image3| image:: images/icons/emoticons/information.gif
.. |image4| image:: images/icons/emoticons/star_green.gif
.. |image5| image:: images/icons/emoticons/check.gif
.. |image6| image:: images/icons/emoticons/information.gif
.. |image7| image:: images/icons/emoticons/star_green.gif
.. |image8| image:: images/icons/emoticons/check.gif
.. |image9| image:: images/icons/bullet_blue.gif
.. |image10| image:: images/border/spacer.gif
.. |image11| image:: images/border/spacer.gif
