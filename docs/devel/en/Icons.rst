Icons
=====

References:

* :doc:`http://wiki.eclipse.org/User\_Interface\_Guidelines#Folder\_Structure`


Organization of the icons/ directory
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

DIRECTORY

distabled

enabled

Other

Banner

Size

Placement

local toolbar

d\ `lcl16 <lcl16.html>`_/

e\ `lcl16 <lcl16.html>`_/

 

 

16x16

left & top clear

toolbar

d\ `tool16 <tool16.html>`_/

e\ `tool16 <tool16.html>`_/

 

 

16x16

left & top clear

view

d\ `view16 <view16.html>`_/

e\ `view16 <view16.html>`_/

 

 

16x16

left & bottom clear

model object

 

 

`obj16 <obj16.html>`_/

 

16x16

centered, bottom clear

overlay

 

 

`ovr16 <ovr16.html>`_/

 

7x8

one pixel white outline

wizard banner

 

 

 

`wizban <wizban.html>`_/

55x45

bottom left on blue gradient

 

 

 

 

 

 

 

PALETTE

color

grayscale

color

color

 

 

\*perspective & fastview icons require the right, and bottom edges to be clear.

Icon Types
~~~~~~~~~~

TYPE

type

description

local toolbar

lcl

found on the far right of the title area of a view

toolbar

tool

used in cascading menus, and the global toolbar

view

view

found in the top, left corner of a new view

model object

obj

used in the tree, list, properties views, and editor tabs

overlay

ovr

placed on top of model object to indicate a change

wizard banner

wizban

used in wizard dialog windows

Filename Conventions
~~~~~~~~~~~~~~~~~~~~

FILENAME SUFFIX

lcl

tool

view

obj

ovr

wizban

invoke a wizard, or graphics in a wizard

 

\_wiz

 

 

 

\_wiz

invoke executable file

 

\_exec

 

 

 

 

in an editor view

 

\_edit

 

 

 

 

in a navigator view

\_nav

\_nav

\_nav

 

 

 

do not fit into a category

\_misc

 

\_misc

 

 

 

represent tasks that user can do

\_tsk

 

\_tsk

\_tsk

\_tsk

 

toggles the working mode of the view

\_mode

 

 

 

 

 

found in a menu

\_menu

 

 

 

 

 

found in a property sheet

\_ps

 

\_ps

 

 

 

used in the tree, list, or property view

 

 

 

\_obj

 

 

model object icons on object palettes

 

 

 

\_pal

 

 

commands that engage the system

\_co

\_co

 

 

 

 

**Differences from Eclipse 2.1 User Interface Guidelines**

The original 2.1 guidelines provided two additional directories clcl & ctool which allowed a full
color palette, the enabled and disabled icons were restricted to 8 and 2 colors respectively. This
restriction seems to be relaxed in the recent eclipse 3.0 product.
