
```scala
package ohnosequences.db.tcr

/** Trait of a Species representing a specific taxon */
sealed
trait Species {

  val taxonomyID: String
}

/** A comprehensive list of all the supported Species */
case object Species {
  /** Human species, taxon 9606 */
  case object human extends Species { val taxonomyID = "9606"   }

  /** Mouse species, taxon 10090 */
  case object mouse extends Species { val taxonomyID = "10090"  }
}

/** Trait of a Chain */
sealed
trait Chain {

  lazy val name: String =
    toString
}

/** A comprehensive list of all the supported Chains */
case object Chain {
  /** TCR α */
  case object TRA extends Chain
  /** TCR β */
  case object TRB extends Chain
}

/** A trait for a segment of a gene */
sealed
trait Segment {

  lazy val name: String =
    toString
}

/** A comprehensive list of all the supported gene segments */
case object Segment {
  /** (V)ariable segment */
  case object V extends Segment

  /** (D)iversity segment */
  case object D extends Segment

  /** (J)oining segment */
  case object J extends Segment
}

/** Gene type, specified by a [[Species]], a [[Chain]] and a [[Segment]] */
final
case class GeneType(
  val species : Species,
  val chain   : Chain,
  val segment : Segment
)
{
  final
  def ID: String =
    s"${species.taxonomyID}.${chain.name}.${segment.name}"
}

/** A [[GeneType]] with a name */
final
case class Gene(
  val name      : String,
  val geneType  : GeneType
)
{
  final
  def ID: String =
    s"${geneType.ID}.${name}"
}

```




[main/scala/names.scala]: names.scala.md
[main/scala/data.scala]: data.scala.md
[main/scala/package.scala]: package.scala.md
[main/scala/model.scala]: model.scala.md
[test/scala/io.scala]: ../../test/scala/io.scala.md
[test/scala/inputData.scala]: ../../test/scala/inputData.scala.md
[test/scala/humanTRB.scala]: ../../test/scala/humanTRB.scala.md
[test/scala/genericTests.scala]: ../../test/scala/genericTests.scala.md
[test/scala/outputData.scala]: ../../test/scala/outputData.scala.md
[test/scala/humanTRA.scala]: ../../test/scala/humanTRA.scala.md
[test/scala/dataGeneration.scala]: ../../test/scala/dataGeneration.scala.md