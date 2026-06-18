# search

```bash
kube-kts search <SUBCOMMAND> [KEYWORD] [オプション]
```

チャートを検索する `helm search …` サブコマンドをまとめます。これらはリポジトリ / Artifact Hub に対して動作するため、**リポジトリもレンダリング手順も不要**です——呼び出しは直接 Helm に転送されます。サブコマンドなしで `search` を呼び出すと使用方法の一覧が表示されます。

## サブコマンド

| サブコマンド | Helm | 説明 |
|---|---|---|
| `search repo [KEYWORD]` | `helm search repo` | 追加済みのリポジトリを検索します。 |
| `search hub [KEYWORD]` | `helm search hub` | Artifact Hub または独自の hub インスタンスを検索します。 |

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `KEYWORD` | いいえ | 検索するキーワード。位置引数 `KEYWORD` として Helm に転送されます。 |

## サブコマンドごとのオプション

| サブコマンド | オプション（すべて `---->`） |
|---|---|
| `repo` | `--devel`、`--fail-on-no-result`、`--max-col-width=UINT`、`-o`/`--output=FORMAT`、`-r`/`--regexp`、`--version=VERSION`、`-l`/`--versions` |
| `hub` | `--endpoint=URL`、`--fail-on-no-result`、`--list-repo-url`、`--max-col-width=UINT`、`-o`/`--output=FORMAT` |

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# 追加済みリポジトリで "nginx" を検索し、すべてのバージョンを一覧表示
kube-kts search repo nginx -l

# Artifact Hub を検索
kube-kts search hub nginx -o json
```
