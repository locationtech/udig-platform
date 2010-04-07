# Just execute this in the scripts directory! It will run through all the plugins in 'plugins' and check the message.propeties files.

cd ../plugins;

for NAME in `ls`; 
do 
cd $NAME; 
echo "Entering directory $NAME";
../../scripts/validate-properties.pl;
cd ..;
cd scripts;
done
