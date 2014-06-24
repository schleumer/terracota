import java.io.File
import java.io.FileInputStream
import java.nio.channels.FileChannel.MapMode._
import java.nio.ByteOrder._
import java.nio.ByteBuffer

object World {
  var version: Int = 0
}

class World(fileName: String) {
  type ArrayOfInts = Array[Int]

  var buffer: ByteBuffer = null

  var version: Int = 0
  var sectionPointers: Array[Int] = null
  var tileTypeCount: Int = 0
  var importantTiles: Array[Boolean] = null
  var name: String = null
  var id: Int = 0
  var bounds = Map(
    ("x", 0),
    ("w", 0),
    ("y", 0),
    ("h", 0)
  )
  var maxBounds = Map(
    ("x", 0),
    ("y", 0)
  )

  var treeX: Array[Int] = null
  var treeStyle: Array[Int] = null
  var caveBackX: Array[Int] = null
  var caveBackStyle: Array[Int] = null
  var moonType: Byte = 0

  def build: ByteBuffer = {
    val file = new File(fileName)
    val fileSize = file.length
    val stream = new FileInputStream(file)
    buffer = stream.getChannel.map(READ_ONLY, 0, fileSize)
    buffer.order(LITTLE_ENDIAN)
  }

  def processHeaders = {
    version = buffer.getInt
    sectionPointers = new Array[Int](buffer.getShort).map(_ => buffer.getInt)
    tileTypeCount = buffer.getShort
    var flags = 0
    var mask = 0x80

    importantTiles = new Array[Boolean](tileTypeCount) map { i =>
      if (mask == 0x80) {
        flags = buffer.get()
        mask = 0x01
      } else {
        mask <<= 1
      }

      (flags & mask).equals(mask)
    }

    name = (0 until buffer.get().toInt).map(_ => buffer.get.toChar).mkString

    id = buffer.getInt

    bounds = Map(
      ("x", buffer.getInt),
      ("w", buffer.getInt),
      ("y", buffer.getInt),
      ("h", buffer.getInt)
    )

    maxBounds = Map(
      ("x", buffer.getInt),
      ("y", buffer.getInt)
    )

    moonType = buffer.get

    treeX = new ArrayOfInts(3).map(_ => buffer.getInt)
    treeStyle = new ArrayOfInts(4).map(_ => buffer.getInt)
    caveBackX = new ArrayOfInts(3).map(_ => buffer.getInt)
    caveBackStyle = new ArrayOfInts(4).map(_ => buffer.getInt)

    bounds

  }

}

object Hi {
  def main(args: Array[String]) {
    val world = new World("daworld4.wld")
    world.build
    world.processHeaders
  }
}

