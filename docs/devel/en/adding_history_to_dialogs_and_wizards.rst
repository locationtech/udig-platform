Adding History to Dialogs and Wizards
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

It is often beneficial to add a 'history' to some of the widgets to increase the useability of a
wizard or dialog. This article will explain how to use Eclipse's IDialogSettings interface to
achieve this easily and will show how it was implemented in uDig's WMS Wizard.

The terms wizard and dialog are interchangeable in this document.

Here is a screenshot of the WMS Wizard, displaying the rememberance of previously-entered URLs.
 |image0|

Here is what the contents of the file look like when saved out to disk:

**dialog\_settings.xml**

::

    <?xml version="1.0" encoding="UTF-8"?>
    <section name="Workbench">
      <section name="WMS_WIZARD">
        <list key="WMS_RECENT">
          <item value="http://wms.cits.rncan.gc.ca/cgi-bin/cubeserv.cgi?VERSION=1.1.0&amp;REQUEST=GetCapabilities"/>
          <item value="http://www2.dmsolutions.ca/cgi-bin/mswms_gmap?VERSION=1.1.0&amp;REQUEST=GetCapabilities"/>
        </list>
      </section>
    </section>

The overview:
 1. In the dialog's constructor, access the previously saved settings, or create them if they don't
exist.
 2. When creating the widgets for the dialog, populate them with previously entered settings.
 3. When the OK button is pushed (or the wizard is finished), save the entered values.

IDialogSettings
^^^^^^^^^^^^^^^

The JavaDoc for IDialogSettings can be viewed
`here <http://www.eclipse.org/documentation/html/plugins/org.eclipse.platform.doc.isv/doc/reference/api/org/eclipse/jface/dialogs/IDialogSettings.html>`_.

The interesting methods are:

-  IDialogSettings getSection(String sectionName)
-  IDialogSettings addNewSection(String name)
-  String[] getArray(String key)
-  void put(String key, String[] value)

Dialog settings are used to store information about particular dialogs. The settings are stored in
the plugin's runtime workspace. Because each plugin can have more than one dialog or wizard, the
settings are divided up into sections designated by a string. To create a new section, call
settings.addNewSection("example\_id").

Values saved into a dialog can be any primitive type, a String or array of Strings. An array of
Strings is the most interesting for dialogs, as they often use combo boxes for fields with history.

The loading and saving is handled by the plugin class.

An Example
^^^^^^^^^^

The WMS Wizard inside uDig has been converted to use IDialogSettings to store previously entered
URLs.

Initializing the settings
^^^^^^^^^^^^^^^^^^^^^^^^^

**WMSWizardPage.java**

::

    private static final String WMS_WIZARD = "WMS_WIZARD"; //$NON-NLS-1$
      private IDialogSettings settings;
        
      public WMSWizardPage() {
        ...

        settings = WmsPlugin.getDefault().getDialogSettings().getSection(WMS_WIZARD);
        if (settings == null) {
          settings = WmsPlugin.getDefault().getDialogSettings().addNewSection(WMS_WIZARD);
        }        
      }

This code accesses the previously entered values stored at the section named 'WMS\_WIZARD'. If they
don't exist (settings == null), the section must be added.

Reading the settings
^^^^^^^^^^^^^^^^^^^^

The code in WMSWizardPage that instantiates the widgets is also responsible for populating those
widgets with previously entered values.

**WMSWizardPage.java**

::

    private static final String WMS_RECENT = "WMS_RECENT"; //$NON-NLS-1$

    public void createControl(Composite parent) {
      String[] recentWMSs = settings.getArray(WMS_RECENT);
      if (recentWMSs == null) {
        recentWMSs = new String[0];
      }

      ...

      url = new Combo(composite, SWT.BORDER);
      url.setItems(recentWMSs);

      ...
    }

This code accesses all the values stored at the key 'WMS\_RECENT'. If no values are there, it will
return null. Later on, the Combo widget 'url' has its items set to those values.

Saving the settings
^^^^^^^^^^^^^^^^^^^

Saving the settings is a little more complicated than using them. The IDialogSettings interface only
allows an entire key to be saved at once, so we must add our new history item to the array and save
it again. Some convienence code has been taken from the
org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage class to make this job easy.

The addToHistory methods also limit the size of the history for each widget. Simply alter the value
of COMBO\_HISTORY\_LENGTH to change the size.

The only one here you should have to alter is saveWidgetValues(). Simply create a key (WMS\_RECENT)
for each widget, retrieve the previous values, and call addToHistory, passing in the value from the
widget as well. After that, just add the key and the values to the IDialogSettings.

**WMSWizardPage.java**

::

    private static final int COMBO_HISTORY_LENGTH = 15;

    /**
      * Saves the widget values
      */
    private void saveWidgetValues() {
      // Update history
      if (settings != null) {
        String[] recentWMSs = settings.getArray(WMS_RECENT);
        if (recentWMSs == null) {
          recentWMSs = new String[0];
        }
        recentWMSs = addToHistory(recentWMSs, url.getText());
        settings.put(WMS_RECENT, recentWMSs);
      }
    }
        
    /**
      * Adds an entry to a history, while taking care of duplicate history items
      * and excessively long histories.  The assumption is made that all histories
      * should be of length <code>COMBO_HISTORY_LENGTH</code>.
      *
      * @param history the current history
      * @param newEntry the entry to add to the history
      * @return the history with the new entry appended
      * Stolen from org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
      */
    private String[] addToHistory(String[] history, String newEntry) {
      ArrayList<String> l = new ArrayList<String>(Arrays.asList(history));
      addToHistory(l, newEntry);
      String[] r = new String[l.size()];
      l.toArray(r);
      return r;
    }
        
    /**
      * Adds an entry to a history, while taking care of duplicate history items
      * and excessively long histories.  The assumption is made that all histories
      * should be of length <code>COMBO_HISTORY_LENGTH</code>.
      *
      * @param history the current history
      * @param newEntry the entry to add to the history
      * Stolen from org.eclipse.team.internal.ccvs.ui.wizards.ConfigurationWizardMainPage
      */
    private void addToHistory(List<String> history, String newEntry) {
      history.remove(newEntry);
      history.add(0,newEntry);
        
      // since only one new item was added, we can be over the limit
      // by at most one item
      if (history.size() > COMBO_HISTORY_LENGTH) {
        history.remove(COMBO_HISTORY_LENGTH);
      }
    }

Once saveWidgetValues() is configured, you simply need to call it when your dialog or wizard is
done.

::

    /*
     * Success! Store the URL in history.
     */
    saveWidgetValues();

.. |image0| image:: /images/adding_history_to_dialogs_and_wizards/wizardHistory.jpg
