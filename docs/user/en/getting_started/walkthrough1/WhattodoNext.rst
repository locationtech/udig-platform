What to do Next
---------------

Congratulations you have finished the first Walkthrough 1.

This is just the start of what uDig can do!

* Try out the :doc:`North Arrow` decorator and change projections in order to see how it works.

  |100000000000026C0000018BE3CEFED9_png|

* There is more great data available in the data directory - have a look !
  
  This information has been collected by the `natural Earth <http:naturalearth.org>`_ project. Their website
  contains more detailed downloads and a host of additional data.
  
  Try downloading the same data at several scales and compare the results.

* Try right-clicking on a Layer - there is plenty to do (especially in the :guilabel:`Operations` menu).

* Try making use of spatial information from your organisation or government.

* Try out the navigation tools such as :guilabel:`Zoom` and :guilabel:`Pan`.
  The :guilabel:`Navigation` menu lets you you retrace your steps.
  
  Changing coordinate reference system is also considered a navigation command.

* Advanced: Use the :guilabel:`Style Editor` to switch automatically from
  a raster layer to a web service.
  
  Switching based on scale allows for performance when zoomed out, while still
  providing full resolution when zoomed in.

* Advanced: Open the :guilabel:`Style Editor` and have a look at the
  :guilabel:`Advanced (XML)` page and see what you make of it.

* Perhaps you have an idea for the tool you always wanted?
  
  Developer tutorials including how to make a custom tool and create a custom
  application are available on the project web site.

Install Plugin
^^^^^^^^^^^^^^

One of the nice things about uDig is the ease to download and install additional plug-ins provided by the developer community.

1. Open up :guilabel:`Help > Find and Install`
   
   |10000000000001D1000000D27D89B6DA_png|

2. Select :guilabel:`Search for New Features to Install` and press :guilabel:`Next`.
   
   |10000000000002750000013D3A5ED79C_png|

3. Press the :guilabel:`New Remote Site` button and type the following parameters:
   
   * Name: :kbd:`uDig Community Updates`
   * URL: :kbd:`http://udig.refractions.net/update/1.2/community`
   
   |1000020100000166000000B09D072A10_png|

4. Check the new :guilabel:`uDig Community Updates` option and press :guilabel:`Finish`
   
   |10000201000002770000029DF0ED262D_png|

5. Please wait while the program checks the update site for new features.
   
   |100002010000025B000000F5B04A0404_png|

6. Once the program finishes getting the complete list of plug-ins, you will see something like this:
   
   |100002010000025A00000229706BBF67_png|


7. Check the latest *es.axios.udig.editingtools* version from the list and press :guilabel:`Next`.

8. Accept the terms in the license agreement and press :guilabel:`Next`.
  
   |100002010000025A000002294F137721_png|


8. Finally confirm the installation location and press :guilabel:`Finish`.
   
   |100002010000025A000002292818B80D_png|


9. Many of the community features have not been formally signed, please click :guilabel:`Install All` 
   when presented with a warning.
   
   |100002010000025A000001F11BE2F1C2_png|

10. You will need to :guilabel:`Restart` when the installation finishes.

    |100002010000025B00000098C874C9A5_png|

11. You may have noticed some new options in the toolbar, such as the merge tool described in the next section. 

Merge
^^^^^

1. We are going to test the :guilabel:`Merge` feature.
    
2. Load a test layer, like :guilabel:`countries` and select the
   :guilabel:`Merge` tool,
    
   |100000000000035C0000009FF4CBA78B_png|

3. Select some features in your layer.
   
   |10000000000002200000025226C9A962_png|
   
   |1000000000000220000002533D172BB1_png|

4. A new View called :guilabel:`Merge Tool` will appear in the lower box.
   
   |10000000000003FC0000014FB2C974F4_png|

5. Click on the green check :guilabel:`Apply` button  in the :guilabel:`Merge` view toolbar
   to run the merge of the features.
   
6. The result will be something like this:
   
   |10000000000002210000024330223155_png|

Adding a Layer from PostGIS
^^^^^^^^^^^^^^^^^^^^^^^^^^^

.. sidebar:: Firewall
   
   This section requires your own local PostGIS. While we have a public PostGIS available
   for demonstrations most corporate environments are unable to access this database due
   to firewall restrictions.
   
   If you are trapped behind a firewall please feel free to skip this section.
   
   The online help does have some advice about :doc:`Running uDig` covering
   firewall access.
   
This section shows how you can add a Layer from a PostGIS table. PostGIS is an extension to the popular
open source PostgreSQL database. uDig handles other databases like Oracle and DB2 in a similar manner:

1. In the File menu, select :guilabel:`New > New Map`.
   
   |10000000000001680000009E5CAF954B_png|

2. In the :guilabel:`Projects` view, right-click on your map and select :guilabel:`Add`.
   
   |10000000000001260000011268DE12AC_png|

3. Select :guilabel:`PostGIS` as the data source and click :guilabel:`Next`.
   
   |100000000000020D000001AD21F4CAF3_png|

4. Enter the following connection information:
   
  * Host: :kbd:`www.refractions.net`
  * Port: :kbd:`5432`
  * Username: kbd:`demo`
  * Password: :kbd:`demo`
  * Store Password: check
  
  Once the connection information is entered press :guilabel:`Next`.
  
  |100000000000020D000001ADB9E75C01_png|


5. This page lists the databases available to the current user.
   
   The www.refractions.net database does not publish a public list so rather than choose from a
   nice easy list we are going to have to enter in :kbd:`demo-bc` by hand.
   
   * Database: :kbd:`demo-bc`
   
   |100000000000020D000001DC5AC9B824_png|

7. We can now press the :guilabel:`List` button to list the available tables. Please choose
   
   *:guilabel:`bc_hospitals`
   *:guilabel:`bc_municipality`
   
   Press :guilabel:`Next` when ready.

8. The resource collection page confirms that :guilabel:`bc_hospitals` and :guilabel:`bc_municipality`
   are published as spatial layers.
   
   We can press :guilabel:`Finish` to add these layers to our Map.
   
   |100000000000020D0000018256DA3A32_png|

9. It may take a short while to fully render since you are zoomed out so far.
   
10. Head on over the the :guilabel:`Layer` view and right click on
    :guilabel:`bc_hospitals` layer and choose :guilabel:`Zoom to Layer`
    
    |1000000000000405000003056AEA1FCC_png|
    
    The map will now zoom in to show the extent of the :guilabel:`bc_hospitals` layer.

11. You can return to your previous position in the world by selecting Back in the Navigation menu.


.. |100002010000025B000000F5B04A0404_png| image:: images/100002010000025B000000F5B04A0404.png
    :width: 10.361cm
    :height: 4.018cm


.. |10000201000002770000029DF0ED262D_png| image:: images/10000201000002770000029DF0ED262D.png
    :width: 8.53cm
    :height: 9.197cm


.. |1000000000000405000003056AEA1FCC_png| image:: images/1000000000000405000003056AEA1FCC.png
    :width: 14.52cm
    :height: 10.91cm


.. |100000000000035C0000009FF4CBA78B_png| image:: images/100000000000035C0000009FF4CBA78B.png
    :width: 12.577cm
    :height: 2.508cm


.. |100000000000026C0000018BE3CEFED9_png| image:: images/100000000000026C0000018BE3CEFED9.png
    :width: 11.479cm
    :height: 7.31cm


.. |1000000000000220000002533D172BB1_png| image:: images/1000000000000220000002533D172BB1.png
    :width: 5.916cm
    :height: 7.121cm


.. |10000000000001D1000000D27D89B6DA_png| image:: images/10000000000001D1000000D27D89B6DA.png
    :width: 8.61cm
    :height: 3.889cm


.. |1000020100000166000000B09D072A10_png| image:: images/1000020100000166000000B09D072A10.png
    :width: 6.618cm
    :height: 3.226cm


.. |100000000000020D000001AD21F4CAF3_png| image:: images/100000000000020D000001AD21F4CAF3.png
    :width: 8.89cm
    :height: 7.26cm


.. |100000000000020D000001DC5AC9B824_png| image:: images/100000000000020D000001DC5AC9B824.png
    :width: 9.631cm
    :height: 8.729cm


.. |100000000000020D000001ADB9E75C01_png| image:: images/100000000000020D000001ADB9E75C01.png
    :width: 9.631cm
    :height: 7.87cm


.. |100000000000020D0000018256DA3A32_png| image:: images/100000000000020D0000018256DA3A32.png
    :width: 9.631cm
    :height: 7.08cm


.. |10000000000001260000011268DE12AC_png| image:: images/10000000000001260000011268DE12AC.png
    :width: 4.979cm
    :height: 4.641cm


.. |10000000000002750000013D3A5ED79C_png| image:: images/10000000000002750000013D3A5ED79C.png
    :width: 13.31cm
    :height: 6.71cm


.. |100002010000025A000001F11BE2F1C2_png| image:: images/100002010000025A000001F11BE2F1C2.png
    :width: 9.627cm
    :height: 8.225cm


.. |100002010000025A00000229706BBF67_png| image:: images/100002010000025A00000229706BBF67.png
    :width: 10.116cm
    :height: 9.594cm


.. |100002010000025A000002294F137721_png| image:: images/100002010000025A000002294F137721.png
    :width: 10.343cm
    :height: 10.045cm


.. |100002010000025B00000098C874C9A5_png| image:: images/100002010000025B00000098C874C9A5.png
    :width: 11.374cm
    :height: 2.822cm


.. |10000000000003FC0000014FB2C974F4_png| image:: images/10000000000003FC0000014FB2C974F4.png
    :width: 13.173cm
    :height: 4.313cm


.. |10000000000002210000024330223155_png| image:: images/10000000000002210000024330223155.png
    :width: 9.629cm
    :height: 10.643cm


.. |10000000000002200000025226C9A962_png| image:: images/10000000000002200000025226C9A962.png
    :width: 5.916cm
    :height: 7.121cm


.. |10000000000001680000009E5CAF954B_png| image:: images/10000000000001680000009E5CAF954B.png
    :width: 6.669cm
    :height: 2.93cm


.. |100002010000025A000002292818B80D_png| image:: images/100002010000025A000002292818B80D.png
    :width: 9.516cm
    :height: 9.243cm

