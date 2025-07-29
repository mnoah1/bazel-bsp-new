load("@bazel_bsp_versions//:versions.bzl", "BAZEL_BSP_ARTIFACT_VERSION")
load("@bazel_skylib//rules:expand_template.bzl", "expand_template")
load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")

expand_template(
    name = "constants-src",
    out = "Constants.kt",
    substitutions = {
        "{VERSION}": BAZEL_BSP_ARTIFACT_VERSION,
    },
    template = "Constants.kt.tmpl",
)

kt_jvm_library(
    name = "constants",
    srcs = [
        ":constants-src",
    ],
    visibility = ["//visibility:public"],
)
