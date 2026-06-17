# template

```bash
kube-kts template <REPOSITORY> [TARGET] --name <NAME> [オプション]
```

リポジトリを Helm Chart にレンダリングし、`helm template` を実行して、完全にレンダリングされた
Kubernetes マニフェストを標準出力に表示します。[`install`](install.md) と異なり、（`--validate` で
明示的に選ばない限り）クラスターに接続も変更もしません。そのため、適用される内容を確認する最も安全な
方法です。変更の差分比較、出力の `kubectl apply`/GitOps への受け渡し、値が結果に与える影響のデバッグに
最適です。

## 仕組み

1. リポジトリをスキャン・コンパイルし、`-f`/`--set*` の値を適用して（一時または明示の）Chart
   ディレクトリにレンダリングします。
2. そのディレクトリで `helm template <NAME> .` を実行し、転送オプションをすべて付加します。
3. レンダリングされたマニフェストを標準出力（または `--output-dir` でファイル）に書き出します。

!!! note "リリース名はオプション、名前空間は `-n`"
    リリース名は `--name` で渡し、位置引数 `NAME` として Helm に転送されます。`-n` は Helm に合わせて
    `--namespace` 用に予約されています。名前にこだわらない場合は `--generate-name` を使えます。

!!! tip "ローカル vs クラスター対応レンダリング"
    既定では template は完全にオフラインです。`--validate` を追加すると稼働中のクラスターに対して
    マニフェストを検証でき、`-a`/`--kube-version` でレンダリング中に使う API 能力を固定できます。

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `REPOSITORY` | はい | template するリポジトリのパス。省略時はカレントディレクトリを使用します。 |
| `TARGET` | いいえ | template 前に Chart をレンダリングするディレクトリ。省略時は一時ディレクトリを使用します。 |

## template オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--name=NAME` | | レンダリング時に使うリリース名。位置引数 `NAME` として Helm に転送されます。`--generate-name` と併用時のみ省略可。 |
| `-a`, `--api-versions=VERSIONS` | `---->` | `Capabilities.APIVersions` で利用可能として報告する API バージョン（例: `networking.k8s.io/v1`）。クラスターに問い合わせずに特定 API が存在するかのようにレンダリングできます。繰り返し可。 |
| `--include-crds` | `---->` | Chart の CRD を出力に含めます。`helm template` は既定で省略します。 |
| `--is-upgrade` | `---->` | `.Release.IsUpgrade` を設定（`.Release.IsInstall` を解除）してアップグレードとしてレンダリングし、アップグレード専用の分岐を通します。 |
| `--kube-version=VERSION` | `---->` | `Capabilities.KubeVersion` で報告する Kubernetes バージョン（例: `1.29`）。特定のクラスターバージョン向けにオフラインでレンダリングできます。 |
| `--output-dir=DIR` | `---->` | すべてを標準出力に出す代わりに、レンダリングした各テンプレートをこのディレクトリ内の個別ファイルに書き出します。 |
| `-s`, `--show-only=TEMPLATE` | `---->` | 指定したテンプレートパスが生成するマニフェストだけに出力を限定します（例: `templates/deployment.yaml`）。繰り返し可。 |
| `--skip-tests` | `---->` | テストマニフェスト（Helm テストとして注釈された resource）を出力から省きます。 |
| `--validate` | `---->` | 現在指しているクラスターに対してレンダリングしたマニフェストを検証します。クラスターアクセスが必要で、template がクラスター対応操作になります。 |
| `--dry-run` | `---->` | レンダリング中にインストールをシミュレートし、dry-run 状態を確認する分岐に影響します。 |
| `-g`, `--generate-name` | `---->` | Helm にリリース名を生成させます。`--name` の代わりに使います。 |
| `-l`, `--labels=KEY=VALUE` | `---->` | （シミュレートされた）リリースのメタデータにラベルを追加します。繰り返し可。 |
| `--create-namespace` | `---->` | レンダリング出力で名前空間を作成対象として印付けします。インストール時のフラグに対応します。 |

## 値オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | 値をインラインで上書き（例: `--set replicas=3`）。型は推論されます。繰り返し可能で後の `--set` が勝ち `-f` を上書きします。 |
| `--set-string=KEY=VALUE` | `---->` | `--set` と同様ですが常に文字列として保持します。繰り返し可。 |
| `--set-file=KEY=PATH` | `---->` | `PATH` のファイル内容全体を `KEY` の値にします。繰り返し可。 |
| `--set-json=KEY=JSON` | `---->` | JSON 式から値を設定し、オブジェクト/配列を表現できます。繰り返し可。 |
| `--set-literal=KEY=VALUE` | `---->` | 書いたとおりに値を設定し、型変換を行いません。繰り返し可。 |
| `-f`, `--values=FILE` | `---->` | レンダリングに使われ Helm に転送される YAML 値ファイル。繰り返し可能で順に重ねられ、`--set*` がこれらを上書きします。 |

## Chart ソースと検証オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--repo=URL` | `---->` | ローカルパスではなく、このリポジトリ URL から Chart を取得します。 |
| `--username=USER` | `---->` | Chart リポジトリのユーザー名（`--password` と併用）。 |
| `--password=PASSWORD` | `---->` | Chart リポジトリのパスワード。コマンドラインより秘密情報/環境変数を推奨。 |
| `--pass-credentials` | `---->` | リポジトリの資格情報を全ドメインに送ります。信頼できるリポジトリのみ。 |
| `--ca-file=FILE` | `---->` | HTTPS Chart サーバーの TLS 証明書検証に使う CA バンドル。 |
| `--cert-file=FILE` | `---->` | Chart サーバーへの相互 TLS 用クライアント TLS 証明書。 |
| `--key-file=FILE` | `---->` | `--cert-file` に対応するクライアント TLS 秘密鍵。 |
| `--insecure-skip-tls-verify` | `---->` | Chart ダウンロード時の TLS 検証をスキップ。安全でない——本番では避けてください。 |
| `--keyring=FILE` | `---->` | 署名付き Chart の検証に使う公開鍵のキーリング（`--verify` と併用）。既定 `~/.gnupg/pubring.gpg`。 |
| `--verify` | `---->` | 使用前に Chart の来歴署名を検証します。欠落/無効なら失敗。 |
| `--version=VERSION` | `---->` | Chart バージョンを選ぶ SemVer 制約。既定は最新の安定版。 |
| `--devel` | `---->` | プレリリース版も対象にします（`>0.0.0-0` 相当）。 |
| `--dependency-update` | `---->` | template 前に不足している Chart 依存をダウンロード/更新します。 |

## レンダリングオプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--no-hooks` | `---->` | レンダリング中、すべての Chart ライフサイクル hooks をスキップします。 |
| `--disable-openapi-validation` | `---->` | レンダリング結果を OpenAPI スキーマで検証しません。 |
| `--name-template=TEMPLATE` | `---->` | 固定の `--name` の代わりにリリース名を計算する Go テンプレート。 |
| `--render-subchart-notes` | `---->` | サブチャートの NOTES.txt もレンダリングします。 |
| `--skip-crds` | `---->` | Chart 同梱の CRD をレンダリングしません（`--include-crds` の逆）。 |
| `--post-renderer=PATH` | `---->` | レンダリング済みマニフェストに対して実行する実行ファイル。Kustomize 風の後処理が可能。 |
| `--post-renderer-args=ARG` | `---->` | `--post-renderer` 実行ファイルへ渡す引数。繰り返し可。 |
| `--timeout=DURATION` | `---->` | 個々の Kubernetes 操作を待つ最大時間。Go の duration 形式（既定 `5m0s`）。主に `--validate` と関係します。 |

## Helm グローバルオプション

| オプション | マーカー | 説明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | レンダリング時に想定する名前空間（名前空間付きリソースや `.Release.Namespace` に影響）。既定は現在の kube-context の名前空間。 |
| `--kube-context=CONTEXT` | `---->` | 使用する kubeconfig 内のコンテキスト（`--validate` と関係）。 |
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
# リリース "my-app" のレンダリング済みマニフェストを表示
kube-kts template /path/to/repository --name my-app

# 単一テンプレートのみを CRD 込みで、インライン上書き付きで
kube-kts template . --name my-app -s templates/deployment.yaml --include-crds --set replicas=3

# 特定のクラスターバージョン向けにレンダリングしてディレクトリへ
kube-kts template ./helm --name my-app --kube-version 1.29 --output-dir ./manifests
```
