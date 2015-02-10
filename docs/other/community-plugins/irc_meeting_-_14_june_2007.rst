Irc Meeting - 14 June 2007
##########################

+---------------------------+---------------------------+---------------------------+---------------------------+
| Community Plugins : IRC   |
| Meeting - 14 June 2007    |
| This page last changed on |
| Mar 08, 2010 by jgarnett. |
| | 1) gvSig collab?        |
| |  2) native libs         |
| |  3) trunk style         |
| |  4) Postgis import      |
| |  5) org.udig namespace  |
|                           |
| | (10:14) <jgarnett>      |
| chorner you are on some   |
| gvSig list? and got an    |
| email about uDig          |
| collaboration             |
| |  (10:14) <jgarnett> is  |
| that a public email list? |
| Or was it something that  |
| was forwarded to you      |
| |  (10:14) <chorner> i'm  |
| on the list... digging    |
| for the thread            |
| |  (10:14) <chorner>      |
| http://runas.cap.gva.es/p |
| ipermail/gvsig_internacio |
| nal/2007-June/000491.html |
| |  (10:15) <jgarnett> So  |
| we did manage to talk to  |
| gvSig last FOSS4G - and   |
| we talked with them about |
| sending us some work      |
| (mostly JTS curve kind of |
| stuff).                   |
| |  (10:16) <jgarnett>     |
| near as I can tell talks  |
| stalled about there; they |
| were interested in our    |
| graph stuff as well - but |
| preferred rolling their   |
| own.                      |
| |  (10:16) <chorner>      |
| alright, i think we need  |
| to roll out the olive     |
| branch                    |
| |  (10:16) <acuster> not  |
| much there                |
| |  (10:16) <jgarnett> the |
| question is in what       |
| manner?                   |
| |  (10:16) <acuster> it's |
| more a geoapi/geotools    |
| link than a udig link     |
| isn't it?                 |
| |  (10:17) <chorner>      |
| acuster: yes              |
| |  (10:17) <moovida> I    |
| think gvSIG, uDig (and    |
| you might also consider   |
| the JUMP family) have     |
| |  (10:17) <moovida>      |
| quite different           |
| approaches at the GUI     |
| level.                    |
| |  (10:17) <jgarnett>     |
| What we want is some of   |
| their paid work; until    |
| then we will work with    |
| people that offer to put  |
| up money or volunteer     |
| time.                     |
| |  (10:17) <moovida> that |
| makes sense Adrian        |
| |  (10:17) <chorner>      |
| let's talk about what     |
| we're respectively doing  |
| and figure out how to get |
| them into geotools        |
| |  (10:17) <jgarnett> As  |
| for the license; we can   |
| make use of code with     |
| them - but the result is  |
| limited to a GPL module.  |
| |  (10:17) <chorner>      |
| since geotools already is |
| the shared library        |
| |  (10:17) <jgarnett> We  |
| could talk to them about  |
| GPL+Classpath exception.  |
| |  (10:18) <acuster>      |
| what's the GPL module?    |
| |  (10:18) <jgarnett> we  |
| would need to create it - |
| just to host shared code  |
| with them.                |
| |  (10:18) <jgarnett> It  |
| could be part of the      |
| formal uDig example app   |
| |  (10:18) <jgarnett> but |
| not part of the SDK (only |
| optional for those people |
| who want to go GPL)       |
| |  (10:18) <jgarnett>     |
| This has been the thing   |
| that prevented their      |
| collaboration with        |
| GeoServer previously.     |
| |  (10:19) <jgarnett> But |
| perhaps GPL+Classpath     |
| exception would be okay   |
| for them? It is what Java |
| is licensed as now .. so  |
| all of our customers have |
| to be happy with it one   |
| way or another.           |
| |  (10:20) <jgarnett> so  |
| chorner what should we do |
| |  (10:20) <chorner> i    |
| think i'll reply to their |
| message on their list     |
| |  (10:20) <jgarnett> can |
| you reply to the message  |
| and say hi? perhaps point |
| them to this IRC log...   |
| |  (10:20) <chorner> and  |
| try to open a dialog      |
| |  (10:20) <chorner> (no  |
| pun intended)             |
| |  (10:20) <jgarnett>     |
| okay; I have the          |
| GTStreeing document if    |
| you want to point them    |
| towards what the broader  |
| community is doing.       |
| |  (10:20) <jgarnett>     |
| (need to update that      |
| picture)                  |
| |  (10:20) <chorner>      |
| \*dialogue                |
| |  (10:21) <acuster>      |
| point them towards        |
| Geotools not towards udig |
| |  (10:21) <chorner>      |
| agreed                    |
| |  (10:21) <jgarnett>     |
| chorner can you get back  |
| to us on udig-devel; and  |
| if you need me to join    |
| their list let me know.   |
| |  (10:21) <chorner>      |
| next?                     |
| |  (10:21) <jgarnett> 2)  |
| native libs               |
| |  (10:21) <moovida> any  |
| ideas?                    |
| |  (10:21) <jgarnett> heh |
| - one of the "advantages" |
| to the eclipse platform;  |
| but a really easy way to  |
| shoot ourselves in the    |
| foot.                     |
| |  (10:22) <jgarnett> do  |
| what swt does if you have |
| a choice and isolate the  |
| native libs into          |
| operating system specific |
| fragments.                |
| |  (10:22) <acuster> what |
| are the choices? Say      |
| moovida wants his and I   |
| want mine                 |
| |  (10:22) <jgarnett> But |
| be warnned - it is        |
| exactly this complexity   |
| that prevented the OSSIM  |
| intergration from being   |
| part of the app (even     |
| though it looks great)    |
| |  (10:22) <acuster>      |
| jgarnett, can you flesh   |
| out what happened with    |
| ossim?                    |
| |  (10:23) <moovida> yes  |
| please                    |
| |  (10:23) <jgarnett> the |
| choices are captured as   |
| fragments?                |
| |  (10:23) <jgarnett> and |
| when you build up your    |
| applicaiton for release   |
| you check of what         |
| fragments to use?         |
| |  (10:23) <jgarnett> um  |
| okay ...                  |
| |  (10:23) <jgarnett> in  |
| FOSS4G2005                |
| |  (10:23) <jgarnett> the |
| OSSIM crew were told to   |
| use uDig as a front end   |
| (because they needed one) |
| |  (10:24) <jgarnett> we  |
| volunteered about three   |
| moths of Jesse's time and |
| got them on screen.       |
| |  (10:24) <jgarnett> But |
| then stalled based on two |
| things:                   |
| |  (10:24) <jgarnett> a)  |
| they could not always     |
| parse the WKT used for    |
| projections               |
| |  (10:24) \* moovida     |
| knows that is also a      |
| GRASS problem...          |
| |  (10:24) <jgarnett> b)  |
| Jesse could not follow    |
| their build instructions  |
| three times for three     |
| different platforms in    |
| order to produce the      |
| native libs               |
| |  (10:25) <jgarnett> I   |
| think OSSIM is in a       |
| better place for the WKT  |
| thing                     |
| |  (10:25) <jgarnett> but |
| really we needed them to  |
| make the native libs      |
| available; and they did   |
| not put up.               |
| |  (10:25) <jgarnett> so  |
| we dropped it until they  |
| got back to us.           |
| |  (10:25) <acuster> what |
| would the plugin/fragment |
| look like with the native |
| lib?                      |
| |  (10:25) <jgarnett>     |
| They did demo at a        |
| GeoIntel conference; but  |
| just like with gvSig      |
| without someone paying    |
| for it they were not      |
| getting anywhere.         |
| |  (10:26) <jgarnett> I   |
| think we can look at the  |
| OSSIM code still in udig  |
| for an example; or at SWT |
| for an example.           |
| |  (10:26) <jgarnett> I   |
| think it ends up being a  |
| core plugin with the Java |
| API.                      |
| |  (10:26) <jgarnett> and |
| three fragments - one for |
| each operating system.    |
| |  (10:26) <moovida> you  |
| are telling me that some  |
| of the ossim fragments    |
| contain native libs?      |
| |  (10:26) <jgarnett>     |
| that "contributes" the    |
| native code               |
| |  (10:26) <jgarnett> yes |
| |  (10:27) <jgarnett> and |
| the SWT fragments as well |
| |  (10:27) <moovida>      |
| great! and how are they   |
| fished out in runtime?    |
| |  (10:27) <jgarnett>     |
| there are lots of native  |
| libs around               |
| |  (10:27) <jgarnett> one |
| of the things that the    |
| eclipse plugin frame work |
| is good at                |
| |  (10:27) <jgarnett> is  |
| letting you manage your   |
| native libs and not screw |
| it up                     |
| |  (10:27) <jgarnett>     |
| (since we got all that    |
| versioned plugin,         |
| fragment goodness around  |
| to help)                  |
| |  (10:27) <jgarnett> the |
| plugin system loads up    |
| the plugin, and applies   |
| the fragments over top.   |
| |  (10:28) <jgarnett> the |
| fragments are marked with |
| the operating system and  |
| plugin version that they  |
| apply to.                 |
| |  (10:28) <jgarnett> so  |
| it is all kind of happy.  |
| |  (10:28) \* moovida is  |
| wondering if you have a   |
| link to a documentation   |
| for that                  |
| |  (10:28) <jgarnett> We  |
| **shoud** be able to      |
| follow the existing       |
| examples; or just read a  |
| book / web page in order  |
| to figure it out.         |
| |  (10:28) <acuster>      |
| jgarnett, any docs that   |
| explain this?             |
| |  (10:28) <jgarnett>     |
| heh...                    |
| |  (10:28) <moovida>      |
| |image5|                  |
| |  (10:28) <jgarnett> I   |
| can do a web search same  |
| as you - let me start     |
| while we go on to the     |
| next section.             |
| |  (10:29) <jgarnett>     |
| (aside blog rant about    |
| working together here:    |
| http://weblogs.java.net/b |
| log/jive/archive/2007/06/ |
| java_gis_so_why.html)     |
| |  (10:29) \* ozzicle has |
| joined #udig              |
| |  (10:29) <acuster> or   |
| could we build an example |
| plugin                    |
| |  (10:29) <acuster> and  |
| 3 fragments               |
| |  (10:30) <acuster> that |
| did nothing but show the  |
| layout                    |
| |  (10:30) <jgarnett>     |
| cool -                    |
| http://wiki.eclipse.org/i |
| ndex.php/Skype_Provider   |
| |  (10:30) <jgarnett>     |
| (the above page shows a   |
| skype provider; but talks |
| about the native code     |
| fragment for windows -    |
| good example)             |
| |  (10:31) <jgarnett>     |
| http://www.eclipsezone.co |
| m/eclipse/forums/t94424.h |
| tml                       |
| has                       |
| |  (10:31) <jgarnett>     |
| Bundle-NativeCode:        |
| |  (10:31) <jgarnett>     |
| libjinput-osx.jnilib;     |
| liblwjgl.jnilib;          |
| openal.dylib;             |
| |  (10:31) <jgarnett>     |
| osname=macosx;processor=x |
| 86                        |
| |  (10:31) <jgarnett> so  |
| it looks like there is    |
| lots of examples around.  |
| |  (10:32) <acuster> ok   |
| |  (10:33) <moovida> so   |
| now we just need a        |
| volunteer to create a     |
| template?                 |
| |  (10:33) <jgarnett>     |
| http://www.macrobug.com/b |
| log/2006/12/18/writing-an |
| -eclipse-plug-in-that-use |
| s-native-code-via-jni/    |
| |  (10:33) <moovida>      |
| |image6|                  |
| |  (10:34) <jgarnett> wow |
| that one is good; makes   |
| use of the CDT and a Java |
| to C script               |
| |  (10:34) <jgarnett>     |
| (wild)                    |
| |  (10:34) <moovida>      |
| alright Jody, you         |
| persuaded me              |
| |  (10:34) <jgarnett>     |
| huh?                      |
| |  (10:34) <moovida> I    |
| volunteer!                |
| |  (10:34) <jgarnett> ha  |
| ha                        |
| |  (10:34) <jgarnett>     |
| sweet                     |
| |  (10:34) <moovida>      |
| |image7|                  |
| |  (10:34) <jgarnett> 3)  |
| trunk style               |
| |  (10:34) <jgarnett> um  |
| chorner - do we have any  |
| time to hack on this?     |
| |  (10:35) <chorner>      |
| heh... eventually         |
| |  (10:35) <jgarnett>     |
| Does anyone else want to  |
| play - or even review     |
| code?                     |
| |  (10:35) <acuster>      |
| moovida, I'll try         |
| following your            |
| instructions and see if I |
| can figure them out       |
| |image8|                  |
| |  (10:35) <acuster> what |
| is "trunk style" ?        |
| |  (10:35) <jgarnett> the |
| style editor              |
| |  (10:35) <acuster> the  |
| 'style' widget            |
| |  (10:35) <jgarnett> is  |
| not working on trunk      |
| |  (10:35) <acuster> ah,  |
| the editor                |
| |  (10:36) <jgarnett>     |
| because we changed some   |
| geotools function names   |
| |  (10:36) <acuster> the  |
| view is dead?             |
| |  (10:36) <chorner>      |
| well, just "theme" is     |
| broken                    |
| |  (10:36) <jgarnett> so  |
| now rather than doing one |
| function call to get the  |
| classification of a       |
| feature                   |
| |  (10:36) <jgarnett> we  |
| need to do two            |
| |  (10:36) <jgarnett> one |
| function call on the      |
| feature collection (to    |
| figure out how many       |
| categories there are)     |
| |  (10:36) <jgarnett> and |
| another one on the        |
| feature itself; to see    |
| which category it falls   |
| into.                     |
| |  (10:37) <jgarnett> I   |
| think for the style theme |
| page                      |
| |  (10:37) <jgarnett> we  |
| just need to figure out   |
| the classification        |
| "buckets" right chorner?  |
| |  (10:37) <chorner> not  |
| quite... we're just       |
| switching to the new api  |
| |  (10:38) <chorner> in   |
| geotools                  |
| |  (10:38) <jgarnett> so  |
| I should not be scared to |
| start - just jump in and  |
| ask questions?            |
| |  (10:38) <acuster>      |
| which new api?            |
| |  (10:38) <acuster>      |
| filter?                   |
| |  (10:38) <chorner>      |
| classification function   |
| |  (10:38) <jgarnett>     |
| thinking how to explain   |
| |  (10:38) <acuster> or   |
| SimpleFeature?            |
| |  (10:38) <jgarnett>     |
| some of the               |
| classification functions  |
| in geotools used to run   |
| on Feature and            |
| FeatureCollection         |
| |  (10:38) <jgarnett>     |
| which was a mistake       |
| |  (10:38) \* moovida is  |
| very sorry but has to     |
| run... please post the    |
| logs, talk to you all in  |
| ml... thanks              |
| |  (10:39) <jgarnett> now |
| they only run on          |
| FeatureCollection.        |
| |  (10:39) <acuster> ciao |
| |  (10:39) <chorner> cya  |
| |  (10:39) <jgarnett> no  |
| worries! ciao             |
| |  (10:39) <moovida>      |
| ciao, thanks              |
| |  (10:39) <acuster> ok   |
| |  (10:39) \* moovida     |
| fading...                 |
| |  (10:39) \* moovida has |
| left #udig                |
| |  (10:39) <jgarnett> The |
| reason I keep bringing    |
| this up - it is the only  |
| thing that is not working |
| on trunk.                 |
| |  (10:39) <jgarnett> as  |
| soon as it is fixed       |
| |  (10:40) <jgarnett> I   |
| can recommend trunk to    |
| people who want to do RnD |
| - like the SoC students.  |
| |  (10:40) <jgarnett>     |
| Also soon as it is fixed  |
| we can make a release     |
| available so you can all  |
| see how much improved the |
| performance is.           |
| |  (10:40) <jgarnett>     |
| (end rant)                |
| |  (10:40) <acuster> are  |
| patches to 1.1 going to   |
| trunk as well?            |
| |  (10:41) <chorner> they |
| are behind                |
| |  (10:41) <chorner> i've |
| been tracking them and    |
| have a queue to push      |
| forward                   |
| |  (10:41) <jgarnett>     |
| Patches to trunk are back |
| ported to 1.1.            |
| |  (10:41) <jgarnett>     |
| trunk is patched first by |
| Jesse or myself.          |
| |  (10:41) <acuster> ok   |
| |  (10:41) <jgarnett> I   |
| think a few others have   |
| just been patching 1.1    |
| (eek!)                    |
| |  (10:42) <chorner> i    |
| might have a little       |
| volunteer time next week, |
| so we'll see              |
| |  (10:42) <acuster>      |
| next?                     |
| |  (10:42) <jgarnett> 5)  |
| org.udig                  |
| |  (10:43) <chorner>      |
| backup...                 |
| |  (10:43) <acuster> (4)  |
| |  (10:43) <jgarnett>     |
| (docs here                |
| http://docs.codehaus.org/ |
| display/GEOTOOLS/Upgrade+ |
| to+2.4#Upgradeto2.4-class |
| ify)                      |
| |  (10:43) <acuster> cool |
| |  (10:43) <jgarnett> 4)  |
| postgis import            |
| |  (10:43) <acuster> I    |
| submitted a patch to      |
| Jesse, which has slipped  |
| to rgould, who forgot     |
| about it                  |
| |  (10:44) <acuster>      |
| which means by the time   |
| anyone looks at it, I     |
| will have forgotten the   |
| code for 1.5+ months      |
| |  (10:44) <acuster> is   |
| there any way we can get  |
| some tighter feedback?    |
| |  (10:44) <acuster> one  |
| of the issues that will   |
| come up, is that I have   |
| changed the contract      |
| |  (10:44) <rgould> idea  |
| |  (10:44) <rgould>       |
| schedule an irc session?  |
| |  (10:44) <acuster>      |
| between db's extending    |
| the abstract class        |
| |  (10:45) <rgould> to do |
| the code review?          |
| |  (10:45) <acuster>      |
| sorry                     |
| |  (10:45) <acuster>      |
| between the db and the    |
| abstract class            |
| |  (10:45) <rgould> the   |
| AbstractDataStoreWizard?  |
| |  (10:45) <rgould> the   |
| AbstractDataStoreWizardPa |
| ge                        |
| rather?                   |
| |  (10:45) <acuster> I'm  |
| guessing this will bring  |
| a lot of issues from      |
| anyone with a proprietary |
| db                        |
| |  (10:45) <acuster>      |
| sounds right              |
| |  (10:45) \* acuster     |
| doesn't have any of this  |
| in his head               |
| |  (10:46) <acuster> the  |
| issue is how extendors    |
| can build on the event    |
| model of the abstract     |
| class                     |
| |  (10:46) <acuster> this |
| was never defined         |
| formally,                 |
| |  (10:46) <acuster> and  |
| I think no one understood |
| it                        |
| |  (10:46) <rgould>       |
| sounds accurate           |
| |  (10:46) <acuster>      |
| since the layers of hacks |
| seem to indicate people   |
| were just getting things  |
| to work                   |
| |  (10:46) <acuster> so   |
| **who** will be able to   |
| make the call?            |
| |  (10:47) <jgarnett> me  |
| |  (10:47) <jgarnett> I   |
| took over the catalog     |
| module; so I will make    |
| the call.                 |
| |  (10:47) <jgarnett>     |
| However there is a cost - |
| I document the resulting  |
| api and then we stick     |
| with it.                  |
| |  (10:47) <acuster>      |
| great                     |
| |  (10:47) <jgarnett>     |
| acuster can we go over    |
| your patch together; make |
| sure the javadocs of the  |
| super class are solid and |
| understood between us.    |
| |  (10:48) <jgarnett> and |
| then write up the result. |
| |  (10:48) <acuster>      |
| there was **no**          |
| documentation, which was  |
| 80% of the issue          |
| |  (10:48) <acuster> I've |
| added a lot of javadoc    |
| |  (10:48) <jgarnett> If  |
| we cannot document it     |
| then it **is** broken.    |
| |  (10:48) <acuster> but  |
| it would be good to have  |
| someone read it and see   |
| if they could write a     |
| myOwnDBWizardPage         |
| |  (10:49) <acuster>      |
| there are also some       |
| spelling issues Database  |
| vs. DataBase which are    |
| inconssistent             |
| |  (10:49) <acuster>      |
| across the codebase       |
| |  (10:49) <jgarnett>     |
| okay                      |
| |  (10:49) <acuster>      |
| which makes for needless  |
| headaches                 |
| |  (10:49) <acuster> so   |
| how do we proceed?        |
| |  (10:49) <jgarnett>     |
| this would be a case of   |
| fix on trunk; and back    |
| port the changes.         |
| |  (10:49) <acuster> back |
| port if you want          |
| |  (10:49) <jgarnett>     |
| well I check out trunk    |
| (done) and start up my    |
| workspace (done)          |
| |  (10:49) <acuster> my   |
| patches were against      |
| trunk iirc                |
| |  (10:49) <jgarnett> the |
| next step is after the    |
| IRC meeting you will help |
| me apply the patch.       |
| |  (10:50) <acuster> ok   |
| |  (10:50) <acuster>      |
| next?                     |
| |  (10:50) <jgarnett>     |
| From there we can take    |
| discussion to email; and  |
| when we have things       |
| figured out on email we   |
| will write it down in the |
| wiki                      |
| |  (10:50) <acuster>      |
| great                     |
| |  (10:50) <jgarnett>     |
| (even if we just cut and  |
| paste the good parts of   |
| the email discussion for  |
| a first draft)            |
| |  (10:50) <jgarnett> 5)  |
| org.udig namespace        |
| |  (10:50) <jgarnett> one |
| problem with that -       |
| http://udig.org/          |
| |  (10:51) <chorner> hehe |
| |  (10:51) <jgarnett>     |
| Serving Flathead and      |
| Lincoln Counties Since    |
| 1977 !                    |
| |  (10:51) <jgarnett>     |
| it's the Law to Call      |
| before you Dig!           |
| |  (10:51) <jgarnett>     |
| (sweet!)                  |
| |  (10:51) <acuster>      |
| udig.net is free          |
| |  (10:52) <acuster> the  |
| issue is that core        |
| funcationality is being   |
| built by different groups |
| |  (10:52) <jgarnett> So  |
| the question is do we buy |
| it                        |
| |  (10:52) <jgarnett> or  |
| stick with                |
| udig.refractions.net      |
| |  (10:52) <acuster> and  |
| collaborating in the      |
| es.axios.udig namespace   |
| feels funny               |
| |  (10:52) <chorner>      |
| udig.net is not free      |
| |  (10:52) <jgarnett>     |
| usually projects stick    |
| with their sponsor (most  |
| of the reason for a       |
| consulting company to do  |
| this stuff is to see      |
| their name in the url)    |
| |  (10:52) <chorner>      |
| "David Frankland" seems   |
| to own it                 |
| |  (10:53) <acuster> so   |
| does the naming solution  |
| become "first group to do |
| serious effort in one     |
| direction gets to name    |
| the plugin space"?        |
| |  (10:53) <jgarnett>     |
| Admitted like             |
| http://geoserver.org/ is  |
| on the ball; but the code |
| base still says topp.     |
| |  (10:53) <jgarnett> I   |
| see your point acuster    |
| |  (10:53) <jgarnett>     |
| thinking                  |
| |  (10:53) \* acuster     |
| just wonders what the     |
| result will be like in 3  |
| years time                |
| |  (10:53) <chorner>      |
| com.johnsmith.fruit.cart  |
| |  (10:53) <jgarnett>     |
| well when code is punted  |
| into the core project     |
| |  (10:54) <jgarnett> we  |
| are supposed to stick the |
| (c) headers on top        |
| |  (10:54) <jgarnett> (do |
| the ip review etc...)     |
| |  (10:54) <jgarnett> and |
| punt it into a            |
| net.refractions.udig      |
| package                   |
| |  (10:54) <acuster> what |
| refractions gets (c) over |
| axios' work?              |
| |  (10:54) <jgarnett> but |
| we did not do that for    |
| the bookmark plugin -     |
| perhaps we should.        |
| |  (10:55) <jgarnett>     |
| Same way TOPP gets (c)    |
| over refractions work,    |
| and axios work.           |
| |  (10:55) \* acuster was |
| thinking that **all**     |
| plugins could be ported   |
| to the core namespace     |
| when they were stable and |
| well documented           |
| |  (10:55) <chorner> i    |
| don't know if that will   |
| fly                       |
| |  (10:55) <jgarnett> I   |
| see your point.           |
| |  (10:55) <chorner>      |
| (stealing (c))            |
| |  (10:55) \* acuster     |
| doesn't really care who   |
| gets (c) just that we     |
| have a policy that is     |
| clear                     |
| |  (10:55) <jgarnett>     |
| From my persepective I    |
| want to see (c) Axiois on |
| the file with the date    |
| (we need the tracibility  |
| just like we have with    |
| the geotools project)     |
| |  (10:56) <acuster> the  |
| way gnumeric does it, is  |
| everyone who works on a   |
| file gets a joint (c) on  |
| that file                 |
| |  (10:56) <jgarnett>     |
| stealing (c) ~= donating  |
| code as long as the       |
| person with (c) is in the |
| loop.                     |
| |  (10:56) <acuster> this |
| is what the US legal      |
| system actually imposes   |
| |  (10:56) <jgarnett> I   |
| see - by person. For      |
| those working for an      |
| organization              |
| |  (10:56) <acuster> you  |
| don't even need to have a |
| written (c) to have       |
| copyright                 |
| |  (10:56) <jgarnett> the |
| organization is listed.   |
| |  (10:57) <acuster> yeah |
| |  (10:57) <chorner> it   |
| sounds like we are        |
| staying with the          |
| net.refractions.udig      |
| namespace?                |
| |  (10:57) <chorner> and  |
| imposing it upon modules  |
| as they "graduate"?       |
| |  (10:57) <acuster> so   |
| perhaps this is something |
| refractions could         |
| decide/document           |
| |  (10:58) <chorner> i    |
| wonder what jgrass thinks |
| |  (10:58) <acuster>      |
| would you then need a     |
| copyright assignment      |
| document? |image9|        |
| (shades of geotools)      |
| |  (10:59) <jgarnett> oh  |
| I see                     |
| |  (10:59) <jgarnett>     |
| just a sec                |
| |  (11:00) <jgarnett> I   |
| do not mind (actually I   |
| love) the idea of         |
| shipping large community  |
| modules built around      |
| specific topics. The      |
| license can even be GPL   |
| (just so long as it is    |
| **in** the plugin)        |
| |  (11:00) <jgarnett>     |
| THis is how we would host |
| a JUMP or gvSIG data      |
| model                     |
| |  (11:00) <jgarnett> and |
| I think it is how JGrass  |
| is planning on going      |
| right? GPL ?              |
| |  (11:00) <chorner> yup  |
| |  (11:01) <jgarnett> For |
| the core SDK we would     |
| like to stick with LGPL - |
| for two reasons.          |
| |  (11:01) <acuster>      |
| yeah, I like the          |
| distributed nature of     |
| things, but it does imply |
| that we agree who's       |
| project is responsible    |
| for what                  |
| |  (11:01) <jgarnett> it  |
| lets any organization be  |
| assured that they can     |
| pick up the SDK; build    |
| what they need; and keep  |
| on walking.               |
| |  (11:01) <acuster> so   |
| that there are clear      |
| boundaries                |
| |  (11:01) <jgarnett>     |
| Maybe in a years time we  |
| can have GPL+CLASSPATH    |
| Exception with the same   |
| confidence.               |
| |  (11:01) <acuster>      |
| axios is working on       |
| "editing tools" for       |
| example                   |
| |  (11:01) <jgarnett> The |
| other modules; are        |
| optional - and are        |
| available via the update  |
| site.                     |
| |  (11:02) <jgarnett> If  |
| organizations are happy   |
| with GPL they can pick up |
| JGrass etc?               |
| |  (11:02) <acuster>      |
| which sounds like they    |
| are close to what already |
| exists                    |
| |  (11:02) <jgarnett>     |
| Indeed - in some cases    |
| they are finishing work   |
| we cut from scope.        |
| |  (11:02) <acuster> and  |
| others are making noises  |
| about similar             |
| functionality             |
| |  (11:02) <jgarnett> For |
| this kind of stuff I      |
| would beg people to       |
| donate it to the core     |
| udig project              |
| |  (11:03) <jgarnett> we  |
| would do a code review    |
| |  (11:03) <jgarnett> and |
| stick it in a             |
| net.refractions.udig      |
| plugin                    |
| |  (11:03) <acuster>      |
| meeting time is over.     |
| |  (11:03) <jgarnett>     |
| they would still have (c) |
| up near the top; but so   |
| would (c) refractions.    |
| And the license would be  |
| LGPL.                     |
| |  (11:03) <acuster> I    |
| suggest refractions needs |
| to decide what they want  |
| to do                     |
| |  (11:03) <jgarnett> If  |
| they are uncomfortable    |
| with that - all is not    |
| lost. But we may end up   |
| duplicating their work in |
| core.                     |
| |  (11:04) <jgarnett> The |
| decision should already   |
| be in the developers      |
| guide                     |
| |  (11:04) <jgarnett>     |
| (going to look now)       |
| |  (11:04) <acuster> I    |
| think we will be          |
| comforatable with         |
| anything                  |
| |  (11:04) <acuster> oh,  |
| my fault then             |
| |  (11:04) <acuster>      |
| jgarnett,                 |
| http://jira.codehaus.org/ |
| browse/UDIG-1264          |
| for my patches            |
| |  (11:04) <jgarnett>     |
| Sorry it is in the        |
| project guide             |
| |  (11:05) <jgarnett>     |
| http://udig.refractions.n |
| et/confluence/pages/viewp |
| age.action?pageId=7433    |
| |  (11:05) <jgarnett> I   |
| will clean up the page.   |
| (action item as result of |
| this meeting)             |
| |  (11:07) <jgarnett> The |
| guidelines have improved  |
| since I wrote them        |
| |  (11:07) <jgarnett>     |
| Jesse now wants find bugs |
| run on code               |
| contributions.            |
+---------------------------+---------------------------+---------------------------+---------------------------+

+-------------+----------------------------------------------------------+
| |image11|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/smile.gif
.. |image1| image:: images/icons/emoticons/biggrin.gif
.. |image2| image:: images/icons/emoticons/biggrin.gif
.. |image3| image:: images/icons/emoticons/smile.gif
.. |image4| image:: images/icons/emoticons/smile.gif
.. |image5| image:: images/icons/emoticons/smile.gif
.. |image6| image:: images/icons/emoticons/biggrin.gif
.. |image7| image:: images/icons/emoticons/biggrin.gif
.. |image8| image:: images/icons/emoticons/smile.gif
.. |image9| image:: images/icons/emoticons/smile.gif
.. |image10| image:: images/border/spacer.gif
.. |image11| image:: images/border/spacer.gif
