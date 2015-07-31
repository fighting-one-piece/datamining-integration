object console {;import org.scalaide.worksheet.runtime.library.WorksheetSupport._; def main(args: Array[String])=$execute{;$skip(60); 
  println("Welcome to the Scala worksheet");$skip(41); 
  val arr = Array("one", "two", "three");System.out.println("""arr  : Array[String] = """ + $show(arr ));$skip(23); 
  val len = arr.length;System.out.println("""len  : Int = """ + $show(len ));$skip(34); 
  for (a <- 0 to len)
  	print(a);$skip(31); 
  arr.foreach(i => println(i));$skip(22); 
  println(arr.length);$skip(23); 
  arr.foreach(println);$skip(46); 
  
  val greetStrings = new Array[String](3);System.out.println("""greetStrings  : Array[String] = """ + $show(greetStrings ));$skip(33); 
	greetStrings.update(0, "Hello");$skip(30); 
	greetStrings.update(1, ", ");$skip(36); 
	greetStrings.update(2, "world!\n");$skip(51); 
	for (i <- 0.to(2))
		print(greetStrings.apply(i));$skip(42); 


	var a1 = Array("zero", "one", "two");System.out.println("""a1  : Array[String] = """ + $show(a1 ));$skip(29); 
	a1.foreach(a => println(a));$skip(46); 

	var a2 = Array.apply("zero", "one", "two");System.out.println("""a2  : Array[String] = """ + $show(a2 ));$skip(29); 
	a2.foreach(a => println(a));$skip(27); 

	val oneTwo = List(1, 2);System.out.println("""oneTwo  : List[Int] = """ + $show(oneTwo ));$skip(28); 
	val threeFour = List(3, 4);System.out.println("""threeFour  : List[Int] = """ + $show(threeFour ));$skip(44); 
	val oneTwoThreeFour = oneTwo ::: threeFour;System.out.println("""oneTwoThreeFour  : List[Int] = """ + $show(oneTwoThreeFour ));$skip(62); 
	println(oneTwo + " and " + threeFour + " were not mutated.");$skip(57); 
	println("Thus, " + oneTwoThreeFour + " is a new List.");$skip(29); 

	val twoThree = List(2, 3);System.out.println("""twoThree  : List[Int] = """ + $show(twoThree ));$skip(33); 
	val oneTwoThree = 1 :: twoThree;System.out.println("""oneTwoThree  : List[Int] = """ + $show(oneTwoThree ));$skip(22); 
	println(oneTwoThree);$skip(42); 
	
	val oneTwoThreeE = 1 :: 2 :: 3 :: Nil;System.out.println("""oneTwoThreeE  : List[Int] = """ + $show(oneTwoThreeE ));$skip(23); 
	println(oneTwoThreeE);$skip(35); 
	
	val pair = (99, "Luftballons");System.out.println("""pair  : (Int, String) = """ + $show(pair ));$skip(18); 
	println(pair._1);$skip(18); 
	println(pair._2);$skip(41); 
	
	var jetSet = Set("Boeing", "Airbus");System.out.println("""jetSet  : scala.collection.immutable.Set[String] = """ + $show(jetSet ));$skip(18); 
	jetSet += "Lear";$skip(36); 
	println(jetSet.contains("Cessna"));$skip(33); 
	jetSet.foreach(s => println(s))
	
	import scala.collection.mutable.Map;$skip(78); 
	val treasureMap = Map[Int, String]();System.out.println("""treasureMap  : scala.collection.mutable.Map[Int,String] = """ + $show(treasureMap ));$skip(39); val res$0 = 
	treasureMap += (1 -> "Go to island.");System.out.println("""res0: console.treasureMap.type = """ + $show(res$0));$skip(47); val res$1 = 
	treasureMap += (2 -> "Find big X on ground.");System.out.println("""res1: console.treasureMap.type = """ + $show(res$1));$skip(30); val res$2 = 
	treasureMap += (3 -> "Dig.");System.out.println("""res2: console.treasureMap.type = """ + $show(res$2));$skip(25); 
	println(treasureMap(2));$skip(83); 
	
	val romanNumeral = Map( 1 -> "I", 2 -> "II", 3 -> "III", 4 -> "IV", 5 -> "V" );System.out.println("""romanNumeral  : scala.collection.mutable.Map[Int,String] = """ + $show(romanNumeral ));$skip(26); 
	println(romanNumeral(4));$skip(28); 
	
	def a(x:Int)=println(x);System.out.println("""a: (x: Int)Unit""");$skip(7); 
	a(10);$skip(35); 
  
  val map = Map[String, Int]();System.out.println("""map  : scala.collection.mutable.Map[String,Int] = """ + $show(map ));$skip(22); val res$3 = 
  map += ("one" -> 1);System.out.println("""res3: console.map.type = """ + $show(res$3));$skip(22); val res$4 = 
  map += ("two" -> 2);System.out.println("""res4: console.map.type = """ + $show(res$4));$skip(26); 
  println(map.get("one"));$skip(28); 
  println(map.get("three"));$skip(37); 
  println(map.getOrElse("three", 3))
  
  import java.util.{Date, Locale}
  import java.text.DateFormat
  import java.text.DateFormat._;$skip(125); 
  
  val date = new Date;System.out.println("""date  : java.util.Date = """ + $show(date ));$skip(16); 
  println(date);$skip(25); 
	println(date.getTime());$skip(48); 
	
	val df = getDateInstance(LONG,Locale.FRANCE);System.out.println("""df  : java.text.DateFormat = """ + $show(df ));$skip(25); 
	println(df format date)}
	
}
