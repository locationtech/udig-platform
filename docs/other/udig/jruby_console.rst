Jruby Console
#############

+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+
| uDig :    |
| JRuby     |
| Console   |
| This page |
| last      |
| changed   |
| on Jun    |
| 23, 2010  |
| by        |
| jgarnett. |
| Many GIS  |
| tools     |
| have a    |
| built-in  |
| scripting |
| environme |
| nt        |
| that      |
| allows    |
| the user  |
| to        |
| program   |
| their own |
| geographi |
| c         |
| analyses. |
| uDIG has  |
| a couple  |
| of        |
| prototype |
| s         |
| in this   |
| area, but |
| it would  |
| be nice   |
| to see a  |
| mature    |
| and       |
| complete  |
| developme |
| nt        |
| as part   |
| of the    |
| uDIG      |
| Core.     |
|           |
|  This     |
| proposal  |
| covers    |
| one key   |
| element   |
| of a      |
| complete  |
| scripting |
| environme |
| nt,       |
| the       |
| interacti |
| ve        |
| console.  |
| I'm       |
| making    |
| this      |
| suggestio |
| n         |
| based on  |
| a         |
| prototype |
| of this   |
| functiona |
| lity      |
| that has  |
| already   |
| been      |
| coded for |
| the       |
| 'amanzi   |
| wireless  |
| explorer' |
| ,         |
| an RCP    |
| app based |
| on the    |
| uDIG SDK. |
| Our       |
| initial   |
| specifica |
| tion      |
| suggestio |
| n         |
| here is   |
| to some   |
| extend a  |
| descripti |
| on        |
| of the    |
| current   |
| design of |
| the       |
| prototype |
| .         |
|           |
| JRuby JIR |
| B Console |
| ========= |
| ========= |
|           |
| JRuby     |
| comes     |
| with a    |
| command-l |
| ine       |
| 'interact |
| ive       |
| ruby'     |
| console,  |
| as well   |
| as a      |
| swing     |
| based     |
| graphical |
| component |
| containin |
| g         |
| that      |
| console.  |
| The swing |
| version   |
| is well   |
| written   |
| and       |
| stable,   |
| and so we |
| used      |
| SWT\_AWT  |
| to embed  |
| that in   |
| an        |
| eclipse   |
| view. We  |
| added     |
| interpret |
| er        |
| startup   |
| hooks to  |
| allow an  |
| applicati |
| on        |
| that      |
| embeds    |
| this      |
| console   |
| to        |
| initializ |
| e         |
| the Ruby  |
| with any  |
| number of |
| globals   |
| and       |
| console   |
| methods.  |
| This      |
| allows,   |
| for       |
| example,  |
| the       |
| support   |
| of        |
| utility   |
| methods   |
| to give   |
| the user  |
| easier    |
| access to |
| the       |
| catalog,  |
| layers    |
| and map.  |
| In our    |
| implement |
| ation,    |
| we have a |
| core      |
| plugin    |
| that      |
| provides  |
| the       |
| framework |
| ,         |
| plus a    |
| simple    |
| example   |
| tat can   |
| plug into |
| any       |
| eclipse   |
| RCP app.  |
| Then we   |
| have a    |
| uDIG      |
| specific  |
| extension |
| that      |
| registers |
| the view  |
| with the  |
| map       |
| perspecti |
| ve,       |
| and also  |
| initializ |
| es        |
| the IRB   |
| with      |
| utility   |
| methods   |
| that      |
| provide a |
| number of |
| direct    |
| accesses  |
| to        |
| projects, |
| layers    |
| and       |
| feature   |
| collectio |
| ns.       |
| The IRB   |
| starts    |
| with a    |
| short     |
| help      |
| describin |
| g         |
| these     |
| capabilit |
| ies       |
| and a     |
| summary   |
| of the    |
| current   |
| project   |
| data      |
| structure |
| .         |
| |image2|  |
|           |
| Future Wo |
| rk        |
| ========= |
| ==        |
|           |
| |         |
| |  The    |
| above     |
| work is   |
| not only  |
| a         |
| prototype |
| ,         |
| but there |
| are       |
| aspects   |
| specific  |
| to the    |
| AWE       |
| applicati |
| on.       |
| The       |
| generic   |
| framework |
| component |
| is        |
| possibly  |
| re-usable |
| with      |
| little    |
| change,   |
| but the   |
| specific  |
| view      |
| shown     |
| above,    |
| while     |
| mostly    |
| uDIG      |
| specific, |
| needs to  |
| be        |
| generaliz |
| ed        |
| further   |
| and       |
| enhanced. |
| We need   |
| to me     |
| much more |
| careful   |
| with      |
| stability |
| ,         |
| handling  |
| exception |
| s         |
| and       |
| infinite  |
| loops,    |
| and test  |
| on a      |
| variety   |
| of data   |
| types.    |
| The JRuby |
| gives a   |
| powerful  |
| direct    |
| access to |
| the       |
| underlyin |
| g         |
| Java      |
| code,     |
| which can |
| also open |
| a         |
| pandoras  |
| box, so   |
| perhaps   |
| implement |
| ation     |
| of a      |
| security  |
| model of  |
| some kind |
| is        |
| necessary |
| .         |
| |         |
| |         |
|           |
| Attachmen |
| ts:       |
| |image3|  |
| `AWEScrip |
| t\_Consol |
| e.png <do |
| wnload/at |
| tachments |
| /8389139/ |
| AWEScript |
| _Console. |
| png>`__   |
| (image/pn |
| g)        |
+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+

+------------+----------------------------------------------------------+
| |image5|   | Document generated by Confluence on Aug 11, 2014 12:31   |
+------------+----------------------------------------------------------+

.. |image0| image:: /images/jruby_console/AWEScript_Console.png
.. |image1| image:: images/icons/bullet_blue.gif
.. |image2| image:: /images/jruby_console/AWEScript_Console.png
.. |image3| image:: images/icons/bullet_blue.gif
.. |image4| image:: images/border/spacer.gif
.. |image5| image:: images/border/spacer.gif
