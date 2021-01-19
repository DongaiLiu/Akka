package com.atguigu.akka

import com.typesafe.config.ConfigFactory

object Test {
  def main(args: Array[String]): Unit = {
    val (clientHost, clientPort, serverHost, serverPort) = ("127.0.0.1", 9990, "127.0.0.1", 9999)
    println(
      s"""
         |akka.actor.provider="akka.remote.RemoteActorRefProvider"
         |akka.remote.netty.tcp.hostname=$clientHost
         |akka.remote.netty.tcp.port=$clientPort
       """.stripMargin('#')
    )
  }
}
