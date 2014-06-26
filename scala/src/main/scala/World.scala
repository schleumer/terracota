import java.io.{FileInputStream, File}
import java.nio.ByteOrder._
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel.MapMode._

import scala.collection.mutable

/**
 * XXX: It should be a object, should it? I don't know.
 * World class for process world stuffs
 * @param fileName File name(duh)
 */
class World(fileName: String) {

  import BufferPimper._

  type ArrayOfInts = Array[Int]

  /**
   * Byte Buffer, unnecessary comment
   */
  var buffer: MappedByteBuffer = null

  /**
   * All the map info that i will not comment
   * 'cuz i hardly want to play Terraria today
   */
  var version: Int = 0
  var sectionPointers: Array[Int] = null
  var tileTypeCount: Short = 0
  var importantTiles: Array[Boolean] = null
  var name: String = null
  var id: Int = 0
  var bounds: MapInfo = null
  var maxBounds: MapBounds = null
  var moonType: Byte = 0
  var treeX: Array[Int] = null
  var treeStyle: Array[Int] = null
  var caveBackX: Array[Int] = null
  var caveBackStyle: Array[Int] = null
  var iceBackStyle: Int = 0
  var jungleBackStyle: Int = 0
  var hellBackStyle: Int = 0
  var spawn: SpawnPoint = null
  var surfaceLevel: Double = 0
  var rockLayer: Double = 0
  var temporaryTime: Double = 0
  var isDayTime: Boolean = false
  var moonPhase: Int = 0
  var isBlooodMoon: Boolean = false
  var isEclipse: Boolean = false
  var dungeonPoint: DungeonPoint = null
  var crimson: Boolean = false
  var defeated: DefeatedEnemies = new DefeatedEnemies()
  var saved: SavedNpcs = new SavedNpcs()
  var shadowOrbs: ShadowOrbs = new ShadowOrbs()

  var meteorSpawned: Boolean = false

  var altars: Altars = new Altars()

  var isInHardMode: Boolean = false
  var invasion: Invasion = new Invasion()

  var oreTiers: ArrayOfInts = null
  var styles: ArrayOfInts = null

  var isRaining: Boolean = false
  var rainTime: Int = 0
  var maxRain: Float = 0

  var cloudsActive: Int = 0
  var numClouds: Short = 0
  var windSpeed: Float = 0

  // WTF?
  var anglerWhoFinishedToday: mutable.HashSet[String] = null
  var anglerQuest: Int = 0

  var tiles: Array[Array[Int]] = null

  /**
   * Take the file, make a stream and build a buffered readable ByteBuffer channel
   */
  def build() = {
    // Look at the file
    val file = new File(fileName)
    // Show me the file
    val fileSize = file.length
    // Give me the file
    val stream = new FileInputStream(file)
    // I want the file
    buffer = stream.getChannel.map(READ_ONLY, 0, fileSize)
    // Setup the file byte order
    buffer.order(LITTLE_ENDIAN)
    // ( ͡° ͜ʖ ͡°)
  }

  /**
   * Process all header data contained on the Terraria® world file
   */
  def processHeaders() = {
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

    name = buffer.getPrefixedString

    id = buffer.getInt

    bounds = new MapInfo(
      x = buffer.getInt,
      width = buffer.getInt,
      y = buffer.getInt,
      height = buffer.getInt
    )

    maxBounds = new MapBounds(
      maxY = buffer.getInt,
      maxX = buffer.getInt
    )

    moonType = buffer.get

    treeX = new ArrayOfInts(3).map(_ => buffer.getInt)
    treeStyle = new ArrayOfInts(4).map(_ => buffer.getInt)
    caveBackX = new ArrayOfInts(3).map(_ => buffer.getInt)
    caveBackStyle = new ArrayOfInts(4).map(_ => buffer.getInt)

    iceBackStyle = buffer.getInt
    jungleBackStyle = buffer.getInt
    hellBackStyle = buffer.getInt

    spawn = new SpawnPoint(
      x = buffer.getInt,
      y = buffer.getInt
    )

    surfaceLevel = buffer.getDouble
    rockLayer = buffer.getDouble
    temporaryTime = buffer.getDouble

    isDayTime = buffer.getBoolean
    moonPhase = buffer.getInt
    isBlooodMoon = buffer.getBoolean
    isEclipse = buffer.getBoolean

    dungeonPoint = new DungeonPoint(
      x = buffer.getInt,
      y = buffer.getInt
    )

    crimson = buffer.getBoolean

    (defeated
      add("Boss1", buffer.getBoolean)
      add("Boss2", buffer.getBoolean)
      add("Boss3", buffer.getBoolean)
      add("QueenBee", buffer.getBoolean)
      add("MechBoss1", buffer.getBoolean)
      add("MechBoss2", buffer.getBoolean)
      add("MechBoss3", buffer.getBoolean)
      add("MechBossAny", buffer.getBoolean)
      add("PlantBoss", buffer.getBoolean)
      add("GolemBoss", buffer.getBoolean))

    (saved
      add("Goblin", buffer.getBoolean)
      add("Wizard", buffer.getBoolean)
      add("Mechanic", buffer.getBoolean))

    (defeated
      add("GoblinArmy", buffer.getBoolean)
      add("Clown", buffer.getBoolean)
      add("Frost", buffer.getBoolean)
      add("Pirates", buffer.getBoolean))

    shadowOrbs.someSmashed = buffer.getBoolean
    meteorSpawned = buffer.getBoolean
    shadowOrbs.smashedCount = buffer.getSmallInt
    altars.smashedCount = buffer.getInt
    isInHardMode = buffer.getBoolean

    invasion.delay = buffer.getInt
    invasion.size = buffer.getInt
    invasion.invasionType = buffer.getInt
    invasion.pointX = buffer.getDouble

    isRaining = buffer.getBoolean
    rainTime = buffer.getInt
    maxRain = buffer.getFloat

    oreTiers = new ArrayOfInts(3).map(_ => buffer.getInt)
    styles = new ArrayOfInts(8).map(_ => buffer.getSmallInt)

    cloudsActive = buffer.getInt
    numClouds = buffer.getShort
    windSpeed = buffer.getFloat

    (0 until buffer.getInt).map(x => {
      anglerWhoFinishedToday.add(buffer.getPrefixedString)
    })

    saved add("Angler", buffer.getBoolean)
    anglerQuest = buffer.getInt
  }

  def processTiles() = {
    tiles = Array.ofDim[Int](maxBounds.maxX, maxBounds.maxY)

    var ntileType = TileProperties.BackgroundOffset
    var run = 0
    var firstHeader = 0
    var secondHeader = 0
    var thirdHeader = 0
    var currentTile = 0
    println(buffer.position)
    (0 until maxBounds.maxX).foreach { ncolumn =>
      (0 until maxBounds.maxY).foreach { nrow =>
        currentTile += 1
        if (run > 0) {
          tiles(ncolumn)(nrow) = ntileType
          run -= 1
        } else {
          if (nrow < surfaceLevel) {
            ntileType = TileProperties.BackgroundOffset
          } else if (nrow == surfaceLevel) {
            ntileType = TileProperties.BackgroundOffset + 1
          } else if (nrow < rockLayer + 38) {
            ntileType = TileProperties.BackgroundOffset + 2
          } else if (nrow == rockLayer + 38) {
            ntileType = TileProperties.BackgroundOffset + 4
          } else if (nrow < maxBounds.maxY - 202) {
            ntileType = TileProperties.BackgroundOffset + 3
          } else if (nrow == maxBounds.maxY - 202) {
            ntileType = TileProperties.BackgroundOffset + 6
          } else {
            ntileType = TileProperties.BackgroundOffset + 5
          }

          secondHeader = 0
          thirdHeader = 0

          firstHeader = buffer.getByte

          if ((firstHeader & 1) == 1) {
            secondHeader = buffer.getByte
            if ((secondHeader & 1) == 1) {
              thirdHeader = buffer.getByte
            }
          }

          if ((firstHeader & 2) == 2) {
            if ((firstHeader & 32) == 32) {
              ntileType = buffer.getShort
            } else {
              ntileType = buffer.getByte
            }
            if (importantTiles.indices.contains(ntileType)
              && importantTiles(ntileType)) {
              val typeX = buffer.getShort
              val typeY = buffer.getShort
              if (ntileType == TileProperties.ExposedGems) {
                ntileType = if (typeX == 0) TileProperties.Amethyst
                  else if (typeX == 18) TileProperties.Topaz
                  else if (typeX == 36) TileProperties.Sapphire
                  else if (typeX == 54) TileProperties.Emerald
                  else if (typeX == 72) TileProperties.Ruby
                  else if (typeX == 90) TileProperties.Diamond
                  else if (typeX == 108) TileProperties.ExposedGems
                  else ntileType
              } else if (ntileType == TileProperties.SmallDetritus) {
                if ((typeX % 36 == 0) && (typeY == 18)) {
                  val typeZ = typeX / 36
                  ntileType = if (typeZ == 16) TileProperties.CopperCache
                    else if (typeZ == 17) TileProperties.SilverCache
                    else if (typeZ == 18) TileProperties.GoldCache
                    else if (typeZ == 19) TileProperties.Amethyst
                    else if (typeZ == 20) TileProperties.Topaz
                    else if (typeZ == 21) TileProperties.Sapphire
                    else if (typeZ == 22) TileProperties.Emerald
                    else if (typeZ == 23) TileProperties.Ruby
                    else if (typeZ == 24) TileProperties.Diamond
                    else ntileType
                }
              } else if (ntileType == TileProperties.LargeDetritus) {
                if ((typeX % 54 == 0) && (typeY == 0)) {
                  val typeZ = typeX / 54
                  ntileType = if (typeZ == 16 || typeZ == 17) TileProperties.CopperCache
                    else if (typeZ == 18 || typeZ == 19) TileProperties.SilverCache
                    else if (typeZ == 20 || typeZ == 21) TileProperties.GoldCache
                    else ntileType
                }
              } else if (ntileType == TileProperties.LargeDetritus2) {
                if ((typeX % 54 == 0) && (typeY == 0)) {
                  val typeZ = typeX / 54
                  ntileType = if (typeZ == 17) TileProperties.EnchantedSword
                    else ntileType
                }
              }

              //if ((ntileType == TileProperties.Chest) && (typeX % 36 == 0) && (typeY == 0)) {
              //
              //}
            }

            if ((thirdHeader & 8) == 8) {
              buffer.get()
            }
          }
          if ((firstHeader & 4) == 4) {
            val wallType = buffer.getByte
            if (ntileType >= TileProperties.Unknown) {
              ntileType = wallType + TileProperties.WallOffset
            }
            if ((thirdHeader & 16) == 16) {
              buffer.get()
            }
          }
          if ((firstHeader & 8) == 8) {
            ntileType = TileProperties.Water
            buffer.get()
          } else if ((firstHeader & 16) == 16) {
            ntileType = TileProperties.Lava
            buffer.get()
          }

          if ((firstHeader & 64) == 64) {
            run = buffer.getByte
          }

          if ((firstHeader & 128) == 128) {
            run = buffer.getShort
          }

          tiles(ncolumn)(nrow) = ntileType
        }
      }
    }
    println(currentTile)
    None
  }
}