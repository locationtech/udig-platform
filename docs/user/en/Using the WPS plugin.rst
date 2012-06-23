Using the WPS plugin
####################

Once you have uDig up and running with the **WPS plugin**, it is fairly straight forward to use. The
first step is to load a **WPS** server into the catalog.

* `Loading a WPS server into the Catalog`_

* `Executing a Process`_

* `Other Useful Functionality`_


Loading a WPS server into the Catalog
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#. Select :menuselection:`File --> Import` to open the **Import Wizard** window
  |image0|
#. Select *Web Processing Service (WPS)* as the import source and click *Next*
#. Enter the *GetCapabilities URL* for your **WPS** server and click *Finish*
  |image1|
#. Your **WPS** server will now appear in the **Catalog View**. Expand it to get a list of processes
   it offers
  |image2|

Executing a Process
~~~~~~~~~~~~~~~~~~~

#. To execute a process offered by your server, right-mouse-click on the process and select
   :menuselection:`Operations --> Execute`
  |image3|
#. A **WPS - Process** View will open to display the process information and input parameters form
   (details shown in screenshot below)
  |image4|
#. You can enter input parameters by manually typing them into the fields (use **Well Known Text**
   format for geometries). You can also **import selected map objects** if they fit the type
   expected for the input paramter:
  |image5|
#. Press **Execute** when you have entered the required input parameters to send your request to the
   **WPS** server. Feedback will be provided in the **Process Info** console. For processes that
   return map data such as a geometry, a new **Scratch Layer** will be created in uDig to display
   the results. For output that is more textual, it will be displayed in the **Process Info**
   console.
  |image6|

Other Useful Functionality
~~~~~~~~~~~~~~~~~~~~~~~~~~

-  Some processes allow for variable number of input parameters. When an input parameter can have a
   variable number of values, it will be displayed on the form under an expanding category (see
   screenshot below). If you select one of these inputs, the **add** and **remove** buttons can be
   used to add and remove instances of this input to the form. The form will not allow you to add
   more than the maximum number of inputs, nor will it allow you to remove more than the minimum
   number of inputs (as described by the server's **describeProcess**).
  |image7|

.. |image0| image:: /images/using_the_wps_plugin/wps_import1.jpg
.. |image1| image:: /images/using_the_wps_plugin/wps_import2.jpg
.. |image2| image:: /images/using_the_wps_plugin/wps_catalog1.jpg
.. |image3| image:: /images/using_the_wps_plugin/wps_catalog2.jpg
.. |image4| image:: /images/using_the_wps_plugin/wps_processview1.jpg
.. |image5| image:: /images/using_the_wps_plugin/wps_processview2.jpg
.. |image6| image:: /images/using_the_wps_plugin/wps_processview4.jpg
.. |image7| image:: /images/using_the_wps_plugin/wps_processview3.jpg
