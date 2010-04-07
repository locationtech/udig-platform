
import java.io._
import java.net._


object Pinger {

  def apply(addr:String, timeoutMs:Int):Option[Long] = {
    apply(addr,80,timeoutMs)
  }
  
  def apply(addr:Array[Byte], timeoutMs:Int):Option[Long] = {
    apply(addr,80,timeoutMs)
  }
  def apply(addr:String, port:Int, timeoutMs:Int):Option[Long] = {
    apply(InetAddress.getByName(addr),port,timeoutMs)
  }
  
  def apply(addr:Array[Byte], port:Int, timeoutMs:Int):Option[Long] = {
    apply(InetAddress.getByAddress(addr),port,timeoutMs)
  }

  def apply(addr:InetAddress, port:Int, timeoutMs:Int):Option[Long] = {
    //pass in a byte array with the ipv4 address, the port & the max time out required
    //make an unbound socket
    val theSock = new Socket();

    try {
      val sockaddr = new InetSocketAddress(addr, port);

      // Create the socket with a timeout
      //when a timeout occurs, we will get timout exp.
      //also time our connection this gets very close to the real time
      val start = System.currentTimeMillis();
      theSock.connect(sockaddr, timeoutMs);
      val end = System.currentTimeMillis();
      Some(end - start)
    } catch{
      case _ => None
    } finally {
      if (theSock != null) {
        theSock.close();
      }

    }
  }
}
