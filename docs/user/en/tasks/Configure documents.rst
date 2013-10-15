Configure documents
###################

Configuration is required to enable resource documents, feature documents
or feature hotlinks.

Support for documents is dependent on the resource selected and may not always be appropriate.

Configure resource documents
----------------------------

To enable resource documents (currently supported for shapefiles):

#. Use :guilabel:`Catalog` view to select the resource (or select a layer from the :guilabel:`Layers` view).
#. Select :menuselection:`Data --> Resource Properties` from the menu bar to open the :guilabel:`Property Dialog`.
#. Navigate open :guilabel:`Resource` and select the :guilabel:`Document` page.
#. From the :guilabel:`Document` page:

   * Check :guilabel:`Enable support for resource documents`

Configure feature documents
---------------------------

To enable feature documents (currently supported for shapefiles):

#. Use :guilabel:`Catalog` view to select the resource (or select a layer from the :guilabel:`Layers` view).
#. Select :menuselection:`Data --> Resource Properties` from the menu bar to open the :guilabel:`Property Dialog`.
#. Navigate open :guilabel:`Resource` and select the :guilabel:`Document` page.
#. From the :guilabel:`Document` page:

   * Check :guilabel:`Enable support for feature documents`

Configure feature hotlinks
--------------------------

To enable feature hotlinks:

#. Use :guilabel:`Catalog` view to select the resource (or select a layer from the :guilabel:`Layers` view).
#. Select :menuselection:`Data --> Resource Properties` from the menu bar to open the :guilabel:`Property Dialog`.
#. Navigate open :guilabel:`Resource` and select the :guilabel:`Document` page.
#. From the :guilabel:`Document` page:

   * Check :guilabel:`Enable support for hotlink on marked attributes`


To set an attribute as a file hotlink
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

#. From the :guilabel:`Document` page
#. Click :guilabel:`Add...` to open the **Add Hotlink Descriptor** dialog
#. Select *File* as the **Type** and select the **Attribute** containing the reference information
#. Provide the appropriate **Label**, and optionally **Description**
#. Click :guilabel:`OK`

To set an attribute as a web hotlink
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

#. From the :guilabel:`Document` page
#. Click :guilabel:`Add...` to open the **Add Hotlink Descriptor** dialog
#. Select *Web* as the **Type** and select the **Attribute** containing the reference information
#. Provide the appropriate **Label**, and optionally **Description**
#. Click :guilabel:`OK`

To set an attribute as an action hotlink
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

#. From the :guilabel:`Document` page
#. Click :guilabel:`Add...` to open the **Add Hotlink Descriptor** dialog
#. Select *Action* as the **Type** and select the **Attribute** containing the reference information
#. Provide the appropriate **Label**, and optionally **Description**
#. Provide the **Action** definition, using the marker '{0}' to indicate where the attribute
   value is used.
#. Click :guilabel:`OK`

Example Actions:

* Google search: ``https://www.google.com.au/search?q={0}``
* Yahoo search: ``http://au.search.yahoo.com/search?p={0}``
* File reference: ``C:\\Reports\\{0}.pdf``
* Open in application: ``notepad {0}``

To edit an existing hotlink definition
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

#. From the :guilabel:`Document` page
#. Select hotlink from the list
#. Click :guilabel:`Edit...` to open the **Edit Hotlink Descriptor** dialog
#. Select **Type**
#. Select the **Attribute** containing the reference information
#. Provide the appropriate **Label**
#. (Optional) Provide the appropriate **Description**
#. (For Action types) Provide the **Action** definition
#. Click :guilabel:`OK`

To remove an existing hotlink definition
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

#. From the :guilabel:`Document` page
#. Select hotlink from the list
#. Click :guilabel:`Remove`

**Sample dataset:**

- Try out *australia.shp* in data_1_4.zip of the Quickstart sample data.

**Related tasks**

:doc:`/tasks/Working with Documents`

**Related reference**

:doc:`/reference/Document view`

:doc:`/reference/Resource page`

:doc:`/reference/Resource Document page`
