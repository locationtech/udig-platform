#!/bin/bash

PLUGINS=`find . -maxdepth 1 -type d -printf '%f\n' | grep -v .svn | grep -v ^.$`
# echo $PLUGINS
for PLUGIN in $PLUGINS
  do
  find $PLUGIN -iname 'messages*.properties' -exec perl -i.orig createMessages.pl '{}' \;
  find $PLUGIN -iname 'Messages.java' -exec perl -pi.orig -e 's/FIXME/$PLUGIN/g' '{}' \;
  find $PLUGIN -iname '*.java' -exec perl -pi.orig policy2messages.pl '{}' \;
  find $PLUGIN -iname 'Policy.java' -exec rm '{}' \;
done
