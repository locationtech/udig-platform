Plugin Dependency
=================

Depending on a Plug-in
~~~~~~~~~~~~~~~~~~~~~~

ie. Plug-in A depends on plug-in B.

Open plug-in A's plugin.xml file. On the dependencies tab press the add button. Add plug-in B.

Depending on an External Library
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

ie. Plug-in A depends on external library 'something.jar'.

Make sure you have set up the net.refractions.udig.libs plug-in as a project. See `2 Plugin
Setup <2%20Plugin%20Setup.html>`_ for details.
 Perform the same as Situation 1, where plug-in B =
:doc:`net.refractions.udig.libs`


If the required jar is not a part of the libs plug-in, follow the instructions on under
`net.refractions.udig.libs <net.refractions.udig.libs.html>`_.
