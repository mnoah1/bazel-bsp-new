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
    
    # Always return info for jest_test targets, even if no sources found
    # The target should still be discoverable
    typescript_info = create_struct(
        sources = sources,
    )

    return dict(typescript_target_info = typescript_info), None
