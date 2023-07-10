package test

import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

/** Abstract Test. */
@RunWith(classOf[JUnitRunner])
abstract class AbstractTest extends AnyFunSpec with Matchers with BeforeAndAfter:
  println(s"Test ${this.getClass.getSimpleName} started.")
