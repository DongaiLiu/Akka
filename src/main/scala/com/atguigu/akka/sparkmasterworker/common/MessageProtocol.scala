package com.atguigu.akka.sparkmasterworker.common

case class RegisterWorkerInfo(id: String, cpu: Int, ram: Int) {

}

class WorkerInfo(val id: String, val cpu: Int, val ram: Int) {
  var lastHeartBeat: Long = System.currentTimeMillis()
}
case object RegisterWorkerInfo

case object SendHeartBeat

case class HeartBeat(id: String)

case object StartTimeOutWorker

case object RemoveTimeOutWorker