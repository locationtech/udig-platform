Connecting to a Web Map Server
-----------------------------------

In this section you will learn how to drag and drop a Web Map Server (WMS) link into uDig for the purpose of viewing its layers.

1. There are many ways to load map data into uDig, including drag and drop.
   To drag a Web Map Server (WMS) link into uDig, open up a web browser.

2. Please connect to the Walkthrough 1 page with your web browser:
   `Walkthrough 1 <http://udig.refractions.net/confluence/display/EN/Walkthrough+1>`_

3. We are going to use the link to:
   `DM Solutions WMS <http://www2.dmsolutions.ca/cgi-bin/mswms_gmap?Service=WMS&VERSION=1.1.0&REQUEST=GetCapabilities>`_
      
4. For most browsers you can simply drag the link into the :guilabel:`Layers` view
        
   |1000000000000388000002E2E485918D_png|

   * If your browser does not support dragging layers please :guilabel:`copy` the link and then select the
     :guilabel:`Layers` view and paste.
     
     The latest version of Internet Explorer and Opera do not support dragging URLs into other application.
   
   * If working with your windows maximized: Drag from the web browser, over to the uDig application
     in the task bar (to switch applications), and then drop into the :guilabel:`Layers` view.
   
  
5. The :guilabel:`Add Layers` wizard will appear asking you what layers from this WMS you want to show in your map. Select
   :guilabel:`Elevation/Bathymetry, Parks, Cities` and press the :guilabel:`Finish` button.
  
   |100000000000020D000001E7638F1787_png|

6. The map layers will now render in the :guilabel:`Map` editor.
   
   |100000000000040000000300A11D76C3_png|

   Notice the bottom right corner of the uDig Application will display a :guilabel:`processing` notice
   while it is requesting and drawing the layers.
   
   * Text and progress bar indicate rendering status
   * Click on the icon to open the :guilabel:`Progress` view to monitor (and cancel) background processes such as rendering.

7. When the layers are done rendering, the :guilabel:`Map` editor will display the visible layers
 
   |10000000000004000000030027731BCF_png|
   
8. Now that you have some data on screen try the navigation tools along the top of the tool palette.
   
   * |zoom_mode| Zoom (keyboard short cut :kbd:`z`)
     Click or drag the left button to zoom in, or right button to zoom out.
   
   * |pan_mode| Pan (keyboard short cut :kbd:`p`)
     Click and drag to move the display.

   **Mouse Wheel**: The mouse scroll wheel can be used to zoom in and out quickly regardless of the currently selected tool.
   
9. The remaining tools are organised into drawers according to function.
   
   You can open and close the drawers by clicking on their title. Drawers will close automatically as you switch between drawers. You can also pin a drawer open for easy access.
 
   |1000000000000081000001924E854422_png|

   Available tools change depending on the currently selected layer.

10. You can right click on a drawer to customise the size of the icons used; and how much information you wish to see in the palette.
    
    |10000000000000820000018F9C5F08A7_png|

11. In addition to the palette the map has a toolbar along the top of the screen for common actions.
    
    * |zoom_extent_co| Extent: zoom out to show all enabled layers
    
    * |zoom_in_co| Zoom In
    
    * |zoom_out_co| Zoom Out
    
    * |cancel_all_co| Stop Rendering
    
    * |refresh_co| Refresh Map
    
12. The :guilabel:`Layers` view shows the order in which layers are drawn. Please select the
    :guilabel:`Elevation/Bathymetry` layer and use the :guilabel:`Move down` button from the
    :guilabel:`Layer` view toolbar to move selected layer to the bottom of the list.
    
    |100000000000015D00000073FDB25240_png|

    **Drag and Drop** You can also drag layers up and down in the layer view to change the order.

13. The order is now changed.
    
    |100000000000015D00000073252C5C23_png|

.. |pan_mode| image:: /images/navigation_tools/pan_mode.gif

.. |zoom_mode| image:: /images/navigation_tools/zoom_mode.gif

.. |zoom_extent_co| image:: /images/navigation_tools/zoom_extent_co.gif

.. |zoom_in_co| image:: /images/navigation_tools/zoom_in_co.gif
    
.. |zoom_out_co| image:: /images/navigation_tools/zoom_out_co.gif
    
.. |cancel_all_co| image:: /images/navigation_tools/cancel_all_co.gif
    
.. |refresh_co| image:: /images/navigation_tools/refresh_co.gif


.. |10000000000000820000018F9C5F08A7_png| image:: images/10000000000000820000018F9C5F08A7.png
    :width: 1.891cm
    :height: 5.791cm


.. |1000000000000081000001924E854422_png| image:: images/1000000000000081000001924E854422.png
    :width: 1.87cm
    :height: 5.83cm


.. |10000000000004000000030027731BCF_png| image:: images/10000000000004000000030027731BCF.png
    :width: 14.861cm
    :height: 11.15cm


.. |100000000000040000000300A11D76C3_png| image:: images/100000000000040000000300A11D76C3.png
    :width: 14.861cm
    :height: 11.15cm


.. |100000000000015D00000073FDB25240_png| image:: images/100000000000015D00000073FDB25240.png
    :width: 5.911cm
    :height: 1.951cm


.. |100002000000001000000010BAAA234E_png| image:: images/100002000000001000000010BAAA234E.png
    :width: 0.423cm
    :height: 0.423cm


.. |1000000000000388000002E2E485918D_png| image:: images/1000000000000388000002E2E485918D.png
    :width: 13.12cm
    :height: 10.71cm


.. |100000000000015D00000073252C5C23_png| image:: images/100000000000015D00000073252C5C23.png
    :width: 5.911cm
    :height: 1.951cm


.. |100000000000020D000001E7638F1787_png| image:: images/100000000000020D000001E7638F1787.png
    :width: 8.89cm
    :height: 8.25cm

