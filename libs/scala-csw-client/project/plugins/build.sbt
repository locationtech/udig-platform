// --------------  Proguard plugin    -----------------

resolvers += "Proguard plugin repo" at "http://siasia.github.com/maven2"

libraryDependencies <+= sbtVersion("com.github.siasia" %% "xsbt-proguard-plugin" % _)


// --------------  Eclipse plugin    -----------------

resolvers += {
  val typesafeRepoUrl = new java.net.URL("http://repo.typesafe.com/typesafe/releases")
  val pattern = Patterns(false, "[organisation]/[module]/[sbtversion]/[revision]/[type]s/[module](-[classifier])-[revision].[ext]")
  Resolver.url("Typesafe Repository", typesafeRepoUrl)(pattern)
}

libraryDependencies <<= (libraryDependencies, sbtVersion) { (deps, version) => 
  deps ++ Seq("com.typesafe.sbteclipse" % "sbteclipse_2.8.1" % "1.3-RC1" extra("sbtversion" -> version))
}
