User Interface Guidelines
=========================

The eclipse user interace guidelines include a
`checklist <http://www.eclipse.org/articles/Article-UI-Guidelines/Contents.html#Checklist%20For%20Developers>`_,
we have linked to some of this content and provided our own summary.

Real quick and easy - follow the eclipse user interface guidelines!

* `http://wiki.eclipse.org/User\_Interface\_Guidelines <http://wiki.eclipse.org/User_Interface_Guidelines>`_
* `http://www.eclipse.org/articles/Article-UI-Guidelines/Index.html <http://www.eclipse.org/articles/Article-UI-Guidelines/Index.html>`_

Icons and Imagery
-----------------

We use a consistent "visual language" to represent services, spatial concepts and abstract ideas.

* :doc:`fonts_and_imagery`

* :doc:`icons` - covers expected directory structure

Eclipse User Interface Guidelines
---------------------------------

Now that the eclipse user interface guidelines have moved to a wiki they are more up to date. In the
past we have had to take some notes about our understanding of the user interface guidelines. These
are our notes!

General UI Guidelines:

-  Do what eclipse does |image0|
-  Use **Headline style** capitalization for menus, tooltip and all titles, including those used for
   windows, dialogs, tabs, column headings and push buttons. Capitalize the first and last words,
   and all nouns, pronouns, adjectives, verbs and adverbs. Do not include ending punctuation.
-  Use **Sentence style** capitalization for all control labels in a dialog or window, including
   those for check boxes, radio buttons, group labels, and simple text fields. Capitalize the first
   letter of the first word, and any proper names such as the word Java.
-  Create localized version of the resources within your plug-in.

Visual Design Guidelines:

-  Re-use the core visual concepts to maintain consistent representation and meaning across Eclipse
   plug-ins.
-  Use the appropriate icon type in the location it is designed for within the user interface.
-  Follow the specific size specifications for each type of icon.
-  Cut the icons with the specific placement shown to ensure alignment in the user interface.
-  Follow the positioning guidelines for the different types of icons for optimal alignment of these
   elements relative to one another.
-  Follow the specific size specifications for wizard graphics.
-  Follow the predefined directory structure and naming convention.
-  Use the enabled, and disabled states provided.

Visual concepts for udig are located on the Icons and Imagery page

Component Development:

-  Re-use the core visual concepts to maintain consistent representation and meaning across Eclipse
   plug-ins.
-  Use the appropriate icon type in the location it is designed for within the user interface.
-  Follow the specific size specifications for each type of icon.
-  Cut the icons with the specific placement shown to ensure alignment in the user interface.
-  Follow the positioning guidelines for the different types of icons for optimal alignment of these
   elements relative to one another.
-  Follow the specific size specifications for wizard graphics.
-  Follow the predefined directory structure and naming convention.
-  Use the enabled, and disabled states provided.

Visual concepts for udig are located on the Icons and Imagery page

Commands Guidelines:

-  require label, tool tip and icon
-  tool tip describes result
-  Follow workbench example of New, Delete and Add
-  Enablement be darn quick, even if you need to cheat

Dialogs Guidelines:

-  When opened focus should be on the first control
-  When using Twin Box for set member ship use '>' '<' '>>' '<<' buttons

Wizards Guidelines:

-  used for any task of many ordered steps
-  required header, banner graphic
-  'Back', 'Next', 'Finish', 'Canel' buttons
-  Start with a prompt not an error message
-  fill out as much information as you can for the user
-  validate in order, prompt for more information, error for invalid information
-  enabled Next, Finish when valid
-  Use a 'Browse' button where possible
-  Open editor on the results of the wizard, at the very least select it, change perspective if you
   have to
-  Use the most specific words possible, "WMS Layer" not "FeatureCollection"

Editors Guidelines:

-  used to edit or browse primary content
-  open-save-close lifecycle, \* indicates save is needed
-  cannot open the same editor twice, within a perspective
-  labeled with name of content
-  drag out multiple tabs if you have to
-  hook into any global commands you can: cut, copy, paste, delete etc ...
-  toolbar contains the most common items from the view menu
-  context menu is based on current selection
-  contenxt menu contents set by selection type, enabled/distable by selection state
-  support extention of context menu with MB\_ADDITIONS and IActionFilter
-  use outline view if contents will not fit on screen
-  table cell editors should work with single click, and commited when user clicks away. Enter
   commit, Esc cancels

Views Guidelines:

-  use view to navigate information, open editor or display properties of an object
-  direct manipulation workflow
-  only one view per perspective, can be opened by several perspectives though
-  only commonly used commands on the toolbar, command must also be in a menu
-  view pulldown menu for presentation commands, in standard order
-  conext menu is for selection actions, in standard order, registered with the platform
-  context menu fixed set of commands by selection type, enabled/disabled based on selection state
-  an object appearing in more then one view should have the same context menu in each
-  support extention of context menu with MB\_ADDITIONS and IActionFilter
-  hook into global commands like cut, copy, paste, delete
-  persist view state between sesssions
-  navigation views should have a "link" button

uDig will occasionally use an apply-canel workflow for a view. When used in this fashion the apply,
cancel buttons are the last entries in the local toolbar. The view toolbar may also support a "link"
button, allowing the option of direct manipulation (if the user can stand the delay). A view used in
this fashion will apply any modifications when selection changes.

Perspectives Guidelines:

-  create perspectives for long lived tasks
-  consider the workflow and view layout, menu bars, etc ...
-  only open the perspective if the user agrees, this is a massive context switch for them
-  limit the "shortcuts" in the New, Open Perspective and Show View menus to around 7

Windows Guidelines

-  contribute ActionSets to the menu first, and then to the toolbar for frequent use
-  each ActionSet should have a specific task in mind, ie "zoom" vs "edit"
-  Be small, be many - an Action Set allows sharing between views and editors, you can't share if
   you have only one ActionSet for an entire plugin
-  let user control visible ActionSets
-  "Open" actions must appear in the Navigate pulldown menu of the window
-  Use the global status bar

Properties Guidelines

-  Use Properties view for quick easy changes, switching between local objects
-  Use Properties Dialog to edit a remote or complex object
-  Properties Dialog contains superset of items from the Properties view

Widgets Guidelines

-  Tree and Table widgets should ne careful when working with a checkbox; changing selection should
   not accidently change the check state

Standard Components Guidelines
------------------------------

-  you can hack the standard components, if you subclass or copy be sure to keep the same
   characteristics
-  We can't use the Navigator View or Tasks View in a RCP application

Preference Dialog

-  is used for global options
-  expose preferences for a view, editor or windows via a menu or toolbar
-  start with a single preference page
-  If needed a preference group should start with wide spread effects and specialize in sub pages
-  try and slot into existing categories

Flat Look Design

-  Use the flat look design for extensive property and configuration editing
-  Have the core selections on the overview page expanded, other pages provide a Home icon to return
   to the overview
-  Have your tree in the outline corraspond to the tabs of your content editor

Tao of Resource

-  If your object is equivalent to a IGeoReference use an adapater to let others play
-  A layer should behave and look the same everywhere (ie. catalog, seach, layer view)

Accessibility Guidelines

-  All of the features provided by a tool should be accessible using a mouse or the keyboard.

.. |image0| image:: images/icons/emoticons/smile.gif
