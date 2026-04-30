# Kube KTS CLI

Kube KTS is a command-line tool that can be executed with the `kube-kts` command.

To process a repository, provide the path to the repository. If no source path is
provided, the current working directory is used.

## Global Options

- `--debug`: Enables debug output.
- `--exception`: Enables full exception output.
- `--experimental`: Enables experimental features.
- `--show-log-level`: Shows the log level in the output.
- `--unsafe`: Enables unsafe mode. This allows imports in Kotlin scripts.
- `--verbose`: Enables verbose output.

## Experimental Features

Experimental features are not stable and may change in future versions. To use them,
enable the `--experimental` flag.

- `--yaml-array-merge`: Defines the merge strategy for arrays in value file overlays.
    - `None`: Keeps the array unchanged.
    - `Replace`: Replaces the base array with the overlay array.
    - `AddFirst`: Adds the overlay array to the beginning of the base array.
    - `AddLast`: Adds the overlay array to the end of the base array.

## Validate

The `kube-kts validate` command validates the repository. It checks whether the
repository is valid and contains all required files.

### Parameters

1. `source path`: Path to the repository. If not provided, the current directory is used.

## Compile

The `kube-kts compile` command compiles the repository. It generates object instances
from the repository, which are used to render the templates in the next step.

### Parameters

1. `source path`: Path to the repository. If not provided, the current directory is used.

## Render

The `kube-kts render` command renders the repository. It generates Helm files from the
repository so they can be used with Helm.

### Parameters

1. `source path`: Path to the repository. If not provided, the current directory is used.
2. `target path`: Path to the target directory where the Helm files are rendered.

## Lint

The `kube-kts lint` command lints the repository by using Helm. It checks whether the
rendered Helm output is valid.

### Parameters

1. `source path`: Path to the repository. If not provided, the current directory is used.
2. `target path`: Path to the target directory where the Helm files are rendered before linting.

## Install

Coming soon.

## Uninstall

Coming soon.