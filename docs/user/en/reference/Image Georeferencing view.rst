


Image Georeferencing view
~~~~~~~~~~~~~~~~~~~~~~~~~



Introduction
------------

The Image Georeferencing view is used to georeference an image using a
set of coordinates and the desired CRS.

To enable the Image Georeferencing view, go to *Window->Show view->
Other...->Select Image Georeferencing* under the *Other* folder.
> <div align="center"><a href='http:
/udig.refractions.net/confluence//download/attachments/13238886
/select-view.jpeg' target='select-view.jpeg' onClick='window.open("htt
p://udig.refractions.net/confluence//download/attachments/13238886
/select-view.jpeg", "select-view.jpeg",
"height=460,width=385,menubar=no,status=no,toolbar=no"); return
false;'>

A map with a valid CRS is needed for the Image Georeferencing view to
be able to run. Open a new map, load the layer you would like to use
as georeferenced base. The coordinates we'll use in the future will be
based on the same CRS as the map.



Features
--------

The figure shows the Image Georeferencing View integrated in the uDig
desktop. This new tool allows you to load an image, specify a set of
marks in it and associate the set of coordinates for each one.
> <div align="center"><a href='http:
/udig.refractions.net/confluence//download/attachments/13238886
/georeferncing-view.png' target='georeferncing-view.png' onClick='wind
ow.open("http://udig.refractions.net/confluence//download/attachments/
13238886/georeferncing-view.png", "georeferncing-view.png",
"height=950,width=814,menubar=no,status=no,toolbar=no"); return
false;'>



Sample Usage
------------

We are going to make an image georeferencing operation using uDig +
Axios Image Georeferencing software.



Step 1
~~~~~~

Click on the ** *Open file*** button and load the image you would like
to georeference. After that, the tools to manipulate the image will be
activated.
> <div align="center"><a href='http:
/udig.refractions.net/confluence//download/attachments/13238886/view-
with-image.jpeg' target='view-with-image.jpeg' onClick='window.open("h
ttp://udig.refractions.net/confluence//download/attachments/13238886
/view-with-image.jpeg", "view-with-image.jpeg",
"height=435,width=1037,menubar=no,status=no,toolbar=no"); return
false;'>

We have the following tools: *Add new mark, delete mark, move mark,
zoom in, zoom out, pan the image, delete all marks and fit to canvas.*
Move around the image using the pan tool and zoom in/out to be
accurate while adding marks.



Step 2
~~~~~~

Add marks to the image, we need at least 6 of them. Select the ** *add
new mark*** tool and add the first mark.
> <div align="center"><a href='http:
/udig.refractions.net/confluence//download/attachments/13238886/add-
mark.jpeg' target='add-mark.jpeg' onClick='window.open("http://udig.re
fractions.net/confluence//download/attachments/13238886/add-
mark.jpeg", "add-mark.jpeg",
"height=436,width=1037,menubar=no,status=no,toolbar=no"); return
false;'>

After the first mark is added, the tools that manipulate the ground
control points in the map are enabled. These tools are: *Add ground
control point, delete ground control point, move ground control point
and delete all ground control points.*



Step 3
~~~~~~

Select the tool ** *add ground control point***. Click on the
corresponding place on the map to add a ground control point. After
that, it'll show the coordinates on the coordinates table.
> <div align="center"><a href='http:
/udig.refractions.net/confluence//download/attachments/13238886/add-
gcp.jpeg' target='add-gcp.jpeg' onClick='window.open("http://udig.refr
actions.net/confluence//download/attachments/13238886/add-gcp.jpeg",
"add-gcp.jpeg",
"height=971,width=1300,menubar=no,status=no,toolbar=no"); return
false;'>

Repeat steps 3 and 4 until we have at least 6 marks with their
associated ground control points.
> <div align="center"><a href='http:
/udig.refractions.net/confluence//download/attachments/13238886/add-
gcp6.jpeg' target='add-gcp6.jpeg' onClick='window.open("http://udig.re
fractions.net/confluence//download/attachments/13238886/add-
gcp6.jpeg", "add-gcp6.jpeg",
"height=971,width=1300,menubar=no,status=no,toolbar=no"); return
false;'>



Step 4
~~~~~~

Only one thing left to be able to perform the georeferencing
operation, specify the output file. Click on the ** *Browse*** button
and specify the save file location.
> <div align="center"><a href='http:
/udig.refractions.net/confluence//download/attachments/13238886/save-
file.jpeg' target='save-file.jpeg' onClick='window.open("http://udig.r
efractions.net/confluence//download/attachments/13238886/save-
file.jpeg", "save-file.jpeg",
"height=632,width=962,menubar=no,status=no,toolbar=no"); return
false;'>



Step 5
~~~~~~

Now that all the parameters are fulfilled, click on the ** *run***
button and perform the Image Georeferencing operation.
> <div align="center"><a href='http: /udig.refractions.net/confluence/
/download/attachments/13238886/run.jpeg' target='run.jpeg' onClick='wi
ndow.open("http://udig.refractions.net/confluence//download/attachment
s/13238886/run.jpeg", "run.jpeg",
"height=437,width=1038,menubar=no,status=no,toolbar=no"); return
false;'>

The result of the operation:
> <div align="center"><a href='http: /udig.refractions.net/confluence/
/download/attachments/13238886/result.jpeg' target='result.jpeg' onCli
ck='window.open("http://udig.refractions.net/confluence//download/atta
chments/13238886/result.jpeg", "result.jpeg",
"height=971,width=1300,menubar=no,status=no,toolbar=no"); return
false;'>



Tips
----


+ You can add a ground control point using the add ground control
  point tool or directly inserting its coordinate in the table. Same for
  move and delete.
+ You can save/load your marks and their associated ground control
  points using the load/save buttons that are located next to the run
  button.
+ You can zoom in/out the image using the mouse wheel whenever you
  want.




