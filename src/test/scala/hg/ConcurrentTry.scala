package hg

import cats.effect.*
import cats.implicits.*
//import cats.syntax.all._
import cats.effect.unsafe.implicits.global
import fs2.Stream
import munit.CatsEffectSuite

import scala.concurrent.duration.*

class ConcurrentTry  extends CatsEffectSuite:
  
   
  test("Hi") {
    val data: Stream[IO, Int] = Stream.range(1, 50).covary[IO].metered(900.millis)
  
    val st = Stream.eval(fs2.concurrent.SignallingRef[IO, Int](0)).
      flatMap(s => Stream(s).concurrently(data.evalMap(s.set))).
      flatMap(_.discrete).takeWhile(_ < 9, true).
      flatTap(n => Stream.exec(IO(println(n))))
  
    st.compile.toList.unsafeRunSync()
  
  }  
  
  


