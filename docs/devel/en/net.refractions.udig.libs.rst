net.refractions.udig.libs
=========================

"Libs" Plugin
~~~~~~~~~~~~~

The libs plugin is the "glue" between uDig and the rest of the open source world. This plugin is
responsible for gathering together all the other code dependencies uDig has on the outside world.

Custom build.xml
~~~~~~~~~~~~~~~~

The libs plugin contains a custom build.xml file, this file fetches the required jars from the
following locations:

* :doc:`http://lists.refractions.net/`

-  your local MAVEN repository

This allows you to run uDig against the latest experimintal geotools code on your computer! More
importantly it lets you test bug fixes and enhancements to these open source projects.

This build.xml file is called via a "builder" in eclispe, to use simply "clean" libs. You can watch
the script run as it downloads all the required files.

Updating the Libs Plugin
~~~~~~~~~~~~~~~~~~~~~~~~

The Libs plugin can be customized in a number of ways, usually for each release:

Changing GeoTools versions:

::

    <target name="update.gt">
        <property name="moduleName" value="gt2"/>
        <property name="geotools.version" value="2.1.RC1"/>
        <property name="geotools.snapshot" value="2.1.1"/>    
        ...
    </target>
    <target name="update.libs">
        <property name="geoapi.version" value="2.0-tiger"/>
        <property name="geotools.version" value="2.1.1"/>
        <property name="geotools.snapshot" value="2.1.1"/>    
        ....
    </target>

How is this information used:

::

    <get src="${repo}/${moduleName}/jars/main-${geotools.version}.jar"
             dest="${lib}/main-${geotools.version}.jar"
             usetimestamp="true" ignoreerrors="true" verbose="true"/>
        ...
        <get src="${updateURL}/${moduleName}/${geotools.snapshot}/main-${geotools.version}.jar"
             dest="${lib}/main-${geotools.version}.jar"
             usetimestamp="true" ignoreerrors="true" verbose="true"/>

If the local maven repository contains the indicated jar, it will be copied to the lib folder. If
not the remote jar is copied from the update site.

.. figure:: http://udig.refractions.net/image/DEV/ngrelr.gif
   :align: center
   :alt: 

`How to fix a broken
build <http://udig.refractions.net/confluence/display/UDIG/How+to+fix+a+broken+build>`_
