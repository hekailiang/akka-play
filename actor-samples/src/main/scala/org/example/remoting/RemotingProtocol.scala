package org.example.remoting

/**
 * Created by kailianghe on 1/18/15.
 */
object RemotingProtocol {
  case class TransformationJob(text: String)
  case class TransformationResult(result: String)
  case class BackendRegistration()
  case class JobFailed(reason: String, job: TransformationJob)
}
