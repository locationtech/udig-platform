Shutdown Tasks
==============

The ShutdownTaskList object provides a consistent way to schedule tasks that need to be run on
shutdown. This is a better solution that running shutdown tasks in a plugin's shutdown method
because the tasks are:

#. Ran in a safe environment so all tasks are guaranteed to run even in the face of exceptions being
   raised.
#. Provides a single ProgressDialog for all tests to update rather than either opening a seperate
   dialog for each task or running silently in the background.

The ShutdownTaskList is a singleton and can be obtained using the ShutdownTaskList.get() method.
Tasks can be ran just before shutdown or after the workbench has been shutdown. Currently only the
Pre-shutdown tasks have a Progress Dialog associated with them because all UI components are
disposed of once the workbench has been shutdown.
