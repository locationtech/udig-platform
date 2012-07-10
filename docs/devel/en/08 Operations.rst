08 Operations
=============

Operations
----------

Operations in uDig are extremely simple to make. A developer extends the
net.refractions.udig.operation extension point and implements the
net.refractions.udig.ui.operations.IOp interface which has a single op() method. An example is the
`Layer Summary Operation <Layer%20Summary%20Operation.html>`_.

The operation is ran in a background thread so updating the UI must be done by calling
display.asyncExec(Runnable) or display.syncExec(Runnable) methods.

In addition to declaring the operation the extension also allows categories to be declared.
Categories allow operations to be organized in the operation menus.
