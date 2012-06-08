Re-projection
-----------------------------------

Since the world is not flat, maps are projected to line up with a Co-ordinate Reference System (CRS).

* Open your
  Canada
  map from the previous section.


* Add some additional shape files to the map:


* 10m_admin_1_states_provinces_shp.shp


* 10m_geography_regions_points.shp


* The resulting map is taking information from a variety of different sources and re-projecting the data into a single display.
  |100000000000026C000001AAB9F3BD06_png|


* You can ask that the map change its projection to match a layer.
  Rght click on
  10m admin 1 states provinces shp
  and use
  Operation > Set Map Projection from Layer
  to use this layers projection.
  |100000000000026E000001AAEC5DE50C_png|


* You can also choose the map coordinate reference system yourself.


* Press the “GCS_WGS_1984” button to bring up the CRS Chooser.
  This brings up the a preference page allowing you to change the Coordinate Reference System for your map. Please enter in
  4326
  and press enter to re-project the map to the “World Geodetic System 1984” CRS commonly used to represent lat/lon information.
  |10000000000002B6000001BC2D4B11ED_png|


* You can have a look at the formal definition of “EPSG:4326 by switching to the “Custom CRS” tab
  |10000000000002B6000001BCE54D8ABC_png|


* Press the
  OK
  button to change the coordinate reference system of your map.


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

