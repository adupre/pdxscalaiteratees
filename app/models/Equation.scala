package models

object Equation {
  import Math._

  var a = .5d
  var b = .3d
  var c = .2d
  var d = .4d

  def x = java.lang.System.currentTimeMillis / 200
  def y = a * cos(b * (x * x)) + c * sin(d * x)

  var contributors: Set[String] = Set()
  def clearContributors = { contributors = Set() }
}
