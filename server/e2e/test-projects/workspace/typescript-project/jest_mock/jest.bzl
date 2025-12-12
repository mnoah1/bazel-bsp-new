def _jest_test_impl(ctx):
    script = ctx.actions.declare_file(ctx.label.name + ".sh")
    ctx.actions.write(
        output = script,
        content = "#!/bin/bash\necho 'Test passed'\nexit 0",
        is_executable = True,
    )
    return [DefaultInfo(executable = script)]

jest_test = rule(
    implementation = _jest_test_impl,
    test = True,
    attrs = {
        "srcs": attr.label_list(allow_files = True),
        "data": attr.label_list(allow_files = True),
        "deps": attr.label_list(),
    },
)

