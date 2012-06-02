


Rendering Preferences
~~~~~~~~~~~~~~~~~~~~~



Rendering Preferences
=====================

There are a number of preferences that control the performance of
rendering maps and appearance of the resulting maps. Often the
preferences are a trade-off between performance and appearance.





Available Preferences
~~~~~~~~~~~~~~~~~~~~~


+ Use anti-aliasing - When anti-aliasing is on the map is much
smoother and accurate than with it off, however it is quite expensive
so performance is much worse. > Default anti-aliasing is off. However
if features that should connect appear to be disconnected at small
zoom levels then turn on antialiasing as it may be an aliasing issue.<
li>
+ Render transparencies - When unchecked transparencies in the styles
will be ignored. Transparencies take slightly longer to render. >
Default is on.< li>
+ Tiling layer rendering (experimental) - Currently this is not quite
  a tiling system. When panning a read-only layer the tiler will only
  request the required areas as opposed to requesting a full rendering
  of the viewport.




Child Preference Pages
~~~~~~~~~~~~~~~~~~~~~~


+ `WMS Preferences`_




Peers
~~~~~


+ `Catalog Preferences`_
+ `Project Preferences`_
+ `Tool Preferences`_


.. _Tool Preferences: Tool Preferences.html
.. _Catalog Preferences: Catalog Preferences.html
.. _Project Preferences: Project Preferences.html
.. _WMS Preferences: WMS Preferences.html


