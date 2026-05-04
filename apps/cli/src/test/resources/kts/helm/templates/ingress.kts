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
            tlsList {
                tls {
                    secretName = value<String>("security.tls.secret")
                    hosts {
                        host(value<String>("security.tls.host"))
                    }
                }
            }
        }

        rules {
            rule {
                host = "rule.example.com"
                httpPaths {
                    httpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                        path = "path"
                        serviceBackend("ruleService") {
                            port(7777)
                        }
                    }
                }
            }
        }
    }
}