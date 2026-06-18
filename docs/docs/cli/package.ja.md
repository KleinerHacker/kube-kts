# package

```bash
kube-kts package <REPOSITORY> [TARGET] [オプション]
```

KTS リポジトリを通常の Helm チャートにレンダリングし、それに対して `helm package .` を実行してバージョン付きチャートアーカイブ（`.tgz`）を生成します。リリース系コマンドと異なり、`package` は **リポジトリが必要**で、まず *スキャン → コンパイル → レンダリング* のパイプライン全体を実行します。

## 動作の仕組み

1. リポジトリをスキャンし、Kotlin スクリプトをコンパイル・評価し、結果を `TARGET` ディレクトリ（省略時は一時ディレクトリ）にレンダリングします。
2. そのディレクトリで `helm package .` が、転送されるすべてのオプションを付加して実行されます。
3. Helm がチャートアーカイブを書き出します。コマンドの終了コードは Helm の結果を反映します。

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `REPOSITORY` | はい | レンダリングしてパッケージ化する Kube KTS リポジトリのパス。 |
| `TARGET` | いいえ | チャートをレンダリングするディレクトリ。省略時は一時ディレクトリを使用します。 |

## package オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--app-version=VERSION` | `---->` | チャートの `appVersion` を設定します。 |
| `--version=VERSION` | `---->` | チャートの（semver）`version` を設定します。 |
| `-d`, `--destination=DIR` | `---->` | チャートアーカイブの書き込み先。 |
| `-u`, `--dependency-update` | `---->` | パッケージ化前に依存関係を `Chart.yaml` から `charts/` へ更新します。 |
| `--sign` | `---->` | PGP 秘密鍵でパッケージに署名します。 |
| `--key=NAME` | `---->` | 署名鍵の名前（`--sign` と併用）。 |
| `--keyring=FILE` | `---->` | 公開鍵リングの場所。 |
| `--pass-stdin` | `---->` | PGP パスフレーズを stdin から読み取ります（`--sign` と併用）。 |

## 値

`package` は [`-f`/`--values`](index.md#values) で値ファイルを受け付け、レンダリング中に Kotlin スクリプトへ供給します。

## Helm グローバルオプション

すべての [Helm グローバルオプション](status.md#helm-global-options)が Helm に転送されます。

## グローバルオプション

すべての[グローバルオプション](index.md)も利用できます。

## 例

```bash
# 明示的なバージョンでリポジトリをレンダリングしてパッケージ化
kube-kts package ./my-repo --version 1.2.3 --app-version 2.0.0 -d ./dist
```
