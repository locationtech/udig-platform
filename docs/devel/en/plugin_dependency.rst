Plugin Dependency
=================

Depending on a Plug-in
----------------------

ie. Plug-in A depends on plug-in B.

Open plug-in A's plugin.xml file. On the dependencies tab press the add button. Add plug-in B.

Depending on an External Library
--------------------------------

ie. Plug-in A depends on external library 'something.jar'.

Make sure you have set up the org.locationtech.udig.libs plug-in as a project. See 
:doc:`Plugin Setup <plugin_setup>` for details.
Perform the same as Situation 1, where plug-in B = org.locationtech.udig.libs_

If the required jar is not a part of the libs plug-in, follow the instructions on under org.locationtech.udig.libs_

.. _org.locationtech.udig.libs: http://udig.refractions.net/confluence//display/UDIG/org.locationtech.udig.libs
