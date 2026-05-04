# Kube KTS CLI

Kube KTS is a command-line tool that can be executed with the `kube-kts` command.

To process a repository, provide the path to the repository. If no repository path is
provided, the current working directory is used.

## Global Options

- `--debug`: Prints debug information including the log level.
- `--verbose`: Prints all information including the log level.
- `--show-log-level`: Prints the log level in the output.
- `--exception`: Prints the full exception stack trace in case of errors.
- `--experimental`: Enables experimental features.
- `--unsafe`: Enables unsafe mode, allowing imports in Kotlin scripts.

## Experimental Features

Experimental features are not stable and may change in future versions. To use them,
enable the `--experimental` flag.

- `--yaml-merge`: Defines the algorithm to use for merging YAML files.
    - `HELM`: Uses the standard Helm merging algorithm (default).
    - `INTERNAL`: Uses a custom internal merging algorithm.
- `--yaml-array-merge`: Defines the merge strategy for arrays when using the `INTERNAL` algorithm.
    - `None`: Keeps the base array unchanged.
    - `Replace`: Replaces the base array with the overlay array (default).
    - `AddFirst`: Adds the overlay array to the beginning of the base array.
    - `AddLast`: Adds the overlay array to the end of the base array.

## Validate

The `kube-kts validate` command validates the repository. It checks whether the
repository is valid and contains all required files.

### Parameters

1. `REPOSITORY`: Path to the repository. If not provided, the current directory is used.

### Example

```bash
# Validate the current directory
kube-kts validate .

# Validate a specific repository
kube-kts validate /path/to/repository
```

## Compile

The `kube-kts compile` command compiles the repository. It generates object instances
from the repository, which are used to render the templates in the next step.

### Parameters

1. `REPOSITORY`: Path to the repository. If not provided, the current directory is used.

### Example

```bash
# Compile the current directory
kube-kts compile .

# Compile a specific repository
kube-kts compile /path/to/repository
```

## Render

The `kube-kts render` command renders the repository. It generates Helm files from the
repository so they can be used with Helm.

### Parameters

1. `REPOSITORY`: Path to the repository. If not provided, the current directory is used.
2. `TARGET`: Path to the target directory where the Helm files are rendered. If not provided, a temporary directory is used.

### Example

```bash
# Render the current directory to a target directory
kube-kts render . /path/to/target

# Render a specific repository to a target directory
kube-kts render /path/to/repository /path/to/target
```

## Lint

The `kube-kts lint` command lints the repository by using Helm. It checks whether the
rendered Helm output is valid.

### Parameters

1. `REPOSITORY`: Path to the repository. If not provided, the current directory is used.
2. `TARGET`: Path to the target directory where the Helm files are rendered before linting. If not provided, a temporary directory is used.

### Example

```bash
# Lint the current directory by rendering to a target directory first
kube-kts lint . /path/to/target

# Lint a specific repository by rendering to a target directory first
kube-kts lint /path/to/repository /path/to/target
```

## Template

The `kube-kts template` command prints the rendered templates of the repository. It uses `helm template` internally.

### Parameters

1. `REPOSITORY`: Path to the repository. If not provided, the current directory is used.
2. `TARGET`: Path to the target directory where the Helm files are rendered. If not provided, a temporary directory is used.

### Options

- `-n`, `--name`: **(Required)** The name of the chart to template.

### Example

```bash
# Template the current directory with a specific name
kube-kts template . /path/to/target --name my-release

# Template a specific repository with a specific name
kube-kts template /path/to/repository /path/to/target -n my-release
```

## Install

Coming soon.

## Uninstall

Coming soon.