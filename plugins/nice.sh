# A Nice File
# list=`find $1 -type f -printf "%p\n"`
list=`find $1 -type f -exec echo {} \; | grep -v .class$ | grep -v .jar$` 

for i in $list
 do
   echo $i
   sed -i '' s/org.tcat.citd.sim.udig/org.locationtech.udig/g $i
   #sed -i.bak s/org.tcat.citd.sim.udig/org.locationtech.udig/g $i
done