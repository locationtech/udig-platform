Create Layer from Query
#######################

There are two ways to create a layer to display the results of a spatial query:

You can filter the layer using a spatial (or any other Query):

#. Add your layer
#. Open the Style Editor
#. navigate to the Filter page
#. Add in your spatial filter using :doc:`/concepts/Constraint Query Language`

   -  Example: LANDLOCKED = 'true'
   -  Tip: You can define a spatial query using the select tool; and copy the filter out of the
      table view

#. The layer will now only show features that match the filter

You can copy a selection into a new Layer:

#. Open the table view
#. Provide a filter (defining a selection by hand - you can use a either full text, attribute match
   or a CQL expression)
#. The features matching the selection are selected
#. Copy and paste the features into a new layer

**Related concepts**

:doc:`concepts/Constraint Query Language`

**Related reference**

:doc:`Table view`

