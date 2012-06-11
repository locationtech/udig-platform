Re-projection
-------------

The shapes making up a map can come from a range of coordinate reference systems - each using different measurements
to record locations on the earth. 

In order to combine these data sets on screen automatic reprojection is used to transform information
into the common coordinate reference system you have selected for your map.

1. Open your :guilabel:`Canada` map from the previous section.

2. Add some additional shape files to the map from your data directory.
   
   * :guilabel:`10m_admin_1_states_provinces.shp`
   * :guilabel:`10m_geography_regions_points.shp`

3. The resulting map is taking information from a variety of different sources
   and re-projecting the data into a single display.
   
   |100000000000026C000001AAB9F3BD06_png|

4. You can ask that the map change its projection to match a layer.

   Right click on :guilabel:`10m admin 1 states provinces` and use
   :guilabel:`Operation > Set Map Projection from Layer`
   to use this layers projection.
   
   |100000000000026E000001AAEC5DE50C_png|
   
   * Not all coordinate reference systems can display all information. Some
     are only valid for a specific region.
     
     This operation can be useful if you are having any trouble displaying
     a layer and want to directly view the data.

5. You can also choose the map coordinate reference system yourself.
   
   In the :guilabel:`Map` editor status area pess the :guilabel:`GCS_WGS_1984` button
   to bring up the :guilabel:`Coordinate Systems` property page for your map.
   
6. The :guilabel:`Coordinate Systems` property page allows you to change the Coordinate Reference
   System of your map. The :guilabel:`Standard CRS` tab allows you to search the predefined coordinate reference systems.
   
   Please type in :kbd:`4326` and press :kbd:`enter`.
   
   |10000000000002B6000001BC2D4B11ED_png|
   
   * This selects the :guilabel:`World Geodetic System 1984` which is commonly
     used to represent lat/lon information.
   
   * You can also search by name, try typing in :kbd:`WGS84` to list matching projections

7. You can have a look at the formal definition of :guilabel:`EPSG:4326` by
   switching to the :guilabel:`Custom CRS` tab.
   
   |10000000000002B6000001BCE54D8ABC_png|
  
   * The definition is provided in *Well Known Text* - you can use this format for your own custom CRS.

8. Press the :guilabel:`OK` button to change the coordinate reference system of your map.

.. |100000000000026E000001AAEC5DE50C_png| image:: images/100000000000026E000001AAEC5DE50C.png
    :width: 9.029cm
    :height: 6.181cm


.. |10000000000002B6000001BCE54D8ABC_png| image:: images/10000000000002B6000001BCE54D8ABC.png
    :width: 12.73cm
    :height: 8.14cm


.. |100000000000026C000001AAB9F3BD06_png| image:: images/100000000000026C000001AAB9F3BD06.png
    :width: 8.999cm
    :height: 6.181cm


.. |10000000000002B6000001BC2D4B11ED_png| image:: images/10000000000002B6000001BC2D4B11ED.png
    :width: 12.73cm
    :height: 8.14cm

