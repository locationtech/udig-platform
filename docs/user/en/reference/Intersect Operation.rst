


Intersect Operation
~~~~~~~~~~~~~~~~~~~

Computes the geometric intersection between two layers.

The *Intersect Operation* takes the features from two *source*
**vector** layers and computes the intersection between their feature
geometries, storing the result on a *Result Layer*.
> If the <em>Source Layer< em> has features selected, the operation
will be performed against them, otherwise, the Features from the whole
Layer will be used.
> The <em>Source Layers< em> are not modified by any means. A new
*Target Layer* is created by default, or the user can specify an
existing editable vector Layer where to store the results.



Sample Usage
------------

The *Intersect Operation* works over the selected features of the
desired layers, or over the whole layers if no selection is set.

On this example, we'll start by selecting a single Feature from one of
the layers of interest, as shown in *Figure 1*.
> <a href='http: /udig.refractions.net/confluence//download/attachment
s/2719854/intersect_1_select.png' target='intersect_1_select.png' onCl
ick='window.open("http://udig.refractions.net/confluence//download/att
achments/2719854/intersect_1_select.png", "intersect_1_select.png",
"height=598,width=527,menubar=no,status=no,toolbar=no"); return
false;'>
> <b>Figure 1. Limit operation scope with selection< b>

Go to the *Spatial Operations* View, and select *Intersect* from the
*Operation* drop down.

The *Intersect Operation*'s specific input options will show up (
*Figure 2*).

` `_
> <b>Figure 2. Intersect Operation controls< b>


+ **Select the Source Layers** > On the <em>Source< em> select the
  *Layers* to intersect from the *First Layer* and *Second Layer* drop
  down lists. By default, the currently selected *Layer* in the *Layers
  View* will be chosen for you for the *First Layer* option.



+ **Select the Result Layer** > The <em>Result Layer< em> drop down
  list will contain a proposed Layer name for the Layer to be created to
  hold the operation's results. You can leave it as is, type another
  Layer name, or select an existing Layer from the *Result Layer* drop
  down. In the later case, the operation's result will be stored on the
  selected Layer.



+ **Perform the operation** > Press the <em>Perform< em> button from
  the View's tool bar and the operation will begin and the operation's
  progress will be shown up on a progress dialog, as shown in Figure 3.
  The operation may take a while to complete, depending on the amount
  and complexity of the input geometries.



> <b>Figure 3. Progress Dialog< b>

Once the Operation completes, if a new Layer were created to hold the
result, it will be automatically added to the current Map.

*Figure 4* shows the newly created Layer ( *Intersect-1*, in green)
added to the map, with the intersecting feature from the selected
Municipality and the Voting Areas Layer.

` `_
> <b>Figure 4. Operation's Result< b>



