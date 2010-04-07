UPDATE:

Navigate to:
- http://udig.refractions.net/confluence/spaces/exportspace.action?key=EN

1. Export Fomrat: select "HTML Output"
   Other Options: uncheck "Include Comments"
2. Pages to export: Check All
3. Export
4. Fix the returned url

Example:
http://udig.refractions.net/confluence/download/temp/export_01232008_105909/EN-20080123-10_59_20.zip

Fixed:
http://udig.refractions.net:8080/confluence/download/temp/export_01232008_105909/EN-20080123-10_59_20.zip

5. Unzip the contents of EN into the html folder
6. Open up EN/index.html in notepad, select everything and copy into the clipboad
7. Open up toc.xml in notepad, select everything and hit paste
9. Try it out!
10. Convert the new lines to your platform:
    linux: dos2unix
    win32: todos (download: http://www.thefreecountry.com/tofrodos/)
11. svn add any new pages
12. svn commit

This process can be repated for each transation:
 nl/de/html and nl/de/toc.xml
 nl/es/html and nl/es/toc.xml
 nl/fr/html and nl/es/toc.xml
 etc...
 
 
 
 