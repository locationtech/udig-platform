package net.refractions.udig.build.pack

import scalax.io.Implicits._
import java.io.File
import java.util.Properties
import scant.{Ant,TarFileSet}
import scant.Ant.{Dir,antElem2Node}
import scala.maven.model.MavenProjectAdapter
import scant.Tar._
import scant.Tar

class LinuxPrepare(params:Params) extends Prepare (params){

  def run() {
    addJRE()
    
    val oldExecutable = new File(uDigFolder,"udig")
    val executable = new File(uDigFolder,"bin")
    oldExecutable.renameTo(executable);
    val oldini = new File(uDigFolder,"udig.ini")
    val ini = new File(uDigFolder,"bin.ini")
    oldini.renameTo(ini)
    
    val startScript = new File(resources, "linux_launch.sh")
    startScript.copyTo(new File(uDigFolder,"udig"))

    val baseName = params.file.getName
    val tarFile = deploy.getAbsolutePath+"/"+baseName.take(baseName.lastIndexOf("."))+"-"+params.timestamp+".tar"
    val gzipFile = tarFile+".gz"
    val baseForZip = uDigFolder.getAbsolutePath.drop(tmpBuild.getAbsolutePath.length+1)
    new File(gzipFile).delete
	Ant( 
		<chmod type="file" perm="ugo+rx">
			{
				Ant.fileset( Dir(uDigFolder+"/jre"), List("bin/java") ,Nil ).xml
			}
		</chmod>,
    Tar(Ant.File(gzipFile), GNU, GZIP, 
      TarFileSet(Dir(tmpBuild.getAbsolutePath)),
      TarFileSet(Dir(tmpBuild.getAbsolutePath),
                List(baseForZip +"/jre/bin/java", baseForZip+"/udig", baseForZip+"/bin"), 
                Nil).mode("755"))
	   )
	   tmpBuild.deleteRecursively()
    
  }
  
}
