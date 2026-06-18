# repo

```bash
kube-kts repo <SUBCOMMAND> [ARGS] [オプション]
```

チャートリポジトリを管理する `helm repo …` サブコマンドをまとめます。これらはローカルの Helm 設定に対して動作するため、**リポジトリもレンダリング手順も不要**です——呼び出しは直接 Helm に転送されます。サブコマンドなしで `repo` を呼び出すと使用方法の一覧が表示されます。

## サブコマンド

| サブコマンド | Helm | 説明 |
|---|---|---|
| `repo add <NAME> <URL>` | `helm repo add` | チャートリポジトリを追加します。 |
| `repo update [REPO...]` | `helm repo update` | 指定（またはすべて）のリポジトリのローカルキャッシュを更新します。 |
| `repo list` | `helm repo list` | 設定済みのチャートリポジトリを一覧表示します。 |
| `repo remove <REPO...>` | `helm repo remove` | 1 つ以上のチャートリポジトリを削除します。 |

## サブコマンドごとのオプション

| サブコマンド | オプション（すべて `---->`） |
|---|---|
| `add` | `--username=USER`、`--password=PASSWORD`、`--pass-credentials`、`--ca-file=FILE`、`--cert-file=FILE`、`--key-file=FILE`、`--insecure-skip-tls-verify`、`--no-update`、`--force-update`、`--allow-deprecated-repos` |
| `update` | `--fail-on-repo-update-fail` |
| `list` | `-o`/`--output=FORMAT` |
| `remove` | – |

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# Bitnami リポジトリを追加
kube-kts repo add bitnami https://charts.bitnami.com/bitnami

# すべてのリポジトリを更新
kube-kts repo update

# リポジトリを JSON で一覧表示
kube-kts repo list -o json

# リポジトリを削除
kube-kts repo remove bitnami
```
