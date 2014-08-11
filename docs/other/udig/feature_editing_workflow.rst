Feature Editing Workflow
########################

uDig : Feature editing workflow

This page last changed on Sep 21, 2012 by jgarnett.

Motivation
==========

There are several RFC's around editing features and editing security, this RFC gathers them together
to be discussed as a single "feature editing workflow".

Reference RFC's:

-  `Per Attribute
   Security <http://udig.refractions.net/confluence/display/UDIG/Per+Attribute+Security>`__

Related issues:

-  https://jira.codehaus.org/browse/UDIG-1944 Commit tool process potentially unwanted commands

uDic current implementations to note:

-  FeatureInterceptor
-  EditManager
-  EditFeature

Inspiration
===========

Confusion watching users choose between "commit", "apply" and "save".

Location for security checks.

EditFeature acting as a data model, but not providing feedback when attributes are changed.

|image0|

Currently we have a "Save" button for the map, a "Commit" button (for the transaction) and an
"Apply" button on each open form.

Proposal:
=========

To stream line the feature editing workflow we have a big fat "save" button with additional options
for those interested.

|image1|

The above user-interface mock-up shows bringing these concepts into one part of the main toolbar.

-  A single "Save" button that ensures the user's current work is saved
-  A drop down list of "Save Map", "Commit Data", "Apply Feature" for fine grain control
-  The "Revert Map", "Rollback Data", "Cancel Feature" is handled in a similar fashion

To support this idea the following technical changes are proposed:

**Save Map**

-  Focused on the above user interface change
-  Currently checks for any layers working with a "temp" resource, inviting users to export the
   result
-  Check: Apply and Commit have both been called prior to saving

**Commit Data**

EditManager:

-  Each Layer is responsible for obtaining a GeoResource from the catalog, holding onto it, and
   wrapping it in a LayerGeoResource (to ensure the EditManager transaction is used).
-  Proposal: Move this responsibility to EditManager. This allows other code (such as operations and
   views) to work with GeoResources with no restriction that the content be displayed on the Map.

**Apply Feature**

EditFeature:

-  Make EditFeature responsible for holding onto any edit errors or warnings, making forms easier to
   write
-  add events to EditFeature allowing the user interface to respond to attribute changes
-  expand notification so we can review a change prior to it being accepted (and reject it if
   needed)
-  Maintain current FeatureInterceptor functionality the EditFeature to be configured with the
   appropriate listeners when it is first created
-  see diagrams below

EditManager:

-  Need to keep a list of "open" EditFeatures so they can be applied before the commit goes out

Design Documents
================

EditFeature Events and Interceptor
----------------------------------

FeatureInterceptor to capture object lifecycle; EditFeatureListener to capture notifications during
editing.

|image2|

| 
|  `EditInterceptor work in
progress <https://github.com/levi-putna/udig-platform/blob/EditFeatureWorkflow/plugins/net.refractions.udig.project/src/net/refractions/udig/project/interceptor/FeatureInterceptor.java>`__

EditFeature and EditManager
---------------------------

EditManager responsible for managing EditFeatures (so they can be applied before commit). Maintains
list of resources used by the map so they can be managed when the map closes.

|image3|

-  `EditFeatureListener work in
   progress <https://github.com/levi-putna/udig-platform/blob/EditFeatureWorkflow/plugins/net.refractions.udig.project/src/net/refractions/udig/project/listener/EditFeatureListener.java>`__.

Status
======

Project Steering committee support:

-  Andrea Antonello: +0
-  Jesse Eichar: +0
-  Jody Garnett: +1
-  Mauricio Pazos: +0

Committer Support:

Documentation
=============

We will need to update the extension point documentation with an example in the Developers Guide.

Tasks
=====

Tasks:

 

no progress

|image4|

in progress

|image5|

blocked

|image6|

help needed

|image7|

done

-  |image8| RFC submitted for review and feedback

   -  |image9| Jody reviewed
   -  |image10| Voting!

-  |image11| Initial API Change

   -  |image12| Define Interface for EditFeatureListener
   -  |image13| FeatureInterceptor life cycle methods
   -  |image14| update feature interceptor schema

-  User Interface update

   -  toolbar changes (i.e. Save / Save Map / Commit Data / Apply Feature)

-  Feature Lifecycle

   -  Create FeatureInterceptorCache to process FeatureInterceptor extension point
   -  Provide methods to programatically contribute to the FeatureInterceptor list.
   -  |image15| Process extension point and hook into CreateFeatureCommand
   -  |image16| Update the DefaultValueFeatureInterceptor example
   -  SecurityFeatureInterceptor that uses Per Attribute Security interface to establish edit
      permissions

-  Add event notification to EditFeature

   -  |image17| Extended EditFeature to track dirty, visible, enable, editable status
   -  |image18| EditFeatureListener notification

-  EditFeature

   -  |image19| Warnings and Error lists
   -  |image20| Hook into attribute change methods
   -  Apply / Cancel support (making use of dirty flags to only write out the effected attributes)
   -  Hook into the apply / cancel notifications

-  EditManager support for EditFeature

   -  method to lease EditFeatures (so EditManager has a list of all current EditFeatures that need
      to be applied)
   -  apply / cancel support

-  EditManager support for resources

   -  port LayerResource management of resource interceptors to EditManager

-  User Guide

   -  Update toolbar reference page

-  Pull request and review

| 

Attachments:

| |image21| `class.PNG <download/attachments/14189473/class.PNG>`__ (image/png)
|  |image22| `EditWorkflow.GIF <download/attachments/14189473/EditWorkflow.GIF>`__ (image/gif)
|  |image23| `EditWorkflow.GIF <download/attachments/14189473/EditWorkflow.GIF>`__ (image/gif)
|  |image24| `EditWorkflow.GIF <download/attachments/14189473/EditWorkflow.GIF>`__ (image/gif)
|  |image25| `SaveCommitApply.png <download/attachments/14189473/SaveCommitApply.png>`__ (image/png)
|  |image26| `EditFeatureWorkflow.png <download/attachments/14189473/EditFeatureWorkflow.png>`__
(image/png)
|  |image27| `EditFeatureWorkflow.png <download/attachments/14189473/EditFeatureWorkflow.png>`__
(image/png)
|  |image28| `EditFeatureWorkflow.png <download/attachments/14189473/EditFeatureWorkflow.png>`__
(image/png)
|  |image29| `EditWorkflow.GIF <download/attachments/14189473/EditWorkflow.GIF>`__ (image/gif)
|  |image30| `EditWorkflow.GIF <download/attachments/14189473/EditWorkflow.GIF>`__ (image/gif)
|  |image31| `EditWorkflow -
Events.GIF <download/attachments/14189473/EditWorkflow%20-%20Events.GIF>`__ (image/gif)
|  |image32| `EditWorkflow - Edit.GIF <download/attachments/14189473/EditWorkflow%20-%20Edit.GIF>`__
(image/gif)

+-------------+----------------------------------------------------------+
| |image34|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+-------------+----------------------------------------------------------+

.. |image0| image:: /images/feature_editing_workflow/SaveCommitApply.png
.. |image1| image:: /images/feature_editing_workflow/EditFeatureWorkflow.png
.. |image2| image:: download/attachments/14189473/EditWorkflow%20-%20Events.GIF
.. |image3| image:: download/attachments/14189473/EditWorkflow%20-%20Edit.GIF
.. |image4| image:: images/icons/emoticons/star_yellow.gif
.. |image5| image:: images/icons/emoticons/error.gif
.. |image6| image:: images/icons/emoticons/warning.gif
.. |image7| image:: images/icons/emoticons/check.gif
.. |image8| image:: images/icons/emoticons/check.gif
.. |image9| image:: images/icons/emoticons/check.gif
.. |image10| image:: images/icons/emoticons/warning.gif
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
.. |image21| image:: images/icons/bullet_blue.gif
.. |image22| image:: images/icons/bullet_blue.gif
.. |image23| image:: images/icons/bullet_blue.gif
.. |image24| image:: images/icons/bullet_blue.gif
.. |image25| image:: images/icons/bullet_blue.gif
.. |image26| image:: images/icons/bullet_blue.gif
.. |image27| image:: images/icons/bullet_blue.gif
.. |image28| image:: images/icons/bullet_blue.gif
.. |image29| image:: images/icons/bullet_blue.gif
.. |image30| image:: images/icons/bullet_blue.gif
.. |image31| image:: images/icons/bullet_blue.gif
.. |image32| image:: images/icons/bullet_blue.gif
.. |image33| image:: images/border/spacer.gif
.. |image34| image:: images/border/spacer.gif
