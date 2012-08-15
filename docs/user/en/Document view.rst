Document view
#############

The Document view shows files and links associated with the selected layer or feature.

.. figure:: /images/document_view/DocumentView.png
   :align: center
   :alt: 

Documents and Attachments
-------------------------

The documents are presented in a tree, with folders used to represent
the files associated with the select feature or layer.

Within each folder several kinds of information are displayed:

|file_logo| - File Document

|web_logo| - Weblink Document

|action_logo| - Action Document

When storing documents associated with a selected feature, specific
attributes are marked along with the type of content they can reference.
Hot link attributes that are not currently used will show up as "Unassigned".

Some layers support the storing of documents as attachments, where attachments are copied
into the application and managed along with the layer.

Add
---

The :guilabel:`Add...` button is used to add a new document to the selected feature or layer.

A document folder must be selected to enable this button.

Edit
----

The :guilabel:`Edit...` button is used to update the selected document.

Open
----

The :guilabel:`Open` button is used to open the selected document.

The document is opened using the default system application.

Save As
-------

The :guilabel:`Save As` button is used to save a new copy of the file reference of the selected document.

This is only enabled for file attachments.

Remove
------

The :guilabel:`Remove` button is used remove file or web reference from the selected hotlink attribute.

Note that the corresponding file will not removed from disk, the attribute hotlink attribute reference is simply cleared.

Delete
------

The :guilabel:`Delete` button is used remove the selected attachment.

A confirmation message will be shown as the file will be removed from disk.

**Related tasks**

:doc:`Working with Documents`

.. |file_logo| image:: /images/document_view/file_doc_obj.jpg

.. |web_logo| image:: /images/document_view/link_doc_obj.png

.. |action_logo| image:: /images/document_view/action_doc_obj.png