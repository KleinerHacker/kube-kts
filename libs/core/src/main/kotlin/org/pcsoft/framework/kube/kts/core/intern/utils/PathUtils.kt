package org.pcsoft.framework.kube.kts.core.intern.utils

import java.nio.file.Path

internal fun Path.resolve(path: Path, vararg more: String) =
    this.resolve(path.toString(), *more)