ingress {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }

    spec {
        ingressClassName = "className"

        defaultServiceBackend("service") {
            port(9999)
        }

        addTls {
            secretName = "secretName"
            addHost("host.example.com")
        }

        addRule {
            host = "rule.example.com"
            addHttpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                path = "path"
                serviceBackend("ruleService") {
                    port(7777)
                }
            }
        }
    }
}