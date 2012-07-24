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

|image0| - File Hotlink

|image1| - Weblink Hotlink

|image0| - Document Attachment

When storing documents associated with a selected feature, specific
attributes are marked along with the type of content they can reference.
Hot link attributes that are not currently used will show up as "(unassigned)".

Some layers support the storing of documents as attachments, where attachments are copied
into the application and managed along with the layer.

Open
----

The :guilabel:`Open` button is used to open the selected document or attachment.

The document is opened using the appropriate system application.

Add File
--------

The :guilabel:`Add File` button is used record a file reference in the selected attribute, or register
an additional file with the selected layer.

Add Link
--------

The :guilabel:`Add Link` button is used to store a web link in the selected attribute, or register
an additional web link with the selected layer.

New Attachment
--------------

The :guilabel:`New Attachment` is used to copy an attachment into the selected feature or layer.

This functionality is only enabled for layers that support attachments.

Edit
----

The :guilabel:`Edit` is used to update the selected document.

It can be used to fix a hotlink in the event a document was moved.

Remove
------

The :guilabel:`Remove` button is used remove the selected hot link.

Note the corresponding file is not removed from disk, the attribute hotlink reference is simply cleared.

Delete Attachment
-----------------

The :guilabel:`Delete Attachment` button is used remove the selected attachment, a confirmation is required
as the file is removed from disk.

**Related tasks**

:doc:`Working with Documents`

.. |image0| image:: /images/document_view/file_doc_obj.jpg

.. |image1| image:: /images/document_view/link_doc_obj.png