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

        exists("security.tls") {
            addTls {
                secretName = value<String>("security.tls.secret")
                addHost(value<String>("security.tls.host"))
            }
        }

        exists("routes.rules") {
            array("routes.rules") {
                addRule {
                    host = it.value<String>("host")
                    addHttpPath(RulesSpec.HttpPathConfig.PathType.Exact) {
                        path = it.value<String>("path")
                        serviceBackend("ruleService") {
                            port(it.value<Int>("port"))
                        }
                    }
                }
            }
        }
    }
}