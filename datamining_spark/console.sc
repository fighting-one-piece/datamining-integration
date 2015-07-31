object console {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  val arr = Array("one", "two", "three")          //> arr  : Array[String] = Array(one, two, three)
  val len = arr.length                            //> len  : Int = 3
  for (a <- 0 to len)
  	print(a)                                  //> 0123
  arr.foreach(i => println(i))                    //> one
                                                  //| two
                                                  //| three
  println(arr.length)                             //> 3
  arr.foreach(println)                            //> one
                                                  //| two
                                                  //| three
  
  val greetStrings = new Array[String](3)         //> greetStrings  : Array[String] = Array(null, null, null)
	greetStrings.update(0, "Hello")
	greetStrings.update(1, ", ")
	greetStrings.update(2, "world!\n")
	for (i <- 0.to(2))
		print(greetStrings.apply(i))      //> Hello, world!


	var a1 = Array("zero", "one", "two")      //> a1  : Array[String] = Array(zero, one, two)
	a1.foreach(a => println(a))               //> zero
                                                  //| one
                                                  //| two

	var a2 = Array.apply("zero", "one", "two")//> a2  : Array[String] = Array(zero, one, two)
	a2.foreach(a => println(a))               //> zero
                                                  //| one
                                                  //| two

	val oneTwo = List(1, 2)                   //> oneTwo  : List[Int] = List(1, 2)
	val threeFour = List(3, 4)                //> threeFour  : List[Int] = List(3, 4)
	val oneTwoThreeFour = oneTwo ::: threeFour//> oneTwoThreeFour  : List[Int] = List(1, 2, 3, 4)
	println(oneTwo + " and " + threeFour + " were not mutated.")
                                                  //> List(1, 2) and List(3, 4) were not mutated.
	println("Thus, " + oneTwoThreeFour + " is a new List.")
                                                  //> Thus, List(1, 2, 3, 4) is a new List.

	val twoThree = List(2, 3)                 //> twoThree  : List[Int] = List(2, 3)
	val oneTwoThree = 1 :: twoThree           //> oneTwoThree  : List[Int] = List(1, 2, 3)
	println(oneTwoThree)                      //> List(1, 2, 3)
	
	val oneTwoThreeE = 1 :: 2 :: 3 :: Nil     //> oneTwoThreeE  : List[Int] = List(1, 2, 3)
	println(oneTwoThreeE)                     //> List(1, 2, 3)
	
	val pair = (99, "Luftballons")            //> pair  : (Int, String) = (99,Luftballons)
	println(pair._1)                          //> 99
	println(pair._2)                          //> Luftballons
	
	var jetSet = Set("Boeing", "Airbus")      //> jetSet  : scala.collection.immutable.Set[String] = Set(Boeing, Airbus)
	jetSet += "Lear"
	println(jetSet.contains("Cessna"))        //> false
	jetSet.foreach(s => println(s))           //> Boeing
                                                  //| Airbus
                                                  //| Lear
	
	import scala.collection.mutable.Map
	val treasureMap = Map[Int, String]()      //> treasureMap  : scala.collection.mutable.Map[Int,String] = Map()
	treasureMap += (1 -> "Go to island.")     //> res0: console.treasureMap.type = Map(1 -> Go to island.)
	treasureMap += (2 -> "Find big X on ground.")
                                                  //> res1: console.treasureMap.type = Map(2 -> Find big X on ground., 1 -> Go to
                                                  //|  island.)
	treasureMap += (3 -> "Dig.")              //> res2: console.treasureMap.type = Map(2 -> Find big X on ground., 1 -> Go to
                                                  //|  island., 3 -> Dig.)
	println(treasureMap(2))                   //> Find big X on ground.
	
	val romanNumeral = Map( 1 -> "I", 2 -> "II", 3 -> "III", 4 -> "IV", 5 -> "V" )
                                                  //> romanNumeral  : scala.collection.mutable.Map[Int,String] = Map(2 -> II, 5 -
                                                  //| > V, 4 -> IV, 1 -> I, 3 -> III)
	println(romanNumeral(4))                  //> IV
	
	def a(x:Int)=println(x)                   //> a: (x: Int)Unit
	a(10)                                     //> 10
  
  val map = Map[String, Int]()                    //> map  : scala.collection.mutable.Map[String,Int] = Map()
  map += ("one" -> 1)                             //> res3: console.map.type = Map(one -> 1)
  map += ("two" -> 2)                             //> res4: console.map.type = Map(one -> 1, two -> 2)
  println(map.get("one"))                         //> Some(1)
  println(map.get("three"))                       //> None
  println(map.getOrElse("three", 3))              //> 3
  
  import java.util.{Date, Locale}
  import java.text.DateFormat
  import java.text.DateFormat._
  
  val date = new Date                             //> date  : java.util.Date = Sun Jul 19 01:10:24 CST 2015
  println(date)                                   //> Sun Jul 19 01:10:24 CST 2015
	println(date.getTime())                   //> 1437239424931
	
	val df = getDateInstance(LONG,Locale.FRANCE)
                                                  //> df  : java.text.DateFormat = java.text.SimpleDateFormat@f8749a64
	println(df format date)                   //> 19 juillet 2015
	
}