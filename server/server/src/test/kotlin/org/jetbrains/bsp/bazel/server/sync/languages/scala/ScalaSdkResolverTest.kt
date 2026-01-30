package org.jetbrains.bsp.bazel.server.sync.languages.scala

import io.kotest.matchers.shouldBe
import org.jetbrains.bsp.bazel.bazelrunner.utils.BazelInfo
import org.jetbrains.bsp.bazel.bazelrunner.utils.BazelRelease
import org.jetbrains.bsp.bazel.bazelrunner.utils.orLatestSupported
import org.jetbrains.bsp.bazel.info.BspTargetInfo
import org.jetbrains.bsp.bazel.server.paths.BazelPathsResolver
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.createTempDirectory

/**
 * Tests that [ScalaSdkResolver.extractVersionFromJar] correctly resolves Scala version from
 * compiler jar by reading compiler.properties (maven.version.number) when the jar filename
 * does not match the path version pattern.
 */
class ScalaSdkResolverTest {
  private lateinit var tempDir: Path
  private lateinit var resolver: ScalaSdkResolver

  companion object {
    private const val SCALA_VERSION = "2.13.10"
    private val SCALA_COMPILER_JAR_URL =
      URL("https://repo1.maven.org/maven2/org/scala-lang/scala-compiler/$SCALA_VERSION/scala-compiler-$SCALA_VERSION.jar")
    private const val RENAMED_JAR_NAME = "scala-compiler.jar"
  }

  @BeforeEach
  fun beforeEach() {
    tempDir = createTempDirectory("scala-sdk-resolver-test")
    val bazelInfo =
      BazelInfo(
        execRoot = tempDir.toString(),
        outputBase = tempDir,
        workspaceRoot = tempDir,
        release = BazelRelease.fromReleaseString("release 6.0.0").orLatestSupported(),
        false,
        true,
      )
    resolver = ScalaSdkResolver(BazelPathsResolver(bazelInfo))
  }

  @AfterEach
  fun afterEach() {
    tempDir.toFile().deleteRecursively()
  }

  @Test
  fun `resolveSdk extracts Scala version from compiler jar when filename has no version`() {
    // Download the real Scala compiler jar and rename so that extractVersionFromPath returns null
    // and extractVersionFromJar is used (reads compiler.properties maven.version.number)
    val downloadedJar = tempDir.resolve("scala-compiler-$SCALA_VERSION.jar")
    SCALA_COMPILER_JAR_URL.openStream().use { input ->
      Files.copy(input, downloadedJar, StandardCopyOption.REPLACE_EXISTING)
    }
    val renamedJar = tempDir.resolve(RENAMED_JAR_NAME)
    Files.move(downloadedJar, renamedJar, StandardCopyOption.REPLACE_EXISTING)

    val targetInfo =
      BspTargetInfo.TargetInfo
        .newBuilder()
        .setId("//test:target")
        .setScalaTargetInfo(
          BspTargetInfo.ScalaTargetInfo
            .newBuilder()
            .addCompilerClasspath(
              BspTargetInfo.FileLocation
                .newBuilder()
                .setRelativePath(renamedJar.toAbsolutePath().toString())
                .build(),
            ).build(),
        ).build()

    val sdk = resolver.resolveSdk(targetInfo)

    sdk shouldBe
      ScalaSdk(
        organization = "org.scala-lang",
        version = SCALA_VERSION,
        binaryVersion = "2.13",
        compilerJars = listOf(renamedJar.toUri()),
      )
  }
}
