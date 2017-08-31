package ohnosequences.db.tcr.test

import ohnosequences.db.tcr._
import ohnosequences.cosas._, klists._, types._
import ohnosequences.fastarious.fasta._
import java.nio.file.Files
import ohnosequences.test._
import ohnosequences.awstools.s3._
import util.{ Success, Failure }

abstract class WellFormedInputs(
  val species : Species       ,
  val chain   : Chain         ,
  val segments: Set[Segment]
)
extends org.scalatest.FunSuite {

  val geneTypes =
    segments map { GeneType(species, chain, _) }

  def idsFor(segment: Segment): List[String] =
    inputData.sequences(GeneType(species, chain, segment))
      .collect { case Right(fa) => fa }
      .map(fa => fa.getV(header).id)
      .toList

  val description: String =
    s"${species} ${chain}:"

  test(s"${description} well-formed FASTA files") {

    geneTypes foreach { geneType =>
      (inputData sequences geneType) foreach { lr => assert( lr.isRight ) }
    }
  }

  test(s"${description} FASTA files have no duplicate IDs") {

    segments foreach { segment =>

      val ids =
        idsFor(segment)

      assert { ids.distinct == ids }
    }
  }

  test(s"${description} all J IDs are in the aux file, same order") {

    assert { idsFor(Segment.J) == inputData.auxIDs(species).toList }
  }
}

abstract class GenerateFASTA(
  val species : Species       ,
  val chain   : Chain         ,
  val segments: Set[Segment]
)
extends org.scalatest.FunSuite {

  val geneTypes =
    segments map { GeneType(species, chain, _) }

  val description: String =
    s"${species} ${chain}:"

  test(s"${description} generate FASTA files with scoped IDs") {

    geneTypes foreach { geneType =>

      val writeTo =
        outputData fastaFileFor geneType

      val deleteIfThere =
        Files deleteIfExists writeTo.toPath


      val writeFiles =
        inputData.sequences(geneType)
          .collect({ case Right(a) => a })
          .map(
            { fa =>

              val gene =
                Gene(fa.getV(header).id, geneType)

              FASTA(
                header( FastaHeader(data.fastaHeader(gene)) ) ::
                sequence( fa.getV(sequence) )                 ::
                *[AnyDenotation]
              )
            }
          )
          .appendTo(writeTo)
    }
  }

  test("TCR beta human: upload files to S3", ReleaseOnlyTest) {

    val s3 =
      S3Client()

    val transferManager =
      s3.createTransferManager

    geneTypes foreach { geneType =>

      transferManager.upload(
        outputData fastaFileFor geneType,
        data fasta geneType
      )
      match {
        case Success(_) => succeed
        case Failure(a) => fail(a.toString)
      }
    }

    transferManager.shutdownNow
  }
}

abstract class AuxFileGeneration(val species: Species, val chain: Chain) extends org.scalatest.FunSuite {

  val geneType =
    GeneType(species, chain, Segment.J)

  val description: String =
    s"${species} ${chain}:"

  test(s"${description} generate aux file") {

    io.printToFile(outputData.auxFileFor(species, chain)) {
      p =>
        inputData.aux(species)
          .map({ a => a.copy(id = data.fastaHeader(Gene(a.id, geneType))) })
          .foreach({ a => p println a.toTSVRow })
    }
  }
}
