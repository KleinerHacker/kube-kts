# verify

```bash
kube-kts verify <PATH> [OPTIONS]
```

Runs `helm verify` to check that a packaged chart has been signed and is valid. It operates on a
local chart package, so it needs **neither a repository nor a render step** — the call is forwarded
directly to Helm.

## How it works

1. No repository is scanned, compiled or rendered; the KTS scripts are irrelevant here.
2. `helm verify <PATH>` is executed with all forwarded options appended.
3. Helm verifies the package; the command's exit code mirrors Helm's result.

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `PATH` | yes | Path to the packaged chart to verify. Forwarded to Helm as the positional `PATH`. |

## Verify options

| Option | Marker | Description |
|---|---|---|
| `--keyring=FILE` | `---->` | Keyring containing the public keys used for verification. |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Verify a packaged chart against a keyring
kube-kts verify my-app-1.0.0.tgz --keyring ~/.gnupg/pubring.gpg
```
