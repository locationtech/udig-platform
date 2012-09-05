Adding a Layer from PostGIS
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. note:: 
  
  This section requires your own local PostGIS. While we have a public PostGIS available
  for demonstrations most corporate environments are unable to access this database due
  to firewall restrictions.
  
  If you are trapped behind a firewall please feel free to skip this section.
  
  The online help does have some advice about :doc:`../../Running uDig` covering firewall access.
  
This section shows how you can add a Layer from a PostGIS table. PostGIS is an extension to the popular
open source PostgreSQL database. uDig handles other databases like Oracle and DB2 in a similar manner:

* Select :menuselection:`File --> New --> New Map` from the menu.
  
  |menubar_new_map_png|

* In the :guilabel:`Projects` view, right-click on your map and select :guilabel:`Add`.
  
  |projects_map_add_png|

* Select :guilabel:`PostGIS` as the data source and click :guilabel:`Next`.
  
  |postgis_wizard_png|

* Enter the following connection information:
  
  * Host: ``www.refractions.net``
  * Port: ``5432``
  * Username: ``demo``
  * Password: ``demo``
  * Store Password: check
  
  Once the connection information is entered press :guilabel:`Next`.
  
  |postgis_connect_png|


* This page lists the databases available to the current user.
  
  The www.refractions.net database does not publish a public list so rather than choose from a
  nice easy list we are going to have to enter in ``demo-bc`` by hand.
  
  * Database: ``demo-bc``
  
  |postgis_list_png|

* We can now press the :guilabel:`List` button to list the available tables. Please choose
  
  * :guilabel:`bc_hospitals`
  * :guilabel:`bc_municipality`
  
  Press :guilabel:`Next` when ready.

* The resource collection page confirms that :guilabel:`bc_hospitals` and :guilabel:`bc_municipality`
  are published as spatial layers.
  
  We can press :guilabel:`Finish` to add these layers to our Map.
  
  |postgis_resource_selection_png|

* It may take a short while to fully render since you are zoomed out so far.
  
* Head on over the the :guilabel:`Layer` view and right click on :guilabel:`bc_hospitals` layer 
  and choose :guilabel:`Zoom to Layer`
   
  |postgis_zoom_to_layer_png|
   
   The map will now zoom in to show the extent of the :guilabel:`bc_hospitals` layer.

* You can return to your previous position in the world by selecting Back in the Navigation menu.

.. |projects_map_add_png| image:: images/projects_map_add.png
   :width: 4.979cm
   :height: 4.641cm

.. |postgis_zoom_to_layer_png| image:: images/postgis_zoom_to_layer.png
   :width: 14.52cm
   :height: 10.91cm

.. |menubar_new_map_png| image:: images/menubar_new_map.png
   :width: 6.669cm
   :height: 2.93cm


.. |postgis_wizard_png| image:: images/postgis_wizard.png
   :width: 8.89cm
   :height: 7.26cm


.. |postgis_list_png| image:: images/postgis_list.png
   :width: 9.631cm
   :height: 8.729cm


.. |postgis_connect_png| image:: images/postgis_connect.png
   :width: 9.631cm
   :height: 7.87cm


.. |postgis_resource_selection_png| image:: images/postgis_resource_selection.png
   :width: 9.631cm
   :height: 7.08cm
