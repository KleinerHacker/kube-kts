# diff

```bash
kube-kts diff upgrade <REPOSITORY> [TARGET] --name <RELEASE> [オプション]
```

外部の **`helm-diff` プラグイン** 上に構築された diff サブコマンドをまとめます。KTS リポジトリをレンダリングし、その結果をクラスターと比較して、操作が適用する変更をプレビューします。各サブコマンドは **リポジトリが必要**で、まず *スキャン → コンパイル → レンダリング* のパイプライン全体を実行します。サブコマンドなしで `diff` を呼び出すと使用方法の一覧が表示されます。

!!! note "helm-diff プラグインが必要"
    これらのコマンドは外部プラグインが提供する `helm diff …` を呼び出します。
    `helm plugin install https://github.com/databus23/helm-diff` でインストールしてください。

## サブコマンド

| サブコマンド | Helm | 説明 |
|---|---|---|
| `diff upgrade <REPOSITORY> [TARGET]` | `helm diff upgrade <RELEASE> .` | `upgrade` が適用する変更をプレビューします。 |

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `REPOSITORY` | はい | レンダリングする Kube KTS リポジトリのパス。 |
| `TARGET` | いいえ | チャートをレンダリングするディレクトリ。省略時は一時ディレクトリを使用します。 |

!!! note "リリース名は `--name` で渡す"
    `REPOSITORY`/`TARGET` がすでに位置引数を占有しているため、リリース名は `--name` で渡し、位置引数 `RELEASE` としてプラグインに転送されます。`-n` は `--namespace` のために予約されています。

## diff オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--name=NAME` | | リリース名（位置引数 `RELEASE` としてプラグインに転送）。 |
| `--detailed-exitcode` | `---->` | 変更がある場合に終了コード `2` を返します。 |
| `--context=NUM` | `---->` | 変更の周囲に `NUM` 行のコンテキストを出力します（`-1` で全コンテキスト）。 |
| `--show-secrets` | `---->` | 出力で secret 値をマスクしません。 |
| `--no-hooks` | `---->` | フックの diff を無効にします。 |
| `--include-tests` | `---->` | Helm test フックの diff を有効にします。 |
| `--reset-values` | `---->` | 値をチャート組み込みの値にリセットし、新しい値をマージします。 |
| `--reuse-values` | `---->` | 直前のリリースの値を再利用し、新しい値をマージします。 |
| `--normalize-manifests` | `---->` | diff 前にマニフェストを正規化し、スタイル差を除外します。 |

## 値

`diff upgrade` は [`--set` ファミリー](install.md) と [`-f`/`--values`](index.md#values) による値ファイルを受け付け、これらは Kotlin スクリプトとプラグインの両方に供給されます。

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# upgrade が適用する変更をプレビュー
kube-kts diff upgrade ./my-repo --name my-app

# 変更があればビルドを失敗させる（終了コード 2）
kube-kts diff upgrade ./my-repo --name my-app --detailed-exitcode
```
