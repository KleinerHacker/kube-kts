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

        if (exists("security.tls")) {
            addTls {
                secretName = value<String>("security.tls.secret")
                addHost(value<String>("security.tls.host"))
            }
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