# get

```bash
kube-kts get <SUBCOMMAND> <RELEASE> [オプション]
```

インストール済みリリースの拡張情報をダウンロードする `helm get …` サブコマンドをまとめます。各サブコマンドは既存のリリースに対して動作するため、**リポジトリもレンダリング手順も不要**です——呼び出しは直接 Helm に転送されます。サブコマンドなしで `get` を呼び出すと使用方法の一覧が表示されます。

## サブコマンド

| サブコマンド | Helm | 説明 |
|---|---|---|
| `get all <RELEASE>` | `helm get all` | リリースのすべての情報をダウンロードします。 |
| `get values <RELEASE>` | `helm get values` | リリースの（指定または計算された）値をダウンロードします。 |
| `get manifest <RELEASE>` | `helm get manifest` | リリースのマニフェストをダウンロードします。 |
| `get hooks <RELEASE>` | `helm get hooks` | リリースのすべてのフックをダウンロードします。 |
| `get notes <RELEASE>` | `helm get notes` | リリースの notes をダウンロードします。 |
| `get metadata <RELEASE>` | `helm get metadata` | リリースのメタデータをダウンロードします。 |

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `RELEASE` | はい | 照会するリリース名。位置引数 `RELEASE` として Helm に転送されます。 |

## サブコマンドごとのオプション

| サブコマンド | オプション（すべて `---->`） |
|---|---|
| `all` | `--revision=INT`、`--template=TEMPLATE`、`-o`/`--output=FORMAT` |
| `values` | `-a`/`--all`、`--revision=INT`、`-o`/`--output=FORMAT` |
| `manifest` | `--revision=INT` |
| `hooks` | `--revision=INT` |
| `notes` | `--revision=INT` |
| `metadata` | `-o`/`--output=FORMAT` |

`--revision` は特定のリビジョンを選択、`values` の `-a`/`--all` は計算済みのすべての値を出力、`--template` は Go テンプレートを適用、`-o`/`--output` は `table`/`json`/`yaml` を選択します。

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# リリースの計算済みのすべての値を JSON で取得
kube-kts get values my-app --all -o json

# 特定リビジョンのレンダリング済みマニフェストを取得
kube-kts get manifest my-app --revision 2
```
