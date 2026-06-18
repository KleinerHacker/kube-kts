# get

```bash
kube-kts get <SUBCOMMAND> <RELEASE> [OPTIONS]
```

Groups the `helm get …` sub-commands that download extended information about an installed release.
Each sub-command operates on an existing release, so it needs **neither a repository nor a render
step** — the call is forwarded directly to Helm. Invoking `get` without a sub-command prints the
usage listing.

## Sub-commands

| Sub-command | Helm | Description |
|---|---|---|
| `get all <RELEASE>` | `helm get all` | Download all information for the release. |
| `get values <RELEASE>` | `helm get values` | Download the (supplied or computed) values of the release. |
| `get manifest <RELEASE>` | `helm get manifest` | Download the manifest of the release. |
| `get hooks <RELEASE>` | `helm get hooks` | Download all hooks of the release. |
| `get notes <RELEASE>` | `helm get notes` | Download the notes of the release. |
| `get metadata <RELEASE>` | `helm get metadata` | Download the metadata of the release. |

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `RELEASE` | yes | Name of the release to query. Forwarded to Helm as the positional `RELEASE`. |

## Options per sub-command

| Sub-command | Options (all `---->`) |
|---|---|
| `all` | `--revision=INT`, `--template=TEMPLATE`, `-o`/`--output=FORMAT` |
| `values` | `-a`/`--all`, `--revision=INT`, `-o`/`--output=FORMAT` |
| `manifest` | `--revision=INT` |
| `hooks` | `--revision=INT` |
| `notes` | `--revision=INT` |
| `metadata` | `-o`/`--output=FORMAT` |

`--revision` selects a specific revision; `-a`/`--all` (on `values`) dumps all computed values;
`--template` applies a Go template; `-o`/`--output` selects `table`/`json`/`yaml`.

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Get all the computed values of a release as JSON
kube-kts get values my-app --all -o json

# Get the rendered manifest of a specific revision
kube-kts get manifest my-app --revision 2
```
