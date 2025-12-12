package org.jetbrains.bsp.bazel.server.sync.languages.typescript

import org.jetbrains.bsp.bazel.server.model.LanguageData
import java.nio.file.Path

data class TypeScriptModule(val sources: List<Path>) : LanguageData
