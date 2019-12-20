import util.Util

import scala.annotation.tailrec

object Day20 extends App {
  val input1 = Util.loadDayKeepWhitespace(20).split("\n")

  /*
  val input1 = """         A
                 |         A
                 |  #######.#########
                 |  #######.........#
                 |  #######.#######.#
                 |  #######.#######.#
                 |  #######.#######.#
                 |  #####  B    ###.#
                 |BC...##  C    ###.#
                 |  ##.##       ###.#
                 |  ##...DE  F  ###.#
                 |  #####    G  ###.#
                 |  #########.#####.#
                 |DE..#######...###.#
                 |  #.#########.###.#
                 |FG..#########.....#
                 |  ###########.#####
                 |             Z
                 |             Z       """.stripMargin.split("\n")



  val input1 = """             Z L X W       C
                 |             Z P Q B       K
                 |  ###########.#.#.#.#######.###############
                 |  #...#.......#.#.......#.#.......#.#.#...#
                 |  ###.#.#.#.#.#.#.#.###.#.#.#######.#.#.###
                 |  #.#...#.#.#...#.#.#...#...#...#.#.......#
                 |  #.###.#######.###.###.#.###.###.#.#######
                 |  #...#.......#.#...#...#.............#...#
                 |  #.#########.#######.#.#######.#######.###
                 |  #...#.#    F       R I       Z    #.#.#.#
                 |  #.###.#    D       E C       H    #.#.#.#
                 |  #.#...#                           #...#.#
                 |  #.###.#                           #.###.#
                 |  #.#....OA                       WB..#.#..ZH
                 |  #.###.#                           #.#.#.#
                 |CJ......#                           #.....#
                 |  #######                           #######
                 |  #.#....CK                         #......IC
                 |  #.###.#                           #.###.#
                 |  #.....#                           #...#.#
                 |  ###.###                           #.#.#.#
                 |XF....#.#                         RF..#.#.#
                 |  #####.#                           #######
                 |  #......CJ                       NM..#...#
                 |  ###.#.#                           #.###.#
                 |RE....#.#                           #......RF
                 |  ###.###        X   X       L      #.#.#.#
                 |  #.....#        F   Q       P      #.#.#.#
                 |  ###.###########.###.#######.#########.###
                 |  #.....#...#.....#.......#...#.....#.#...#
                 |  #####.#.###.#######.#######.###.###.#.#.#
                 |  #.......#.......#.#.#.#.#...#...#...#.#.#
                 |  #####.###.#####.#.#.#.#.###.###.#.###.###
                 |  #.......#.....#.#...#...............#...#
                 |  #############.#.#.###.###################
                 |               A O F   N
                 |               A A D   M                     """.stripMargin.split("\n")

*/
  val input = input1.map(_.padTo(input1(3).length+2, ' '))


  val parsed = (for {
    x <- input(0).indices
    y <- input.indices
  } yield label(input, x, y)).toMap


  val labels = parsed.values.filter(_.length > 1)

  //val finalMap = update(parsed, labels.toList.distinct)

  //val finalLabels = finalMap.values.filter(_.length > 1)

  val graph = labels.map(l => (l, bfs(parsed, l))).toMap

  //println(dijkstra(graph, "aa", "zz"))

  Util.time{println(dijkstraRec(graph, State("aa", 0), State("zz", 0)))}
  
  /*

  for(y <- input.indices) {
    for(x <- input(0).indices) {
      print(parsed((x,y)))
    }
    println()
  }
   */


  case class State(label: String, layer: Int)

  def dijkstraRec(graph: Map[String, List[(String, Int)]], start: State, target: State): Int = {
    val distance = scala.collection.mutable.HashMap[State, Int]().withDefault(_ => 1000000)
    distance(start) = 0
    val visited = scala.collection.mutable.HashSet[State]()
    val q = scala.collection.mutable.ArrayBuffer[State]()
    q.addAll(start :: (0 to 200).flatMap(layer => graph.keys.map(k => State(k, layer))).toList)
    var max = 0
    while(q.nonEmpty) {
      val node = q.minBy(distance)
      q.remove(q.indexOf(node))
      if(node.layer > max) {
        max = node.layer
        println(max)
      }
      for(neigh <- recNeighbors(graph, node)) {
        if(q.contains(neigh._1) && neigh._1.layer < 200) {
          if(neigh._1 == State("zz", 0)) {
            val newDistance = distance(node) + neigh._2
            return Math.min(distance(neigh._1), newDistance)
          }
          val newDistance = distance(node) + neigh._2
          if(newDistance < distance(neigh._1)) {
            distance(neigh._1) = newDistance
          }
        }
      }
      visited.add(node)
    }

    0
  }

  def recNeighbors(graph: Map[String, List[(String, Int)]], which: State): List[(State, Int)] = {
    val n = graph(which.label).map(e => (State(e._1, which.layer), e._2)).filter(s => !(s._1.layer != 0 && (s._1.label == "aa" || s._1.label == "zz")))

    val fin = if(which.label != "aa" && which.label != "zz") {
      if(which.label.forall(_.isLower)) {
        if(which.layer != 0)
          (State(which.label.toUpperCase, which.layer - 1), 1) :: n
        else
          n
      }
      else{
        (State(which.label.toLowerCase, which.layer + 1), 1) :: n
      }
    }
    else {
      n
    }

    fin
  }



  def dijkstra(graph: Map[String, List[(String, Int)]], start: String, target: String): Int = {
    val distance = scala.collection.mutable.HashMap[String, Int]().withDefault(_ => 1000)
    val pred = scala.collection.mutable.HashMap[String, String]()
    distance(start) = 0
    val q = scala.collection.mutable.ArrayBuffer[String]()
    q.addAll(graph.keys)
    while(q.nonEmpty) {
      val node = q.minBy(e => distance(e))
      q.remove(q.indexOf(node))
      for(neigh <- graphNeighbors(graph, node)) {
        if(q.contains(neigh._1)) {
          val newDist = distance(node) + neigh._2
          if(newDist < distance(neigh._1)) {
            distance(neigh._1) = newDist
            pred(neigh._1) = node
          }
        }
      }
    }
    distance(target)
  }

  def graphNeighbors(graph: Map[String, List[(String, Int)]], which: String): List[(String, Int)] = {
    val n = graph(which)

    val fin = if(which != "zz" && which != "aa") {
      if(which.forall(_.isLower)) {
        n ++ List((which.toUpperCase, 1))
      } else {
        n ++ List((which.toLowerCase, 1))
      }
    } else {
      n
    }
    fin
  }


  @tailrec
  def update(map: Map[(Int, Int), String], labels: List[String]): Map[(Int, Int), String] = labels match {
    case x::xs =>
      val coords = map.find(_._2 == x).get._1
      update(map.updated(coords, x.toLowerCase()), xs)
    case Nil =>
      map
  }

  def label(input: Array[String], x: Int, y: Int): ((Int, Int), String) = {
    input(y)(x) match {
      case '#' =>
        ((x, y), "#")
      case '.' =>
        if(y > 1 && input(y - 1)(x).isUpper){
          if(y > input.length/2)
            ((x, y), input(y-2)(x) +""+ input(y-1)(x))
          else
            ((x, y), input(y-2)(x).toLower +""+ input(y-1)(x).toLower)
        }
        else if(y < (input.length-2) && input(y+1)(x).isUpper) {
          if(y < input.length/2)
            ((x, y), input(y+1)(x) +""+ input(y+2)(x))
          else
            ((x, y), input(y+1)(x).toLower +""+ input(y+2)(x).toLower)
        }
        else if(x > 1 && input(y)(x - 1).isUpper) {
          if(x > input(0).length/2)
            ((x, y), input(y)(x-2) + "" + input(y)(x-1))
          else
            ((x, y), input(y)(x-2).toLower + "" + input(y)(x-1).toLower)
        }
        else if(x < (input(0).length - 2) && input(y)(x+1).isUpper) {
          if(x < input(0).length/2)
            ((x, y), input(y)(x+1) + "" + input(y)(x+2))
          else
            ((x, y), input(y)(x+1).toLower + "" + input(y)(x+2).toLower)
        }
        else ((x, y), ".")
      case _ =>
        ((x, y), " ")
    }
  }

  def bfs(map: Map[(Int, Int), String], find: String): List[(String, Int)] = {
    var toVisit = scala.collection.mutable.HashSet[(Int, Int)](map.find(_._2 == find).get._1)
    val visited = scala.collection.mutable.HashSet[(Int, Int)]()
    var depth = 0
    val list = scala.collection.mutable.ArrayBuffer[(String, Int)]()
    while(toVisit.nonEmpty) {
      val newNodes = scala.collection.mutable.HashSet[(Int, Int)]()
      for(node <- toVisit) {
        val next = neighbors(node).filter(p => {
          val l = map.getOrElse(p, " ")
          !visited.contains(p) && l != "#" && l != " "
        })
        for(n <- next) {
          if(map(n).length > 1) {
            list.append((map(n), depth + 1))
          } else {
            newNodes.add(n)
          }
        }

        visited.add(node)
      }
      depth = depth + 1
      toVisit = newNodes
    }
    list.toList
  }

  def neighbors(pos: (Int, Int)): List[(Int, Int)] = {
    val u = (pos._1, pos._2 - 1)
    val d = (pos._1, pos._2 + 1)
    val l = (pos._1 - 1, pos._2)
    val r = (pos._1 + 1, pos._2)
    List(u, d, l, r)
  }
}

