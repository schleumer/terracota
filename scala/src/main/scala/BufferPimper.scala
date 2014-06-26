import java.nio.ByteBuffer

object BufferPimper {
  implicit class BufferPimper(buffer: ByteBuffer) {
    implicit def getBoolean: Boolean = buffer.get == 1
    implicit def getSmallInt: Int = buffer.get.toInt

    // For - who the fuck knows - some reason nio.ByteBuffer is returning negative Byte, eg. -84 what should be 172
    implicit def getByte: Int = buffer.get() & 0xff

    //implicit def getInt16: Int = Array[Byte](2).map(_ => buffer.get())
    implicit def getPrefixedString: String = (0 until buffer.get().toInt).map(_ => buffer.get.toChar).mkString
  }
}