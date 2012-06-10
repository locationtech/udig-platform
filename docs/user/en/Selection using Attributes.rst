Selection using Attributes
##########################

You can define a selection by matching attributes:

#. Select a Feature Layer is the Layer View
#. Open the Table View
#. Select an Attribute from the list (or choose **All**)
#. Type in the value you are searching for.
#. Press **Enter**, the table and Map selection selection will be updated accordingly

Examples
--------

+--------------------+-----------------------------------------------------------------+
| hello              | will match any word containing the string **hello**             |
+--------------------+-----------------------------------------------------------------+
| \\Ahello           | will match any word that starts with **hello**                  |
+--------------------+-----------------------------------------------------------------+
| \\Ahello\\Z        | *Exactly* matches the word **hello**                            |
+--------------------+-----------------------------------------------------------------+
| \\Ahello\|\\Aboo   | will match strings that start with **hello** or **boo**         |
+--------------------+-----------------------------------------------------------------+
| \\Ahe.\*o\\Z       | will match strings that start with **he** and ends with **o**   |
+--------------------+-----------------------------------------------------------------+
| [ln]               | will match any string that contains **l** or **n**              |
+--------------------+-----------------------------------------------------------------+
| \\A[ln]            | will match many string that starts with **l** or **n**          |
+--------------------+-----------------------------------------------------------------+

**Note:** The search is NOT case sensitive.
 **Note:** In most cases **\\A** can be replaced with **^** (start of line) and \\Z can be replaced
with **$** (end of line)

**Related reference**


* :doc:`Table view`

* :doc:`Selection using CQL`

* :doc:`Regular Expression`


