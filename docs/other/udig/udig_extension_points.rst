Udig Extension Points
#####################

+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+
| uDig  |
| :     |
| uDig  |
| Exten |
| sion  |
| Point |
| s     |
| This  |
| page  |
| last  |
| chang |
| ed    |
| on    |
| Jan   |
| 27,   |
| 2009  |
| by    |
| dmidd |
| lecam |
| p@ave |
| ncia. |
| com.  |
| Rich  |
| Clien |
| t Pla |
| tform |
|  Exte |
| ntion |
|  Poin |
| ts    |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~    |
|       |
| We    |
| are   |
| makin |
| g     |
| use   |
| of    |
| the   |
| follo |
| wing  |
| Eclip |
| se    |
| Rich  |
| Clien |
| t     |
| Platf |
| orm   |
| Exten |
| tion  |
| Point |
| s:    |
|       |
| -  or |
| g.ecl |
| ipse. |
| core. |
| runti |
| me.ap |
| plica |
| tions |
|       |
|    -  |
|  id=" |
| net.r |
| efrac |
| tions |
| .udig |
| .ui.U |
| DigAp |
| plica |
| tion" |
|       |
| -  or |
| g.ecl |
| ipse. |
| ui.pe |
| rspec |
| tives |
|       |
|    -  |
|  id=" |
| net.r |
| efrac |
| tions |
| .udig |
| .ui.U |
| DigPe |
| rspec |
| tive" |
|       |
| UDig  |
| Appli |
| catio |
| n Ext |
| entio |
| n Poi |
| nts   |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~   |
|       |
| The   |
| list  |
| of    |
| all   |
| uDig' |
| s     |
| exten |
| sion  |
| point |
| s     |
| and   |
| the   |
| docum |
| entat |
| ion   |
| can   |
| be    |
| found |
| `here |
|  <htt |
| p://u |
| dig.r |
| efrac |
| tions |
| .net/ |
| confl |
| uence |
| /disp |
| lay/D |
| EV/2+ |
| uDig+ |
| exten |
| sion+ |
| point |
| s+lis |
| t>`__ |
|       |
| Exten |
| tions |
|  Poin |
| ts &  |
| Plug- |
| in Ma |
| nifes |
| t     |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~~~~~ |
| ~     |
|       |
| | Whe |
| n     |
| setti |
| ng    |
| up    |
| your  |
| plug- |
| in    |
| manif |
| est   |
| the   |
| exten |
| tion  |
| point |
| s     |
| bind  |
| the   |
| Java  |
| class |
| es    |
| in    |
| your  |
| Plug- |
| in    |
| to    |
| ids.  |
| |     |
| Becau |
| se    |
| we    |
| are   |
| sane  |
| we    |
| are   |
| going |
| to    |
| keep  |
| the   |
| ids   |
| match |
| ing   |
| the   |
| class |
| name  |
| they  |
| refer |
| to.   |
| This  |
| level |
| of    |
| sanit |
| y     |
| |  is |
| not   |
| requi |
| red   |
| by    |
| the   |
| Eclip |
| se    |
| frame |
| work  |
| - it  |
| is an |
| addit |
| ional |
| restr |
| ictio |
| n     |
| of    |
| the   |
| uDig  |
| appli |
| catio |
| n.    |
|       |
| Examp |
| le    |
| Top-L |
| evel  |
| Plug- |
| in    |
| Eleme |
| nt:   |
|       |
| .. co |
| de::  |
| code- |
| xml   |
|       |
|     < |
| exten |
| sion  |
|       |
|       |
|   id= |
| "uDig |
| Appli |
| catio |
| n"    |
|       |
|       |
|   poi |
| nt="o |
| rg.ec |
| lipse |
| .core |
| .runt |
| ime.a |
| pplic |
| ation |
| s">   |
|       |
|       |
|   <ap |
| plica |
| tion> |
|       |
|       |
|       |
|  <run |
|       |
|       |
|       |
|       |
| class |
| ="net |
| .refr |
| actio |
| ns.ud |
| ig.ui |
| .UDig |
| Appli |
| catio |
| n">   |
|       |
|       |
|       |
|  </ru |
| n>    |
|       |
|       |
|   </a |
| pplic |
| ation |
| >     |
|       |
|    </ |
| exten |
| sion> |
|       |
| Becau |
| se    |
| this  |
| is a  |
| "top- |
| level |
| plug- |
| in    |
| eleme |
| nt"   |
| the   |
| id="u |
| DigAp |
| plica |
| tion" |
| gets  |
| prepe |
| nded  |
| with  |
| "net. |
| refra |
| ction |
| s.udi |
| g.cor |
| e"    |
| givin |
| g     |
| us    |
| the   |
| direc |
| t     |
| match |
| we    |
| were  |
| after |
| .     |
|       |
| Examp |
| le    |
| Plug- |
| in    |
| Eleme |
| nt:   |
|       |
| .. co |
| de::  |
| code- |
| xml   |
|       |
|     < |
| exten |
| sion  |
|       |
|       |
|   poi |
| nt="o |
| rg.ec |
| lipse |
| .ui.p |
| erspe |
| ctive |
| s">   |
|       |
|       |
|   <pe |
| rspec |
| tive  |
|       |
|       |
|       |
|  name |
| ="%pe |
| rspec |
| tiveN |
| ame"  |
|       |
|       |
|       |
|  clas |
| s="ne |
| t.ref |
| racti |
| ons.u |
| dig.u |
| i.UDi |
| gPers |
| pecti |
| ve"   |
|       |
|       |
|       |
|  id=" |
| net.r |
| efrac |
| tions |
| .udig |
| .ui.U |
| DigPe |
| rspec |
| tive" |
| >     |
|       |
|       |
|   </p |
| erspe |
| ctive |
| >     |
|       |
|    </ |
| exten |
| sion> |
|       |
| The   |
| subel |
| ement |
| perse |
| pecti |
| ve    |
| has   |
| to be |
| compl |
| etely |
| speci |
| fied  |
| with  |
| id="n |
| et.re |
| fract |
| ions. |
| udig. |
| ui.UD |
| igPer |
| spect |
| ive". |
|       |
| **Lin |
| ks**  |
|       |
| -  `N |
| otes  |
|    on |
|    th |
| e     |
|    Ec |
| lipse |
|    Pl |
| ug-in |
|    Ar |
| chite |
| cture |
|  <htt |
| p://w |
| ww.ec |
| lipse |
| .org/ |
| artic |
| les/A |
| rticl |
| e-Plu |
| g-in- |
| archi |
| tectu |
| re/pl |
| ugin_ |
| archi |
| tectu |
| re.ht |
| ml>`_ |
| _     |
       
+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+-------+

+------------+----------------------------------------------------------+
| |image1|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: images/border/spacer.gif
.. |image1| image:: images/border/spacer.gif
