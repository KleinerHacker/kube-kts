# status

```bash
kube-kts status <RELEASE> [オプション]
```

`helm status` を実行して、すでにインストール済みのリリースの状態を表示します。レンダリング系コマンドと
異なり、`status` はクラスター上に存在するリリースを対象とするため、**リポジトリもレンダリング手順も
不要**で、呼び出しはそのまま Helm に転送されます。

## 仕組み

1. リポジトリのスキャン・コンパイル・レンダリングは行いません。KTS スクリプトはここでは無関係です。
2. `helm status <RELEASE>` を実行し、転送オプションをすべて付加します。
3. Helm がリリース状態を表示します。終了コードは Helm の結果を反映します。

!!! note "リリース名は位置引数、名前空間は `-n`"
    リリース名は最初の位置引数（`RELEASE`）として渡し、そのまま Helm に転送されます。`-n` は
    `--namespace` 用に予約されています。

!!! note "リポジトリは不要"
    `status` は何もレンダリングしないため、Kube KTS リポジトリを指定する必要はありません。これは
    レンダリング不要の基底クラス `BaseDirectHelmCommand` を用いた最初のコマンドです。

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `RELEASE` | はい | 照会するリリース名。位置引数 `RELEASE` として Helm に転送されます。 |

## status オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--revision=INT` | `---->` | 最新ではなく、指定したリビジョンのリリース状態を表示します。 |
| `--output=FORMAT` | `---->` | 指定形式で出力します: `table`（既定）、`json`、`yaml`。 |
| `--show-desc` | `---->` | リリースに記録された説明メッセージも表示します。 |
| `--show-resources` | `---->` | リリースに属する Kubernetes リソースも一覧表示します。 |

## Helm グローバルオプション

| オプション | マーカー | 説明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | リリースが存在する名前空間。既定は現在の kube-context の名前空間。正しい名前空間を照会するため明示してください。 |
| `--kube-context=CONTEXT` | `---->` | 使用する kubeconfig 内のコンテキスト。現在のコンテキストを切り替えずに特定のクラスター/ユーザーを対象にできます。 |
| `--kubeconfig=FILE` | `---->` | 既定の代わりに使う kubeconfig ファイルのパス。 |
| `--kube-apiserver=ADDRESS` | `---->` | kubeconfig の API サーバーアドレスとポートを上書き。 |
| `--kube-as-user=USER` | `---->` | このユーザーを偽装します。偽装権限が必要。 |
| `--kube-as-group=GROUP` | `---->` | このグループを偽装します。繰り返し可。 |
| `--kube-ca-file=FILE` | `---->` | API サーバーの TLS 証明書検証に使う CA 証明書ファイル。 |
| `--kube-token=TOKEN` | `---->` | kubeconfig の資格情報の代わりに認証に使うベアラートークン。 |
| `--kube-tls-server-name=NAME` | `---->` | API サーバー証明書の検証に使うサーバー名。 |
| `--kube-insecure-skip-tls-verify` | `---->` | API サーバー証明書の検証をスキップ。安全でない——信頼済み/テストのみ。 |
| `--burst-limit=INT` | `---->` | API リクエストのクライアント側スロットリングのバースト上限（既定 `100`）。 |
| `--qps=QPS` | `---->` | API サーバーへのクライアント側の毎秒クエリ上限。 |
| `--registry-config=FILE` | `---->` | OCI レジストリ設定（資格情報）ファイルのパス。 |
| `--repository-cache=DIR` | `---->` | 依存解決に使うキャッシュ済みリポジトリインデックスのディレクトリ。 |
| `--repository-config=FILE` | `---->` | リポジトリ名と URL を対応づけるファイルのパス（`repositories.yaml`）。 |

## グローバルオプション

すべての[グローバルオプション](index.md)（`--debug`、`--unsafe` など）も利用できます。

## 例

```bash
# "prod" 名前空間のリリース状態を表示
kube-kts status my-app -n prod

# 特定のリビジョンを JSON で表示
kube-kts status my-app --revision 3 --output json

# リリースの説明とリソースも含めて表示
kube-kts status my-app --show-desc --show-resources
```
