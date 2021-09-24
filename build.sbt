name := "Alpakka-Techhub"

version := "0.1"

scalaVersion := "2.13.6"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.16",
  "com.typesafe.akka" %% "akka-stream-kafka" % "2.1.1",
  "com.typesafe.akka" %% "akka-actor" % "2.6.16",
  "com.lightbend.akka" %% "akka-stream-alpakka-cassandra" % "1.1.0" ,
"com.datastax.cassandra" % "cassandra-driver-core" % "2.0.0-rc2",
  "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % "2.0.0",
  "com.typesafe.play" %% "play-json" % "2.8.1"
)
