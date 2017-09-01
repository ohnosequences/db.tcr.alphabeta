
```scala
package ohnosequences.db.tcr.test

import ohnosequences.db.tcr._
import ohnosequences.fastarious._, fasta._
import scala.collection.JavaConverters._
import java.nio.file.Files
import java.io.File
import ohnosequences.cosas.types._

case object inputData {

  // TODO fix this
  def sequences(geneType: GeneType): Iterator[Either[ParseDenotationsError, FASTA.Value]] =
    io.sequences( new File(s"data/${geneType.species.toString}.tcr.beta.${geneType.segment.name}.fasta") )
    
  def idsFor(geneType: GeneType): List[String] =
    inputData.sequences(geneType)
      .collect { case Right(fa) => fa }
      .map(fa => fa.getV(header).id)
      .toList

  def auxLines(species: Species): Iterator[String] =
    io.lines( new File(s"data/${species.toString}.tcr.beta.J.aux") )

  def parseChain(rep: String): Option[Chain] =
    rep match {
      case "JA" => Some(Chain.TRA)
      case "JB" => Some(Chain.TRB)
      case _    => None
    }

  def chainToAuxFormat(chain: Chain): String =
    chain match {
      case Chain.TRA => "JA"
      case Chain.TRB => "JB"
    }

  def aux(species: Species): Iterator[Aux] =
    auxLines(species)
      .map(_.split('\t'))
      .filter(_.length == 4)
      .map(
        { fields =>
            Aux(
              id          = fields(0),
              codonStart  = fields(1).toInt,
              chain       = parseChain(fields(2)) getOrElse Chain.TRB,
              stopCDR3    = fields(3).toInt
            )
          }
      )

  def auxIDs(species: Species): Iterator[String] =
    auxLines(species) map { _.takeWhile(_ != '\t') }

  case class Aux(
    val id          : String,
    val codonStart  : Int   ,
    val chain       : Chain ,
    val stopCDR3    : Int
  )
  {

    def toTSVRow: String =
      Seq(id, codonStart.toString, chainToAuxFormat(chain), stopCDR3.toString).mkString("\t")
  }
}

```




[test/scala/outputData.scala]: outputData.scala.md
[test/scala/genericTests.scala]: genericTests.scala.md
[test/scala/inputData.scala]: inputData.scala.md
[test/scala/io.scala]: io.scala.md
[test/scala/humanTRB.scala]: humanTRB.scala.md
[main/scala/package.scala]: ../../main/scala/package.scala.md
[main/scala/model.scala]: ../../main/scala/model.scala.md
[main/scala/data.scala]: ../../main/scala/data.scala.md