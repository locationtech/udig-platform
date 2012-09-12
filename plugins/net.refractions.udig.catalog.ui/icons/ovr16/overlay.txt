STATE: n/a
TYPE: overlay
SIZE: 7x pixels wide x 8
PALETTE: Eclipse 183 colour palette

Overlay 
-------

Maximum 7 pixels wide x 8 pixels high, always centered.

Icon should have a white outside keyline around it to separate it from the icon it is being appended to. 
Position of overlay based on what kind of information is being communicated.

Nature Overlay:
  Top Right of 16x16 icon
  Overlay indicating the "nature" of the object. 
  Only a few project nature overlay icons should be used to prevent crowding in the interface.
  Quickly identify the type object.
  The white keyline border is applied around the image to enhance legibility.

  Example: background layer.

Auxillary Overlay:
  Bottom Left of 16x16 icon
  The auxiliary overlay quickly identifies the status of an objec  displayed in all tree views)
  Examples of auxiliary overlays are connected, waiting, warning, error, failure, and success.
  
  Example: Resource warning feedback
  

Layer Overlay:
  Overlays extend the base 16x16 icon offering positions A, B and C.
  
  Overlays are displayed in the Layer, Info and Document view to identify attributes of an object.
  It is important to make sure the base object icon can support the addition of overlays without compromising readability.
  
  
  Slots A and B are used to communicate attributes.
  Slot C is used to communicate "action".
  
  Example: Document marked with attachment in position A, and a "new" in position C to indicate a template. 
  
Underlay
--------

  Suggested to communicate interaction status.
  
  Example: Highlight layers that interact with the current tool.
  