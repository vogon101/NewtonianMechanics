name := "PhysicsC2"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "org.lwjgl.lwjgl" % "lwjgl-platform" % "2.9.0" classifier "natives-windows" classifier "natives-linux" classifier "natives-osx",
  "slick-util" % "slick-util" % "1.0.0" from "http://slick.ninjacave.com/slick-util.jar",
  "org.lwjgl.lwjgl" % "lwjgl_util" % "2.9.0"
)

// Scala compiler options.
// See: https://tpolecat.github.io/2014/04/11/scalac-flags.html

scalacOptions ++= Seq(
  "-Xmax-classfile-name", "72",
  "-encoding", "UTF-8",
  "-deprecation",
  "-unchecked",
  "-feature",
  //  "-Xfatal-warnings",
  "-Xfuture",
  //  "-Xlint",
  "-language:implicitConversions",
  "-language:existentials",
  "-language:higherKinds",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Ywarn-unused-import",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code"
)

// JVM Options.
// See: http://blog.sokolenko.me/2014/11/javavm-options-production.html

javaOptions ++= Seq(
  "-server",
  "-Xverify:none",
  // Memory settings
  "-XX:InitialHeapSize=1G",
  "-XX:MaxHeapSize=1G",
  "-XX:NewRatio=1",
  "-XX:MetaspaceSize=32M",
  "-XX:MaxMetaspaceSize=32M",
  "-XX:SurvivorRatio=10",
  "-XX:CompressedClassSpaceSize=16M",
  "-XX:+UseCompressedOops",
  "-XX:+UseCompressedClassPointers",
  // Other settings
  s"-Djava.library.path=${unmanagedBase.value}",
  "-Dfile.encoding=UTF-8"
)

// Native libraries extraction - LWJGL has some native libraries provided as JAR files that I have to extract

compile in Compile <<= (compile in Compile).dependsOn(Def.task {
  val r = "^(\\w+).*".r
  val r(os) = System.getProperty( "os.name" )

  val jars = ( update in Compile ).value
    .select( configurationFilter( "compile" ) )
    .filter( _.name.contains( os.toLowerCase ) )

  jars foreach { jar =>
    println( s"[info] Processing '${jar.getName}' and saving to '${unmanagedBase.value}'" )
    IO.unzip(  jar, unmanagedBase.value )
  }

  Seq.empty[File]
})

mainClass in Compile := Some("com.vogonjeltz.physics.MainTest")

