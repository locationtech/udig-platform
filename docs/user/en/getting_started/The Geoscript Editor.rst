The Geoscript editor
-----------------------------------

Scritping is one of the powerful things in GIS. Non developers have the possibility
to create great automatisations through it.

The `geoscript <http://geoscript.org/>`_ scripting engine can be used
from within uDig to do geo-scripting.

Open the editor
~~~~~~~~~~~~~~~~

Two icons can be used to open the scripting editor: one for creating a new empty script
and one to open an existing script.


.. figure:: /images/geoscript_editor/01_open_editor.png
   :width: 80%
   :align: center
   :alt:

Let's create a new one. The user will be prompted to save the new script to file and an empty editor is opened.

.. figure:: /images/geoscript_editor/02_empty_editor.png
   :width: 80%
   :align: center
   :alt:

There are a few tool inside the editor, needed to start and stop scripts, or set the heap memory
allowed to be used by a script or enable logging.

Script away, with command completion and syntax coloring
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Inside the editor some basic command completion is available.
For geoscript objects, as for example the widely use Geometry:

.. figure:: /images/geoscript_editor/03_complete_class.png
   :width: 80%
   :align: center
   :alt:

but also for methods, as for example the fromWKT, a handy way to create geometries on the fly:

.. figure:: /images/geoscript_editor/04_complete_method.png
   :width: 80%
   :align: center
   :alt:


You might have noted that first the completion proposals that start with
the inserted text are suggested and after those also the once that simply contain the text.

You might also have noted that keywords have a nice syntax coloring,
in order to make the script more readable... and often to help users to make sure they have no typos :)


Run your script
~~~~~~~~~~~~~~~~~~

Once you have something you want to run, simply push the start button.
The script will be run through the Spatial Toolbox engine and print the output
in the console view. Let's create two polygons and intersect them.

.. figure:: /images/geoscript_editor/05_first_script_run.png
   :width: 80%
   :align: center
   :alt:


Plot some result - missing imports
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Geoscript needs you to define the modules you want to use in your
script through the import directive, which is usually placed at the top of the script.

If we try to plot the result by simply adding the plotting
directive, it will fail, because the plot module was not imported:

.. figure:: /images/geoscript_editor/06_missing_imports.png
   :width: 80%
   :align: center
   :alt:

The editor supplies a quick way to import the most common modules, which
can be useful for people starting with the scripting and that do not
know where the modules are. Push the button at the right of the stop
button and the imports are added to the top. After that the script will work:

.. figure:: /images/geoscript_editor/07_plot.png
   :width: 80%
   :align: center
   :alt:

Geoscript
~~~~~~~~~~~~~~~~

Geoscript allows for some fun, the best way to get into it is to start
from the `tutorials page <http://geoscript.org/tutorials/index.html>`_.
Just to add one more complex example, lets see
a script that can render a map, properly styled, to an image:

.. figure:: /images/geoscript_editor/08_render.png
   :width: 80%
   :align: center
   :alt:




