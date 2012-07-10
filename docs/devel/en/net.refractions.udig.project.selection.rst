net.refractions.udig.project.selection
======================================

Maintains Selection state for a Map, used in conjunction with the LayerManager by the renderer to
determine screen appearance.

Functional Requirements
'''''''''''''''''''''''

-  selection by map interaction
-  selection by table interaction
-  selection by "Query" ui
-  scalable
-  programmatic access for operative plug-ins

Non Functional Requirements
'''''''''''''''''''''''''''

-  sub-second notification of selection activity
-  selection should take less then 5 to 10 seconds for alpha

Design Notes:
'''''''''''''

-  maintain a Filter associated with each Layer
-  Filter can be Fid based to capture individual selections
-  Filter can be BBox based to capture spatial selection on Map
-  Filter can be be constructed via a "Query ui"
-  Selection inverts can be formed quickly with the use of "not( filter )"
-  receives events from Visualization Stack and transforms Bbox into the "Layer" coordinate system.

