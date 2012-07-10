How should I use a Progress Monitor
===================================

**Q** How should Progress Monitors be used?

**A** A few tips on using Progress Monitors:

-  Always start the progress monitor and do at least 1 bit of work. For example:

   ::

       monitor.beginTask("Working", 4);
       monitor.work(1);

-  Always finish started job.

   ::

       try{
         monitor.beginTask("Working", 4);
         monitor.work(1);
         // some work
       }finally{
         monitor.done();
       }

-  Make use of SubProgressMonitor if sending the monitor to another method:

   ::

       try{
         monitor.beginTask("Working", 8);
         monitor.work(1);

         SubProgressMonitor sub=new SubProgressMonitor(monitor, 3);
         doSomeWork(sub);
         sub.done();  // don't forget to make sure the sub monitor is done

         sub=new SubProgressMonitor(monitor, 3);
         doSomeMoreWork(sub);
         sub.done();  // don't forget to make sure the sub monitor is done (callee might not use it)

       }finally{
         monitor.done();
       }


