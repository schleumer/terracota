import scala.collection.mutable

class MapInfo(var width: Int = 0,
              var height: Int = 0,
              var x: Int = 0,
              var y: Int = 0)

class MapBounds(var maxX: Int = 0,
                var maxY: Int = 0)

class SpawnPoint(var x: Int = 0,
                 var y: Int = 0)

class DungeonPoint(var x: Int = 0,
                   var y: Int = 0)

class ShadowOrbs(var smashedCount: Int = 0,
                 var someSmashed: Boolean = false)

class Altars(var smashedCount: Int = 0)

class Invasion(var delay: Int = 0,
               var size: Int = 0,
               var invasionType: Int = 0,
               var pointX: Double = 0)


class GameData(hash: mutable.Map[String, Boolean] = mutable.Map[String, Boolean]()) {
  def add(data: (String, Boolean)): GameData = {
    hash += data
    this
  }
}

class DefeatedEnemies extends GameData

class SavedNpcs extends GameData