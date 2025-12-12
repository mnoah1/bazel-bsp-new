load("//aspects:utils/utils.bzl", "create_struct")

def extract_typescript_info(target, ctx, **kwargs):
    kind = getattr(ctx.rule, "kind", "")

    # Only process jest_test targets
    if kind != "jest_test":
        return None, None

    sources = []

    # Check srcs attribute first (standard location for source files)
    if hasattr(ctx.rule.attr, "srcs") and ctx.rule.attr.srcs:
        for src_file in ctx.rule.attr.srcs:
            if hasattr(src_file, "files"):
                for file in src_file.files.to_list():
                    sources.append(file.path)

    # Also check data attribute (jest_test puts test files here)
    if hasattr(ctx.rule.attr, "data") and ctx.rule.attr.data:
        for data_file in ctx.rule.attr.data:
            if hasattr(data_file, "files"):
                for file in data_file.files.to_list():
                    sources.append(file.path)

    typescript_info = create_struct(
        sources = sources,
    )

    sources_as_file_locations = [create_struct(relative_path = s, is_source = True, is_external = False) for s in sources]

    return dict(
        typescript_target_info = typescript_info,
        sources = sources_as_file_locations,
    ), None
