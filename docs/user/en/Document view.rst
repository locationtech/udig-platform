Document view
#############

The Document view shows attachments, links and hotlinks associated with the selected layer or feature.

.. figure:: /images/document_view/DocumentView.png
   :align: center
   :alt: 

Documents
---------

The documents are presented in a tree-table structure. Documents are grouped into folders that represent their associated feature or layer.

Within each folder several kinds of information are displayed:

|file_logo| - File Document (or the icon of the operating system's default program for file type)

|web_logo| - Web Document

|action_logo| - Action Document

Some layers and features support storing of documents as attachments. Attachments are copied into the application and managed along with the resource.

For features, specific attributes can be marked to reference a document, we call these *hotlinks*. Hotlinks that are not currently used will show up as "Unassigned".

View Menu
---------

The following commands are available in the view menu:

:guilabel:`Save As` - used to save an attachment as another file.

:guilabel:`Properties` - used to open the Document properties page of the selected resource.

Attach
------

The :guilabel:`Attach...` button is used to attach a document to the selected feature or layer.

Link
----

The :guilabel:`Link...` button is used to link a document to the selected feature or layer.

Edit
----

The :guilabel:`Edit...` button is used to update the selected document.

Open
----

The :guilabel:`Open` button is used to open the selected document.

The document is opened using the default system application.

Save As
-------

The :guilabel:`Save As...` button is used to save a new copy of an attachment document.

Clear
------

The :guilabel:`Clear` button is used remove the document reference from the selected hotlink attribute.

For file hotlinks, the referenced file will not removed from disk, the reference is simply cleared.

Delete
------

The :guilabel:`Delete` button is used remove the selected attached or linked document.

For attached files, a confirmation message will be shown as the file will be removed from disk.
For linked files, the referenced file will not removed from disk, the reference is simply cleared.

**Related reference**

:doc:`Resource page`

**Related tasks**

:doc:`Working with Documents`

.. |file_logo| image:: /images/document_view/file_doc_obj.jpg

.. |web_logo| image:: /images/document_view/link_doc_obj.png

.. |action_logo| image:: /images/document_view/action_doc_obj.png