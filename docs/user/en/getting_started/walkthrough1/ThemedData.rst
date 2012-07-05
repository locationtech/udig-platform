Style Editor
------------

The :guilabel:`Style Editor` allows fine grain control of the layer rendering.
In addition to shapes, layers contain attribute values which we can use to
control the rendering process.

In this section we will create a *Styled Layer Descriptor* (SLD) file with
the uDig :guilabel:`Style Editor` in order to generate labels and theme
by attribute values.

* Create a new map using :menuselection:`File --> New --> New Map`

* Select your new map in the :guilabel:`Projects` view, and right click to choose :guilabel:`Rename`.
  
  |projects_map_rename_jpg|

* Enter the name ``Natural Earth`` and press :guilabel:`OK`.

* In the :guilabel:`Layer` menu, select :guilabel:`Add`

* Choose :guilabel:`Files` from the provided list and press :guilabel:`Next`.
 
  |add_data_files_png|

* Select the following files from your data directory:

  * :guilabel:`10m_admin_0_countries.shp`
  * :guilabel:`NE1_50M_SR_W.tif`
   
  On windows hold down the control key to selecting multiple files.
   
  |file_dialog_multiselect_png|

* Press :guilabel:`Open` and both layers will be added to your map and
  rendered with a default style.
   
  * The default style for the :guilabel:`10m admin 0 countries` layer is
    slightly transparent so can see the base raster layer.
  * You may wish to zoom out using the toolbar :guilabel:`Zoom Extent` command

* Right click on the countries layer and select :guilabel:`Change Style`
  to open the :guilabel:`Style Editor`.
    
  |layers_change_style_png|

* The :guilabel:`Style Editor` is arranged into a series of pages.
    
* Select the :guilabel:`Polygons` page from the list on the right.
    
  We will use this page to add labels to the countries layer.
    
* Click the :guilabel:`Labels` tab on the :guilabel:`Polygon` page.

  * Enable labels by checking the :guilabel:`enable/disable labelling` checkbox
  * In the list to the far right of :guilabel:`label` choose the :guilabel:`NAME` attribute
    
  |style_polygon_label_png|
    
* You can press the :guilabel:`Apply` button to see what your map will look like with labels

Theme
^^^^^

A common use for maps is as a visual display of attribute values. In cartographic
terms this is known as creating a *Thematic* map, accomplished by defining
a *theme* for a layer.

The :guilabel:`Style Editor` is used to theme data by setting up a thematic style
for a layer based on attribute values.

* Please switch to the :guilabel:`Theme` page and select the following:
   
   * :guilabel:`Attribute`: Select the :guilabel:`POP_EST` attribute.
   * :guilabel:`Normalize`: Select the :guilabel:`GDP_MD_EST` attribute.

   |style_theme_normalize_png|
    
* Filter the available colour palettes to show a subset of those available:
   
  * :guilabel:`Show`: Change from guilabel:`Show All` to :guilabel:`Sequential`
  * Press the :guilabel:`Colour-blind`, :guilabel:`LCD` and :guilabel:`CRT` buttons
  * Select the :guilabel:`light orange to dark red` color palette
   
  |style_theme_filter_png|
   
  These options show palettes which are a ramp of color suitable for viewing by color
  blind people on either an LCD or CRT monitor.

* Press the :guilabel:`OK` button
   
  |themed_map_png|

* The countries layer is now displayed as theme of population data normalized by gross domestic
  product as a quick measure of population productivity.

Legend
^^^^^^

You can use a legend to review the colours and symbology used to illustrate your themaitic map.
 
* From the menubar select :menuselection:`Layer --> Legend`.
    
  |menubar_legend_png|
    
* The :guilabel:`Legend` decorator offers a quick summary of your themed data.

Mylar
^^^^^

The map display can become very complicated depending on the level of detail being displayed.

The Mylar functionality is used to focus on the selected layer.

* Press the :guilabel:`Mylar` button in the :guilabel:`Layers` view toolbar.
    
* Select each layer and observe the effect.
   
  |mylar_png|
    
* The Mylar effect is used to focus on the selected layer by fading out everything else.

Layout and Perspectives
-----------------------

* :guilabel:`Map` editors can be resized and expanded

  * Double click on the :guilabel:`Natural Earth` editor tab to maximize the editor display.
     
  * Use using the zoom tool to explore the world while the editor is maximized.
   
  |editor_maximized_png|

  * Your :guilabel:`Layer` view is still available as a fast view the left hand side of the screen.
    Press :guilabel:`Layer` view button to slide the :guilabel:`layer` view on and off the screen as needed.

  * Double click on the :guilabel:`Natural Earth` editor tab again to restore the previous size.
   
* You can also open two map editors and arrange them side by side by dragging
  the :guilabel:`Map` editor tab into position.

* You have a lot of flexibility in arranging views around your :guilabel:`Map` editor.
   
  * You can drag views into different locations along the edge of your map by dragging their
    :guilabel:`View` tab into the desired location.
  * You can detach a view by dragging it completely out of the window
  * You can right click on the view for more options including a slide out :guilabel:`Fast` view.
  * More views are available using the menubar :menuselection:`Window --> Show Views`

* The arrangement of the :guilabel:`Workbench` editors and views is called a :guilabel:`Perspective`.
   
  * Switch between perspectives using the menubar :menuselection:`Window --> Open Perspective`
  * Use the menubar :menuselection:`Window --> Reset Perspective` to restore any views you accidentally closed
    during experimentation.
   
  |menubar_window_reset_perspective_png|

.. |menubar_legend_png| image:: images/menubar_legend.png
    :width: 5.2cm
    :height: 3.129cm


.. |style_theme_filter_png| image:: images/style_theme_filter.png
    :width: 10.71cm
    :height: 8.691cm


.. |style_theme_normalize_png| image:: images/style_theme_normalize.png
    :width: 10.77cm
    :height: 6.729cm


.. |projects_map_rename_jpg| image:: images/projects_map_rename.jpg
    :width: 6.219cm
    :height: 5.42cm


.. |style_polygon_label_png| image:: images/style_polygon_label.png
    :width: 14.7cm
    :height: 10.261cm


.. |menubar_window_reset_perspective_png| image:: images/menubar_window_reset_perspective.png
    :width: 14.891cm
    :height: 2.949cm


.. |mylar_png| image:: images/mylar.png
    :width: 14.91cm
    :height: 11.12cm


.. |file_dialog_multiselect_png| image:: images/file_dialog_multiselect.png
    :width: 13.46cm
    :height: 10.389cm


.. |editor_maximized_png| image:: images/editor_maximized.png
    :width: 14.861cm
    :height: 11.15cm


.. |layers_change_style_png| image:: images/layers_change_style.png
    :width: 5.241cm
    :height: 4.33cm


.. |add_data_files_png| image:: images/add_data_files.png
    :width: 7.62cm
    :height: 7.47cm


.. |themed_map_png| image:: images/themed_map.png
    :width: 10.991cm
    :height: 6.17cm
