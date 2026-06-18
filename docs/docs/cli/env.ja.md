# env

```bash
kube-kts env [NAME] [オプション]
```

`helm env` を実行して Helm の環境情報を表示します。純粋に情報目的であり、**リポジトリもレンダリング手順も不要**です——呼び出しは直接 Helm に転送されます。

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `NAME` | いいえ | 表示する単一の環境変数名。省略時はすべての変数を表示します。位置引数 `NAME` として Helm に転送されます。 |

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# Helm のすべての環境変数を表示
kube-kts env

# 単一の変数を表示
kube-kts env HELM_BIN
```
