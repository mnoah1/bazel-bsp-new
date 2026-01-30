load("@//rules/testing:junit5.bzl", "kt_junit5_test")

kt_junit5_test(
    name = "ScalaSdkResolverTest",
    size = "medium",
    src = "ScalaSdkResolverTest.kt",
    tags = ["requires-network"],
    deps = [
        "//server/server/src/main/kotlin/org/jetbrains/bsp/bazel/server/sync",
    ],
)
