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

For features, specific attributes can be marked as a *hotlink* to reference a document. Hotlinks that are not currently set will show up as "Unassigned".

View Menu
---------

.. figure:: /images/document_view/ViewMenu.png
   :align: left
   :figwidth: 100%

Fig. View menu

- :guilabel:`Save As` - used to save an attachment as another file.
- :guilabel:`Properties` - used to open the Document properties page of the selected resource.

Attach
------

The :guilabel:`Attach...` button is used to attach a document to the selected feature or layer.

.. figure:: /images/document_view/AddAttachment.png
   :align: left
   :figwidth: 100%

Fig. Add attachment dialog

- :guilabel:`Type` - used to determine the type of attachment.
- :guilabel:`File` - used to set the file to be attached.
- :guilabel:`Set as template` - used to mark the attachment as a template.
- :guilabel:`Label` - used to set a label.
- :guilabel:`Description` - used to set a description.
- :guilabel:`Open` - used to open the file set to be attached.
- :guilabel:`Browse...` - used to open a file selection dialog and select a file to be attached.
- :guilabel:`New...` - used to create a new file from a template and set it as the file to be attached.
- :guilabel:`Attach` - used to save the attachment.
- :guilabel:`Cancel` - used to cancel adding the attachment.

Link
----

The :guilabel:`Link...` button is used to link a document to the selected feature or layer.

A link can either be a file link or a website link as shown below. Selecting the **Type** determines what will be linked.

.. figure:: /images/document_view/AddFileLink.png
   :align: left
   :figwidth: 100%

Fig. Add file link dialog

- :guilabel:`Type` - used to determine the type of link.
- :guilabel:`File` - used to set the file to be linked.
- :guilabel:`Label` - used to set a label.
- :guilabel:`Description` - used to set a description.
- :guilabel:`Open` - used to open the file set to be linked.
- :guilabel:`Browse...` - used to open a file selection dialog and select a file to be linked.
- :guilabel:`New...` - used to create a new file from a template and set it as the file to be linked.
- :guilabel:`Link` - used to save the link.
- :guilabel:`Cancel` - used to cancel adding the link.

.. figure:: /images/document_view/AddWebLink.png
   :align: left
   :figwidth: 100%

Fig. Add web link dialog

- :guilabel:`Type` - used to determine the type of link.
- :guilabel:`URL` - used to set the website to be linked.
- :guilabel:`Label` - used to set a label.
- :guilabel:`Description` - used to set a description.
- :guilabel:`Open` - used to open the website set to be linked.
- :guilabel:`Link` - used to save the link.
- :guilabel:`Cancel` - used to cancel adding the link.

Edit
----

The :guilabel:`Edit...` button is used to update the selected document.

For *attachments* and *links*, the same dialog shown above will be opened in edit mode to allow updating inputed values.

For *hotlinks*, a dialog below will be shown depending on the type.

.. figure:: /images/document_view/EditFileHotlink.png
   :align: left
   :figwidth: 100%

Fig. Edit file hotlink dialog

- :guilabel:`Type` - used to display the type of hotlink.
- :guilabel:`Value` - used to set the file to be hotlinked.
- :guilabel:`Attribute` - used to display the attribute name.
- :guilabel:`Label` - used to display the label.
- :guilabel:`Description` - used to display the description.
- :guilabel:`Open` - used to open the file set to be linked.
- :guilabel:`Browse...` - used to open a file selection dialog and select a file to be hotlinked.
- :guilabel:`New...` - used to create a new file from a template and set it as the file to be hotlinked.
- :guilabel:`OK` - used to save changes.
- :guilabel:`Cancel` - used to discard changes.

.. figure:: /images/document_view/EditWebHotlink.png
   :align: left
   :figwidth: 100%

Fig. Edit website hotlink dialog

- :guilabel:`Type` - used to display the type of hotlink.
- :guilabel:`Value` - used to set the website URL to be hotlinked.
- :guilabel:`Attribute` - used to display the attribute name.
- :guilabel:`Label` - used to display the label.
- :guilabel:`Description` - used to display the description.
- :guilabel:`Open` - used to open the website set to be linked.
- :guilabel:`OK` - used to save changes.
- :guilabel:`Cancel` - used to discard changes.

.. figure:: /images/document_view/EditActionHotlink.png
   :align: left
   :figwidth: 100%

   Edit action hotlink dialog

- :guilabel:`Type` - used to display the type of hotlink.
- :guilabel:`Value` - used to set the attribute value.
- :guilabel:`Action` and :guilabel:`Go` - used to select and perform an action on the attribute value.
- :guilabel:`Attribute` - used to display the attribute name.
- :guilabel:`Label` - used to display the label.
- :guilabel:`Description` - used to display the description.
- :guilabel:`OK` - used to save changes.
- :guilabel:`Cancel` - used to discard changes.

Open
----

The :guilabel:`Open` button is used to open the selected document.

* File: Opened using the default system application
* Link: Opened using your web browser
* Action: Opened as defined by the action.

  * Action referencing ``notepad {0}`` opens in notepad
  * Action resulting in a web links opened in the system browser
  * Action resulting in a file link opens in default system application

  The :guilabel:`Open Action` dialog is used if more than one hotlink action is defined
  for an attribute.

  .. figure:: /images/document_view/OpenActionDialog.png
     :align: left
     :figwidth: 100%

     Open Action Dialog

Save As
-------

The :guilabel:`Save As...` button is used to save a new copy of an attachment document.

Clear
------

The :guilabel:`Clear` button is used remove the document reference from the selected hotlink attribute.

For file hotlinks, the referenced file will not be removed from disk, the reference is simply cleared.

Delete
------

The :guilabel:`Delete` button is used remove the selected attached or linked document.

For attached files, a confirmation message will be shown as the file will be removed from disk.
For linked files, the referenced file will not be removed from disk, the reference is simply cleared.

**Related tasks**

:doc:`/tasks/Working with Documents`

**Related reference**

:doc:`Resource Information page`

:doc:`Resource Document page`

.. |file_logo| image:: /images/document_view/file_doc_obj.jpg

.. |web_logo| image:: /images/document_view/link_doc_obj.png

.. |action_logo| image:: /images/document_view/action_doc_obj.png