load("//aspects:utils/utils.bzl", "create_struct")

def extract_typescript_info(target, ctx, **kwargs):
    kind = getattr(ctx.rule, "kind", "")
    
    if kind != "jest_test":
        return None, None

    sources = []
    
    if hasattr(ctx.rule.attr, "data") and ctx.rule.attr.data:
        for data_file in ctx.rule.attr.data:
            if hasattr(data_file, "files"):
                for file in data_file.files.to_list():
                    if file.path.endswith(".ts"):
                        sources.append(file.path)
    
    if not sources:
        return None, None

    typescript_info = create_struct(
        sources = sources,
    )

    return dict(typescript_target_info = typescript_info), None
