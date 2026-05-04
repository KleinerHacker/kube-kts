# Service DSL

Die `service` DSL wird verwendet, um Kubernetes Service-Ressourcen zu konfigurieren, die einen logischen Satz von Pods und eine Richtlinie für den Zugriff darauf definieren.

## Grundlegende Verwendung

Eine minimale Service-Konfiguration erfordert `metadata` und mindestens einen Port im `spec`.

```kotlin
service {
    metadata("my-service") {
        namespace = "default"
    }

    spec {
        type = ServiceSpec.Type.ClusterIP
        ports {
            port("http") {
                port = 80
                targetPort = 8080
            }
        }
    }
}
```

## Detailliertes Beispiel

Unten findest du ein umfassendes Beispiel, das verschiedene Konfigurationsoptionen für einen Service zeigt.

```kotlin
service {
    metadata("full-service") {
        namespace = "production"
    }

    spec {
        type = ServiceSpec.Type.LoadBalancer
        
        ports {
            port("https") {
                port = 443
                targetPort = 8443
                protocol = PortMappingSpec.Protocol.TCP
                appProtocol = "https"
            }
        }

        // IP-Konfiguration
        clusterIPs {
            clusterIP("10.0.0.1")
        }
        ipFamilies {
            ipFamily(ServiceSpec.IPFamily.IPv4)
        }
        ipFamilyPolicy = ServiceSpec.FamilyPolicy.SingleStack

        // Externer Zugriff
        externalIPs {
            externalIP("1.2.3.4")
        }
        externalTrafficPolicy = ServiceSpec.TrafficPolicy.Local
        
        // Load Balancer Einstellungen
        allocateLoadBalancerNodePorts = true
        loadBalancerClass = "example.com/internal-lb"
        loadBalancerSourceRanges {
            loadBalancerSourceRange("192.168.0.0/24")
        }

        // Sitzungsaffinität
        sessionAffinity = ServiceSpec.SessionAffinity.ClientIP
        sessionAffinityClientTimeout = 60.seconds.toJavaDuration()

        // Verkehrsmanagement
        publishNotReadyAddresses = false
        trafficDistribution = ServiceSpec.TrafficDistribution.PreferClose
    }
}
```

## Konfigurationsreferenz

### Metadaten (`metadata`)

| Eigenschaft | Typ | Beschreibung |
| :--- | :--- | :--- |
| `name` | `String` | Der Name des Service (als erstes Argument übergeben). |
| `namespace` | `String?` | Der Namespace für die Ressource. |
| `generateName` | `String?` | Ein optionales Präfix zur Generierung eines eindeutigen Namens. |

### Service-Spezifikation (`spec`)

| Eigenschaft / Methode | Beschreibung |
| :--- | :--- |
| `type` | Der Typ des Service (z. B. `ClusterIP`, `LoadBalancer`). |
| `ports { port(name) { ... } }` | Fügt eine Port-Mapping-Konfiguration hinzu. (Alternativ: `addPort`) |
| `clusterIPs { clusterIP(ip) }` | Legt Cluster-IP-Adressen fest. (Alternativ: `addClusterIP`, `addClusterIPs`) |
| `ipFamilies { ipFamily(family) }` | Fügt IP-Familien hinzu (IPv4/IPv6). (Alternativ: `addIpFamily`, `addIpFamilies`) |
| `ipFamilyPolicy` | Legt die IP-Familien-Richtlinie fest. |
| `externalIPs { externalIP(ip) }` | Fügt externe IP-Adressen hinzu. (Alternativ: `addExternalIP`, `addExternalIPs`) |
| `externalName` | Der externe Name für Services vom Typ `ExternalName`. |
| `externalTrafficPolicy` | Verkehrsrichtlinie für externen Verkehr. |
| `internalTrafficPolicy` | Verkehrsrichtlinie für internen Verkehr. |
| `allocateLoadBalancerNodePorts` | Ob Node-Ports für LoadBalancer-Services zugewiesen werden sollen. |
| `loadBalancerIP` | **Veraltet.** Verwende stattdessen implementierungsspezifische Annotationen. |
| `loadBalancerClass` | Die Klasse des Load Balancers. |
| `loadBalancerSourceRanges { loadBalancerSourceRange(range) }` | Fügt erlaubte CIDR-Bereiche für den Load Balancer hinzu. (Alternativ: `addLoadBalancerSourceRange`) |
| `sessionAffinity` | Die Richtlinie für die Sitzungsaffinität. |
| `sessionAffinityClientTimeout` | Zeitüberschreitung für die Sitzungsaffinität basierend auf der Client-IP. |
| `publishNotReadyAddresses` | Ob Adressen von Pods veröffentlicht werden sollen, die nicht bereit sind. |
| `healthCheckNodePort` | Der Port für Gesundheitsprüfungen (für Type=LoadBalancer). |
| `trafficDistribution` | Die Richtlinie für die Verkehrsverteilung. |

### Port-Mapping (`port`)

| Eigenschaft | Typ | Beschreibung |
| :--- | :--- | :--- |
| `port` | `Int` | Der Port, der durch den Service exponiert wird. |
| `targetPort` | `Int?` | Der Port auf den Pods, an den der Service den Verkehr weiterleiten soll. |
| `nodePort` | `Int?` | Der Port an jedem Knoten, an dem der Service exponiert wird. |
| `protocol` | `Protocol?` | Das IP-Protokoll (TCP, UDP, SCTP). |
| `appProtocol` | `String?` | Das Anwendungsprotokoll (z. B. http, https). |

## Spezielle Typen

- `ServiceSpec.Type`: `ClusterIP`, `NodePort`, `LoadBalancer`, `ExternalName`.
- `PortMappingSpec.Protocol`: `TCP`, `UDP`, `SCTP`.
- `ServiceSpec.IPFamily`: `IPv4`, `IPv6`.
- `ServiceSpec.FamilyPolicy`: `SingleStack`, `PreferDualStack`, `RequireDualStack`.
- `ServiceSpec.TrafficPolicy`: `Cluster`, `Local`.
- `ServiceSpec.SessionAffinity`: `None`, `ClientIP`.
- `ServiceSpec.TrafficDistribution`: `PreferClose`.