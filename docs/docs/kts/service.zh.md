# Service DSL

`service` DSL 用于配置 Kubernetes Service 资源，它定义一组逻辑上的 Pod 以及访问它们的策略。

!!! warning "安全性：导入限制"
    默认情况下，KTS 脚本**不允许**使用 `import` 语句或完全限定的类名
    （例如 `java.lang.Runtime`）。只能使用通过预配置默认导入提供的类型。

    使用 `--unsafe` 标志可解除这些限制。

## 基本用法

最小的 Service 配置需要 `metadata` 以及 `spec` 中至少一个端口。

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

## 详细示例

下面是一个完整示例，展示了 Service 的各种配置选项。

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

        // IP configuration
        clusterIPs {
            clusterIP("10.0.0.1")
        }
        ipFamilies {
            ipFamily(ServiceSpec.IPFamily.IPv4)
        }
        ipFamilyPolicy = ServiceSpec.FamilyPolicy.SingleStack

        // External access
        externalIPs {
            externalIP("1.2.3.4")
        }
        externalTrafficPolicy = ServiceSpec.TrafficPolicy.Local
        
        // Load balancer settings
        allocateLoadBalancerNodePorts = true
        loadBalancerClass = "example.com/internal-lb"
        loadBalancerSourceRanges {
            loadBalancerSourceRange("192.168.0.0/24")
        }

        // Session affinity
        sessionAffinity = ServiceSpec.SessionAffinity.ClientIP
        sessionAffinityClientTimeout = 60.seconds.toJavaDuration()

        // Traffic management
        publishNotReadyAddresses = false
        trafficDistribution = ServiceSpec.TrafficDistribution.PreferClose
    }
}
```

## 配置参考

### 元数据（`metadata`）

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `name` | `String` | Service 的名称（作为第一个参数传递）。 |
| `namespace` | `String?` | 资源的命名空间。 |
| `generateName` | `String?` | 用于生成唯一名称的可选前缀。 |

### Service 规约（`spec`）

| 属性 / 方法 | 说明 |
| :--- | :--- |
| `type` | Service 的类型（例如 `ClusterIP`、`LoadBalancer`）。 |
| `ports { port(name) { ... } }` | 添加端口映射配置。（替代方法：`addPort`） |
| `clusterIPs { clusterIP(ip) }` | 设置集群 IP 地址。（替代方法：`addClusterIP`、`addClusterIPs`） |
| `ipFamilies { ipFamily(family) }` | 添加 IP 协议族（IPv4/IPv6）。（替代方法：`addIpFamily`、`addIpFamilies`） |
| `ipFamilyPolicy` | 设置 IP 协议族策略。 |
| `externalIPs { externalIP(ip) }` | 添加外部 IP 地址。（替代方法：`addExternalIP`、`addExternalIPs`） |
| `externalName` | 类型为 `ExternalName` 的 Service 的外部名称。 |
| `externalTrafficPolicy` | 外部流量的流量策略。 |
| `internalTrafficPolicy` | 内部流量的流量策略。 |
| `allocateLoadBalancerNodePorts` | 是否为 LoadBalancer 类型的 Service 分配节点端口。 |
| `loadBalancerIP` | **已弃用。** 请改用特定于实现的注解。 |
| `loadBalancerClass` | 负载均衡器的类别。 |
| `loadBalancerSourceRanges { loadBalancerSourceRange(range) }` | 添加负载均衡器允许的 CIDR 范围。（替代方法：`addLoadBalancerSourceRange`） |
| `sessionAffinity` | 会话亲和性策略。 |
| `sessionAffinityClientTimeout` | 基于客户端 IP 的会话亲和性的超时时间。 |
| `publishNotReadyAddresses` | 是否发布尚未就绪的 Pod 的地址。 |
| `healthCheckNodePort` | 健康检查端口（用于 Type=LoadBalancer）。 |
| `trafficDistribution` | 流量分配策略。 |

### 端口映射（`port`）

| 属性 | 类型 | 说明 |
| :--- | :--- | :--- |
| `port` | `Int` | Service 暴露的端口。 |
| `targetPort` | `Int?` | Service 将流量转发到的 Pod 端口。 |
| `nodePort` | `Int?` | 在每个节点上暴露 Service 的端口。 |
| `protocol` | `Protocol?` | IP 协议（TCP、UDP、SCTP）。 |
| `appProtocol` | `String?` | 应用层协议（例如 http、https）。 |

## 特殊类型

- `ServiceSpec.Type`：`ClusterIP`、`NodePort`、`LoadBalancer`、`ExternalName`。
- `PortMappingSpec.Protocol`：`TCP`、`UDP`、`SCTP`。
- `ServiceSpec.IPFamily`：`IPv4`、`IPv6`。
- `ServiceSpec.FamilyPolicy`：`SingleStack`、`PreferDualStack`、`RequireDualStack`。
- `ServiceSpec.TrafficPolicy`：`Cluster`、`Local`。
- `ServiceSpec.SessionAffinity`：`None`、`ClientIP`。
- `ServiceSpec.TrafficDistribution`：`PreferClose`。
