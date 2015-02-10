Style Page With Predefined Styles
#################################

uDig : Style Page with Predefined Styles

This page last changed on Sep 21, 2012 by jgarnett.

Motivation
----------

The motivation here is to pull out the predefined style concept from moovida's individual style
pages (see `Review Simple Style Pages <Review%20Simple%20Style%20Pages.html>`__).

This will give us a single place to:

-  let the user manage their style
-  access the list of predefined styles
-  a sensible place to let users reset they style to the default.

Inspiration
-----------

We are writing up a proposal to share Styles at the uDig catalog level; as a result we are going to
have the concept of predefined styles be a bit more built in. We will start with moovida's existing
work (which saves the styles in the plugin configuration directory).

Notes

-  **FeatureTypeStyle** is called a "Group" (suggest calling it a **Style**)
-  It has the ability to "Add" the Group into the current style; it makes no effort to fix the
   attribute names but it is still a useful bit of functionality
-  The ColorBrewer twenty questions also results in the existing style being replaced
-  We currently have a very bad checkbox to "reset" the style to a generated default in the
   **Simple** page

Proposal
--------

(`view as
slideshow </confluence/plugins/advanced/gallery-slideshow.action?pageId=13534663&decorator=popup>`__)

 

|image0|

 

|image1|

 

 

 

 

 

|image2|

 

 

 

 

 

 

 

**Style Page**

One Style page to Rule them All - i.e. single **Style** page used to control the style as a whole.

It offers:

-  **Reset** button that recreates the style from first principles (i.e. from GeoResource)
-  **Default** button that resets the style to the default (only enabled if default has been set)
-  **>>** button which moves the current style into the predefined style list. Internally this adds
   it to the catalog; if there is already a style with this name for the feature type it will prompt
   the user to replace or rename
-  **<<** replaces the current style with the selected predefined style
-  **append** adds the rules of the selected predefined style to the end of the current style
-  List of predefined styles for the current feature type

   -  **default** checkbox allows the user to mark the selected style as the default for this
      feature type. Any new layers of this feature type will make use of this style by default; the
      user can also use the **Default** to reset the current style to this default at any time

**Details Page**

We need a place to define the title and description of the style.

**Interaction**

-  New Style Wizard: covered in more detail in `Style Dialog
   Improvement <Style%20Dialog%20Improvement.html>`__

**Workflow**

| Use a predefined style:
|  1) move to the theme page
|  2) select the style you want to use and hit load
|  3) Change to the point, line or polygon page and make any changes
|  4) hit apply or ok

| New a predefined style:
|  1) use the wizard to create a new style
|  2) modify the style in the point, line or polygon page
|  3) move to the details page add a title and description
|  4) move to the theme page and save your predefined style.

| Edit a predefined style
|  1) move to the move to the theme page.
|  2) select the style you want to edit
|  3) move to the point, line, polygon and details page and make changes
|  4) move back to the theme page and save the change

| Remove a predefined style
|  1) move to the move to the theme page.
|  2) select the style you want to delete
|  3) hit the delete key

Status
------

Project Steering committee support:

-  Andrea Antonello: +0
-  Jesse Eichar: +0
-  Jody Garnett: +0
-  Mauricio Pazos: +0

Committer Support:

-  

A vote of -1 requires an alternate suggestion; community members are invited to indicate
support/suggestions.

Documentation
-------------

Documentation change to `Users Guide <http://udig.refractions.net/confluence//display/EN/Home>`__
(for an accepted change).

Tasks
=====

A list of the tasks needed to accomplish this change; if you prefer you can use a single Jira issue
with subtasks. It is important to include any deadlines so the community knows when you are working
to a schedule.

 

no progress

|image3|

in progress

|image4|

blocked

|image5|

help needed

|image6|

done

Tasks:

#. |image7| Initial interface wireframe for community review and feedback
#. Move moovida's style pages work into its own page (predefined)
#. Add the new details page under predefined
#. Move cache and filter page content into new details page
#. Modify moovida's work to allow grouping
#. allow predefined styles to be stored in the catalog
#. Wait for http://udig.refractions.net/confluence/display/UDIG/Style+Dialog+Improvement work to
   finish
#. Add the new style wizard button
#. Rename predefined page to theme
#. Move the point, line and polygon page under theme
#. Updated user guide documentation

Estimated Delivery: Early Jan

Status:

-  [ UDIG-xxxx http://jira.codehaus.org/browse/UDIG-xxxx]

| 

Attachments:

| |image8| `PredefinedStylePage.png <download/attachments/13534663/PredefinedStylePage.png>`__
(image/png)
|  |image9| `StyleDetailsPage.png <download/attachments/13534663/StyleDetailsPage.png>`__
(image/png)
|  |image10| `NewStyleWizard.png <download/attachments/13534663/NewStyleWizard.png>`__ (image/png)

+-------------+----------------------------------------------------------+
| |image12|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+-------------+----------------------------------------------------------+

.. |image0| image:: download/thumbnails/13534663/NewStyleWizard.png
   :target: /confluence/plugins/advanced/gallery-slideshow.action?imageNumber=1&pageId=13534663&decorator=popup
.. |image1| image:: download/thumbnails/13534663/StyleDetailsPage.png
   :target: /confluence/plugins/advanced/gallery-slideshow.action?imageNumber=2&pageId=13534663&decorator=popup
.. |image2| image:: download/thumbnails/13534663/PredefinedStylePage.png
   :target: /confluence/plugins/advanced/gallery-slideshow.action?imageNumber=3&pageId=13534663&decorator=popup
.. |image3| image:: images/icons/emoticons/star_yellow.gif
.. |image4| image:: images/icons/emoticons/error.gif
.. |image5| image:: images/icons/emoticons/warning.gif
.. |image6| image:: images/icons/emoticons/check.gif
.. |image7| image:: images/icons/emoticons/check.gif
.. |image8| image:: images/icons/bullet_blue.gif
.. |image9| image:: images/icons/bullet_blue.gif
.. |image10| image:: images/icons/bullet_blue.gif
.. |image11| image:: images/border/spacer.gif
.. |image12| image:: images/border/spacer.gif
