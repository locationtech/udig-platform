Udig 0.9 Quickstart
###################

+------------+------------+------------+------------+------------+------------+------------+------------+------------+------------+
| uDig :     |
| UDIG 0.9   |
| Quickstart |
| This page  |
| last       |
| changed on |
| Mar 14,    |
| 2008 by    |
| jgarnett.  |
| The UDIG   |
| 0.9.1      |
| release is |
| available  |
| here:      |
|            |
| -  `udig0. |
| 9.1.exe <h |
| ttp://udig |
| .refractio |
| ns.net/dow |
| nloads/udi |
| g0.9.1.exe |
| >`__       |
| -  `udig0. |
| 9.1.zip <h |
| ttp://udig |
| .refractio |
| ns.net/dow |
| nloads/udi |
| g0.9.1.zip |
| >`__       |
|            |
| This       |
| release    |
| contains   |
| the first  |
| round of   |
| usability  |
| improvemen |
| ts         |
| (thanks    |
| for the    |
| helpful    |
| bugs       |
| reports).  |
| See the    |
| `GeoConnec |
| tions      |
| Project    |
| Schedule < |
| GeoConnect |
| ions%20Pro |
| ject%20Sch |
| edule.html |
| >`__       |
| more       |
| informatio |
| n          |
| on future  |
| releases.  |
|            |
| Recent     |
| developmen |
| t          |
| has        |
| focused on |
| (click     |
| link to    |
| view       |
| issues):   |
|            |
| -  wms     |
|    `report |
|    any     |
|    problem |
| s          |
|    here <h |
| ttp://jira |
| .codehaus. |
| org/secure |
| /IssueNavi |
| gator.jspa |
| ?reset=tru |
| e&mode=hid |
| e&pid=1060 |
| 0&sorter/o |
| rder=DESC& |
| sorter/fie |
| ld=priorit |
| y&resoluti |
| onIds=-1&c |
| omponent=1 |
| 1372>`__   |
| -  wfs     |
|    `report |
|    any     |
|    problem |
| s          |
|    here <h |
| ttp://jira |
| .codehaus. |
| org/secure |
| /IssueNavi |
| gator.jspa |
| ?reset=tru |
| e&mode=hid |
| e&pid=1060 |
| 0&sorter/o |
| rder=DESC& |
| sorter/fie |
| ld=priorit |
| y&resoluti |
| onIds=-1&c |
| omponent=1 |
| 0811>`__   |
| -  style   |
|    `report |
|    any     |
|    problem |
| s          |
|    here <h |
| ttp://jira |
| .codehaus. |
| org/secure |
| /IssueNavi |
| gator.jspa |
| ?view=&tem |
| pMax=1000& |
| decorator= |
| printable& |
| start=0&mo |
| de=hide>`_ |
| _          |
| -  tools   |
|    `report |
|    any     |
|    problem |
| s          |
|    here <h |
| ttp://jira |
| .codehaus. |
| org/secure |
| /IssueNavi |
| gator.jspa |
| ?view=&tem |
| pMax=1000& |
| decorator= |
| printable& |
| start=0&mo |
| de=hide>`_ |
| _          |
|            |
| Feedback   |
| is         |
| requested  |
| (testers   |
| are        |
| co-develop |
| ers!):     |
|            |
| -  `Issue  |
|    Tracker |
|  <http://j |
| ira.codeha |
| us.org/bro |
| wse/UDIG>` |
| __         |
| -  `Email  |
|    List <h |
| ttp://list |
| s.refracti |
| ons.net/ma |
| ilman/list |
| info/udig- |
| devel>`__  |
|            |
| +--------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| -------+   |
| | |image3| |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|            |
|        |   |
| | When per |
| forming a  |
| manual ins |
| tall of `u |
| dig0.9.1.z |
| ip <http:/ |
| /udig.refr |
| actions.ne |
| t/download |
| s/udig0.9. |
| 1.zip>`__, |
|  you will  |
| need the ` |
| S-3.1M4 RC |
| P <http:// |
| download.e |
| clipse.org |
| /eclipse/d |
| ownloads/d |
| rops/S-3.1 |
| M4-2004121 |
| 62000/inde |
| x.php>`__  |
| from `ecli |
| pse org <h |
| ttp://ecli |
| pse.org/do |
| wnloads>`_ |
| _. For a m |
| anual inst |
| all your J |
| RE will ne |
| ed to be e |
| xtended wi |
| th `JAI <h |
| ttp://java |
| shoplm.sun |
| .com/ECom/ |
| docs/Welco |
| me.jsp?Sto |
| reId=22&Pa |
| rtDetailId |
| =7341-JAI- |
| 1.1.2-oth- |
| JPR&SiteId |
| =JSC&Trans |
| actionId=n |
| oreg>`__,  |
| `ImageIO < |
| http://jav |
| ashoplm.su |
| n.com/ECom |
| /docs/Welc |
| ome.jsp?St |
| oreId=22&P |
| artDetailI |
| d=jaiio-1. |
| 0_01-oth-J |
| PR&SiteId= |
| JSC&Transa |
| ctionId=no |
| reg>`__ an |
| d `Soap <h |
| ttp://udig |
| .refractio |
| ns.net/dow |
| nloads/soa |
| p_ext.zip> |
| `__ (these |
|  extention |
| s modify y |
| our JRE/li |
| bs directo |
| ry).   |   |
| +--------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| ---------- |
| -------+   |
            
+------------+------------+------------+------------+------------+------------+------------+------------+------------+------------+

+------------+----------------------------------------------------------+
| |image5|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/information.gif
.. |image1| image:: images/icons/emoticons/information.gif
.. |image2| image:: images/icons/emoticons/information.gif
.. |image3| image:: images/icons/emoticons/information.gif
.. |image4| image:: images/border/spacer.gif
.. |image5| image:: images/border/spacer.gif
