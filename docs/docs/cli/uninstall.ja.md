# uninstall

```bash
kube-kts uninstall <REPOSITORY> [TARGET] --name <RELEASE> [--name <RELEASE>...] [オプション]
```

リポジトリをレンダリングし、`helm uninstall` を実行してクラスターから 1 つ以上のリリースを削除します。
アンインストールはリリースに属するリソースを削除し、既定ではその履歴も削除します。他の Helm ベースの
コマンドと同様に kube-context 経由でクラスターと通信し、サポートされるすべての Helm uninstall フラグを
転送します。

## 仕組み

1. リポジトリをスキャン・コンパイル・レンダリングします（レンダリング手順は他コマンドとワークフローを
   統一するためで、レンダリング結果が作業ディレクトリとして使われます）。
2. `helm uninstall <RELEASE>...` を実行し、転送オプションをすべて付加します。
3. Helm が各リリースを削除します。終了コードは Helm の結果を反映します。

!!! note "リリース名はオプション、名前空間は `-n`"
    リリース名は `--name`（繰り返し可）で渡し、位置引数 `RELEASE` として Helm に転送されます。`-n` は
    `--namespace` 用に予約されています。少なくとも 1 つの `--name` が必要です。

!!! warning "クラスターのリソースを削除します"
    アンインストールは破壊的です。まず `--dry-run` でプレビューし、後で確認やロールバックのために履歴を
    残したい場合は `--keep-history` を使ってください。

## パラメータ

| パラメータ | 必須 | 説明 |
|---|---|---|
| `REPOSITORY` | はい | リポジトリのパス。省略時はカレントディレクトリを使用します。 |
| `TARGET` | いいえ | Chart をレンダリングするディレクトリ。省略時は一時ディレクトリを使用します。 |

## uninstall オプション

| オプション | マーカー | 説明 |
|---|---|---|
| `--name=RELEASE` | | アンインストールするリリース。繰り返して 1 回の呼び出しで複数を削除でき、各々が位置引数 `RELEASE` として Helm に転送されます。**少なくとも 1 つ必要。** |
| `--cascade=STRING` | `---->` | 依存オブジェクトの削除方法: `background`（既定、バックグラウンド削除）、`foreground`（依存を先に待つ）、`orphan`（依存を残す）。 |
| `--description=TEXT` | `---->` | アンインストール操作に記録する人間可読のカスタム説明。 |
| `--dry-run` | `---->` | クラスターを変更せずにアンインストールをシミュレートし、削除対象を表示します。 |
| `--ignore-not-found` | `---->` | 見つからないリリースをエラーではなく成功として扱います。コマンドを冪等にします——クリーンアップスクリプトに便利。 |
| `--keep-history` | `---->` | リソースを削除しリリースを削除済みとしつつ、後の確認やロールバックのために履歴を保持します。 |
| `--no-hooks` | `---->` | アンインストール中、pre/post-delete hooks をスキップします。 |
| `--timeout=DURATION` | `---->` | 個々の Kubernetes 操作を待つ最大時間。Go の duration 形式（既定 `5m0s`）。`--wait` と関係します。 |
| `--wait` | `---->` | 返す前に、リリースのすべてのリソースが実際に削除されるまでブロックします。 |

## Helm グローバルオプション

| オプション | マーカー | 説明 |
|---|---|---|
| `-n`, `--namespace=NAMESPACE` | `---->` | リリースが存在する名前空間。既定は現在の kube-context の名前空間。間違った名前空間からのアンインストールを避けるため明示してください。 |
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
# "prod" 名前空間から単一リリースをアンインストール
kube-kts uninstall /path/to/repository --name my-app -n prod

# 複数リリースを履歴を保持してアンインストール
kube-kts uninstall . --name my-app --name my-worker --keep-history

# アンインストールをプレビューし、見つからないリリースを成功として扱う
kube-kts uninstall ./helm --name my-app --dry-run --ignore-not-found
```
