package com.atguigu.akka.yellowchicken.server

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import com.atguigu.akka.yellowchicken.common.{ClientMessage, ServerMessage}
import com.typesafe.config.ConfigFactory

class YellowChickenServer extends Actor {
  override def receive: Receive = {
    case "start" => println("start 小黄鸡客服开始工作了....")
    case ClientMessage(mes) =>
      mes match {
        case "大数据学费" => sender() ! ServerMessage("35000RMB")
        case "学校地址" => sender() ! ServerMessage("北京昌平xx路xx大楼")
        case "学习什么技术" => sender() ! ServerMessage("大数据 前端 python")
        case _ => sender() ! ServerMessage("你说的啥子~")
      }
  }
}

object YellowChickenServer extends App {
  val host = "127.0.0.1"
  val port = 9999

  val config = ConfigFactory.parseString(
    s"""
       |akka.actor.provider=cluster
       |akka.actor.allow-java-serialization=on
       |akka.remote.artery.transport=tcp
       |akka.remote.artery.canonical.hostname=$host
       |akka.remote.artery.canonical.port=$port
         """.stripMargin
  )

  val serverActorSystem: ActorSystem = ActorSystem("Server", config)
  val yellowChickenServerRef: ActorRef = serverActorSystem.actorOf(Props[YellowChickenServer], "YellowChickenServer")

  yellowChickenServerRef ! "start"
}
