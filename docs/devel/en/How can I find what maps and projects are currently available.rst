How can I find what maps and projects are currently available
=============================================================

**Q:** How can I find what maps and projects are currently available?

**A:** ApplicationGIS provides access to all the projects currently registered with the uDig
instance.

**ApplicationGIS.getProjects()** will return all the projects currently registered with the system.
Given a project all the contained elements can be searched via the **getElements()** method. If a
single type of elements is desired such as maps the **getElements( Class<T> )** method can be used.
For example:

::

    ApplicationGIS.openMap(ApplicationGIS.getProjects().get(0).getElements(IMap.class).get(0));

will return open the first map in the first registered project.
