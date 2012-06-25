PostGIS page
############

The PostGIS page is used to define a connection to a PostGIS database.

  .. figure:: /images/postgis_page/PostGISPage.png
     :align: center
     :alt: 


+-------------------+-----------------------------------------+
| **Field**         | **Description**                         |
+-------------------+-----------------------------------------+
| Host              | server hosting PostGIS service          |
+-------------------+-----------------------------------------+
| Port              | port number of PostGIS service          |
+-------------------+-----------------------------------------+
| Username          | for database authentication             |
+-------------------+-----------------------------------------+
| Password          | for database authentication             |
+-------------------+-----------------------------------------+
| Database          | list of available databases             |
+-------------------+-----------------------------------------+
| Schema            | schema of tables to connect to          |
+-------------------+-----------------------------------------+

Press **Next** to connect to the database. If you are adding data directly to a map you will proceed
to the :doc:`Resource Selection page` page.

Advanced Options
----------------

You can enabled the advanced options if you wish to experiment with a few 
configuration options effecting performance.

+--------------------+-----------------------------------------+
| **Advanced**       | Used to toggle on advanced options      |
+--------------------+-----------------------------------------+
| **Use WKB**        | on for binary communication; false for  |
|                    | text communication                      |
+--------------------+-----------------------------------------+
| **Use Loose BBox** | on for fast bounding box queries that   |
|                    | may return more information             |
+--------------------+-----------------------------------------+

**Related concepts**


:doc:`PostGIS`


**Related reference**


:doc:`Add Data wizard`

 :doc:`Resource Selection page`

