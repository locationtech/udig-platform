Guide to MigLayout
~~~~~~~~~~~~~~~~~~

MigLayout has been added as a core dependency for uDig; giving us a consistent layout manager that
is a little bit shorter on ceremony then the traditional SWT FormLayout. It is also aware of
platform differences and can sort out the correct "Gap" between fields and the correct button order
in order to fit in with other windows, linux, mac osx applications.

Related:

* `http://www.miglayout.com/ <http://www.miglayout.com/>`_
* `http://www.migcalendar.com/miglayout/swingdemoapp.jnlp <http://www.migcalendar.com/miglayout/swingdemoapp.jnlp>`_
   (Webstart demo; double click on the tab under each example to see the source code)
* `http://www.medicalgenomics.org/miglayout\_sample <http://www.medicalgenomics.org/miglayout_sample>`_
   (The shortest clearest example to copy)
-  `MigLayout: Easing the Pain of Swing/SWT Layout
   Management <http://www.devx.com/Java/Article/38017/1954>`_

Draw a Diagram
^^^^^^^^^^^^^^

Always start with a plan, on paper or a whiteboard, or your will drive yourself batty!
 |image0|

Draw a grid onto your plan; or arrows or whatever else you need to sort out what the layout settings
will be.
 |image1|

And then turn it into code.

::

    shell.setLayout( new MigLayout("","[right]10[left,grow][min!][min!]","30"));

    Label label = new Label(shell, SWT.SHADOW_IN);
    label.setText("Country:");

    name = new Text(shell, SWT.SHADOW_IN | SWT.BORDER);
    name.setLayoutData("span 3, growx, wrap");
    name.addKeyListener(this);
    ...

Suggestions for the above example:

#. 10 represents 10 pixels (or "10px") is not a very good idea as display resolutions are starting
   to change between computers.

   -  Consider "10lp" (for 10 logical pixels) works better and will adjust for the current
      resolution
   -  Consider "related" to indicate the two are related; the actual gap here will depend on
      windows,linux, osx
   -  Other gaps to consider: "unrelated", and "paragraph"

.. |image0| image:: /images/guide_to_miglayout/plan.jpg
.. |image1| image:: /images/guide_to_miglayout/plan_grid-1.jpg
