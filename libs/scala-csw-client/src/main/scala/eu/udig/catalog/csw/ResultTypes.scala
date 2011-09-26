package eu.udig.catalog.csw

object ResultTypes extends Enumeration {
  type ResultType = Value
  val hits,results = Value
  val resultsWithSummary = Value("results_with_summary")
}
