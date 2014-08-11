Irc Meeting - 15 March 2007
###########################

+-----------------------------------+-----------------------------------+-----------------------------------+
| Community Plugins : IRC Meeting - |
| 15 March 2007                     |
| This page last changed on Mar 08, |
| 2010 by jgarnett.                 |
| | (11:06) \* Jesse\_Eichar77      |
| changes topic to '1) uDig         |
| 1.1.RC10 Release 2) udig sdk      |
| loads tiffs, windows no 3) R      |
| connection 4) Live CD'            |
| |  (11:06) <Jesse\_Eichar77> 1)   |
| uDig 1.1.RC10 Release             |
| |  (11:06) <Jesse\_Eichar77> One  |
| of the catalogs that our search   |
| plugin uses has been shut down.   |
| |  (11:07) <Jesse\_Eichar77> so   |
| we need to make a new uDig        |
| release that doesn't have that    |
| plugin                            |
| |  (11:07) <Jesse\_Eichar77> also |
| I like to occasionally get        |
| releases out.                     |
| |  (11:07) <moovida> which one is |
| that?                             |
| |  (11:07) <Jesse\_Eichar77> the  |
| CGDI                              |
| |  (11:07) <chorner> what about   |
| the 1.0.x series?                 |
| |  (11:08) <Jesse\_Eichar77>      |
| trying to decide.                 |
| |  (11:08) <Jesse\_Eichar77>      |
| might just strant them            |
| |  (11:09) <Jesse\_Eichar77>      |
| Maybe magna can comment on that   |
| since I think they are the only   |
| ones still using it.              |
| |  (11:09) \* moovida wondering   |
| what strant means, his dictionary |
| doesn't report about it |image19| |
| |  (11:09) <Jesse\_Eichar77>      |
| strant=strand                     |
| |  (11:09) <Jesse\_Eichar77>      |
| |image20|                         |
| |  (11:09) <moovida> ok |image21| |
| |  (11:09) <magna> hello          |
| |  (11:09) <Jesse\_Eichar77> The  |
| search is pretty terrible on      |
| 1.0.x any how it was horribly     |
| slow and painful                  |
| |  (11:10) <Jesse\_Eichar77> Hi   |
| Magna                             |
| |  (11:10) <magna> i ve just read |
| the mails about connection with R |
| |  (11:10) <magna> i didn't know  |
| you were interested in            |
| |  (11:10) <moovida> connection   |
| with R, you mean?                 |
| |  (11:10) <magna> yes            |
| |  (11:10) <Jesse\_Eichar77>      |
| we're talking about R connection  |
| in a little while can we continue |
| that then magna?                  |
| |  (11:11) <moovida> we are VERY  |
| interested                        |
| |  (11:11) <Jesse\_Eichar77>      |
| Right nowwe're talking about a    |
| catalog search uses that is being |
| dropped.                          |
| |  (11:11) <magna> ok             |
| |  (11:11) <moovida> right        |
| |  (11:11) <Jesse\_Eichar77> it   |
| will cause the search on 1.0.x to |
| hang (and the entire app)         |
| |  (11:11) <Jesse\_Eichar77> The  |
| fix is to remove the plugin from  |
| the build.                        |
| |  (11:11) <Jesse\_Eichar77> and  |
| it will work again.               |
| |  (11:12) <Jesse\_Eichar77> I am |
| going to do a new release of      |
| 1.1.x                             |
| |  (11:12) <Jesse\_Eichar77> do   |
| you need a new release for 1.0.x? |
| |  (11:12) <Jesse\_Eichar77>      |
| magna?                            |
| |  (11:13) <moovida> |image22|    |
| |  (11:13) <magna> yes, if it is  |
| possible                          |
| |  (11:13) <Jesse\_Eichar77> ok.  |
| we can                            |
| |  (11:13) <magna> i mean, 1.1.X  |
| |  (11:13) <Jesse\_Eichar77> so a |
| 1.1-RC9 and a 1.0.7               |
| |  (11:14) <Jesse\_Eichar77> oh.  |
| so you don't need a 1.0.7?        |
| |  (11:14) <Jesse\_Eichar77> we   |
| are definately going to make a    |
| 1.1-RC1-                          |
| |  (11:14) <Jesse\_Eichar77> rc10 |
| |  (11:14) <magna> right but i am |
| using also 1.1.x                  |
| |  (11:15) <moovida> great, so we |
| can build against that...         |
| |  (11:15) <moovida> yo making    |
| also a sdk?                       |
| |  (11:15) <Jesse\_Eichar77> ok   |
| we'll make both. A release        |
| includes an SDK.                  |
| |  (11:15) <Jesse\_Eichar77>      |
| usually a day later but it does   |
| include it.                       |
| |  (11:15) <moovida> why is the   |
| sdk striked through on the wiki?  |
| |  (11:15) <Jesse\_Eichar77> ? I  |
| don't know                        |
| |  (11:16) <moovida> one second   |
| |  (11:16) <Jesse\_Eichar77> Oh   |
| those are the nightly builds      |
| |  (11:16) <Jesse\_Eichar77> they |
| are broken right now.             |
| |  (11:16) <Jesse\_Eichar77> FYI  |
| I have a meeting in 15 minutes so |
| someone will have to take over    |
| the meeting then.                 |
| |  (11:16) <moovida> yes, I see,  |
| nightly builds                    |
| |  (11:17) <Jesse\_Eichar77> ok   |
| 2) sdk loads tiffs, windows no    |
| |  (11:17) <Jesse\_Eichar77>      |
| you're up moovida                 |
| |  (11:17) <moovida> anyone       |
| experienced the problem?          |
| |  (11:17) <moovida> On my linux: |
| |  (11:17) <moovida> - I can load |
| tiffs and jpgs                    |
| |  (11:17) <moovida> - my icons a |
| shitty                            |
| |  (11:18) <moovida> - the        |
| selection box has no transparency |
| |  (11:18) <moovida> on windows:  |
| |  (11:18) <moovida> - tiffs -    |
| jpgs and something else I can't   |
| remember do not load              |
| |  (11:18) <moovida> any idea?    |
| |  (11:18) <moovida> - my icons a |
| shitty = - my cursors a shitty    |
| |  (11:19) <moovida> sorry        |
| |  (11:19) <Jesse\_Eichar77>      |
| linux: icons are shitty because   |
| we made a mistake back in the day |
| to only use a single bitmap for   |
| cursor.                           |
| |  (11:19) <Jesse\_Eichar77> it   |
| is just work that needs to be     |
| done but haven't got around to    |
| doing                             |
| |  (11:20) <Jesse\_Eichar77>      |
| selection box has no transparency |
| because you are using the "bad"   |
| cairo. If you have the newest or  |
| 1.2 (I think) you can have it.    |
| |  (11:20) <Jesse\_Eichar77>      |
| windows...                        |
| |  (11:20) <moovida> cairo is     |
| what?                             |
| |  (11:20) <Jesse\_Eichar77>      |
| cairo is the library that SWT     |
| uses for drawing                  |
| |  (11:20) <Jesse\_Eichar77> (on  |
| linux)                            |
| |  (11:20) <moovida> alright,     |
| I'll check                        |
| |  (11:20) <moovida> I had a      |
| similar problem                   |
| |  (11:21) <moovida> and it was   |
| because the color instance was    |
| created with integers instead of  |
| with floats                       |
| |  (11:21) <Jesse\_Eichar77>      |
| there was one version of cairo    |
| that broke SWT. The newer version |
| works better from what I hear.    |
| Haven't personally tested it.     |
| |  (11:21) <moovida> is           |
| |  (11:21) <Jesse\_Eichar77>      |
| That's not the problem here.      |
| |  (11:21) <Jesse\_Eichar77> If   |
| you use Fedora Core 5 it works.   |
| |  (11:21) <moovida> does that    |
| solve the first warning with the  |
| accelleration?                    |
| |  (11:22) \* pombreda has quit   |
| IRC (Connection timed out)        |
| |  (11:22) <Jesse\_Eichar77> that |
| is exactly why we added that      |
| warning                           |
| |  (11:22) <moovida> perfect!     |
| |  (11:22) <Jesse\_Eichar77>      |
| Windows I'm not sure about.       |
| |  (11:22) <moovida> when did you |
| think to release?                 |
| |  (11:22) <Jesse\_Eichar77> It   |
| worked last I checked.            |
| |  (11:22) <Jesse\_Eichar77>      |
| friday will be the sanity check   |
| release                           |
| |  (11:23) <moovida> i.e.?        |
| |  (11:23) <Jesse\_Eichar77> then |
| monday the official release if    |
| there aren't any major outcries.  |
| |  (11:23) <Jesse\_Eichar77>      |
| (Tiffs and JPegs worked last I    |
| checked)                          |
| |  (11:23) <moovida> alright,     |
| good. Let's say I try that        |
| release                           |
| |  (11:23) <Jesse\_Eichar77> In   |
| fact richard is working on trunk  |
| loading tiffs and jpegs in the    |
| last few days.                    |
| |  (11:23) <moovida> when it      |
| comes and do some better problem  |
| tracking if there is one          |
| |  (11:23) <Jesse\_Eichar77>      |
| GeoTiffs I'd have to test again.  |
| |  (11:24) <Jesse\_Eichar77> I    |
| agree.                            |
| |  (11:24) <Jesse\_Eichar77> 3) R |
| Connection                        |
| |  (11:24) <Jesse\_Eichar77>      |
| Moovida and magna                 |
| |  (11:24) <Jesse\_Eichar77>      |
| moovida could you take over. I    |
| have to run.                      |
| |  (11:24) <moovida> yes, ciao    |
| Jesse                             |
| |  (11:24) <Jesse\_Eichar77>      |
| Hopefully I'll be back before the |
| end of the hour but otherwise...  |
| ciao                              |
| |  (11:24) <moovida> Magna, you   |
| are interested in R connection?   |
| |  (11:25) <moovida> Jesse        |
| alright, later then               |
| |  (11:25) <moovida> Magna, you   |
| still there?                      |
| |  (11:25) \* Jesse\_Eichar77\_   |
| has joined #udig                  |
| |  (11:27) <rgould> not trunk,    |
| 1.1.x |image23|                   |
| |  (11:28) <moovida> Richard?     |
| what do you mean?                 |
| |  (11:28) <rgould> (jesse said I |
| was working on trunk, but I am    |
| infact on 1.1.x)                  |
| |  (11:28) <Jesse\_Eichar77\_> oh |
| did I?                            |
| |  (11:28) <Jesse\_Eichar77\_> my |
| bad sorry                         |
| |  (11:28) <rgould> |image24|     |
| |  (11:28) <moovida> ah |image25| |
| |  (11:29) <moovida> |image26| I  |
| feel this IRC won't last much     |
| longer... the last about life CD  |
| if the thing I started in         |
| mailinglist, I guess?             |
| |  (11:29) <Jesse\_Eichar77\_>    |
| sure lets do that                 |
| |  (11:29) <Jesse\_Eichar77\_> 4) |
| Live CD                           |
| |  (11:29) <moovida> you are here |
| Jesse?                            |
| |  (11:29) <moovida> meeting      |
| over?                             |
| |  (11:29) <Jesse\_Eichar77\_>    |
| Call hasn't come in yet           |
| |  (11:29) <Jesse\_Eichar77\_> I  |
| will drop out suddently           |
| |  (11:29) <moovida> ok           |
| |  (11:30) <Jesse\_Eichar77\_> I  |
| will try to do the relative path  |
| stuff.                            |
| |  (11:30) <Jesse\_Eichar77\_>    |
| but I'm pretty limited for time   |
| especially since we need to do    |
| the release.                      |
| |  (11:30) \* pombreda has joined |
| #udig                             |
| |  (11:30) <moovida> right now I  |
| am going through the eclipse RCP  |
| book                              |
| |  (11:30) <Jesse\_Eichar77\_> ah |
| it is a very useful book          |
| |  (11:30) <moovida> in order to  |
| be able to help out a bit more    |
| indeep in future                  |
| |  (11:31) <moovida> it is indeed |
| |image27|                         |
| |  (11:31) <moovida> some things  |
| in udig are getting clearer       |
| |  (11:31) <Jesse\_Eichar77\_>    |
| any other issues you have         |
| encountered in your quest for a   |
| Live CD?                          |
| |  (11:31) <Jesse\_Eichar77\_> or |
| for your uDig Lite?               |
| |  (11:31) <moovida> I feel we    |
| should do something like the      |
| following:                        |
| |  (11:32) <moovida> have the     |
| possibility to start udig from cd |
| with a dataset on cd              |
| |  (11:32) <moovida> but have a   |
| button that starts installation   |
| on the pc                         |
| |  (11:32) <moovida> then that    |
| would be a nice lifecd            |
| |  (11:33) <moovida> since udig   |
| is all inside a folder, this      |
| should be fairly easy             |
| |  (11:33) <Jesse\_Eichar77\_>    |
| That would be. pretty doable.     |
| |  (11:33) <Jesse\_Eichar77\_>    |
| only hangup I think is the        |
| relative path problem. Anything   |
| else?                             |
| |  (11:34) <moovida> for now I    |
| don't know, I will go further     |
| into it, I didn't try that one    |
| you said in ml                    |
| |  (11:34) <moovida> with the     |
| %home% variable                   |
| |  (11:34) <Jesse\_Eichar77\_> oh |
| right.                            |
| |  (11:34) <moovida> but I would  |
| need the relative paths first     |
| |  (11:34) <moovida> I tried to   |
| make a folder readonly on disk    |
| |  (11:35) <moovida> but it       |
| didn't behave as expected         |
| |  (11:35) <moovida> windows does |
| follow the rules only in part     |
| |  (11:35) <Jesse\_Eichar77\_>    |
| See what I can do about that. For |
| now why don't you do the other    |
| stuff. And we'll see how much     |
| progress I make on that issue.    |
| |  (11:36) <moovida> Yes, I will  |
| work on the raster loader by      |
| viewregion                        |
| |  (11:36) <moovida> that is the  |
| only part that seems to need      |
| coding                            |
| |  (11:37) \* jgarnett has joined |
| #udig                             |
| |  (11:37) <Jesse\_Eichar77\_>    |
| ok.                               |
| |  (11:37) <Jesse\_Eichar77\_>    |
| then we can wrap this up...       |
| |  (11:37) <moovida> ok, anything |
| else?                             |
| |  (11:37) <Jesse\_Eichar77\_> oh |
| 5)                                |
| |  (11:37) <Jesse\_Eichar77\_>    |
| meeting time                      |
| |  (11:37) <Jesse\_Eichar77\_>    |
| how is this time for you          |
| |  (11:37) <moovida> ah, yes      |
| |  (11:38) <Jesse\_Eichar77\_>    |
| would you like 10 better?         |
| |  (11:38) <moovida> 10?          |
| |  (11:38) <Jesse\_Eichar77\_>    |
| sorry my time |image28|           |
| |  (11:38) <moovida> wasn't it 10 |
| at your place?                    |
| |  (11:38) <Jesse\_Eichar77\_> no |
| it was 11....                     |
| |  (11:38) <Jesse\_Eichar77\_> oh |
| right daylight savings            |
| |  (11:38) <Jesse\_Eichar77\_>    |
| bleh                              |
| |  (11:38) <moovida> yes, I was   |
| going to ask                      |
| |  (11:38) <Jesse\_Eichar77\_> is |
| the time good? is an hour earlier |
| better?                           |
| |  (11:39) <moovida> in two weeks |
| the google calendar gives me 20   |
| |  (11:39) <magna> one moment     |
| |  (11:39) <jgarnett> (aside:     |
| bring on the students             |
| http://groups.google.com/group/go |
| ogle-summer-of-code-announce/web/ |
| guide-to-the-gsoc-web-app-for-stu |
| dent-applicants                   |
| )                                 |
| |  (11:39) <Jesse\_Eichar77\_>    |
| sure                              |
| |  (11:39) <moovida> by that 19   |
| would be better for me            |
| |  (11:39) <Jesse\_Eichar77\_> ok |
| |  (11:39) <moovida> which is...  |
| don't know for you |image29|      |
| |  (11:39) <Jesse\_Eichar77\_>    |
| doesn't matter too much for me on |
| thursdays                         |
| |  (11:40) <Jesse\_Eichar77\_>    |
| lets send an email to the ml      |
| |  (11:40) <Jesse\_Eichar77\_>    |
| and see the response.             |
| |  (11:40) <moovida> great        |
| |  (11:40) <Jesse\_Eichar77\_> Ok |
| got to run                        |
| |  (11:40) <Jesse\_Eichar77\_>    |
| ciao                              |
| |  (11:40) <moovida> yup          |
| |  (11:40) <moovida> ciao         |
| |  (11:40) \* Jesse\_Eichar77\_   |
| has quit IRC                      |
| |  (11:40) <moovida> Jody         |
| |  (11:40) <moovida> sorry, first |
| Magna                             |
| |  (11:40) <moovida> Magna, go    |
| |  (11:40) <magna> hello, im      |
| back, ok where were we?           |
| |  (11:41) <moovida> |image30|    |
| you wanted to talk about R?       |
| |  (11:41) <moovida> Jody, can    |
| you wait or are you on a hurry?   |
| |  (11:41) \* Jesse\_Eichar77 has |
| quit IRC (Read error: 110         |
| (Connection timed out))           |
| |  (11:42) <moovida> Jody seems   |
| to be busy, Magna let's talk      |
| |  (11:42) <magna> ok             |
| |  (11:42) <moovida> you are      |
| interested in R?                  |
| |  (11:43) <moovida> in which     |
| sense? what kind of analyses?     |
| rasters? rain? vectors?           |
| |  (11:43) <moovida> what is your |
| target?                           |
| |  (11:43) <magna> as i was       |
| saying before, i did not you were |
| interested in R. I was using it   |
| since last year and that was the  |
| first time i heard something      |
| about Rserve                      |
| |  (11:43) <jgarnett> hello?      |
| |  (11:43) <moovida> oh, alright  |
| |  (11:44) <moovida> so tell me,  |
| what do you want to know?         |
| |  (11:44) <moovida> (hi Jody)    |
| |  (11:44) <jgarnett> Oh cool -   |
| yes magna / moovida my best       |
| advice on R is to talk to each    |
| other (and acuster if he is       |
| around)                           |
| |  (11:44) <moovida> Adrian is    |
| not here |image31|                |
| |  (11:45) <magna> hi Jody!, you  |
| owe me that photo in le Chateau   |
| |image32|                         |
| |  (11:45) <magna> ....We were    |
| working at cip with R and then    |
| there was the need to unite it    |
| with a java application           |
| |  (11:45) <magna> so I was using |
| Rserve to make statistical        |
| analysis and display the result   |
| in a java application             |
| |  (11:46) <moovida> yeah, the    |
| same we did, but we had many      |
| connection problems               |
| |  (11:46) <moovida> how did it   |
| work for you?                     |
| |  (11:48) <moovida> Magna? You   |
| there?                            |
| |  (11:48) \* moovida has to      |
| leave in about 10 minutes...      |
| |  (11:48) <magna> yes we had     |
| some problems also but we solve   |
| them by the help of Simon Urbanek |
| |  (11:48) <moovida> oh, I see    |
| |  (11:48) <magna> we solved them |
| with his help                     |
| |  (11:49) <magna> we had to do   |
| some validations adjustments ans  |
| something like that               |
| |  (11:49) <moovida> you were     |
| using the spatial GRASS linked    |
| part for rasters?                 |
| |  (11:50) <moovida> we would     |
| like to implement it like grass   |
| does                              |
| |  (11:50) <moovida> inside a     |
| shell                             |
| |  (11:50) <moovida> in order to  |
| exploit the R-GRASS link          |
| |  (11:50) <moovida> which is     |
| very very good                    |
| |  (11:50) <magna> no we dont use |
| GRASS at the mmoment, but we look |
| forward to use it                 |
| |  (11:50) <magna> that sounds    |
| GOOD, very good                   |
| |  (11:51) <moovida> tomorrow a   |
| developer starts on this, and     |
| will work on it for several       |
| months                            |
| |  (11:51) <moovida> so he will   |
| be in IRC from now on             |
| |  (11:52) <magna> so, will you   |
| use Rserve or other tool?         |
| |  (11:52) <moovida> I think      |
| there is only Rserve for those    |
| needs, right?                     |
| |  (11:52) <magna> as far as i    |
| know                              |
| |  (11:52) <moovida> I don't know |
| how the JGR works                 |
| |  (11:52) <magna> me neither     |
| |  (11:53) <moovida> we will      |
| check those two... but if nothing |
| new was born, Rserver will be the |
| one                               |
| |  (11:53) <moovida> something    |
| more integrated would be          |
| better...                         |
| |  (11:53) <moovida> obviously    |
| |image33|                         |
| |  (11:54) <magna> ok, we are     |
| still using Rserver for some      |
| Stand alone applications, and for |
| some plugins for DIVA             |
| |  (11:54) <magna> we know there  |
| are still limitations on this but |
| as you said a more integrated     |
| tool would be better              |
| |  (11:55) <moovida> we could     |
| have proposed a GSoC for JNI on R |
| |  (11:55) <moovida> a project    |
| for a 24 month summer |image34|   |
| |  (11:55) <magna> so you are     |
| going to start an integration     |
| with R-GRASS and then apply it to |
| uDIG?                             |
| |  (11:56) <moovida> Yes, we will |
| work directly in UDig             |
| |  (11:56) <moovida> first a      |
| command interpret                 |
| |  (11:56) <moovida> and then add |
| R support                         |
| |  (11:56) <moovida> our dream is |
| to come to something like the     |
| mathematica sheets                |
| |  (11:57) <moovida> to be able   |
| to save them and re-edit them     |
| |  (11:57) <moovida> mathematica  |
| = http://www.wolfram.com/         |
| |  (11:57) <moovida> but that     |
| candy is the last thing           |
| |  (11:58) <moovida> let's see    |
| what comes out of it              |
| |  (11:58) <magna> ok, so lets    |
| keep in touch more frequently to  |
| exchange ideas |image35|          |
| |  (11:59) <moovida> yes          |
| |  (11:59) <moovida> in two weeks |
| I should be able to say how we    |
| think to start |image36|          |
| |  (12:00) <magna> ok, right      |
| |  (12:00) <moovida> last         |
| question for Jody, if we are ok,  |
| Magna                             |
| |  (12:00) <moovida> the I have   |
| to run                            |
| |  (12:00) <moovida> Jody?        |
| |  (12:01) <jgarnett> hi          |
| |  (12:01) <jgarnett> hi          |
| |  (12:01) <moovida> hi, very     |
| quickly                           |
| |  (12:01) <jgarnett> yep         |
| |  (12:01) <magna> hi Jody        |
| |  (12:01) <moovida> I got the    |
| GSoC mail                         |
| |  (12:01) <moovida> so we are in |
| it with OSGEO                     |
| |  (12:01) <jgarnett> sweet       |
| |  (12:01) <jgarnett> Now all we  |
| need is students                  |
| |  (12:02) <moovida> very sweet,  |
| but I didn't realize that that    |
| would have been a big one         |
| |  (12:02) <moovida> big one =    |
| big entity                        |
| |  (12:02) <jgarnett> fair enough |
| |  (12:02) <moovida> sonow        |
| students will choose              |
| |  (12:02) <jgarnett> well the    |
| offered to organize and we want   |
| to spend time on udig; so it was  |
| a good match                      |
| |  (12:02) <moovida> yes,         |
| absolutely agreed                 |
| |  (12:03) <moovida> we now wait  |
| and answer questions, if there    |
| are any, right?                   |
| |  (12:03) <jgarnett> correct     |
| |  (12:03) <moovida> that's it    |
| until judgement day?              |
| |  (12:03) <jgarnett> really what |
| will happen is                    |
| |  (12:04) <jgarnett> students    |
| will submit something at the      |
| deadline mark                     |
| |  (12:04) <jgarnett> and we will |
| go through 5 proposals            |
| |  (12:04) <jgarnett> and try and |
| figure out which ones are cool    |
| and if anyone can mentor them     |
| |  (12:04) <chorner> 5?           |
| |  (12:04) <jgarnett> well 5 to   |
| 10 student proposals was what we  |
| had to sort through last year     |
| |  (12:04) <moovida> that means 5 |
| projects can be sponsored at max? |
| |  (12:04) <jgarnett> nope        |
| |  (12:05) <jgarnett> we don't    |
| know what the max will be yet     |
| |  (12:05) <jgarnett> (that is    |
| for google to figure out - since  |
| they are the ones paying the      |
| students for the summer)          |
| |  (12:05) <chorner> i would      |
| expect uDig to get one            |
| |  (12:05) <chorner> "1"          |
| |  (12:05) <jgarnett> we get to   |
| recommend which proposals do not  |
| suck                              |
| |  (12:05) <jgarnett> I am not    |
| sure if we will even get 1        |
| |  (12:05) <jgarnett> but lets    |
| fall off that bridge when we get  |
| to it                             |
| |  (12:05) <jgarnett> I do expect |
| we will reveiw more then 1        |
| |  (12:06) <moovida> we =         |
| geotools-udig-geoserver?          |
| |  (12:06) <moovida> or           |
| |  (12:06) <moovida> we = osgeo   |
| |  (12:07) <jgarnett> here is     |
| some news anouncements on         |
| slashgeo                          |
| |  (12:07) <jgarnett>             |
| http://industry.slashgeo.org/indu |
| stry/07/03/15/1723233.shtml       |
| |  (12:07) <chorner> we = udig    |
| |  (12:07) <chorner> it all       |
| depends how many students google  |
| allocates to osgeo                |
| |  (12:07) <chorner> we got a     |
| student last year, so we are      |
| likely on the bottom of the osgeo |
| pile                              |
| |  (12:07) <jgarnett> the number  |
| of proposals we review depends on |
| how well udig does at attracting  |
| students                          |
| |  (12:07) <chorner> if google    |
| gives us 9, we are happy          |
| |  (12:08) <moovida> |image37| or |
| full of work to mentor them...    |
| |  (12:08) <chorner> ... or was   |
| it 7                              |
| |  (12:08) <chorner> anyhow       |
| |  (12:08) <moovida> yup          |
| |  (12:08) <moovida> alright      |
| |  (12:09) <moovida> if there is  |
| nothing else, I'm gone            |
| |  (12:09) <moovida> ciao, thanks |
| for the time                      |
| |  (12:09) <jgarnett> thanks      |
+-----------------------------------+-----------------------------------+-----------------------------------+

+-------------+----------------------------------------------------------+
| |image39|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/smile.gif
.. |image1| image:: images/icons/emoticons/smile.gif
.. |image2| image:: images/icons/emoticons/biggrin.gif
.. |image3| image:: images/icons/emoticons/smile.gif
.. |image4| image:: images/icons/emoticons/smile.gif
.. |image5| image:: images/icons/emoticons/smile.gif
.. |image6| image:: images/icons/emoticons/biggrin.gif
.. |image7| image:: images/icons/emoticons/smile.gif
.. |image8| image:: images/icons/emoticons/smile.gif
.. |image9| image:: images/icons/emoticons/tongue.gif
.. |image10| image:: images/icons/emoticons/biggrin.gif
.. |image11| image:: images/icons/emoticons/smile.gif
.. |image12| image:: images/icons/emoticons/sad.gif
.. |image13| image:: images/icons/emoticons/smile.gif
.. |image14| image:: images/icons/emoticons/smile.gif
.. |image15| image:: images/icons/emoticons/smile.gif
.. |image16| image:: images/icons/emoticons/smile.gif
.. |image17| image:: images/icons/emoticons/smile.gif
.. |image18| image:: images/icons/emoticons/smile.gif
.. |image19| image:: images/icons/emoticons/smile.gif
.. |image20| image:: images/icons/emoticons/smile.gif
.. |image21| image:: images/icons/emoticons/biggrin.gif
.. |image22| image:: images/icons/emoticons/smile.gif
.. |image23| image:: images/icons/emoticons/smile.gif
.. |image24| image:: images/icons/emoticons/smile.gif
.. |image25| image:: images/icons/emoticons/biggrin.gif
.. |image26| image:: images/icons/emoticons/smile.gif
.. |image27| image:: images/icons/emoticons/smile.gif
.. |image28| image:: images/icons/emoticons/tongue.gif
.. |image29| image:: images/icons/emoticons/biggrin.gif
.. |image30| image:: images/icons/emoticons/smile.gif
.. |image31| image:: images/icons/emoticons/sad.gif
.. |image32| image:: images/icons/emoticons/smile.gif
.. |image33| image:: images/icons/emoticons/smile.gif
.. |image34| image:: images/icons/emoticons/smile.gif
.. |image35| image:: images/icons/emoticons/smile.gif
.. |image36| image:: images/icons/emoticons/smile.gif
.. |image37| image:: images/icons/emoticons/smile.gif
.. |image38| image:: images/border/spacer.gif
.. |image39| image:: images/border/spacer.gif
