Document View
#############

uDig : Document View

This page last changed on Sep 21, 2012 by jgarnett.

Motivation
----------

Some shape files include attributes which "link" to documents or URLs. We also have the example of
geospatial wikis that combine a WFS with a Content manager (so we can use the feature id to look up
an associated page of content).

Inspiration
-----------

We are going to break out an API that a GeoResource can use to advertise the fact that features may
have some Document action going on. We can write a reference implementation for shape files (to
cover the attribute as a link scenario). For a few of the other cases we will need to mix it up a
bit (so the metaphor may be attachment, or open rather than just providing a link).

The general idea is to list the documents associated with a feature; and provide the appropriate
buttons so the user can act on the selected file.

Proposal
--------

UI
~~

The following might be displayed when "tasmania" is selected in the "Australia" layer.

|image0|

**Hotlink**

Hotlink support is configured in a **Document** property page associated with the IGeoResource. The
**Label** and **Description** are configured at that time, and are not editable on a
feature-by-feature basis.

When editing the hotlink value, the text field can be used, or if it is a "file" hotlink a
**Browse** button is available. If the field is empty, and any templates are attached to the
"Australia" layer then the **New** button allows the user to choose a template for the creation of a
new file.

Note: These are "hotlinks" with only the value stored in the feature. As such you cannot **Add** or
**Remove** a hotlink document. You can use the **Edit** button and clear the recorded value, but the
placeholder will remain listed in DocumentView.

|image1|

An interesting example is the **Action** hotlink recorded above against the NAME attribute. A number
of actions have been configured for the NAME field, basically constructing web searches using the
provided value. You can review the actions that have been configured here, and try them out with the
value prior to hitting **OK** to accept the changes. This will prevent the round trip of editing,
and then trying things out in the document view.

**Attachment**

Attachments are "copied into" the system, similar to email attachments. In addition to **Open** you
can **Save As** an attachment for editing offline.

You can actually **Edit** an attachment, in order to fill out the label and description information,
this screen also supports the use of templates if needed.

Update: Testing shows users use **Open** to review a document and save as from the target
application.

|image2|

The same screen is used when to **Add** an attachment, the user must select the **Type** of
attachment in order to enable the rest of the fields.

API
~~~

|image3|

.. code:: code-java

    interface IDocument {
      enum Type { FILE, WEB, Action };
      ....
      Type getType();
      boolean open();
      boolean isEmpty;
    }

Support files associated with the GeoReosurce as a whole:

.. code:: code-java

    // GeoResource access to support documents
    // Example: shapefile.txt sidecar file, README.txt in same directory etc..
    interface DocumentSource {
       List<IDocument> documents();

       boolean canAdd();
       IDocument addFile( File file );
       IDocument addLink( URL link);

       boolean canRemove();
       void remove(IDocument doc);
    }

Per feature - support storing "hotlink" values in marked attributes. This interface has seen a
number of revises as it was very hard allow a catalog level interface to take part in the current
transition provided by the EditManager. We have solved the problem after several false starts by
making the following interface only responsible for updating attribute values; it is up to the
client code (such as the DocumentView) to apply the value using a SetAttributeCommand or similar.

.. code:: code-java

    // Per Feature Hotlink access File or Web Links stored in marked attributes
    // (Attributes marked in GeoResourcePersistedProperties)
    interface IHotlink {
       class HotlinkDescriptor {
          String attributeName;
          Document.Type type;
       }
       List<HotlinkDescriptor> getHotlinkDescriptors();
       List<IDocument> getDocuments(SimpleFeature);
       IDocument getDocument(SimpleFeature, String);
       IDocument setFile(SimpleFeature, String, File);
       IDocument setLink(SimpleFeature, String, URL);
       IDocument clear(SimpleFeature, String);
    }

| Attachments are copied into the system, and are referenced by FeatureId. This extends IHotlink in
order
|  to gather all "per feature" documents into one class.

.. code:: code-java

    /** Per Feature Attachments File attachments stored in the system */
    interface IAttach extends IHotlink {
       List<IDocument> getDocuments(SimpleFeature);
       IDocument attach( FeatureId, File);
       IDocument delete( FeatureId, IDocument)
    }

Code Example
^^^^^^^^^^^^

Open README.txt for a shapefile:

.. code:: code-java

    IGeoReosurce resource = layer.getGeoResource();
    DocumentSource documentSource = resource.resolve( DocumentSource.class, new NullProgressMonitor() );

    IDocument documentList = documentSource.documents();
    for( IDocument document : documents ){
         if( document.getName().equals("README.txt") ){
              document.open();
              break;
         }
    }

Assign a report PDF and source link to a news report location:

.. code:: code-java

    FeatureId fid = ... news report location

    IGeoReosurce resource = layer.getGeoResource();
    HotlinkSource hotlinkSource = resource.resolve( HotlinkSource.class, new NullProgressMonitor() );

    hotlink.file( fid, "report", report_pdf );
    hotlink.link( fid, "source", new URL("http://google.com/") );

Status
------

Project Steering committee support:

-  Andrea Antonello: +1
-  Jesse Eichar: +1
-  Jody Garnett: +0
-  Mauricio Pazos: +1

Committer Support:

-   

A vote of -1 requires an alternate suggestion; community members are invited to indicate
support/suggestions.

Documentation
-------------

User Guide Documentation:

-  Reference: **Document view** with traditional screen snap and naming the buttons
-  Tasks: Working with hot-links, Define Hotlink

Developer Guide Documentation:

-  Document Support page (code example of listing files, opening, and attaching)

Tasks
=====

A list of the tasks needed to accomplish this change; if you prefer you can use a single Jira issue
with subtasks. It is important to include any deadlines so the community knows when you are working
to a schedule.

 

no progress

|image4|

in progress

|image5|

blocked

|image6|

help needed

|image7|

done

Tasks:

#. Initial Research

   -  PP: |image8| Initial interface wireframe for community review and feedback
   -  PP: |image9| Research into shapefile hotline / document capability

#. Stage 1 - initial Shapefile implementation

   -  PP: |image10| Initial implementation in git fork
   -  NC: |image11| Review implementation
   -  NC: |image12| Clean up DocumentView user interface
   -  NC: |image13| Split into DocumentSource (i.e. GeoResource) and AttachmentSource (i.e. per
      feature)
   -  NC: |image14| Review shapefile.properties as the data model for ShapefileDocumentSource
   -  JG: |image15| Sample data australia.shp (from natural earth countries with example file and
      web links)
   -  JG: |image16| Update user guide with **Display view** and various **Working with Documents**
      pages
   -  JG: |image17| Review pull request https://github.com/uDig/udig-platform/pull/117

#. Stage 2 General purpose implementation

   -  |image18| JG: Clean room BasicHotlink implementation working off IGeoResource persisted
      properties
   -  |image19| JG: HotlinkProperty Page to configure IGeoResource persisted properties
   -  |image20| JG: IGeoResourceProperty Dialog
   -  |image21| JG: Work through enablement issues around IGeoResource bringing up a Property Dialog
   -  |image22| NC: Code review/revision checks
   -  |image23| ML: Review pull request

#. Stage 3 Shapefile Attachment support

   -  |image24| NC: ShapefileAttachmentSource implementation (simple implementation to store file in
      project folder)
   -  |image25| NC: DisplayView Attach button to copy file into same directory as shapefile
   -  |image26| NC: DisplayView Delete button to delete attachment file
   -  |image27| NC: Review user guide pages and update screen snaps
   -  |image28| JG: Review pull request: https://github.com/uDig/udig-platform/pull/121

#. Cleanup

   -  |image29| NC: Tone down the number of user interfaces classes presented as API (DocumentItem,
      Document Folder etc..)
   -  |image30| JG: Rename net.refractions.udig.tool.info to net.refractions.udig.info for
      consistency
   -  |image31| : When australia.shp is first set up, attachment support is marked as enabled in the
      Document property page, but the view does not support attachments until hotlink configuration
      is performed.
   -  |image32| JG: Need consistent "Hotlink", "Document" and "Attachment" icons. [Naz] Calling the
      creative ones out there, need help creating the images. |image33|
   -  |image34| : When editing a hotlink action definition the label is reset to the attribtue name
      (even if the user had supplied a value). default label name here may be taken from the action?
      "notepad {0}" could default to "notepad" and "http://www.google.com/search?as\_q={0}" could
      default to "www.google.com". [Naz] Fixed to set the label only when it is blank. Too hard to
      determine from action string unless we have a map of possible actions.
   -  |image35| : When editing a hotlink action the dialog cannot be resized to provide more room to
      fill in description and action
   -  |image36| : Be a bit more aggressive hunting down map selection when document view first
      created so it does not open up empty
   -  |image37| : When editing a hotlink attribute the action and description of the first action is
      shown, however the combo is empty and the go button does not perform. [Naz] Fixed to select
      first item in combo box.
   -  |image38| : When editing a hotlink the dialog cannot be resized
   -  |image39| : Move **Save As** functionality to view menu
   -  |image40| : Publish a ViewSite selection and make sure that the selection can resolve to a
      feature (this will allow the map to flash the location of the feature in the same manner as
      the InfoView)
   -  |image41| : When choosing "Attach..." the dialog OK button should be called "Attach". [Naz]
      Fixed button label.
   -  |image42| NC: Final review of javadocs
   -  |image43| AA: Review pull request https://github.com/uDig/udig-platform/pull/145
       Status: Merged into master! Yahoo!

#. Publish

   -  |image44| JG: Close Issue in Jira for the release notes
      https://jira.codehaus.org/browse/UDIG-1923
   -  |image45| NC: Update What is new page: http://udig.github.com/docs/user/What%20is%20new.html
   -  |image46| NC: Update developers guide:
      http://udig.github.com/docs/dev/working_with_gis_application.html

| 

Attachments:

| |image47| `udigDocumentView.png <download/attachments/13534665/udigDocumentView.png>`__
(image/png)
|  |image48| `documentSource.PNG <download/attachments/13534665/documentSource.PNG>`__ (image/png)
|  |image49| `documentSource.PNG <download/attachments/13534665/documentSource.PNG>`__ (image/png)
|  |image50| `IDocument.PNG <download/attachments/13534665/IDocument.PNG>`__ (image/png)
|  |image51| `EditHotlink.png <download/attachments/13534665/EditHotlink.png>`__ (image/png)
|  |image52| `EditAttachment.png <download/attachments/13534665/EditAttachment.png>`__ (image/png)
|  |image53| `DocumentView.png <download/attachments/13534665/DocumentView.png>`__ (image/png)
|  |image54| `EditHotlink.png <download/attachments/13534665/EditHotlink.png>`__ (image/png)
|  |image55| `EditAttachment.png <download/attachments/13534665/EditAttachment.png>`__ (image/png)
|  |image56| `DocumentView.png <download/attachments/13534665/DocumentView.png>`__ (image/png)
|  |image57| `EditAttachment.png <download/attachments/13534665/EditAttachment.png>`__ (image/png)
|  |image58| `DocumentView.png <download/attachments/13534665/DocumentView.png>`__ (image/png)

+-------------+----------------------------------------------------------+
| |image60|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+-------------+----------------------------------------------------------+

.. |image0| image:: /images/document_view/DocumentView.png
.. |image1| image:: /images/document_view/EditHotlink.png
.. |image2| image:: /images/document_view/EditAttachment.png
.. |image3| image:: /images/document_view/IDocument.PNG
.. |image4| image:: images/icons/emoticons/star_yellow.gif
.. |image5| image:: images/icons/emoticons/error.gif
.. |image6| image:: images/icons/emoticons/warning.gif
.. |image7| image:: images/icons/emoticons/check.gif
.. |image8| image:: images/icons/emoticons/check.gif
.. |image9| image:: images/icons/emoticons/check.gif
.. |image10| image:: images/icons/emoticons/check.gif
.. |image11| image:: images/icons/emoticons/check.gif
.. |image12| image:: images/icons/emoticons/check.gif
.. |image13| image:: images/icons/emoticons/check.gif
.. |image14| image:: images/icons/emoticons/check.gif
.. |image15| image:: images/icons/emoticons/check.gif
.. |image16| image:: images/icons/emoticons/check.gif
.. |image17| image:: images/icons/emoticons/check.gif
.. |image18| image:: images/icons/emoticons/check.gif
.. |image19| image:: images/icons/emoticons/check.gif
.. |image20| image:: images/icons/emoticons/check.gif
.. |image21| image:: images/icons/emoticons/check.gif
.. |image22| image:: images/icons/emoticons/check.gif
.. |image23| image:: images/icons/emoticons/check.gif
.. |image24| image:: images/icons/emoticons/check.gif
.. |image25| image:: images/icons/emoticons/check.gif
.. |image26| image:: images/icons/emoticons/check.gif
.. |image27| image:: images/icons/emoticons/check.gif
.. |image28| image:: images/icons/emoticons/check.gif
.. |image29| image:: images/icons/emoticons/check.gif
.. |image30| image:: images/icons/emoticons/check.gif
.. |image31| image:: images/icons/emoticons/check.gif
.. |image32| image:: images/icons/emoticons/check.gif
.. |image33| image:: images/icons/emoticons/smile.gif
.. |image34| image:: images/icons/emoticons/check.gif
.. |image35| image:: images/icons/emoticons/check.gif
.. |image36| image:: images/icons/emoticons/check.gif
.. |image37| image:: images/icons/emoticons/check.gif
.. |image38| image:: images/icons/emoticons/check.gif
.. |image39| image:: images/icons/emoticons/check.gif
.. |image40| image:: images/icons/emoticons/check.gif
.. |image41| image:: images/icons/emoticons/check.gif
.. |image42| image:: images/icons/emoticons/check.gif
.. |image43| image:: images/icons/emoticons/check.gif
.. |image44| image:: images/icons/emoticons/check.gif
.. |image45| image:: images/icons/emoticons/check.gif
.. |image46| image:: images/icons/emoticons/check.gif
.. |image47| image:: images/icons/bullet_blue.gif
.. |image48| image:: images/icons/bullet_blue.gif
.. |image49| image:: images/icons/bullet_blue.gif
.. |image50| image:: images/icons/bullet_blue.gif
.. |image51| image:: images/icons/bullet_blue.gif
.. |image52| image:: images/icons/bullet_blue.gif
.. |image53| image:: images/icons/bullet_blue.gif
.. |image54| image:: images/icons/bullet_blue.gif
.. |image55| image:: images/icons/bullet_blue.gif
.. |image56| image:: images/icons/bullet_blue.gif
.. |image57| image:: images/icons/bullet_blue.gif
.. |image58| image:: images/icons/bullet_blue.gif
.. |image59| image:: images/border/spacer.gif
.. |image60| image:: images/border/spacer.gif
