package org.jetbrains.bsp.bazel

import ch.epfl.scala.bsp4j.BuildTarget
import ch.epfl.scala.bsp4j.BuildTargetCapabilities
import ch.epfl.scala.bsp4j.BuildTargetIdentifier
import ch.epfl.scala.bsp4j.WorkspaceBuildTargetsResult
import org.jetbrains.bsp.bazel.base.BazelBspTestBaseScenario
import org.jetbrains.bsp.bazel.base.BazelBspTestScenarioStep
import kotlin.time.Duration.Companion.minutes

object BazelBspTypeScriptProjectTest : BazelBspTestBaseScenario() {
  private val testClient = createTestkitClient()

  override fun additionalServerInstallArguments(): Array<String> =
    arrayOf(
      "--enabled-rules",
      "aspect_rules_jest",
      "--enabled-rules",
      "aspect_rules_ts",
      "--enabled-rules",
      "rules_ts",
      "--enabled-rules",
      "rules_jest",
    )

  @JvmStatic
  fun main(args: Array<String>) = executeScenario()

  override fun expectedWorkspaceBuildTargetsResult(): WorkspaceBuildTargetsResult =
    WorkspaceBuildTargetsResult(
      listOf(
        helloTestBuildTarget(),
        exampleLibTestBuildTarget(),
      ),
    )

  private fun helloTestBuildTarget(): BuildTarget {
    val buildTarget =
      BuildTarget(
        BuildTargetIdentifier("$targetPrefix//example:hello_test"),
        listOf("test"),
        listOf("typescript"),
        emptyList(),
        BuildTargetCapabilities().also {
          it.canCompile = false
          it.canTest = true
          it.canRun = false
          it.canDebug = false
        },
      )
    buildTarget.displayName = "$targetPrefix//example:hello_test"
    buildTarget.baseDirectory = "file://\$WORKSPACE/example/"
    return buildTarget
  }

  private fun exampleLibTestBuildTarget(): BuildTarget {
    val buildTarget =
      BuildTarget(
        BuildTargetIdentifier("$targetPrefix//lib:example_lib_test"),
        listOf("test"),
        listOf("typescript"),
        emptyList(),
        BuildTargetCapabilities().also {
          it.canCompile = false
          it.canTest = true
          it.canRun = false
          it.canDebug = false
        },
      )
    buildTarget.displayName = "$targetPrefix//lib:example_lib_test"
    buildTarget.baseDirectory = "file://\$WORKSPACE/lib/"
    return buildTarget
  }

  private fun workspaceBuildTargets(): BazelBspTestScenarioStep {
    val workspaceBuildTargetsResult = expectedWorkspaceBuildTargetsResult()

    return BazelBspTestScenarioStep("workspace build targets") {
      testClient.testWorkspaceTargets(
        1.minutes,
        workspaceBuildTargetsResult,
      )
    }
  }

  override fun scenarioSteps(): List<BazelBspTestScenarioStep> =
    listOf(
      workspaceBuildTargets(),
    )
}
