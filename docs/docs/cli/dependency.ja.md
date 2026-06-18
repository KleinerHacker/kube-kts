# dependency

```bash
kube-kts dependency <SUBCOMMAND> <REPOSITORY> [TARGET] [オプション]
```

チャートの依存関係を管理する `helm dependency …` サブコマンド（別名 `dep`）をまとめます。各サブコマンドは **リポジトリが必要**で、まず *スキャン → コンパイル → レンダリング* のパイプライン全体を実行し、その後レンダリング済みチャートに対して操作します（`helm dependency <sub> .`）。サブコマンドなしで `dependency` を呼び出すと使用方法の一覧が表示されます。

## サブコマンド

| サブコマンド | Helm | 説明 |
|---|---|---|
| `dependency build <REPOSITORY> [TARGET]` | `helm dependency build .` | `Chart.lock` から `charts/` を再構築します。 |
| `dependency update <REPOSITORY> [TARGET]` | `helm dependency update .` | `Chart.yaml` の内容から `charts/` を更新します。 |
| `dependency list <REPOSITORY> [TARGET]` | `helm dependency list .` | チャートの依存関係を一覧表示します。 |

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `REPOSITORY` | はい | レンダリングする Kube KTS リポジトリのパス。 |
| `TARGET` | いいえ | チャートをレンダリングするディレクトリ。省略時は一時ディレクトリを使用します。 |

## サブコマンドごとのオプション

| サブコマンド | オプション（すべて `---->`） |
|---|---|
| `build` | `--keyring=FILE`、`--skip-refresh`、`--verify` |
| `update` | `--keyring=FILE`、`--skip-refresh`、`--verify` |
| `list` | `--max-col-width=UINT` |

## 値

これらのコマンドは [`-f`/`--values`](index.md#values) で値ファイルを受け付け、レンダリング中に Kotlin スクリプトへ供給します。

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# チャートの依存関係をレンダリングして更新
kube-kts dependency update ./my-repo

# レンダリング済みチャートの依存関係を一覧表示
kube-kts dependency list ./my-repo
```
