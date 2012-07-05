Import Directly to The Catalog
------------------------------

Earlier we learned how to work with content from the Catalog view, in this section we will import content directly into the catalog.

* On the :guilabel:`Catalog` view please press the :guilabel:`Import` button:
   
  .. image:: images/catalog_ocean.png
    :width: 10.91cm
    :height: 3.35cm

   
* Select :menuselection:`Other --> Data` and press :guilabel:`Next`.
   
  .. image:: images/import_data.png
    :width: 7.62cm
    :height: 5.89cm

* Choose :guilabel:`Web Map Server` from the list and press :guilabel:`Next`.
   
  .. image:: images/import_wms.png
    :width: 7.62cm
    :height: 7.47cm


  .. note:: 
     You can copy the `URL <http://demo.opengeo.org/geoserver/ows?service=wms&version=1.1.1&request=GetCapabilities>`_ from the :doc:`Walkthrough 1 <../../Walkthrough 1>` page.

* This time we are going to import an OpenGeo demo Web Map Server using the following Capabilities document:

  .. image:: images/wms_wizard.png
    :alt: WMS Wizard

* Press :guilabel:`Finish` to import the Web Map Server into the :guilabel:`Catalog` view.
   
* Now that we have imported the WMS into the catalog we can add it to our Map.

* Drag and Drop the :guilabel:`bluemarble` layer directly onto :guilabel:`Map` editor.
   
   |catalog_dnd_map_png|

* Earlier we used the the layer view to reorder using the up and down buttons,
  this time we will drag the layers into the right order.

* Select the :guilabel:`Blue Marble` layer in the layer view and drag it to the bottom of the list.
   
   |layers_dnd_png|


.. |catalog_dnd_map_png| image:: images/catalog_dnd_map.png
    :width: 14.87cm
    :height: 11.24cm


.. |layers_dnd_png| image:: images/layers_dnd.png
    :width: 3.731cm
    :height: 1.769cm


.. |wms_wizard_png| image:: images/wms_wizard.png
    :width: 9.721cm
    :height: 5.459cm





