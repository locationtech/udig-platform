Web Feature Server page
#######################

The Web Feature Server page of the Add Data wizard is used to connect to to OGC Web Feature Servers.
Web Feature Servers provide direct access to raw feature data (rather than simply a picture).

.. figure:: /images/web_feature_server_page/WebFeatureServerPage.png
   :align: center
   :alt: 

Field

Description

URL

The capabilities document of the web feature service.

Currently uDig supports WFS 1.0 servers, making use of the GML2 format.

Advanced Configuration Options
------------------------------

These settings are optional and are only of interest if you are performance tunning or having
trouble connecting using the defaults.

Field

Description

GET

Service may be accessed using HTTP Get

POST

Service may be accessed using HTTP Post, helpful when making complicated queries by hand.

Maximum Features

The default value of 100 is fine, lower values may be needed when working with large individual
features with lots of attributes. Higher values may be worth while when working with point data.

Timeout

You may wish to adjust this delay based of service reliability

**Related concepts**


* :doc:`Web Feature Server`


**Related reference**


* :doc:`Add Data wizard`

* :doc:`Drag and Drop`

* :doc:`Web view`


