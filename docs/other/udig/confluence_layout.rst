Confluence Layout
#################

+-----------------------------------+-----------------------------------+-----------------------------------+
| uDig : Confluence Layout          |
| This page last changed on Jun 23, |
| 2010 by jgarnett.                 |
| .. code:: code-java               |
|                                   |
|     <html>                        |
|     <head>                        |
|        <title>$title - UDIG</titl |
| e>                                |
|        #standardHeader()          |
|     </head>                       |
|                                   |
|     <body onload="placeFocus()"   |
|       STYLE="background: white ur |
| l(/confluence/download/attachment |
| s/3/bg_gradient.gif) repeat-x;">  |
|                                   |
|     <table border="0" cellpadding |
| ="0" cellspacing="0">             |
|        <tr style="padding: 4 0 0  |
| 0;">            <td valign=top>   |
|                                   |
|     <table border="0" cellspacing |
| ="0" width=900>                   |
|      <tr>                         |
|        <td width=150>             |
|        </td>                      |
|        <td height=79 bgcolor=whit |
| e width="730"                     |
|            align=center valign=bo |
| ttom                              |
|            style="padding: 5 5 0  |
| 5; background:white               |
|               url(/confluence/dow |
| nload/attachments/3/header_new.jp |
| g) no-repeat center;">            |
|          #usernavbar()            |
|        </td>                      |
|      </tr>                        |
|      <tr>                         |
|        <td align=right valign=cen |
| ter>                              |
|           #helpicon()             |
|           #printableicon()        |
|        </td>                      |
|        <td valign=top class="page |
| body" bgcolor=white width=730 sty |
| le="padding: 2 5 5 2;">           |
|                  #breadcrumbsAndS |
| earch()                           |
|        </td>                      |
|      </tr>                        |
|                                   |
|      <tr>                         |
|           <td valign="top" style= |
| "padding: 4;">                    |
|             <div class="navmenu"> |
|                <div class="menuhe |
| ading">                           |
|                   <a href="/confl |
| uence/display/UDIG/Home" title="H |
| ome Page">uDig Home</a>           |
|                </div>             |
|                <div class="menuit |
| ems">                             |
|                  <a href="http:// |
| lists.refractions.net/mailman/lis |
| tinfo/udig-devel">                |
|                    Email Lists    |
|                  </a><br>         |
|                  <a href="http:// |
| jira.codehaus.org/browse/UDIG">Is |
| sue Tracker</a><br>               |
|                  <a href="http:// |
| svn.geotools.org/udig/">Version C |
| ontrol</a><br>                    |
|                  <a href="/conflu |
| ence/display/UDIG/Schedule">Sched |
| ule</a><br>                       |
|                  <a href="/conflu |
| ence/display/UDIG/Documents">Docu |
| ments</a>                         |
|                </div>             |
|                                   |
|                <div class="menuhe |
| ading">                           |
|                  <a href="/conflu |
| ence/display/UDIG/Download">Downl |
| oad</a>                           |
|                </div>             |
|                <div class="menuit |
| ems">                             |
|                  <a href="/conflu |
| ence/display/UDIG/Latest">Latest< |
| /a>                               |
|                </div>             |
|                                   |
|                <div class="menuhe |
| ading">                           |
|                   <a href="/confl |
| uence/display/UDIG/Project">Proje |
| ct</a>                            |
|                </div>             |
|                                   |
|                <div class="menuit |
| ems">                             |
|                  <a href="/conflu |
| ence/display/UDIG/Environment">En |
| vironment</a><br>                 |
|                  <a href="/conflu |
| ence/display/UDIG/Source+Code">So |
| urce Code</a><br>                 |
|                  <a href="/conflu |
| ence/display/UDIG/Build">Build</a |
| ><br>                             |
|                  <a href="/conflu |
| ence/display/UDIG/Status">Status< |
| /a>                               |
|                </div>             |
|                                   |
|                <div class="menuhe |
| ading">                           |
|                   <a href="/confl |
| uence/display/UDIG/Developer">Dev |
| eloper</a>                        |
|                </div>             |
|                <div class="menuit |
| ems">                             |
|                  <a href="/conflu |
| ence/display/UDIG/Programmer%27s+ |
| Guide">Programmer's Guide</a>     |
|                </div>             |
|              #if ($page.getProper |
| ty("page.operations"))            |
|                <div class="menuhe |
| ading">Page Operations</div>      |
|                <div class="menuit |
| ems">                             |
|                   <div class="ope |
| rations">                         |
|                      $page.getPro |
| perty("page.operations")          |
|                   </div>          |
|                </div>             |
|              #end                 |
|             </div>                |
|             #globalnavbar("text") |
|           </td>                   |
|        <td bgcolor=white style="p |
| adding: 5;" width="730" valign="t |
| op" class="pagebody">             |
|            ## The "toolbar-style" |
|  page operations                  |
|            ##if ($page.getPropert |
| y("page.operations"))             |
|            ##<table align="right" |
|  class="toolbar"><tr><td>         |
|            ##   $page.getProperty |
| ("page.operations")               |
|            ##</td></tr></table>   |
|            ##end                  |
|                                   |
|             #if ($page.getPropert |
| y("page.surtitle"))               |
|                 $page.getProperty |
| ("page.surtitle")                 |
|             #end                  |
|                                   |
|             #if (!$page.getProper |
| ty("page.no-page-header"))        |
|                 <div class="pageh |
| eader">                           |
|                     <span class=" |
| pagetitle">$title</span>          |
|                 </div>            |
|             #end                  |
|                                   |
|             $body                 |
|        </td>                      |
|                                   |
|         ##if ($infoPanelRequired  |
| == true)                          |
|         ##<td valign=top align=le |
| ft>                               |
|         ##    #infoPanel(false fa |
| lse false false)                  |
|         ##         #globalnavbar( |
| "text")                           |
|         ## </td>                  |
|         ##end                     |
|      </tr>                        |
|         <tr>                      |
|           <td></td>               |
|           <td>#poweredby()</td>   |
|         </tr>                     |
|     </table>                      |
|             </td>                 |
|         </tr>                     |
|     </table>                      |
|     </body>                       |
|     </html>                       |
                                   
+-----------------------------------+-----------------------------------+-----------------------------------+

+------------+----------------------------------------------------------+
| |image1|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/border/spacer.gif
.. |image1| image:: images/border/spacer.gif
