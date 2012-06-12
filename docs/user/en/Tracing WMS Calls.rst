Tracing WMS Calls
#################

Occasionally you will have trouble connecting to a WMS service; here is how to enable tracing.

:doc:`Traceing WMS Requests`


* :doc:`Example Tracing Information`

* :doc:`Trouble Shooting a WMS Server`

* :doc:`Mac OSX`


Traceing WMS Requests
=====================

#. Please use the the provided .options File. The ".options" file should be dropped into you uDig
   folder to enable additional tracing information.

   -  Create the following file:

      **.options**

      ::

          #Master Tracing Options
          #Tue Aug 05 14:56:31 PDT 2008
          net.refractions.udig.catalog.wms/debug=true
          net.refractions.udig.catalog.wms/debug/request=true

   -  Place it in your uDig folder (next to your udig.exe file):
       C:\\Program Files\\uDig\\1.1\\eclipse

#. Now when you restart uDig additional messages will be added to your log. These messages show the
   Requests sent to the WMS service.
#. Check the log for tracing information

Example Tracing Information
---------------------------

When connecting to the JPL server and choosing the blue marble layer the following trace was
produced:

!ENTRY net.refractions.udig.catalog.internal.wms 4 0 2008-09-05 08:26:15.561
 !MESSAGE GetCapabilities:
:doc:`http://wms.jpl.nasa.gov/wms.cgi?REQUEST=GetCapabilities&VERSION=1.1.1&SERVICE=WMS`


!ENTRY net.refractions.udig.catalog.internal.wms 4 0 2008-09-05 08:26:24.781
 !MESSAGE GetMap: `http://wms.jpl.nasa.gov/wms.cgi?
SERVICE=WMS&LAYERS=BMNG&EXCEPTIONS=application/vnd.ogc.se\_xml&
FORMAT=image/png&HEIGHT=357&TRANSPARENT=TRUE&REQUEST=GetMap&BBOX=-180.0,-90.0,180.0,90.0&WIDTH=714&STYLES=default&
SRS=EPSG:4326&VERSION=1.1.1 <http://wms.jpl.nasa.gov/wms.cgi?SERVICE=WMS&LAYERS=BMNG&EXCEPTIONS=application/vnd.ogc.se_xml&FORMAT=image/png&HEIGHT=357&TRANSPARENT=TRUE&REQUEST=GetMap&BBOX=-180.0,-90.0,180.0,90.0&WIDTH=714&STYLES=default&SRS=EPSG:4326&VERSION=1.1.1>`_

The URLs mentioned during tracing can be cut and pasted into a browser to verify an error message,
or see the same image that uDig displayed.

Trouble Shooting a WMS Server
-----------------------------

Please keep the following in mind when connecting to a WMS. The URL you provide should be a valid
**GetCapabilities** URL with the following elements:

-  REQUEST=GetCapabilities: his is actually a requirement; uDig will supply it for you if needed.
-  SERVICE=WMS: this is also a requirement - many servers speak several protocols
-  VERSION=X.X.X: this is version of the WMS protocol you would like to use

The uDig will enter into "version negotiation" with the server using the above information.

#. uDig will will request the GetCapabilities document with the above information
#. The server will respond with a valid capabilities document that contains the "closest" version
   number the server can produce.
#. Based on this response uDig may

   -  Connect and ask you to choose a layer; or
   -  Change the VERSION and try again ...

Mac OSX
-------

1. right click on uDig.app and choose "Show Package Contents"
 2. Navigate to MacOS folder
 3. Next the the file udig\_internal.ini create the file ".options" as described above.

You can either review the application logs located (from the about udig screen); or start the
application from the command line with:

::

    udig.app/Contents/MacOS/udig_internal -consoleLog

**Related tasks**


`View Error Log and Configuration
Details <View%20Error%20Log%20and%20Configuration%20Details.html>`_
