# registry

```bash
kube-kts registry <SUBCOMMAND> <HOST> [OPTIONS]
```

Groups the `helm registry …` sub-commands that manage authentication against OCI registries. They
operate on your local registry config, so they need **neither a repository nor a render step** — the
call is forwarded directly to Helm. Invoking `registry` without a sub-command prints the usage
listing.

## Sub-commands

| Sub-command | Helm | Description |
|---|---|---|
| `registry login <HOST>` | `helm registry login` | Log in to an OCI registry. |
| `registry logout <HOST>` | `helm registry logout` | Log out from an OCI registry. |

## Parameters

| Parameter | Required | Description |
|---|---|---|
| `HOST` | yes | Registry host. Forwarded to Helm as the positional `HOST`. |

## Options per sub-command

| Sub-command | Options (all `---->`) |
|---|---|
| `login` | `-u`/`--username=USER`, `-p`/`--password=PASSWORD`, `--password-stdin`, `--insecure`, `--ca-file=FILE`, `--cert-file=FILE`, `--key-file=FILE`, `--plain-http` |
| `logout` | – |

## Helm global options

All [Helm global options](status.md#helm-global-options) are forwarded to Helm.

## Global options

All [global options](index.md#global-options) are available as well.

## Examples

```bash
# Log in to a registry (reading the password from stdin)
echo "$TOKEN" | kube-kts registry login registry.example.com -u robot --password-stdin

# Log out
kube-kts registry logout registry.example.com
```
