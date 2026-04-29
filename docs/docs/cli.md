# Kube KTS CLI

Kube KTS is a command line tool. You can run it with `kube-kts` command. To
handle repository you must define the path to the repository.

## Global Options

- `--debug`: Enable debug output.
- `--exception`: Enable complete exception output.
- `--experimental`: Enable experimental features.
- `--show-log-level`: Show log level in output.
- `--unsafe`: Enable unsafe mode. It means that allows to use imports in Kotlin Scripts.
- `--verbose`: Enable verbose output.

##### Experimental Features

These features are not stable and may change in the future. To use them you must enable
via the `--experimental` flag.

- `--yaml-array-merge`: Merge strategy for arrays in value files overlays.
    - `None`: The array is untouched.
    - `Replace`: Replace the array with the overlay array.
    - `AddFirst`: Add overlay array to beginning of base array
    - `AddLast`: Add overlay array to end of base array

## Validate

The command `kube-kts validate` validates the repository. It checks if the repository
is valid and if it contains all the required files.

##### Parameters
1. `source path` Path to the repository. If not provided, the current directory is used.

## Compile

The command `kube-kts compile` compiles the repository. It generates the object instances
from the repository to render the templates in next step.

##### Parameters
1. `source path` Path to the repository. If not provided, the current directory is used.

## Render

The command `kube-kts render` renders the repository. It generates the Helm files 
from the repository to use with Helm.

##### Parameters
1. `source path` Path to the repository. If not provided, the current directory is used.
2. `target path` Path to the target directory to render the Helm files.