Composite Commands
##################

Composite Commnads
~~~~~~~~~~~~~~~~~~

Composite Commands provide a way to combine commands into one unit. A composite command is executed
and undone as if it is a single command.

Example taken from Zoom.java which is a tool see `Tools <Tools.html>`_:

::

    NavigationCommandFactory factory = NavigationCommandFactory.getInstance();

    NavCommand[] commands = new NavCommand[] {
       factory.createSetViewportCenterCommand(m.pixelToWorld( r.x + r.width / 2, r.y + r.height / 2)),
       factory.createZoomCommand(r.width / getContext().getMapDisplay().getDisplaySize().getWidth()) };

       map.sendCommand(factory.createCompositeCommand(commands));

