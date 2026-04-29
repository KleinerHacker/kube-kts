# Kube KTS

Kube KTS is a solution and wrapper for Helm to change from classic Go-Templating
to Kotlin Scripts. 

---

IMPORTANT HINT: This is a work in progress. The complete documentation will follow
with the help of MK Docs in future times.

## Overview

### Motivation

The Helm Go-Templates destroy YAML structure and make it hard to read templates
and debug them. Like usage in Gradle with Kotlin Script (KTS) you can use the 
Vorteil of its declarative look and feel. At the same time you can use classic
programmatic structures known from Kotlin. Additionally, you get type safety and
validation while compiling and rendering.

### Structure

Like Helm create a normal `helm` directory with all known files. Instead, to
create YAML files, you can use Kotlin Script files. With this tool you can now
compile and render it to classic YAML files, 100% compatible with Helm.

#### Legacy Support

Kube KTS also supports the classic Helm Go-Templates. All files with the `.yaml` or `.yml`
extension are used as classic Helm Go-Templates. Additionally, all other file types
are copied to the YAML repository, too.

### Values

The values.yaml file is usable like in classic Helm Go-Templates. Multiple values
would be combined into one map.

In KTS you do not need to set the root key `values`. This is done automatically. In
the case of complex objects, it is required to use lambda functions to use the new root
node from the values.yaml file at this position. 

## Examples

## chart.yaml > chart.kts

This is a complete example of a chart.yaml file.

```kotlin
chart("name", "1.0.0") {
    kubeVersion {
        minInclusive("1.0.0")
        maxExclusive("2.0.0")
    }
    description = "description"
    type = ChartSpec.Type.Library

    addKeyword("keyword")

    home = "home"

    addSource("source")

    addDependency("dependency", "1.0.0") {
        repository = URI("https://repo.example.com")
        alias = "alias"
        condition = "condition"

        addTag("tag")
        addPathImportValue("path")
        addMappingImportValue("key", "value")
    }

    addMaintainer("maintainer") {
        email = "email"
        url = URI("https://url.example.com")
    }

    icon = "icon"
    appVersion = "appVersion"
    deprecated = true

    addAnnotation("annotation", "value")
}
```

### servcie.yaml > service.kts

This is a complete example of a service.yaml file.

```kotlin
service {
    metadata("metadata") {
        namespace = "namespace"
        generateName = "generateName"
    }
    
    spec {
        type = ServiceSpec.Type.LoadBalancer

        addPort("port") {
            port = 9999
            targetPort = 8888
            nodePort = 7777
            protocol = PortSpec.Protocol.SCTP
            appProtocol = "https"
        }

        addClusterIP("clusterIP")

        addIpFamily(ServiceSpec.IPFamily.IPv4)
        addIpFamily(ServiceSpec.IPFamily.IPv6)
        ipFamilyPolicy = ServiceSpec.FamilyPolicy.RequireDualStack

        addExternalIP("externalIP")
        externalName = "externalName"

        externalTrafficPolicy = ServiceSpec.TrafficPolicy.Local
        internalTrafficPolicy = ServiceSpec.TrafficPolicy.Local

        allocateLoadBalancerNodePorts = false
        loadBalancerIP = "loadBalancerIP"
        loadBalancerClass = "loadBalancerClass"
        addLoadBalancerSourceRange("loadBalancerSourceRange")

        sessionAffinity = ServiceSpec.SessionAffinity.None
        sessionAffinityClientTimeout = 30.seconds.toJavaDuration()

        publishNotReadyAddresses = true
        healthCheckNodePort = 3000
        trafficDistribution = ServiceSpec.TrafficDistribution.PreferClose
    }
}
```

### ingress.yaml > ingress.kts

This is a complete example of an ingress.yaml file.

```kotlin
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

        exists("security.tls") { // values
            addTls {
                secretName = value<String>("security.tls.secret")
                addHost(value<String>("security.tls.host"))
            }
        }

        exists("routes.rules") { // values
            array("routes.rules") { // values
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
```

### values.yaml

This is a complete example of a values.yaml file.

```yaml
values:
    security:
      tls:
        secret: "secretName"
        host: "host.example.com"
    
    routes:
      rules:
        - host: "rule1.example.com"
          path: "path1"
          port: 9999
        - host: "rule2.example.com"
          path: "path2"
          port: 8888
```

