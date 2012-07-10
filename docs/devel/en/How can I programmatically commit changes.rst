How can I programmatically commit changes
=========================================

**Q: How can I programmatically commit changes?**

**A:** There is a Commit Command that can be sent to the map to commit:

::

    MapCommand commitCommand=EditCommandFactory.getInstance().createCommitCommand();
    map.sendCommandSync(commitCommand);

