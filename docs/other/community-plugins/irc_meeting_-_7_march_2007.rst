Irc Meeting - 7 March 2007
##########################

+-----------------------------------+-----------------------------------+-----------------------------------+
| Community Plugins : IRC Meeting - |
| 7 March 2007                      |
| This page last changed on Mar 08, |
| 2010 by jgarnett.                 |
| | (11:12) Jesse\_Eichar77: 1)     |
| OpenMI                            |
| |  (11:12) Jesse\_Eichar77: 2)    |
| Summer of Code                    |
| |  (11:12) Jesse\_Eichar77: 3)    |
| Code Sprint                       |
| |  (11:12) Jesse\_Eichar77:       |
| anything else?                    |
| |  (11:13) rgould: hard coded     |
| source path lookups in .classpath |
| files? |image23|                  |
| |  (11:13) Jesse\_Eichar77: sure  |
| but good luck                     |
| |  (11:13) rgould: (i am against, |
| not for |image24|                 |
| |  (11:14) Jesse\_Eichar77: ok    |
| lets start with that its quick    |
| |  (11:14) Jesse\_Eichar77: 0)    |
| .classpath files.                 |
| |  (11:14) Jesse\_Eichar77:       |
| starting with 0)                  |
| |  (11:14) rgould: if you look at |
| .classpath in libs                |
| |  (11:14) rgould: lots of hard   |
| coded source lookup paths exist   |
| |  (11:14) rgould: (they have     |
| been committed)                   |
| |  (11:15) rgould: I have had my  |
| eclipse freak out about this a    |
| couple times                      |
| |  (11:15) Jesse\_Eichar77: I     |
| know.                             |
| |  (11:15) rgould: anything to be |
| done?                             |
| |  (11:15) Jesse\_Eichar77: but   |
| how to avoid it.                  |
| |  (11:15) Jesse\_Eichar77: we    |
| can make a source plugin for the  |
| GT jars                           |
| |  (11:15) Jesse\_Eichar77: and   |
| then the paths would be relative  |
| |  (11:15) rgould: that could     |
| work                              |
| |  (11:15) Jesse\_Eichar77: but   |
| then we'd have to keep it in sync |
| |  (11:15) rgould: hmm            |
| |  (11:16) rgould: sounds worse   |
| than just erasing the paths from  |
| the classpath                     |
| |  (11:16) chorner: hmm... this   |
| again                             |
| |  (11:16) chorner: we tried to   |
| add an svn:ignore to the          |
| classpath file, but subversion    |
| didn't work as expected           |
| |  (11:16) Jesse\_Eichar77: this  |
| again.                            |
| |  (11:16) Jesse\_Eichar77: What  |
| the issue is richard is that I    |
| need the source code and so I     |
| assign it                         |
| |  (11:16) Jesse\_Eichar77: and   |
| then accidentally commit it       |
| |  (11:16) rgould: yea            |
| |  (11:16) Jesse\_Eichar77: we    |
| tried to do a svn:ignore so it    |
| doesn't commit it but it doesn't  |
| work                              |
| |  (11:17) rgould: I wonder if    |
| there is a different way to       |
| specify where it looks for source |
| code                              |
| |  (11:17) Jesse\_Eichar77:       |
| plugin is the only way that I     |
| know of.                          |
| |  (11:17) rgould: (why doesn't   |
| the svn ignore work? because it   |
| is already in version control?)   |
| |  (11:17) Jesse\_Eichar77: is    |
| there a maven command to bundle   |
| the code up and put it in the     |
| maven repo?                       |
| |  (11:17) chorner: yes, they     |
| didn't consider this case         |
| |  (11:17) rgould: and we want to |
| leave them in version control?    |
| |  (11:18) Jesse\_Eichar77: no    |
| geotools code into the .m2 repo   |
| |  (11:18) chorner: "bundle the   |
| code up" ?                        |
| |  (11:18) Jesse\_Eichar77: then  |
| we can do the same thing as libs  |
| for those files.                  |
| |  (11:18) Jesse\_Eichar77: just  |
| make a zip or jar containing just |
| the source for a maven project.   |
| |  (11:18) chorner: oh... a src   |
| jar?                              |
| |  (11:18) Jesse\_Eichar77: yes   |
| |  (11:18) chorner: we should be  |
| able to do that                   |
| |  (11:19) Jesse\_Eichar77: lets  |
| look into it.                     |
| |  (11:19) rgould: what about a   |
| classpath variable?               |
| |  (11:19) Jesse\_Eichar77: good  |
| one                               |
| |  (11:19) rgould: if you look,   |
| there is already a JRE\_SRC       |
| andJUNIT\_SRC\_HOME               |
| |  (11:19) Jesse\_Eichar77: I     |
| like that.                        |
| |  (11:19) Jesse\_Eichar77: Yes   |
| lets do that instead.             |
| |  (11:19) Jesse\_Eichar77: Cory? |
| |  (11:20) Jesse\_Eichar77: sorry |
| times up we have a tight schedule |
| today                             |
| |  (11:20) Jesse\_Eichar77: 1)    |
| OpenMI                            |
| |  (11:20) Jesse\_Eichar77:       |
| moovida where do you want to      |
| start?                            |
| |  (11:20) moovida: |image25|     |
| |  (11:20) moovida: I want to     |
| start to code                     |
| |  (11:20) moovida: I just want   |
| to know if the way is ok for the  |
| udig community                    |
| |  (11:21) \* jgarnett has joined |
| #udig                             |
| |  (11:21) Jesse\_Eichar77: In    |
| short yes                         |
| |  (11:21) moovida: we need to    |
| use the udig for our modelling    |
| |  (11:21) moovida: that is       |
| enough for today                  |
| |  (11:21) moovida: the yes I     |
| mean                              |
| |  (11:21) Jesse\_Eichar77: Just  |
| a few comments:                   |
| |  (11:21) Jesse\_Eichar77: 1) It |
| should be dependent on Catalog    |
| but not Project                   |
| |  (11:21) moovida: since there   |
| will be lot we will have to do    |
| and we will expose the steps to   |
| the community                     |
| |  (11:22) moovida: (first        |
| enumerate or explain step by      |
| step?)                            |
| |  (11:22) jgarnett: hello -      |
| sorry I am late (on the bright    |
| side I have looked at OpenMI now) |
| |  (11:22) Jesse\_Eichar77: 2)    |
| There is a lot of duplication     |
| between it and GeoAPI so we       |
| eventually (not necessarily       |
| immediately) want to create       |
| adapters between the OpenMI and   |
| GeoAPI interfaces                 |
| |  (11:22) Jesse\_Eichar77: ok go |
| on moovida                        |
| |  (11:23) moovida: ok, to 1      |
| |  (11:23) moovida: depend on     |
| catalog? What do you mean by      |
| that?                             |
| |  (11:23) \* Jesse\_Eichar77     |
| changes topic to 'Agenda: 0)      |
| .classpath files 1) OpenMI 2)     |
| Summer of Code 3) Code Sprint'    |
| |  (11:23) moovida: not project = |
| not map?                          |
| |  (11:24) moovida: however I     |
| think we are thinking the same    |
| |  (11:24) moovida: I will need   |
| to chose maps when I do modelling |
| |  (11:24) moovida: so were to    |
| chose from? The catalog           |
| |  (11:24) Jesse\_Eichar77: It    |
| would be nice that it doesn't     |
| require the project plugins in    |
| order to create the operations.   |
| |  (11:24) Jesse\_Eichar77: all   |
| views and editors in eclipse      |
| provide a "selection"             |
| |  (11:24) moovida: ahhhh... I'm  |
| wrong                             |
| |  (11:25) moovida: now I get it  |
| |  (11:25) moovida: code          |
| dependency                        |
| |  (11:25) Jesse\_Eichar77: yes   |
| |  (11:25) Jesse\_Eichar77:       |
| that's right                      |
| |  (11:25) moovida: alright then, |
| should be ok                      |
| |  (11:25) Jesse\_Eichar77: cool. |
| |  (11:25) Jesse\_Eichar77: Do    |
| you want to lay out your plan?    |
| |  (11:26) moovida: what do you   |
| mean?                             |
| |  (11:26) moovida: ok            |
| |  (11:26) Jesse\_Eichar77: do    |
| you have a plan for how you are   |
| going to integrate it. I saw a    |
| Java project that has openMI.     |
| |  (11:27) Jesse\_Eichar77: Are   |
| you going to borrow code from     |
| that?                             |
| |  (11:27) Jesse\_Eichar77: what  |
| technologies are you going to     |
| use...                            |
| |  (11:27) moovida: you mean the  |
| one I attached?                   |
| |  (11:27) Jesse\_Eichar77: and   |
| so on.                            |
| |  (11:27) Jesse\_Eichar77: There |
| was one on source forge.          |
| |  (11:27) Jesse\_Eichar77: that  |
| I saw                             |
| |  (11:27) moovida: alright, that |
| should be the same I have         |
| |  (11:27) moovida: so:           |
| |  (11:28) moovida: what I would  |
| like to do (design will start in  |
| twoo weeks)                       |
| |  (11:28) moovida: is to use the |
| openmi chaining                   |
| |  (11:28) moovida: but put no    |
| JNI in the core plugins           |
| |  (11:29) Jesse\_Eichar77: (yay) |
| |  (11:29) moovida: (you will     |
| tell me where to put that one     |
| part)                             |
| |  (11:29) Jesse\_Eichar77: I'll  |
| address it after you finish       |
| |  (11:29) moovida: we want to    |
| convert all the JGrass            |
| applications with openmi          |
| interfaces                        |
| |  (11:29) jgarnett: OpenMiGrass  |
| |  (11:29) jgarnett: :-D          |
| |  (11:30) moovida: |image26|     |
| yes, kinda like that. In order to |
| be able to use them in            |
| timedependent environments and    |
| not                               |
| |  (11:30) moovida: also they     |
| will be linked in a gef           |
| environment                       |
| |  (11:31) moovida: instead of    |
| having tousand toolbars           |
| |  (11:31) moovida: at the begin  |
| I wanted to create the engine     |
| |  (11:31) moovida: and use the   |
| GEF stuff also for doing simple   |
| operations                        |
| |  (11:31) moovida: example#:     |
| |  (11:31) moovida: I want to     |
| create a map of flowdirections    |
| from a dtm                        |
| |  (11:32) moovida: 1) I take a   |
| openmigrassudig module            |
| |  (11:32) moovida: 2) I link a   |
| map datastore to it               |
| |  (11:32) moovida: 3) I link an  |
| output map datastore to it        |
| |  (11:32) moovida: and launch    |
| the thing                         |
| |  (11:32) moovida: what happens  |
| is:                               |
| |  (11:32) moovida: the links are |
| checked for consistency           |
| |  (11:33) moovida: every object, |
| model or whatever will have       |
| parameter arguments for a proper  |
| initialization                    |
| |  (11:33) moovida: which will    |
| have to be set (prior to launch)  |
| |  (11:34) moovida: the           |
| environment will be saved and can |
| be reopened and reexecuted        |
| |  (11:34) moovida: so you can    |
| supply modeling schenarios        |
| |  (11:34) \* moovida beeing to   |
| confusing?                        |
| |  (11:35) Jesse\_Eichar77: nope  |
| |  (11:35) Jesse\_Eichar77: makes |
| sense to me.                      |
| |  (11:36) moovida: good! The     |
| same way many models can be       |
| chained and what I hoped is that  |
| every single model can be         |
| launched also standalong          |
| |  (11:36) moovida: I mean        |
| |  (11:36) Jesse\_Eichar77: so    |
| you could pull a resource from    |
| the catalog or a layer from a map |
| for example if it can             |
| |  (11:36) Jesse\_Eichar77: adapt |
| to the correct paramter for that  |
| type then it will be adapted.     |
| |  (11:36) Jesse\_Eichar77: (just |
| brainstorming)                    |
| |  (11:37) Jesse\_Eichar77: is    |
| that kind of what you are         |
| thinking w.r.t the UI component?  |
| |  (11:37) moovida: that is       |
| something I'm not used to         |
| |  (11:37) moovida: let's talk    |
| with an example                   |
| |  (11:37) moovida: I do one:     |
| |  (11:38) moovida: because I'm   |
| hoping that with the same         |
| interfaces we are able also to    |
| create buttons                    |
| |  (11:38) moovida: that execute  |
| those models                      |
| |  (11:38) moovida: again         |
| flowdirections:                   |
| |  (11:38) moovida: I have a      |
| button on the toolbar             |
| |  (11:38) moovida: I click it    |
| and a gui pops up                 |
| |  (11:39) moovida: which two     |
| textfields and two browse buttons |
| |  (11:39) moovida: I will have   |
| to browse for **raster** maps and |
| then execute                      |
| |  (11:39) moovida: how do I do   |
| for the browse?                   |
| |  (11:40) moovida: so that a     |
| user doesn't take a feature map?  |
| |  (11:40) Jesse\_Eichar77: that  |
| is where I was thinking Drag and  |
| Drop would be useful.             |
| |  (11:40) Jesse\_Eichar77: But   |
| you could just browse the         |
| catalog.                          |
| |  (11:40) Jesse\_Eichar77: or    |
| possibly the selected map.        |
| |  (11:40) moovida: drag and drob |
| is nice for visualization         |
| |  (11:40) Jesse\_Eichar77: I     |
| agree.                            |
| |  (11:40) Jesse\_Eichar77:       |
| scenario:                         |
| |  (11:41) moovida: but when you  |
| have a textfield, no one will     |
| drag a file into it               |
| |  (11:41) moovida: they expact a |
| browse button                     |
| |  (11:41) moovida: go            |
| |  (11:41) Jesse\_Eichar77: (this |
| is another vision)                |
| |  (11:41) Jesse\_Eichar77: 1)    |
| You press the create work flow    |
| button or menu item               |
| |  (11:41) Jesse\_Eichar77: 2) A  |
| editor opens                      |
| |  (11:42) Jesse\_Eichar77:       |
| (editor would be for workflow     |
| |  (11:42) Jesse\_Eichar77: -     |
| Editor would have a toolbox       |
| containing all the operations     |
| |  (11:42) Jesse\_Eichar77: -     |
| Tools for maybe moving items,     |
| possibly linking items            |
| |  (11:42) Jesse\_Eichar77: 3)    |
| drag a operation on to editor     |
| |  (11:42) Jesse\_Eichar77: - it  |
| will add the operation to the     |
| work flow                         |
| |  (11:43) Jesse\_Eichar77: 4)    |
| (two ways to do this              |
| |  (11:43) Jesse\_Eichar77: -     |
| Drag a resource from the catalog  |
| on to the editor                  |
| |  (11:43) Jesse\_Eichar77: - or  |
| drag a layer from a map (in       |
| projects view) onto the editor    |
| |  (11:43) Jesse\_Eichar77: 5)    |
| select link tool                  |
| |  (11:44) Jesse\_Eichar77: 6)    |
| draw link from resource to        |
| operation                         |
| |  (11:44) moovida: in 4 you mean |
| drag a resource from the catalog  |
| on the operation to define it as  |
| input or output?                  |
| |  (11:44) Jesse\_Eichar77: -     |
| maybe a dialog will open asking   |
| if it is an input or output       |
| |  (11:44) moovida: ok, sorry,    |
| that was it                       |
| |  (11:44) Jesse\_Eichar77: - it  |
| also may ask which input it is    |
| (if there are multiple inputs)    |
| |  (11:45) Jesse\_Eichar77: 7) do |
| the same for the output           |
| |  (11:45) Jesse\_Eichar77:       |
| another way is drop resource      |
| directly on operation             |
| |  (11:45) Jesse\_Eichar77: and   |
| the menu would open asking if it  |
| is an input or output.            |
| |  (11:45) Jesse\_Eichar77: Just  |
| ideas.                            |
| |  (11:45) Jesse\_Eichar77: what  |
| do you think?                     |
| |  (11:45) moovida: the first     |
| part is what I was talking about  |
| when talking about GEF, the       |
| second part is a very nice part   |
| that I like!!                     |
| |  (11:46) moovida: drag the      |
| resources into the editor and     |
| then link! Great!                 |
| |  (11:46) Jesse\_Eichar77: cool. |
| |  (11:46) moovida: what is not   |
| resource                          |
| |  (11:46) moovida: can be put    |
| into the palette                  |
| |  (11:46) moovida: operations    |
| |  (11:47) moovida: values        |
| |  (11:47) Jesse\_Eichar77:       |
| that's right.                     |
| |  (11:47) moovida: database      |
| connections for dischare data     |
| (example)                         |
| |  (11:47) moovida: etc. etc      |
| |  (11:47) moovida: dischare =    |
| discharge                         |
| |  (11:47) Jesse\_Eichar77: to    |
| clarify about the values for      |
| example                           |
| |  (11:47) Jesse\_Eichar77: what  |
| exactly would be on the palette   |
| again?                            |
| |  (11:48) Jesse\_Eichar77: a DB  |
| Connection?                       |
| |  (11:48) Jesse\_Eichar77: then  |
| you'd drag it to the editor       |
| |  (11:48) moovida: values =      |
| thresholds, particular parameters |
| of the model                      |
| |  (11:48) Jesse\_Eichar77: and   |
| fill in the exact values?         |
| |  (11:48) moovida: alright       |
| |  (11:48) Jesse\_Eichar77: it    |
| was a question. It that kind of   |
| what you were thinking?           |
| |  (11:49) moovida: in the        |
| palette we have the operations    |
| and everything not in the catalog |
| |  (11:49) moovida: so a model    |
| could need value, and the palette |
| will have a scalar object         |
| |  (11:49) Jesse\_Eichar77: I     |
| understand that. But we wouldn't  |
| have "concrete" values would we?  |
| IE the value 14.                  |
| |  (11:50) moovida: a model will  |
| need a database connection to get |
| the meterological data            |
| |  (11:50) jgarnett: Hey guys 10  |
| mins left ...                     |
| |  (11:50) moovida: so you drag   |
| this non -spatial object into it  |
| |  (11:50) jgarnett: (The last    |
| two should go quick )             |
| |  (11:50) moovida: and in a      |
| properties tab define the         |
| connection                        |
| |  (11:50) moovida: Jody ok,      |
| sorry                             |
| |  (11:50) Jesse\_Eichar77: ok    |
| that was what I was thinking too. |
| We're talking about the same      |
| thing.                            |
| |  (11:50) Jesse\_Eichar77: Good. |
| |  (11:50) moovida: GREAT!        |
| |  (11:51) Jesse\_Eichar77: Do    |
| you have any other questions you  |
| need resolved right now moovida?  |
| |  (11:51) Jesse\_Eichar77: (this |
| is your chance) |image27|         |
| |  (11:51) moovida: No, I have a  |
| lot to do on it before it comes   |
| to problems                       |
| |  (11:51) moovida: |image28|     |
| |  (11:51) Jesse\_Eichar77:       |
| |image29|                         |
| |  (11:51) Jesse\_Eichar77: ok    |
| sounds good.                      |
| |  (11:51) moovida: I mean udig   |
| related ones                      |
| |  (11:51) Jesse\_Eichar77: Next  |
| topic                             |
| |  (11:51) Jesse\_Eichar77: 2)    |
| Summer of Code                    |
| |  (11:51) Jesse\_Eichar77: Cory? |
| Jody?                             |
| |  (11:52) chorner: oh            |
| |  (11:52) chorner: yes, see      |
| e-mail – we need ideas for the    |
| summer of code                    |
| |  (11:52) moovida: until when is |
| time to contribute ideas?         |
| |  (11:53) chorner: when can go   |
| on indefinitely                   |
| |  (11:53) chorner: \*we          |
| |  (11:53) chorner: but google    |
| will starting looking on monday   |
| |  (11:53) moovida: |image30|     |
| alright                           |
| |  (11:53) chorner: students will |
| start looking a few weeks after   |
| that                              |
| |  (11:54) moovida: so if we of   |
| the JGrass part add a topic...    |
| what will happen next?            |
| |  (11:54) moovida: what will we  |
| have to do?                       |
| |  (11:54) chorner: we will need  |
| a mentor to lead the student, if  |
| we get a student                  |
| |  (11:54) moovida: how much to   |
| write?                            |
| |  (11:54) chorner:               |
| realistically, we'll probably     |
| have a few mentors helping out    |
| |  (11:55) chorner: someone on    |
| the uDig side, some on the JGrass |
| side                              |
| |  (11:55) chorner: whoever is    |
| willing to help students out with |
| jgrass (a few hours per week) can |
| be an mentor                      |
| |  (11:56) moovida: but the       |
| students?                         |
| |  (11:56) moovida: do we have to |
| supply them?                      |
| |  (11:56) chorner: no            |
| |  (11:56) chorner: the students  |
| will come from all over           |
| |  (11:56) chorner: and google    |
| will pay them                     |
| |  (11:57) chorner: we will       |
| probably just get one             |
| |  (11:57) moovida: so "just"     |
| write down a project of three     |
| months and hope that someone will |
| chose it?                         |
| |  (11:57) chorner: yes           |
| |  (11:57) moovida: and that      |
| google will chose it?             |
| |  (11:57) chorner: you can also  |
| nudge some students and say "hey! |
| look! isn't this cool"            |
| |  (11:58) chorner: google        |
| doesn't choose a particular idea  |
| – they will just look to see that |
| we have good ideas floating       |
| around                            |
| |  (11:58) moovida: so if I find  |
| someone that does it, get the     |
| project?                          |
| |  (11:58) moovida: or is google  |
| filtering first?                  |
| |  (11:58) chorner: there is a    |
| complicated process which         |
| determines who gets a student     |
| |  (11:58) chorner: as mentors we |
| will have a small say             |
| |  (11:58) chorner: in who is     |
| selected                          |
| |  (11:59) moovida: alright, I    |
| got it                            |
| |  (11:59) jgarnett: Um sanity    |
| check here                        |
| |  (11:59) chorner:               |
| http://udig.refractions.net/confl |
| uence/display/HACK/Summer+of+Code |
| +2007                             |
| |  (11:59) jgarnett: what we      |
| **really** need is a list of      |
| potential mentors                 |
| |  (12:00) chorner: i think that  |
| is jesse, jody, richard, me       |
| |  (12:00) jgarnett: Based on our |
| experience last time I would like |
| to see more information on each   |
| idea (so students get off on the  |
| right foot).                      |
| |  (12:00) chorner: and a jgrass  |
| folk or two                       |
| |  (12:00) jgarnett: moovida ?    |
| |  (12:01) moovida: yes, I would  |
| candidate as mentor, that was     |
| obvious for me if I contribute an |
| idea                              |
| |  (12:01) jgarnett: For          |
| reference here is the page for    |
| GeoServer                         |
| (GEOSDEV/GeoServer+Summer+of+Code |
| +Ideas)                           |
| |  (12:01) jgarnett: bah -        |
| http://docs.codehaus.org/display/ |
| GEOSDEV/GeoServer+Summer+of+Code+ |
| Ideas                             |
| |  (12:01) jgarnett: actually we  |
| had lots of ideas with no mentor  |
| last year                         |
| |  (12:01) jgarnett: there is     |
| some responsibility in being a    |
| mentor (and some money)           |
| |  (12:02) moovida: I made mentor |
| for different tesises... kinda    |
| the same?                         |
| |  (12:02) chorner: potential     |
| mentors is a new thing            |
| |  (12:02) chorner: i think they  |
| are pre-screening                 |
| |  (12:02) \* FrankW has joined   |
| #udig                             |
| |  (12:03) jgarnett: Hi Frank -   |
| we are talking about getting a    |
| list of potential mentors for you |
| |  (12:03) chorner: k...          |
| potential mentors need to put up  |
| their name and google account     |
| |  (12:03) jgarnett: would Friday |
| be okay?                          |
| |  (12:03) chorner: it will be on |
| our wiki page jody                |
| |  (12:03) FrankW: Friday is      |
| fine, with google account names.  |
| |  (12:03) jgarnett: But frank    |
| needs to collapse them all into   |
| one list ...                      |
| |  (12:04) jgarnett:              |
| http://wiki.osgeo.org/index.php/2 |
| 007_Google_SoC_Application        |
| |  (12:04) FrankW: Right - they   |
| all have to be listed in the      |
| application.                      |
| |  (12:04) moovida: so a mentor   |
| would be related to a certain     |
| proposal, right?                  |
| |  (12:04) chorner: no            |
| |  (12:04) jgarnett: Nope - just  |
| are you available if a student    |
| needs mentoring                   |
| |  (12:05) moovida: does that     |
| make sense? What if I can't help  |
| him?                              |
| |  (12:05) jgarnett: we can       |
| assume that for the uDig          |
| community we will match up the    |
| right two people together         |
| |  (12:05) jgarnett: (so you      |
| would be our top pick for OpenMI  |
| GEF editor)                       |
| |  (12:05) jgarnett: Do I have    |
| this right Frank?                 |
| |  (12:05) moovida: alright I     |
| see, it is kinda interchangable?  |
| |  (12:05) FrankW: jgarnett: yes  |
| I think so.                       |
| |  (12:06) moovida: or better, a  |
| net of mentors?                   |
| |  (12:06) jgarnett: yes we need  |
| to collect two things             |
| |  (12:06) jgarnett: 1) list of   |
| mentors                           |
| |  (12:06) jgarnett: 2) list of   |
| ideas                             |
| |  (12:06) chorner: okay.. i      |
| think we've beaten this one to    |
| death                             |
| |  (12:06) moovida: sorry if I    |
| bother, but I have to be sure     |
| guys                              |
| |  (12:06) jgarnett: The list of  |
| mentors is more important needed  |
| for the weekend; the ideas can be |
| ready end of day Monday.          |
| |  (12:07) moovida: for example   |
| I'm no feature guy                |
| |  (12:07) moovida: what if I get |
| one about features?               |
| |  (12:07) jgarnett: yep. To be   |
| blunt you will have a chance to   |
| say **no**                        |
| |  (12:07) jgarnett: so dont      |
| worry about getting matched up    |
| with something you know nothing   |
| about.                            |
| |  (12:07) jgarnett: :-D          |
| |  (12:07) moovida: :S            |
| |  (12:07) moovida: alright, I    |
| trust |image31|                   |
| |  (12:08) jgarnett: 3) Code      |
| Sprint                            |
| |  (12:08) jgarnett: We have a    |
| day set aside at the end of the   |
| FOSS4G conference |image32|       |
| |  (12:08) moovida: joyful,       |
| joyful |image33|                  |
| |  (12:08) jgarnett: It sounds    |
| like from email that we would     |
| like to get a hackfest in gear    |
| |  (12:08) moovida: will the long |
| codesprint be makable?            |
| |  (12:08) jgarnett: (part of     |
| this is so you can enjoy the      |
| conference)                       |
| |  (12:09) jgarnett: Depends how  |
| big Jesse's new place is          |
| |image34|                         |
| |  (12:09) jgarnett: (that was a  |
| joke)                             |
| |  (12:09) moovida: |image35|     |
| |  (12:09) chorner: i think there |
| is interest in hacking more the   |
| monday after                      |
| |  (12:09) chorner: we can get a  |
| sprint on friday                  |
| |  (12:09) chorner: but some will |
| want **more** |image36|           |
| |  (12:10) jgarnett: Well we have |
| paid for facilities on the friday |
| - and uDig can get in on that.    |
| |  (12:10) moovida: yes, one day  |
| is too short!!                    |
| |  (12:10) jgarnett: For **more** |
| we will need to organize          |
| ourselves.                        |
| |  (12:10) chorner: i think       |
| sat/sun is a bad idea             |
| |  (12:10) Jesse\_Eichar77: we    |
| can find something. I'm not too   |
| worried.                          |
| |  (12:10) jgarnett: (sigh - we   |
| are all such hackers - don't know |
| why they put up with us)          |
| |  (12:10) moovida: which means   |
| Victorians organise for the whole |
| world? |image37|                  |
| |  (12:10) chorner: after 5 days  |
| of conference, people will want   |
| to go surfing                     |
| |  (12:10) chorner: or to the pub |
| |  (12:11) \* moovida wants to go |
| to the pub...                     |
| |  (12:11) jgarnett: moovida I    |
| was including you as a hacker -   |
| ie 'show me the code'             |
| |  (12:11) Jesse\_Eichar77:       |
| |image38|                         |
| |  (12:11) moovida: |image39|     |
| |  (12:11) jgarnett: So lets get  |
| down to biz ...                   |
| |  (12:11) chorner: let's leave   |
| it open then: we will have a uDig |
| code sprint on friday             |
| |  (12:11) jgarnett: moovida it   |
| makes a difference to you -       |
| having a code sprint.             |
| |  (12:11) chorner: and will do   |
| some more the next week           |
| |  (12:11) \* sfarber has joined  |
| #udig                             |
| |  (12:11) jgarnett: and if you   |
| are going to fly out here you     |
| would like more then a single day |
| hacking?                          |
| |  (12:12) moovida: yes, that     |
| would make my presence there much |
| much better |image40|             |
| |  (12:12) jgarnett: (Worst case  |
| Jesse and I take a day's holiday  |
| on Monday and I have wireless at  |
| my house)                         |
| |  (12:12) jgarnett: Jesse are we |
| confident we can arrange          |
| something? Enough for moovida to  |
| make plans ...                    |
| |  (12:13) \* moovida says WOW    |
| (not like in vista... ut)         |
| |  (12:13) Jesse\_Eichar77:       |
| Definately                        |
| |  (12:13) jgarnett: sweet.       |
| |  (12:13) moovida: jody, thanks, |
| I would need to be sure           |
| |  (12:13) moovida: Great!        |
| |  (12:13) moovida: amazing       |
| |  (12:13) moovida: |image41|     |
| |  (12:13) jgarnett: Okay I will  |
| sum up this kind of stuff on the  |
| sprint page; and I am going to    |
| try and contact some of the TOPP  |
| developers that have been to      |
| sprints before.                   |
| |  (12:13) Jesse\_Eichar77:       |
| Should be fun |image42|           |
| |  (12:13) moovida: I'm sure it   |
| will |image43|                    |
| |  (12:13) jgarnett: It will be   |
| very cool.                        |
| |  (12:14) jgarnett: Um but guys; |
| to have a successful sprint       |
| |  (12:14) jgarnett: you need to  |
| choose something that can be      |
| done; and is useful...            |
| |  (12:14) jgarnett: aka no       |
| random hacking.                   |
| |  (12:14) moovida: never been at |
| a sprint                          |
| |  (12:14) jgarnett: Something to |
| think about over email; and it    |
| may be you see something at the   |
| conference and decide what to do  |
| on the spot.                      |
| |  (12:14) FrankW: Well, the      |
| alternative is a less structured  |
| hack-a-thon.                      |
| |  (12:14) moovida: but I guess   |
| soemone will have to coordinate   |
| |  (12:15) moovida: hack-a-ton?   |
| |image44|                         |
| |  (12:15) jgarnett: FrankW++ I   |
| was hoping each developer         |
| community woudl self coordinate - |
| but a lot of ideas cut across     |
| project groups.                   |
| |  (12:15) jgarnett: It may be a  |
| nicer "message" for OSGeo to      |
| promote collaboration across      |
| developer communities.            |
| |  (12:15) FrankW: jgarnett: With |
| different groups in the same      |
| place, it seems like we should    |
| contemplate cross-project         |
| efforts.                          |
| |  (12:16) jgarnett: I will write |
| it on the page; this is going to  |
| be one wild crazy week ...        |
| |  (12:16) jgarnett: ... I do     |
| have to warn you that the         |
| GeoTools developers last time     |
| ignored the conference and just   |
| coded most of the week            |
| |  (12:16) FrankW: That is has    |
| been our inspiration!             |
| |  (12:16) jgarnett: ... I would  |
| like to promiss of a sprint in    |
| order to let them be social and   |
| meet others.                      |
| |  (12:17) moovida: I can         |
| immagine... |image45|             |
| |  (12:17) jgarnett: Cool ...     |
| meeting is over time. Shall we    |
| kick out the lights?              |
| |  (12:17) Jesse\_Eichar77:       |
| sounds good                       |
| |  (12:17) \* FrankW fades...     |
| |  (12:17) moovida: good for me   |
| |  (12:17) \* FrankW has left     |
| #udig ("Leaving")                 |
| |  (12:17) Jesse\_Eichar77:       |
| thanks for everyone coming by     |
| |  (12:17) jgarnett: thanks       |
| |  (12:17) moovida: thanks guys.. |
| ciao                              |
+-----------------------------------+-----------------------------------+-----------------------------------+

+-------------+----------------------------------------------------------+
| |image47|   | Document generated by Confluence on Aug 11, 2014 12:24   |
+-------------+----------------------------------------------------------+

.. |image0| image:: images/icons/emoticons/smile.gif
.. |image1| image:: images/icons/emoticons/smile.gif
.. |image2| image:: images/icons/emoticons/smile.gif
.. |image3| image:: images/icons/emoticons/biggrin.gif
.. |image4| image:: images/icons/emoticons/wink.gif
.. |image5| image:: images/icons/emoticons/biggrin.gif
.. |image6| image:: images/icons/emoticons/biggrin.gif
.. |image7| image:: images/icons/emoticons/smile.gif
.. |image8| image:: images/icons/emoticons/smile.gif
.. |image9| image:: images/icons/emoticons/smile.gif
.. |image10| image:: images/icons/emoticons/biggrin.gif
.. |image11| image:: images/icons/emoticons/smile.gif
.. |image12| image:: images/icons/emoticons/biggrin.gif
.. |image13| image:: images/icons/emoticons/biggrin.gif
.. |image14| image:: images/icons/emoticons/biggrin.gif
.. |image15| image:: images/icons/emoticons/smile.gif
.. |image16| image:: images/icons/emoticons/biggrin.gif
.. |image17| image:: images/icons/emoticons/smile.gif
.. |image18| image:: images/icons/emoticons/biggrin.gif
.. |image19| image:: images/icons/emoticons/smile.gif
.. |image20| image:: images/icons/emoticons/smile.gif
.. |image21| image:: images/icons/emoticons/biggrin.gif
.. |image22| image:: images/icons/emoticons/smile.gif
.. |image23| image:: images/icons/emoticons/smile.gif
.. |image24| image:: images/icons/emoticons/smile.gif
.. |image25| image:: images/icons/emoticons/smile.gif
.. |image26| image:: images/icons/emoticons/biggrin.gif
.. |image27| image:: images/icons/emoticons/wink.gif
.. |image28| image:: images/icons/emoticons/biggrin.gif
.. |image29| image:: images/icons/emoticons/biggrin.gif
.. |image30| image:: images/icons/emoticons/smile.gif
.. |image31| image:: images/icons/emoticons/smile.gif
.. |image32| image:: images/icons/emoticons/smile.gif
.. |image33| image:: images/icons/emoticons/biggrin.gif
.. |image34| image:: images/icons/emoticons/smile.gif
.. |image35| image:: images/icons/emoticons/biggrin.gif
.. |image36| image:: images/icons/emoticons/biggrin.gif
.. |image37| image:: images/icons/emoticons/biggrin.gif
.. |image38| image:: images/icons/emoticons/smile.gif
.. |image39| image:: images/icons/emoticons/biggrin.gif
.. |image40| image:: images/icons/emoticons/smile.gif
.. |image41| image:: images/icons/emoticons/biggrin.gif
.. |image42| image:: images/icons/emoticons/smile.gif
.. |image43| image:: images/icons/emoticons/smile.gif
.. |image44| image:: images/icons/emoticons/biggrin.gif
.. |image45| image:: images/icons/emoticons/smile.gif
.. |image46| image:: images/border/spacer.gif
.. |image47| image:: images/border/spacer.gif
