import util.Random.nextInt

object Main {
	def main(args: Array[String]) {
		var myArray = Array.ofDim[Int](8400, 2400)
		println("yey")
		(0 until 1000).map { xx =>
			myArray.zipWithIndex.foreach { case(x, k) => 
				x.map { y => 
					y * nextInt(1000000)
				}
				//printf("\r%d", k * nextInt(1000000))
			}
			println(xx)
			//println(xx + " yey")
		}
	}
}