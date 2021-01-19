package com.atguigu.akka.actors

import akka.actor.{ActorRef, ActorSystem, Props}

object ActorGame extends App{
  val actorfactory: ActorSystem = ActorSystem("actorfactory")
  val bActorRef: ActorRef = actorfactory.actorOf(Props[BActor], "bActor")
  val aActorRef: ActorRef = actorfactory.actorOf(Props(new AActor(bActorRef)), "aActor")

  aActorRef ! "start"
}
