# show

```bash
kube-kts show <SUBCOMMAND> <CHART> [オプション]
```

チャートの情報を表示する `helm show …` サブコマンド（別名 `inspect`）をまとめます。これらはリモート/ローカルのチャートに対して動作するため、**リポジトリもレンダリング手順も不要**です——呼び出しは直接 Helm に転送されます。サブコマンドなしで `show` を呼び出すと使用方法の一覧が表示されます。

## サブコマンド

| サブコマンド | Helm | 説明 |
|---|---|---|
| `show all <CHART>` | `helm show all` | チャートのすべての情報を表示します。 |
| `show chart <CHART>` | `helm show chart` | チャート定義（`Chart.yaml`）を表示します。 |
| `show values <CHART>` | `helm show values` | チャートの既定値（`values.yaml`）を表示します。 |
| `show readme <CHART>` | `helm show readme` | チャートの README を表示します。 |
| `show crds <CHART>` | `helm show crds` | チャートの CRD を表示します。 |

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `CHART` | はい | 検査するチャート参照。位置引数 `CHART` として Helm に転送されます。 |

## オプション

各サブコマンドは[チャートダウンロードオプション](pull.md#チャートダウンロードオプション)（`--repo`、`--version`、認証情報、TLS、`--verify` など）を受け付けます。さらに `show values` は次を受け付けます：

| オプション | マーカー | 説明 |
|---|---|---|
| `--jsonpath=EXPRESSION` | `---->` | 出力をフィルターする JSONPath 式。 |

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# リポジトリのチャートの既定値を表示
kube-kts show values bitnami/nginx --version 15.0.0

# JSONPath で値の image のみ表示
kube-kts show values bitnami/nginx --jsonpath '{.image}'
```
