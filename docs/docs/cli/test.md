# test

```bash
kube-kts test <RELEASE> [OPTIONS]
```

Runs `helm test` to execute the tests defined in a release. It operates on an existing release, so it
needs **neither a repository nor a render step** — the call is forwarded directly to Helm.

## How it works

1. No repository is scanned, compiled or rendered; the KTS scripts are irrelevant here.
2. `helm test <RELEASE>` is executed with all forwarded options appended.
3. Helm runs the test hooks; the command's exit code mirrors Helm's result.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `RELEASE` | yes | Name of the release to test. Forwarded to Helm as the positional `RELEASE`. |

## Test options

| Option | Marker | Description |
|---|---|---|
| `--filter=KEY=VALUE` | `---->` | Select tests by attribute (e.g. `name=...`), or exclude with `!attribute=value`. Repeatable. |
| `--hide-notes` | `---->` | Do not show notes in the test output. |
| `--logs` | `---->` | Dump the logs from the test pods after all tests complete. |
| `--timeout=DURATION` | `---->` | Time to wait for any individual Kubernetes operation. |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Run the tests of a release and show their logs
kube-kts test my-app --logs

# Run only a specific test
kube-kts test my-app --filter name=my-test
```
