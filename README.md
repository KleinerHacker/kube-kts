# Kube KTS

Kube KTS is a solution and wrapper for Helm to change from classic Go-Templating
to Kotlin Scripts. 

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

