package com.wjr.akka.sparkMasterWorker.common

/**
 *
 * @Package: com.wjr.akka.sparkMasterWorker.common
 * @ClassName: MessageProtocol
 * @Author: 86157
 * @CreateTime: 2021/7/14 10:14
 * @Description:
 */
class MessageProtocol {

}
//Worker注册信息
case class RegisterWorkerInfo(id:String,cpu:Int,ram:Int)
//WorkerInfo ，这个信息将来是保存到master的hashMap中的（用于管理worker）
//为什么不写case，因为在通信机制中，WorkerInfo不参与
//可以扩展，（增加worker上一次心跳时间）
class WorkerInfo(val id:String,val cpu:Int,val ram:Int){
    var lastHeartBeat:Long = System.currentTimeMillis()
}

//当worker注册成功，服务器返回一个这样一个对象
case object RegisterWorkerInfo

//worker 每隔一段时间，发给自己的信息
case object SendHeartBeat
//worker 每隔一段时间，向master发送的协议信息
case class HeartBeat(id:String)


//master给自己触发检查超时worker的信息
case object StartTimeOutWorker
//master给自己发消息，检查worker，对于心跳检查
case object RemoveTimeOutWorker