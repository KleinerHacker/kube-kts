# version

```bash
kube-kts version [オプション]
```

`helm version` を実行して Helm のバージョン情報を表示します。純粋に情報目的であり、**リポジトリもレンダリング手順も不要**です——呼び出しは直接 Helm に転送されます。

## version オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--short` | `---->` | バージョン番号のみを表示します。 |
| `--template=TEMPLATE` | `---->` | バージョン文字列の書式用 Go テンプレート。 |

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# Helm の完全なバージョンを表示
kube-kts version

# バージョン番号のみ表示
kube-kts version --short
```
