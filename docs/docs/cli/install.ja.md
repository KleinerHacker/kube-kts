# install

```bash
kube-kts install <REPOSITORY> [TARGET] --name <NAME> [オプション]
```

リポジトリを Helm Chart にレンダリングし、`helm install` を実行して新しいリリースとして Kubernetes
クラスターにデプロイします。実際にクラスターの状態を変えるコマンドなので、kube-context（または下記の
上書き）を使って API サーバーと通信し、サポートされるすべての Helm install フラグを転送します。

## 仕組み

1. リポジトリをスキャン・コンパイルし、`-f`/`--set*` の値を適用して（一時または明示の）Chart
   ディレクトリにレンダリングします。
2. そのディレクトリで `helm install <NAME> .` を実行し、転送オプションをすべて付加します。
3. Helm がリリースを作成します。終了コードは Helm の結果を反映します。失敗時、`--atomic` を渡さない
   限りロールバックは行われません。

!!! note "リリース名はオプション、名前空間は `-n`"
    リリース名は `--name` で渡し、位置引数 `NAME` として Helm に転送されます。`-n` ショートオプションは
    意図的に名前**ではなく**、Helm に合わせて `--namespace` 用に予約されています。`--name` を省略できる
    のは `--generate-name` と併用するときだけです。

!!! tip "より安全なロールアウト"
    `--atomic`（失敗時にロールバック）を `--wait` / `--wait-for-jobs` と妥当な `--timeout` と組み合わせ、
    失敗したインストールが中途半端なリソースを残さないようにします。

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `REPOSITORY` | はい | インストールするリポジトリのパス。省略時はカレントディレクトリを使用します。 |
| `TARGET` | いいえ | インストール前に Chart をレンダリングするディレクトリ。省略時は一時ディレクトリを使用します。 |

## install オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--name=NAME` | | 作成するリリース名。位置引数 `NAME` として Helm に転送されます。`--generate-name` を使わない限り必須です。 |
| `--atomic` | `---->` | インストールが失敗したら作成済みのものをすべて自動削除し、クラスターを元の状態に戻します。`--wait` を含みます。無人/CI インストールに強く推奨。 |
| `--create-namespace` | `---->` | 対象の名前空間（`-n`/`--namespace`）が存在しなければ失敗せず作成します。 |
| `--dry-run` | `---->` | クラスターを変更せずにインストールをシミュレートし、何が起こるかを表示します。リリースのプレビューに最適。 |
| `--enable-dns` | `---->` | レンダリング中にテンプレートが DNS ルックアップを行えるようにします（Helm の `getHostByName` など）。再現性のため既定でオフです。 |
| `--force` | `---->` | リソースを置換して更新を強制します。動かなくなった状態からの復旧に使います。破壊的になり得ます——リソースを削除・再作成する場合があります。 |
| `-g`, `--generate-name` | `---->` | Helm にリリース名を自動生成させます。`--name` の代わりに使います。 |
| `-l`, `--labels=KEY=VALUE` | `---->` | リリースのメタデータ（レンダリング結果ではなく Helm のリリースオブジェクト）にラベルを追加します。繰り返し可。 |
| `--description=TEXT` | `---->` | このリリースリビジョンに人間可読のカスタム説明を付けます。 |
| `-o`, `--output=FORMAT` | `---->` | コマンド結果の出力形式: `table`（既定）、`json`、`yaml`。スクリプト化時は `json`/`yaml` を使います。 |
| `--replace` | `---->` | 履歴に残る、以前削除されたリリースの名前を再利用します。本番では非推奨。クリーンなアンインストール/インストールが望ましいです。 |
| `--wait` | `---->` | リリースを成功とする前に、作成されたすべてのリソース（Pod、PVC、Service、Deployment など）が準備完了を報告するまでブロックします。`--timeout` に従います。 |
| `--wait-for-jobs` | `---->` | `--wait` に加え、リリースが作成したすべての Job が完了するまでブロックします。 |

## 値オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--set=KEY=VALUE` | `---->` | 値をインラインで上書き（例: `--set image.tag=1.2.3`）。型は推論されます。繰り返し可能で、後の `--set` が勝ち `-f` を上書きします。 |
| `--set-string=KEY=VALUE` | `---->` | `--set` と同様ですが常に文字列として保持します（例: `"01"`）。繰り返し可。 |
| `--set-file=KEY=PATH` | `---->` | `PATH` のファイル内容全体を `KEY` の値にします（証明書、スクリプトなど）。繰り返し可。 |
| `--set-json=KEY=JSON` | `---->` | JSON 式から値を設定し、オブジェクト/配列を表現できます（例: `--set-json 'ports=[80,443]'`）。繰り返し可。 |
| `--set-literal=KEY=VALUE` | `---->` | 書いたとおりに値を設定し、型変換を一切行いません。繰り返し可。 |
| `-f`, `--values=FILE` | `---->` | レンダリングに使われ Helm に転送される YAML 値ファイル。繰り返し可能で順に重ねられ、`--set*` がこれらを上書きします。 |

## Chart ソースと検証オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--repo=URL` | `---->` | ローカルパスではなく、このリポジトリ URL から Chart を取得します。 |
| `--username=USER` | `---->` | Chart リポジトリのユーザー名（`--password` と併用）。 |
| `--password=PASSWORD` | `---->` | Chart リポジトリのパスワード。コマンドラインより秘密情報/環境変数を推奨します。 |
| `--pass-credentials` | `---->` | リポジトリの資格情報を Chart のホストだけでなく全ドメインに送ります。信頼できるリポジトリでのみ。 |
| `--ca-file=FILE` | `---->` | HTTPS Chart サーバーの TLS 証明書検証に使う CA バンドル。 |
| `--cert-file=FILE` | `---->` | Chart サーバーへの相互 TLS 用クライアント TLS 証明書。 |
| `--key-file=FILE` | `---->` | `--cert-file` に対応するクライアント TLS 秘密鍵。 |
| `--insecure-skip-tls-verify` | `---->` | Chart ダウンロード時の TLS 検証をスキップします。安全でない——本番では避けてください。 |
| `--keyring=FILE` | `---->` | 署名付き Chart の検証に使う公開鍵のキーリング（`--verify` と併用）。既定 `~/.gnupg/pubring.gpg`。 |
| `--verify` | `---->` | インストール前に Chart の来歴署名を検証します。欠落/無効なら失敗します。 |
| `--version=VERSION` | `---->` | Chart バージョンを選ぶ SemVer 制約（例: `1.2.3` や `^1.2`）。既定は最新の安定版です。 |
| `--devel` | `---->` | プレリリース版も対象にします（`>0.0.0-0` 相当）。 |
| `--dependency-update` | `---->` | インストール前に不足している Chart 依存をダウンロード/更新します（`helm dependency update` 相当）。 |

## レンダリングオプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--no-hooks` | `---->` | インストール中、すべての Chart ライフサイクル hooks（pre/post-install など）をスキップします。 |
| `--disable-openapi-validation` | `---->` | レンダリング結果をクラスターの OpenAPI スキーマで検証しません。高速ですが安全網を省きます。 |
| `--name-template=TEMPLATE` | `---->` | 固定の `--name` の代わりにリリース名を計算する Go テンプレート。 |
| `--render-subchart-notes` | `---->` | 親だけでなくサブチャートの NOTES.txt もレンダリングします。 |
| `--skip-crds` | `---->` | Chart 同梱の CRD をインストールしません（CRD を別管理する場合）。 |
| `--post-renderer=PATH` | `---->` | 適用前のレンダリング済みマニフェストに対して実行する実行ファイル。Kustomize 風の後処理が可能です。 |
| `--post-renderer-args=ARG` | `---->` | `--post-renderer` 実行ファイルへ渡す引数。繰り返し可。 |
| `--timeout=DURATION` | `---->` | 個々の Kubernetes 操作を待つ最大時間。Go の duration 形式（`5m0s`、`90s` など）。既定 `5m0s`。`--wait` と併用時に最も重要。 |

## Helm グローバルオプション

| オプション | マーカー | 説明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | リリースをインストールする名前空間。存在しない場合は `--create-namespace` と併用します。既定は現在の kube-context の名前空間。 |
| `--kube-context=CONTEXT` | `---->` | 使用する kubeconfig 内のコンテキスト。現在のコンテキストを切り替えずに特定のクラスター/ユーザーを対象にできます。 |
| `--kubeconfig=FILE` | `---->` | `$KUBECONFIG` / `~/.kube/config` の代わりに使う kubeconfig ファイルのパス。 |
| `--kube-apiserver=ADDRESS` | `---->` | kubeconfig の API サーバーアドレスとポートを上書きします。 |
| `--kube-as-user=USER` | `---->` | このユーザーを偽装します。偽装権限が必要です。 |
| `--kube-as-group=GROUP` | `---->` | このグループを偽装します。繰り返し可。 |
| `--kube-ca-file=FILE` | `---->` | API サーバーの TLS 証明書検証に使う CA 証明書ファイル。 |
| `--kube-token=TOKEN` | `---->` | kubeconfig の資格情報の代わりに認証に使うベアラートークン。 |
| `--kube-tls-server-name=NAME` | `---->` | API サーバー証明書の検証に使うサーバー名（URL のホストと異なる場合）。 |
| `--kube-insecure-skip-tls-verify` | `---->` | API サーバー証明書の検証をスキップします。安全でない——信頼済み/テストのみ。 |
| `--burst-limit=INT` | `---->` | API リクエストのクライアント側スロットリングのバースト上限（既定 `100`）。非常に大きな Chart では引き上げます。 |
| `--qps=QPS` | `---->` | API サーバーへのクライアント側の毎秒クエリ上限。小数可。 |
| `--registry-config=FILE` | `---->` | OCI レジストリ設定（資格情報）ファイルのパス。 |
| `--repository-cache=DIR` | `---->` | 依存解決に使うキャッシュ済みリポジトリインデックスのディレクトリ。 |
| `--repository-config=FILE` | `---->` | リポジトリ名と URL を対応づけるファイルのパス（`repositories.yaml`）。 |

## グローバルオプション

すべての[グローバルオプション](index.md)（`--debug`、`--unsafe` など）も利用できます。

## 例

```bash
# "prod" 名前空間にリリース "my-app" をインストールし、準備完了を待機
kube-kts install /path/to/repository --name my-app -n prod --create-namespace --wait

# 値の上書きと長めのタイムアウトを伴うアトミックインストール
kube-kts install . --name my-app --atomic --timeout 10m --set image.tag=1.2.3 -f prod.yaml

# クラスターに触れずにインストール内容をプレビュー
kube-kts install ./helm --name my-app --dry-run
```
