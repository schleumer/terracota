/**
 * Yep, it could be really beauty if made with structural data n' shit, but i wont
 * It's for study purposes, that doesn't mean it will not be serious in a distant Future[]
 * Yeah, my english sucks hard, thanks for notice.
 */



object Hi {
  def main(args: Array[String]) {
    println("Starting...")
    val world = new World("../daworld4.wld")
    world.build()
    world.processHeaders()
    world.processTiles()
    println("Finished...")
  }
}

