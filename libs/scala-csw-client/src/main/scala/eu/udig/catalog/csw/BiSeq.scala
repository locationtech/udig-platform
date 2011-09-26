package eu.udig.catalog.csw;
import collection.JavaConverters._

class BiSeq[R](val seq:Seq[R]) {
	val java = seq.asJava
}
