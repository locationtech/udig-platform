Shapefile Export Tutorial
=========================

.. figure:: /images/shapefile_export_tutorial/ShapefileExportWorkbook.png
   :alt: 

Reference:

* `Working with the GIS Platform <working_with_the_gis_platform>` (Developers Guide)
* `http://docs.geotools.org/ <http://docs.geotools.org/>`_

Source code:

-  Available in your uDig SDK (import the `source code <code_examples>` from the plugins
   view)
-  plugin: `org.locationtech.udig.tutorials.shpexport <https://github.com/uDig/udig-platform/tree/master/plugins/org.locationtech.udig.tutorials.shpexport>`_ (github)

Introduction
------------

We are going to use an operation to work with the GeoTools FeatureSource interface. The
FeatureSource API allows access to geographic data in the form of simple features: consisting of a
geometry along with some attributes. Features of the same kind are said to belong to the same
FeatureType (much as java objects are said to belong to the same class.

Here is a comparison between Java Class and Geographic FeatureType:

+-----------------+----------------------+-----------------------+----------------------------+
| **Domain**      | **Java**             | **Geographic Rich**   | **Geographic Simple**      |
+-----------------+----------------------+-----------------------+----------------------------+
| Classification  | Class<?>             | FeatureType           | SimpleFeatureType          |
+-----------------+----------------------+-----------------------+----------------------------+
| Instance        | Object               | Feature               | SimpleFeature              |
+-----------------+----------------------+-----------------------+----------------------------+
| Data            | Field (simple types) | Attribute             | Attribute                  |
+-----------------+----------------------+-----------------------+----------------------------+
| Association     | Field (object types) | Association           | n/a                        |
+-----------------+----------------------+-----------------------+----------------------------+
| Messages        | Methods              | Operation             | n/a                        |
+-----------------+----------------------+-----------------------+----------------------------+

After completing this section, you will have gained the skills to:

-  Create a new shapefile (as a FeatureStore)
-  Access a FeatureCollection (from a FeatureSource)
-  Copy features from a FeatureSource to a FeatureStore

GeoTools and Features
---------------------

In this section you will implement an operation which will take features from an arbitrary
FeatureSource, and copy them into a shapefile FeatureStore.

.. figure:: /images/shapefile_export_tutorial/datastore.png
   :align: center
   :alt: 

FeatureSource provides read-only methods for accessing geographic data. The subclass FeatureStore
provides read-write methods if available. You can check if a file is writable using a simple Java
"instance of" check.

.. code-block:: java

    String typeNames = dataStore.getTypeNames()[0];
    SimpleFeatureSource source = store.getfeatureSource( typeName );
    if( source instanceof SimpleFeatureStore){
       SimpleFeatureStore store = (SimpleFeatureStore) source; // write access!
       store.addFeatures( featureCollection );
       store.removeFeatures( filter ); // filter is like SQL WHERE
       store.modifyFeature( attribute, value, filter );
    }

When you access the content of a FeatureSource you will get back a FeatureCollection - please be
careful and treat this more like a "PrepairedStatement" or "ResultSet". A FeatureCollection is lazy
and offers streamed access to remote content - you must be sure to close any Iterators you open on a
FeatureCollection.

.. code-block:: java

    SimpleFeatureIterator iterator = featureCollection.features();
        try {
            while( iterator.hasNext() ){
                SimpleFeature feature = iterator.next();
                // process feature
            }
        }
        finally {
            iterator.close();
        }

For more information and examples on the handling of FeatureCollection please check the GeoTools
User Guide:

* `GeoTools User Guide <http://docs.geotools.org/latest/userguide/>`_
* `GeoTools Feature Tutorial <http://docs.geotools.org/latest/userguide/tutorial/feature/csv2shp.html>`_

What to Do Next
---------------

For better understanding of these facilities please try the following:

Prompt the user for filename
````````````````````````````

Hint: Look at the existing "promptOverwrite" if you are having trouble opening a FileDialog in the
display thread.

Process Features
````````````````

You now know how to make operations on real data. Can you make your own operation to calculate the
entire length for line features? Or the entire area for polygon information?

Hint: You will need to use the FeatureCollection - remember to close your iterator!
 Hint: Check the GeoTools User Guide mentioned earlier if you are stuck!

Catalog Add
```````````

Advanced: Did you find it annoying to hunt down your file after you created it? The Eclipse User
Interface Guidelines say that we should make the results of an operation visible after it is
performed. Please add your new shapefile to to the local catalog after it has been created.

Bonus points for selecting it in the CatalogView after it is added.

Operation Enablement
````````````````````

The shape file format is limited to handling one geometry type at a time (so Point or LineString or
Polygon - not a general Geometry).

To meet the Eclipse House Rules we should not contribute an operation to the user interface when it
cannot operate - can you change when the operation is enabled based on the schema information?

Calculated Attribute
````````````````````

Can you write out your shape file with an additional Area (or Length) attribute?

Hint: Working with FeatureType is hard; the FeatureBuilder and DataUtilities class contains methods
to help you along.

Filter
``````

Can you write out a shape file with only the features that passes a certain test? ( like cities with
POP\_RANK > 5 )

The OGC filter specification, or the GeoAPI Filter interfaces, let you write tests in a manner
similar to SQL. Can you figure out how to make a Filter?

Hint: The CQL parser will make Filters based on a provided String.

Tips, Tricks and Suggestions
----------------------------

The following tips have been provided by the udig-devel list; please stop by and introduce yourself.

Display Thread
``````````````

If you check back in the first :doc:`tool_plugin_tutorial` or :doc:`iadaptable_and_operations_tutorial` 
tutorial you will find a couple examples of how to open something on the display thread.

Many examples on the internet show how to use the **Display** class provided by **swt**. This class
can actually be a bit tricky to use.

Jesse has provided a couple of helper methods that capture the best practices around the use of this
class (that take care of all the annoying null checks):

.. code-block:: java

    PlatformGIS.asyncInDisplayThread( runnable, true ); // if in the display thread it will run right away

There are more helper methods there covering several possible scenarios; including
syncInDisplayThread.

Returning a Result from Sync Runnable
`````````````````````````````````````

There are a couple of ways to return a result from a **synchronized** runnable (one where your
thread pauses while the display thread queues up your Runnable and prompts the user for something):

-  You can make the Runnable into a inner class; and use a field to store the result you wish to
   return.

   .. code-block:: java

       class FilePromptRunnable implements Runnable {
          void String result;
          public void run(){
              result = "Hello World";
          }
          public String getResult(){
              return result;
          }
       };
       FilePromptRunnable runPrompt = new FilePromptRunnable();
       PlatformGIS.asyncInDisplayThread( runPrompt );

       System.out.println("File:"+runPrompt.getResult());

-  You can also cheat by using an array as a **Java Pointer**; this allows you to make use of an
   anonymous Runnable:

   .. code-block:: java

       final static String result[] = new String[1];

       PlatformGIS.asyncInDisplayThread( new Runnable(){
           public void run(){
                result[0] = "hello world";
           }
       });
       System.out.println( "Result:"+result[0] );

A common mistake when writing an operation is to make use of a **field**. A long running operation
may be run more than once at the same time - so it is nice to make sure each Runnable has a chance
to return its result independently.

Commercial Training Materials
-----------------------------

Please contact any of the organisations listed on the main `uDig support
page <http://udig.refractions.net/users/>`_ for details on uDig training.

The workbooks and slides for the training course are available here:

* `http://svn.refractions.net/udig_training/trunk <http://svn.refractions.net/udig_training/trunk>`_

This is a private svn repository that is open to those who have taken the training course.

Academic Access
```````````````

The course materials can be made available to those working at academic institutions - we ask for an
email from your Professor.

Please ask your professor to email admin@refractions.net with the request.
