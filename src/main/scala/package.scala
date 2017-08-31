package ohnosequences.db

import ohnosequences.awstools.s3._

package object tcr {

  val name: String =
    "ohnosequences.db.tcr"

  private val metadata =
    generated.metadata.tcr

  def projectID: String =
    s"${metadata.organization}.${metadata.artifact}.${metadata.version}"

  val s3prefix: S3Folder =
    s3"resources.ohnosequences.com" /
    metadata.organization           /
    metadata.artifact               /
    metadata.version                /
}