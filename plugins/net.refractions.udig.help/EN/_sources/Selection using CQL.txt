Selection using CQL
###################

You can define a selection using Common Query Langague (similar to an SQL where statement):

#. Select a layer with feature content in the Layer View
#. Open the Table View
#. From the list of available attributes go to the bottom of the list and choose: **CQL**
#. Type in a filer:

   -  COLOR\_MAP=3 OR COLOR\_MAP=2
   -  NAME like '%Gulch%' AND DESIG = 'valley'

Common Query Language Examples
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

+-------------------------+-------------------------------------------+
| population > 30000      | population attribute greater than 30000   |
+-------------------------+-------------------------------------------+
| age BETWEEN 20 AND 30   | age between 20 and thirty                 |
+-------------------------+-------------------------------------------+

**Related concepts**

:doc:`Constraint Query Language`


**Related reference**

:doc:`Table view`
