#!/bin/bash

# DEPLOY_DIR is where the sdk ant script will place the final build
export DEPLOY_DIR=/tmp/udig-sdk


if [ -d "$DEPLOY_DIR" ]; then
    echo "WARNING: A build may be progress by another process"
fi

LOG_FILE=/var/log/udig-builds/1.1.x-sdk.log

cd  /opt/udig-build/checkouts/1.1.x/
svn up 2>1 >$LOG_FILE
cd  /opt/udig-build/checkouts/1.1.x/net.refractions.udig.build-sdk/
ant build-deploy-locally 2>1 >> $LOG_FILE

deploy()
{
  echo "uploading $1 to rackmount..." 2>1 >> $LOG_FILE
  curl --user username:password --upload-file $1 http://udig.refractions.net/files/nightly-builds/$2 2>1 >> $LOG_FILE

  echo "Done uploading: $FILE_TO_DEPLOY" >> $LOG_FILE
}

for file in `ls ${DEPLOY_DIR}`
do
    deploy $DEPLOY_DIR/$file $file
done

rm -rf $DEPLOY_DIR

echo "Done build" >> $LOG_FILE

deploy $LOG_FILE 1.1.x-sdk.log

