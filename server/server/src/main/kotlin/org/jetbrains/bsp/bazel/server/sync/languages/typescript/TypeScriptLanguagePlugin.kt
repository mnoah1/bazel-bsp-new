package org.jetbrains.bsp.bazel.server.sync.languages.typescript

import ch.epfl.scala.bsp4j.BuildTarget
import org.jetbrains.bsp.bazel.info.BspTargetInfo.TargetInfo
import org.jetbrains.bsp.bazel.server.paths.BazelPathsResolver
import org.jetbrains.bsp.bazel.server.sync.languages.LanguagePlugin

class TypeScriptLanguagePlugin(private val bazelPathsResolver: BazelPathsResolver) : LanguagePlugin<TypeScriptModule>() {
  override fun applyModuleData(moduleData: TypeScriptModule, buildTarget: BuildTarget) {}

  override fun resolveModule(targetInfo: TargetInfo): TypeScriptModule? {
    if (!targetInfo.hasTypescriptTargetInfo()) {
      return null
    }

    val tsInfo = targetInfo.typescriptTargetInfo

    return TypeScriptModule(
      sources =
        tsInfo.sourcesList.map {
          java.nio.file.Paths
            .get(it)
        },
    )
  }
}
