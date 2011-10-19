name := "csw-client"

organization := "eu.udig"

version := "1.0-SNAPSHOT"

scalaVersion := "2.9.1"

seq(ProguardPlugin.proguardSettings :_*)

proguardOptions += "-keep public class eu.udig.** { *; }"