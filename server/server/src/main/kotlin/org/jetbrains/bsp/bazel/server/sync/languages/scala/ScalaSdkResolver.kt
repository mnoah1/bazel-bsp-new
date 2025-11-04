package org.jetbrains.bsp.bazel.server.sync.languages.scala

import org.jetbrains.bsp.bazel.info.BspTargetInfo
import org.jetbrains.bsp.bazel.server.paths.BazelPathsResolver
import java.nio.file.Path
import java.util.jar.JarFile
import java.util.regex.Pattern

class ScalaSdkResolver(private val bazelPathsResolver: BazelPathsResolver) {
  fun resolveSdk(targetInfo: BspTargetInfo.TargetInfo): ScalaSdk? {
    if (!targetInfo.hasScalaTargetInfo()) {
      return null
    }
    val scalaTarget = targetInfo.scalaTargetInfo
    val compilerJars =
      bazelPathsResolver.resolvePaths(scalaTarget.compilerClasspathList).sorted()
    val maybeVersions = compilerJars.mapNotNull(::extractVersion)
    if (maybeVersions.none()) {
      return null
    }
    val version = maybeVersions.distinct().maxOf { it }
    val binaryVersion = toBinaryVersion(version)
    return ScalaSdk(
      "org.scala-lang",
      version,
      binaryVersion,
      compilerJars.map(bazelPathsResolver::resolveUri),
    )
  }

  private fun extractVersion(path: Path): String? {
    val name = path.fileName.toString()
    val matcher = VERSION_PATTERN.matcher(name)
    return if (matcher.matches()) matcher.group(1) else extractVersionFromJar(path)
  }

  private fun extractVersionFromJar(path: Path): String? {
    if(!path.toString().endsWith(".jar")) {
      return null
    }

    try {
      JarFile(path.toFile()).use { jar ->
        jar.manifest?.mainAttributes?.let { attributes ->
          attributes.getValue("Bundle-Version")?.let {
            val version = it.split("-").first()
            return version
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
    return null
  }

  private fun toBinaryVersion(version: String): String =
    version
      .split("\\.".toRegex())
      .toTypedArray()
      .take(2)
      .joinToString(".")

  companion object {
    private val VERSION_PATTERN =
      Pattern.compile("(?:processed_)?scala3?-(?:library|compiler|reflect)(?:_3)?-([.\\d]+)\\.jar")
  }
}
