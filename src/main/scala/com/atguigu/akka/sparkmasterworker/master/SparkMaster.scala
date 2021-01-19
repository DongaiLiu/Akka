package com.atguigu.akka.sparkmasterworker.master

import akka.actor.{Actor, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common.{HeartBeat, RegisterWorkerInfo, RemoveTimeOutWorker, StartTimeOutWorker, WorkerInfo}
import com.typesafe.config.ConfigFactory
import scala.language.postfixOps

import scala.collection.mutable
import scala.concurrent.duration.DurationInt

class SparkMaster extends Actor {
  val workers = mutable.Map[String, WorkerInfo]()

  override def receive: Receive = {
    case "start" =>
      println("master服务器启动了")
      self ! StartTimeOutWorker
    case RegisterWorkerInfo(id, cpu, ram) =>
      if (!workers.contains(id)) {
        val workerInfo = new WorkerInfo(id, cpu, ram)
        workers += ((id, workerInfo))
        println("服务器的workers = " + workers)
        sender() ! RegisterWorkerInfo
      }
    case HeartBeat(id) =>
      val workerInfo = workers(id)
      workerInfo.lastHeartBeat = System.currentTimeMillis()
      println("master更新了 " + id + " 心跳时间...")
    case StartTimeOutWorker =>
      println("开始了定时检测worker心跳的任务")
      import context.dispatcher
      context.system.scheduler.scheduleAtFixedRate(0 millis, 9000 millis, self, RemoveTimeOutWorker)
    case RemoveTimeOutWorker => {
      val workerInfos = workers.values
      val nowTime = System.currentTimeMillis()
      workerInfos.filter(workerInfo => (nowTime - workerInfo.lastHeartBeat) > 6000)
        .foreach(workerInfo => workers.remove(workerInfo.id))
      println("当前有 " + workers.size + " 个worker存活的")
    }
  }

}

object SparkMaster {
  def main(args: Array[String]): Unit = {
    if (args.length != 3) {
      println("请输入参数 host port sparkMasterActor名字")
      sys.exit()
    }

    val host = args(0)
    val port = args(1)
    val name = args(2)

    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider=cluster
         |akka.actor.allow-java-serialization=on
         |akka.remote.artery.transport=tcp
         |akka.remote.artery.canonical.hostname=$host
         |akka.remote.artery.canonical.port=$port
         """.stripMargin
    )
    val sparkMasterSystem = ActorSystem("SparkMaster", config)
    val sparkMasterRef = sparkMasterSystem.actorOf(Props[SparkMaster], s"$name")
    sparkMasterRef ! "start"
  }
}