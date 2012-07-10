How do I get an SWT layout to display properly
==============================================

**Q:** How do I get an SWT layout to display properly?

**A:** The size of a control sometimes defaults to zero width and height – this drives us all mad.
The solution lies in the composite, rather than the control...

Method 1: call the layout() method

::

    Composite parent = new Composite(grandParent, SWT.NONE);
    ...
    (create controls)
    ...
    parent.layout();

Method 2: resize the composite

-  programatically with parent.setSize(...)?
-  manually by the user

