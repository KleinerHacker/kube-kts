# history

```bash
kube-kts history <RELEASE> [オプション]
```

`helm history` を実行してリリースのリビジョン履歴を表示します。既存のリリースに対して動作するため、**リポジトリもレンダリング手順も不要**です——呼び出しは直接 Helm に転送されます。

## 動作の仕組み

1. リポジトリのスキャン・コンパイル・レンダリングは行われません。KTS スクリプトはここでは無関係です。
2. `helm history <RELEASE>` が、転送されるすべてのオプションを付加して実行されます。
3. Helm がリビジョン履歴を表示します。コマンドの終了コードは Helm の結果を反映します。

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `RELEASE` | はい | 照会するリリース名。位置引数 `RELEASE` として Helm に転送されます。 |

## history オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--max=INT` | `---->` | 履歴に含めるリビジョンの最大数。 |
| `-o`, `--output=FORMAT` | `---->` | 出力形式：`table`（既定）、`json`、`yaml`。 |

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# リリースの完全なリビジョン履歴を表示
kube-kts history my-app

# 直近 5 件のリビジョンを JSON で表示
kube-kts history my-app --max 5 -o json
```
