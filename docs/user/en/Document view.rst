Document view
#############

The Document view shows attachments, links and hotlinks associated with the selected layer or feature.

.. figure:: /images/document_view/DocumentView.png
   :align: center
   :alt: 

Documents
---------

The documents are presented in a tree, with folders used to represent
the files associated with the select feature or layer.

Within each folder several kinds of information are displayed:

|file_logo| - File Document

|web_logo| - Web Document

|action_logo| - Action Document

When storing documents associated with a selected feature, specific
attributes are marked along with the type of content they can reference.

Hotlink attributes that are not currently used will show up as "Unassigned".

Some layers and features support the storing of documents as attachments, where attachments are copied
into the application and managed along with the layer.

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

**Related tasks**

:doc:`Working with Documents`

.. |file_logo| image:: /images/document_view/file_doc_obj.jpg

.. |web_logo| image:: /images/document_view/link_doc_obj.png

.. |action_logo| image:: /images/document_view/action_doc_obj.png