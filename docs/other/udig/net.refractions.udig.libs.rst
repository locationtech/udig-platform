Net.refractions.udig.libs
#########################

+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+
| uDig  |
| :     |
| net.r |
| efrac |
| tions |
| .udig |
| .libs |
| This  |
| page  |
| last  |
| chang |
| ed    |
| on    |
| Jul   |
| 14,   |
| 2012  |
| by    |
| jgarn |
| ett.  |
| "Libs |
| " Plu |
| gin   |
| ~~~~~ |
| ~~~~~ |
| ~~~   |
|       |
| The   |
| libs  |
| plugi |
| n     |
| is    |
| the   |
| "glue |
| "     |
| betwe |
| en    |
| uDig  |
| and   |
| the   |
| rest  |
| of    |
| the   |
| open  |
| sourc |
| e     |
| world |
| .     |
| This  |
| plugi |
| n     |
| is    |
| respo |
| nsibl |
| e     |
| for   |
| gathe |
| ring  |
| toget |
| her   |
| all   |
| the   |
| other |
| code  |
| depen |
| denci |
| es    |
| uDig  |
| has   |
| on    |
| the   |
| outsi |
| de    |
| world |
| .     |
|       |
| Custo |
| m bui |
| ld.xm |
| l     |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~     |
|       |
| The   |
| libs  |
| plugi |
| n     |
| conta |
| ins   |
| a     |
| custo |
| m     |
| build |
| .xml  |
| file, |
| this  |
| file  |
| fetch |
| es    |
| the   |
| requi |
| red   |
| jars  |
| from  |
| the   |
| follo |
| wing  |
| locat |
| ions: |
|       |
| -  ht |
| tp:// |
| lists |
| .refr |
| actio |
| ns.ne |
| t/    |
| -  yo |
| ur    |
|    lo |
| cal   |
|    MA |
| VEN   |
|    re |
| posit |
| ory   |
|       |
| This  |
| allow |
| s     |
| you   |
| to    |
| run   |
| uDig  |
| again |
| st    |
| the   |
| lates |
| t     |
| exper |
| imint |
| al    |
| geoto |
| ols   |
| code  |
| on    |
| your  |
| compu |
| ter!  |
| More  |
| impor |
| tantl |
| y     |
| it    |
| lets  |
| you   |
| test  |
| bug   |
| fixes |
| and   |
| enhan |
| cemen |
| ts    |
| to    |
| these |
| open  |
| sourc |
| e     |
| proje |
| cts.  |
|       |
| This  |
| build |
| .xml  |
| file  |
| is    |
| calle |
| d     |
| via a |
| "buil |
| der"  |
| in    |
| eclis |
| pe,   |
| to    |
| use   |
| simpl |
| y     |
| "clea |
| n"    |
| libs. |
| You   |
| can   |
| watch |
| the   |
| scrip |
| t     |
| run   |
| as it |
| downl |
| oads  |
| all   |
| the   |
| requi |
| red   |
| files |
| .     |
|       |
| Updat |
| ing t |
| he Li |
| bs Pl |
| ugin  |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~  |
|       |
| The   |
| Libs  |
| plugi |
| n     |
| can   |
| be    |
| custo |
| mized |
| in a  |
| numbe |
| r     |
| of    |
| ways, |
| usual |
| ly    |
| for   |
| each  |
| relea |
| se:   |
|       |
| Chang |
| ing   |
| GeoTo |
| ols   |
| versi |
| ons:  |
|       |
| .. co |
| de::  |
| code- |
| java  |
|       |
|     < |
| targe |
| t nam |
| e="up |
| date. |
| gt">  |
|       |
|    <p |
| roper |
| ty na |
| me="m |
| odule |
| Name" |
|  valu |
| e="gt |
| 2"/>  |
|       |
|    <p |
| roper |
| ty na |
| me="g |
| eotoo |
| ls.ve |
| rsion |
| " val |
| ue="2 |
| .1.RC |
| 1"/>  |
|       |
|    <p |
| roper |
| ty na |
| me="g |
| eotoo |
| ls.sn |
| apsho |
| t" va |
| lue=" |
| 2.1.1 |
| "/>   |
|       |
|       |
|    .. |
| .     |
|     < |
| /targ |
| et>   |
|     < |
| targe |
| t nam |
| e="up |
| date. |
| libs" |
| >     |
|       |
|    <p |
| roper |
| ty na |
| me="g |
| eoapi |
| .vers |
| ion"  |
| value |
| ="2.0 |
| -tige |
| r"/>  |
|       |
|    <p |
| roper |
| ty na |
| me="g |
| eotoo |
| ls.ve |
| rsion |
| " val |
| ue="2 |
| .1.1" |
| />    |
|       |
|    <p |
| roper |
| ty na |
| me="g |
| eotoo |
| ls.sn |
| apsho |
| t" va |
| lue=" |
| 2.1.1 |
| "/>   |
|       |
|       |
|    .. |
| ..    |
|     < |
| /targ |
| et>   |
|       |
| How   |
| is    |
| this  |
| infor |
| matio |
| n     |
| used: |
|       |
| .. co |
| de::  |
| code- |
| java  |
|       |
|     < |
| get s |
| rc="$ |
| {repo |
| }/${m |
| odule |
| Name} |
| /jars |
| /main |
| -${ge |
| otool |
| s.ver |
| sion} |
| .jar" |
|       |
|       |
|    de |
| st="$ |
| {lib} |
| /main |
| -${ge |
| otool |
| s.ver |
| sion} |
| .jar" |
|       |
|       |
|    us |
| etime |
| stamp |
| ="tru |
| e" ig |
| noree |
| rrors |
| ="tru |
| e" ve |
| rbose |
| ="tru |
| e"/>  |
|       |
|    .. |
| .     |
|       |
|    <g |
| et sr |
| c="${ |
| updat |
| eURL} |
| /${mo |
| duleN |
| ame}/ |
| ${geo |
| tools |
| .snap |
| shot} |
| /main |
| -${ge |
| otool |
| s.ver |
| sion} |
| .jar" |
|       |
|       |
|    de |
| st="$ |
| {lib} |
| /main |
| -${ge |
| otool |
| s.ver |
| sion} |
| .jar" |
|       |
|       |
|    us |
| etime |
| stamp |
| ="tru |
| e" ig |
| noree |
| rrors |
| ="tru |
| e" ve |
| rbose |
| ="tru |
| e"/>  |
|       |
| If    |
| the   |
| local |
| maven |
| repos |
| itory |
| conta |
| ins   |
| the   |
| indic |
| ated  |
| jar,  |
| it    |
| will  |
| be    |
| copie |
| d     |
| to    |
| the   |
| lib   |
| folde |
| r.    |
| If    |
| not   |
| the   |
| remot |
| e     |
| jar   |
| is    |
| copie |
| d     |
| from  |
| the   |
| updat |
| e     |
| site. |
|       |
| |imag |
| e1|   |
| `How  |
| to    |
| fix a |
| broke |
| n     |
| build |
|  <htt |
| p://u |
| dig.r |
| efrac |
| tions |
| .net/ |
| confl |
| uence |
| /disp |
| lay/U |
| DIG/H |
| ow+to |
| +fix+ |
| a+bro |
| ken+b |
| uild> |
| `__   |
+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+

+------------+----------------------------------------------------------+
| |image3|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: http://udig.refractions.net/image/UDIG/ngrelr.gif
.. |image1| image:: http://udig.refractions.net/image/UDIG/ngrelr.gif
.. |image2| image:: images/border/spacer.gif
.. |image3| image:: images/border/spacer.gif
