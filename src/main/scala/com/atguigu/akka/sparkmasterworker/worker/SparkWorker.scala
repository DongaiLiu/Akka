package com.atguigu.akka.sparkmasterworker.worker

import akka.actor.{Actor, ActorSelection, ActorSystem, Props}
import com.atguigu.akka.sparkmasterworker.common.{HeartBeat, RegisterWorkerInfo, SendHeartBeat}
import com.typesafe.config.ConfigFactory
import scala.language.postfixOps

import scala.concurrent.duration.DurationInt

class SparkWorker(masterHost: String, masterPort: Int, masterName: String) extends Actor{
  var masterProxy: ActorSelection = _
  val id = java.util.UUID.randomUUID().toString

  override def preStart(): Unit = {
    println("preStart()调用")
    masterProxy = context.actorSelection(s"akka://SparkMaster@$masterHost:$masterPort/user/$masterName")
    println("masterProxy=" + masterProxy)
  }

  override def receive: Receive = {
    case "start" =>
      println("worker启动了")
      masterProxy ! RegisterWorkerInfo(id, 16, 16 * 1024)
    case RegisterWorkerInfo =>
      println("workerid = " + id + "注册成功")
      import context.dispatcher

      context.system.scheduler.scheduleAtFixedRate(0 millis, 3000 millis, self, SendHeartBeat)
    case SendHeartBeat => {
      println("worker = " + id + "给master发送心跳")
      masterProxy ! HeartBeat(id)
    }
  }
}

object SparkWorker{
  def main(args: Array[String]): Unit = {

    if (args.length != 6) {
      println("请输入参数 workerHost workerPort workerName masterHost masterPort masterName")
      sys.exit()
    }

    val workerHost = args(0)
    val workerPort = args(1)
    val workerName = args(2)
    val masterHost = args(3)
    val masterPort = args(4)
    val masterName = args(5)

    val config = ConfigFactory.parseString(
      s"""
         |akka.actor.provider=cluster
         |akka.actor.allow-java-serialization=on
         |akka.remote.artery.transport=tcp
         |akka.remote.artery.canonical.hostname=$workerHost
         |akka.remote.artery.canonical.port=$workerPort
         """.stripMargin
    )

    val sparkWorkerSystem = ActorSystem("SparkWorker", config)
    val sparkWorkerRef = sparkWorkerSystem.actorOf(Props(new SparkWorker(masterHost, masterPort.toInt, masterName)), s"$workerName")

    sparkWorkerRef ! "start"
  }
}
