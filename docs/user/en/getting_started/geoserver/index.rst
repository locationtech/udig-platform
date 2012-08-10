GeoServer Install
=================


|refractions_png| |lisasoft_png| |geoserver_png|

.. image:: ../../../../images/background.png
   :width: 100%


Introduction
------------

GeoServer is Java Enterprise Edition application used to publish spatial information. It supports a number of Industry Standard protocols and also offers a REST API for configuration.

In this workbook we are going to install GeoServer and go over a couple of configuration options.

After completing this workbook, you will have:

 * Installed a Java Runtime Environment onto your computer

 * Installed GeoServer using the Windows installer

 * Started up and Customize GeoServer

 * Started the Open Layers Tasmania Preview

Java Runtime Environment
------------------------

GeoServer is a Java application – if you do not already have Java on your system please go ahead and install it now. Any version of Java 5 or newer will do fine.

  * Download the latest Java SE Runtime Environment from oracle:

  At the time of writing jre_7u1-windows-i586.exe had just been released.

  * Double-click the installer (jre_7u1-windows-i586.exe is used in this example) and accept the license agreement.

  .. figure:: images/jre_welcome.png
    :width: 7.5cm


  * Press the Install but to get going.

  .. figure:: images/jre_installing.png
    :width: 7.5cm


  * Congratulations Java is now installed.

  .. figure:: images/java_install_success.png
    :width: 7.5cm


GeoServer Install
-----------------

GeoServer can be installed as a windows service or as an application. We will be testing our 
GeoServer as a normal application.

  * Downloaded the latest GeoServer release:

  As this workbook went to press we got a hold of geoserver-2.1.2.exe

  * Double-click the geoserver installer to start.
  
  .. figure:: images/installer_welcome.png
    :width: 7.5cm


  * GeoServer is released under the GPL license, press :guilabel:`I Agree` to continue.

  .. note::
     GeoServer uses the same LGPL GeoTools and ImageIO-Ext projects as uDig.


  .. figure:: images/installer_license.png
    :width: 7.5cm


  * GeoServer will install into the *Programmers Files* directory, press :guilabel:`Next` to continue.

  .. figure:: images/installer_location.png
    :width: 7.5cm


  * Press *Next* to create the default start menu folder

  .. figure:: images/installer_menu.png
    :width: 7.5cm


  * GeoServer select your current Java Runtime Environment. Press :guilabel:`Next` to continue.

  .. figure:: images/installer_jre.png
    :width: 7.5cm


  * GeoServer will store your configuration in a “data directory”, the default location will work fine. Press *Next* to continue.

  .. figure:: images/installer_data_dir.png
    :width: 7.5cm


  * GeoServer provides a web based configuration system allowing you to administer the application remotely. Please accept the default credentials (admin and geoserver) for the purpose of this tutorial.

  .. figure:: images/installer_admin.png
    :width: 7.5cm


  * You can also choose the port for the service, 8080 will be fine for this tutorial.

  .. note:: 
     The GeoServer installer uses the Jetty application container.  GeoServer is also available as a war
     if you would like to use Tomcat.


  .. figure:: images/installer_port.png
    :width: 7.5cm


  * We now get to choose “Run Manually” (as we are just interested in evaluating GeoServer at this time).

  .. figure:: images/installer_type.png
    :width: 7.5cm


  * You can review your settings and then press :guilabel:`Finish` to install GeoServer.

  .. figure:: images/installer_ready.png
    :width: 7.5cm


  * Please wait while GeoServer installs.

  .. figure:: images/installer_installing.png
    :width: 7.5cm


  * Thank you for installing GeoServer!

  .. figure:: images/installer_finish.png
    :width: 7.5cm

OpenLayers Tasmania Preview
---------------------------

GeoServer provides a layer preview implemented with the
OpenLayers
AJAX library.

  * From the Welcome screen select *Layer Preview* from the list on the left.

  * From the Layers Preview page type *Tasmania* into the search box and press enter.

  * In the *tasmania* row click on the *OpenLayers* link.

  .. figure:: images/web_preview.png
    :width: 14.46cm
    :height: 6.549cm


  * Click on the highlighted OpenLayers link at the bottom to open a new tab. As this Open Layers
    application is started up you can watch the GeoServer console respond to queries.
  
  .. figure:: images/console_getmap.png
    :width: 11.46cm
    :height: 6.6cm


  * This will bring up a web application put together with “Open Layers”.

  .. note:: 
     This is the same map used as a reference point for the :doc:`uDig Walkthrough 2 <../walkthrough2/index>` tutorial.


  .. figure:: images/tasmania.png
    :width: 10.16cm
    :height: 9.541cm


Starting up GeoServer
---------------------

In this section you will learn how to start GeoServer, and explore its editing abilities.

  * Click :menuselection:`Start --> Programs --> GeoServer 2.0.1 --> Start GeoServer`

  .. figure:: images/start_geoserver.png
    :width: 4.41cm
    :height: 2.69cm


  * When starting for the first time you will need to grant the Firewall permission for GeoServer to open a port.

  .. figure:: images/firewall.png
   :width: 9.92cm
   :height: 7.1cm


  * GeoServer will open up a console running the Jetty Application Server; please wait until
    Jetty indicates it is listening on port 8080 as shown below.

  .. note::
     If you already jave a web server running on 8080 you can modify the jetty xml file by hand.


  .. figure:: images/console_start.png
    :width: 12.42cm
    :height: 6.271cm


  * You can use the start menu to open the “GeoServer Web Admin Page”.
  
  The web browser will open:
  
  .. figure:: images/welcome.png
    :width: 14.439cm
    :height: 6.56cm


  * We need to login before we can use the configuration screen. Fill in `admin` and `geoserver` along the top of the screen and press :guilabel:`Login`.

  .. figure:: images/web_login.png
    :width: 14.439cm
    :height: 1.36cm


  * The welcome screen now has a great many more options than in previous versions.

  .. figure:: images/geoserver_welcome_admin.png
    :width: 13.27cm
    :height: 9.49cm


  * You may notice that this GeoServer belongs to “The ancient geographes INC”. Let us go in and fix that up.

  * Click on *Contact Information* and fill in the details for your organisation.
  
  .. figure:: images/web_contact.png
    :width: 12.51cm
    :height: 9.49cm


  * Press the *Submit* button, you will be returned to the Welcome screen and you can see the contact organization has been changed.

  * In a similar fashion change the title of the WMS to “Local Web Map Server”.

  .. figure:: images/web_config_wms.png
    :width: 14.469cm
    :height: 6.549cm


What to do Next
---------------

If you finish early here are some challenges.

  * The “Sample Requests” demo lets you try out requests one at a time by selecting them from a List 
    and hitting Submit.
  
    * Try out a GetFeature request by hand.
  
    * Try out WMS GetCapabilities and confirm the title is as you defined it.

  * Turn down the amount of logging to let GeoServer run faster

  * Advanced: Have a look at the *open layers* link for the Tasmania layer preview. Can you explain
    what trick Geoserver is doing.
  
  .. hint:: 
     Look at the URL and source code of the Tasmania layer preview.


  * How can you use GeoServer in your organisation?


.. |refractions_png| image:: ../../../../images/refractions_logo.png
   :height: 3cm

.. |lisasoft_png| image:: ../../../../images/lisasoft_logo.png
   :height: 3cm

.. |geoserver_png| image:: ../../../../images/geoserver_logo.png
   :height: 3cm

