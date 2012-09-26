Working with Attributes
=======================

In this section you will learn how to edit an Attribute, along the way we will explore the use of styling.

To start out with lets figure out the name of that city:

* Select :menuselection:`Navigation --> Zoom to All` from the menu bar.

* Select the Tasmanian cities in the Layers View.

  .. image:: images/TasmanianCitiesInlayersView.png
    :width: 4.701cm
    :height: 2.649cm
  
* Select the :menuselection:`Edit --> Delete` command from the menu bar

* In the Catalog view expand the GeoServer Web Feature Service (WFS 1.0.0) entry and select **Tasmania cities**.

  .. image:: images/AddTasmaniaCitiesFromGeoServer.png
    :width: 13.91cm
    :height: 4.33cm

* Right Click and select :guilabel:`Add to Current Map`.

* Right click on **Tasmania cities** in the Layers view, and click on :guilabel:`Change Style`.
  
  .. image:: images/ChangeStyleForTasmaniaCitiesLayer.png
    :width: 6.331cm
    :height: 5.41cm
  
  This opens the Style Editor dialog.

* Define the point shape using style as follow:

* Select the Points page from the list on the left hand side of the Style Editor.

* Choose :guilabel:`Simple Style` to access the the built in shapes.


  .. image:: images/10000000000003CD000002D6317F97A2.png
    :width: 14.12cm
    :height: 10.539cm

  Choose circle from the list of built in shapes.

* Configure the style properties for labeling:

 * Click on the Labels tab

 * Check the enable/disable labeling

 * Choose CITY_NAME for the label property.
  
   .. image:: images/1000000000000181000002148DF89435.png
      :width: 5.59cm
      :height: 7.721cm

 * After making changes, hit the  :guilabel:`Apply` button to update the map, you can experiment with the settings and press
   :guilabel:`OK` when you are finished.

* Ensure that **Tasmania cities** is still selected in the layers view, and select the Table view.

* The first time you use table view you will need to accept a warning that all the features will be loaded into memory.

  .. image:: images/1000000000000214000000D8E5C9CDB2.png
    :width: 9.85cm
    :height: 3.96cm


* The Table view shows all the features for the current layer.

  .. image:: images/10000000000002EE0000010E41AFEB49.png
    :width: 10.88cm
    :height: 3.919cm

* Go ahead and rename the city after yourself. Change the CITY_NAME attribute and you can watch the map update.
  
  .. image:: images/10000000000002EB0000028E8B0BDDA9.png
    :width: 10.841cm
    :height: 9.49cm


  .. note::
     Not all aspects of a feature are editable. - bounds are derived from the geometry, and ID is dictated the WFS.


* Press the :guilabel:`Commit Changes` button, in the tool bar, to send your changes off to the Web Feature Server.
