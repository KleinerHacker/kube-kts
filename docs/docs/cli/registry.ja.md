# registry

```bash
kube-kts registry <SUBCOMMAND> <HOST> [オプション]
```

OCI レジストリへの認証を管理する `helm registry …` サブコマンドをまとめます。これらはローカルのレジストリ設定に対して動作するため、**リポジトリもレンダリング手順も不要**です——呼び出しは直接 Helm に転送されます。サブコマンドなしで `registry` を呼び出すと使用方法の一覧が表示されます。

## サブコマンド

| サブコマンド | Helm | 説明 |
|---|---|---|
| `registry login <HOST>` | `helm registry login` | OCI レジストリにログインします。 |
| `registry logout <HOST>` | `helm registry logout` | OCI レジストリからログアウトします。 |

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `HOST` | はい | レジストリホスト。位置引数 `HOST` として Helm に転送されます。 |

## サブコマンドごとのオプション

| サブコマンド | オプション（すべて `---->`） |
|---|---|
| `login` | `-u`/`--username=USER`、`-p`/`--password=PASSWORD`、`--password-stdin`、`--insecure`、`--ca-file=FILE`、`--cert-file=FILE`、`--key-file=FILE`、`--plain-http` |
| `logout` | – |

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# レジストリにログイン（パスワードを stdin から読み取り）
echo "$TOKEN" | kube-kts registry login registry.example.com -u robot --password-stdin

# ログアウト
kube-kts registry logout registry.example.com
```
