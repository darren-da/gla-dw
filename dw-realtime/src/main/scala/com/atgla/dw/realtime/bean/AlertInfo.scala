package com.atgla.dw.realtime.bean

case class AlertInfo(
                    //增加一个id
                      mid:String,
                             uids:java.util.HashSet[String],
                             itemIds:java.util.HashSet[String],
                             events:java.util.List[String],
                             ts:Long
                           )


