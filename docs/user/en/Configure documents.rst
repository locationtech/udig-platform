Configure documents
###################

To open settings page:
----------------------

#. Use :guilabel:`Catalog` view to select the resource or select a layer from the :guilabel:`Layers` view 
#. Open the :doc:`Resource page` from the **Data** menu.
#. Select the :doc:`Resource Document page` tab

To enable resource documents:
-----------------------------

#. Open settings page
#. Check :guilabel:`Enable support for resource documents`

To enable feature documents:
----------------------------

#. Open settings page
#. Check :guilabel:`Enable support for feature documents`

To enable feature hotlinks:
---------------------------

#. Open settings page
#. Check :guilabel:`Enable support for hotlink on marked attributes`

To set an attribute as a file hotlink:
--------------------------------------

#. Enable feature hotlinks
#. Click :guilabel:`Add...` to open the **Add Hotlink Descriptor** dialog 
#. Select *File* as the **Type**
#. Select the **Attribute** containing the reference information
#. Provide the appropriate **Label**
#. (Optional) Provide the appropriate **Description**
#. Click :guilabel:`OK`
   
To set an attribute as a web hotlink:
-------------------------------------

#. Enable feature hotlinks
#. Click :guilabel:`Add...` to open the **Add Hotlink Descriptor** dialog 
#. Select *Web* as the **Type**
#. Select the **Attribute** containing the reference information
#. Provide the appropriate **Label**
#. (Optional) Provide the appropriate **Description**
#. Click :guilabel:`OK`
   
To set an attribute as an action hotlink:
-----------------------------------------

#. Enable feature hotlinks
#. Click :guilabel:`Add...` to open the **Add Hotlink Descriptor** dialog 
#. Select *Action* as the **Type**
#. Select the **Attribute** containing the reference information
#. Provide the appropriate **Label**
#. (Optional) Provide the appropriate **Description**
#. Provide the **Action** definition
#. Click :guilabel:`OK`

Action definition examples:

- *https://www.google.com.au/search?q={0}* - For a Google search action
- *http://au.search.yahoo.com/search?p={0}* - For a Yahoo! search action
- *C:\\Reports\\{0}.pdf* - For a file reference action

**Note:** Use the marker '{0}' in the action definition to mark parts to be replaced with the attribute's value.
   
To edit an existing hotlink definition:
---------------------------------------
   
#. Open settings page
#. Select hotlink from the list
#. Click :guilabel:`Edit...` to open the **Edit Hotlink Descriptor** dialog
#. Select **Type**
#. Select the **Attribute** containing the reference information
#. Provide the appropriate **Label**
#. (Optional) Provide the appropriate **Description**
#. (For Action types) Provide the **Action** definition
#. Click :guilabel:`OK`
   
To remove an existing hotlink definition:
-----------------------------------------
   
#. Open settings page
#. Select hotlink from the list
#. Click :guilabel:`Remove`
   
**Sample dataset:**

- Try out *australia.shp* in data_1_4.zip of the Quickstart sample data.

**Related tasks**

:doc:`Working with Documents`

**Related reference**

:doc:`Document view`

:doc:`Resource page`

:doc:`Resource Document page`
