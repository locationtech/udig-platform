net.refractions.udig.help
=========================

The help plugin contains the following docuemntat:

To update the contents of the Help Plugin
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#. `Export <http://docs.codehaus.org/spaces/exportspace.action?key=UDIGGuide>`_ the entire online
   help wiki

-  Export Options: check html
-  Other options: uncheck include comments

#. This will cause a big old zip file to be generated
#. Unzip the produced file overtop of the net.refractions.udig.help project
#. Updated the toc files

Individual plug-ins are responsible for defining IHelpContexts and matching context files and
extention points.
