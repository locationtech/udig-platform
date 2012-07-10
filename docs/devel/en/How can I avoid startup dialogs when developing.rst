How can I avoid startup dialogs when developing
===============================================

**Q: How can I avoid startup dialogs when developing?**

**A:** Before launching uDig from your eclipse workspace, modify the run configuration from the
"Run..." menu item as shown below.

.. figure:: /images/how_can_i_avoid_startup_dialogs_when_developing/run_menu.gif
   :align: center
   :alt: 

On the **Arguments** tab, add "-DUDIG\_DEVELOPING" to your VM arguments.

.. figure:: /images/how_can_i_avoid_startup_dialogs_when_developing/run_config.gif
   :align: center
   :alt: 

Now when you clear your workspace and launch uDig, you won't have to close the tips dialog nor
navigate from the intro screen to the workbench.
