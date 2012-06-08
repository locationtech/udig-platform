Themed Data
-----------------------------------

Some data contains attributes we may use to thematically style a layer. In this section we will create a “Styled Layer Descriptor” (SLD) with the uDig
SLD Editor
in order to interpret these attributes:

* Create a new map using
  File > New > New Map


* Select “Map 2” in the project view, and right click to choose
  Rename
  .
  |100000000000012600000100A3BFB98C_jpg|


* Enter the name “Natural Earth” and press
  OK
  .


* In the
  Layer
  menu, select
  Add…


* Choose
  Files
  from the provided list and press
  Next
  |100000000000020D00000203728201CC_png|


* Select the following files from the data directory:
  10m_admin_0_countries.shp
  NE1_50M_SR_W.tif
  |1000000000000280000002075AA28808_png|


* Press
  Open and
  both
  layers will be added and
  rendered with a default style.


* The default style for the
  countries
  (named 10m admin 0 countries) layer is slightly transparent so can see the base raster layer.


* Right click on the
  countries
  layer and select
  Change Style
  |100000000000014A00000111DE88E6CF_png|


* The Style Editor is arranged into a series of pages. Select the
  Polygons
  page from the list on the right; we will use this page to add labels to the
  countries
  layer.


* Click the
  Labels
  tab on the
  Polygon
  page


* Enable labels by checking the “
  enable/disable labelling
  ” check box


* In the list to the far right of “
  label
  ” choose “
  NAME
  ”
  |10000000000003F5000002C307148CF1_png|


* You can press the
  Apply
  button to see what your map will look like with labels.


* Please switch to the
  Theme
  style page and select the following options:


* Attribute:
  POP_EST


* Normalize:
  GDP_MD_EST
  |10000000000002E6000001D0C3BF163B_png|


* Filter the available colour palettes to show a subset of those available:


* Changing from s
  how
  “
  All
  ” to s
  how
  “
  Sequential
  ”


* Press
  the
  Colour-blind, LCD
  and
  CRT
  buttons


* Select the “
  light orange to dark red
  ” palette
  |10000000000002E20000025740AFFEC9_png|


* Press the
  OK
  button
  |10000000000002F5000001A9713A4194_png|


* Open up the
  Layer
  menu and choose
  Legend
  |1000000000000166000000D890C47BCC_png|


* Press the
  Mylar
  button in the
  Layers View
  . Select each layer and observe the effect.
  |1000000000000402000002FE20171B5A_png|


* Double click on the
  Natural Earth
  tab at the top of the screen to maximize the
  editor. Use using the zoom tool to explore the world while the editor is maximized.
  |100000000000040000000300208450BB_png|


* Double click on the “Map tab” at the top of the screen to restore the previous size.


* You have a lot of flexibility in arranging views:


* You can drag views into different locations along the edge of your map


* You can detach a view by dragging it completely out of the window


* You can right click on the view for more options including a slide out “fast” view.


* More views are available using the menubar
  Window > Show Views


* You can use
  Reset Perspective
  from the
  Window
  menu to restore any views you accidentally close during experimentation.
  |10000000000003AA000000BA587A941A_png|


.. |1000000000000166000000D890C47BCC_png| image:: images/1000000000000166000000D890C47BCC.png
    :width: 5.2cm
    :height: 3.129cm


.. |10000000000002E20000025740AFFEC9_png| image:: images/10000000000002E20000025740AFFEC9.png
    :width: 10.71cm
    :height: 8.691cm


.. |10000000000002E6000001D0C3BF163B_png| image:: images/10000000000002E6000001D0C3BF163B.png
    :width: 10.77cm
    :height: 6.729cm


.. |100000000000012600000100A3BFB98C_jpg| image:: images/100000000000012600000100A3BFB98C.jpg
    :width: 6.219cm
    :height: 5.42cm


.. |10000000000003F5000002C307148CF1_png| image:: images/10000000000003F5000002C307148CF1.png
    :width: 14.7cm
    :height: 10.261cm


.. |10000000000003AA000000BA587A941A_png| image:: images/10000000000003AA000000BA587A941A.png
    :width: 14.891cm
    :height: 2.949cm


.. |1000000000000402000002FE20171B5A_png| image:: images/1000000000000402000002FE20171B5A.png
    :width: 14.91cm
    :height: 11.12cm


.. |1000000000000280000002075AA28808_png| image:: images/1000000000000280000002075AA28808.png
    :width: 13.46cm
    :height: 10.389cm


.. |100000000000040000000300208450BB_png| image:: images/100000000000040000000300208450BB.png
    :width: 14.861cm
    :height: 11.15cm


.. |100000000000014A00000111DE88E6CF_png| image:: images/100000000000014A00000111DE88E6CF.png
    :width: 5.241cm
    :height: 4.33cm


.. |100000000000020D00000203728201CC_png| image:: images/100000000000020D00000203728201CC.png
    :width: 7.62cm
    :height: 7.47cm


.. |10000000000002F5000001A9713A4194_png| image:: images/10000000000002F5000001A9713A4194.png
    :width: 10.991cm
    :height: 6.17cm

