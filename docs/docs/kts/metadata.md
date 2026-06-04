# Metadata DSL

The `metadata` DSL is used to define the metadata of a Kubernetes resource. Metadata is present in almost all Kubernetes resources (such as Services, Ingresses, Deployments) and serves to identify and organize them.

## Basic Usage

In Kube KTS, the `metadata` block is defined within a resource (e.g. `service`, `ingress`). The **name** of the resource is passed as the first argument to the `metadata` function.

### Minimal Configuration

A minimal configuration only requires the name of the resource.

```kotlin
service {
    metadata("my-service") {
        // No further properties required
    }
    // ... remaining service configuration
}
```

## Full Configuration

The following is an example showing all available configuration options within the `metadata` block.

```kotlin
ingress {
    metadata("full-metadata-example") {
        namespace = "production"
        generateName = "app-prefix-"
        
        labels {
            label("app", "my-app")
            label("env", "prod")
        }
        
        annotations {
            annotation("cert-manager.io/cluster-issuer", "letsencrypt-prod")
            annotation("description", "Managed by Kube KTS")
        }
        
        finalizers {
            finalizer("kubernetes.io/pvc-protection")
        }
        
        ownerReferences {
            ownerReference("apps/v1", "Deployment", "parent-deploy", UUID.fromString("550e8400-e29b-11d4-a716-446655440000")) {
                controller = true
                blockOwnerDeletion = true
            }
        }
    }
    // ... remaining ingress configuration
}
```

## Configuration Reference

### Properties

| Property | Type | Description |
| :--- | :--- | :--- |
| `name` | `String` | The name of the resource (passed as the first argument of the `metadata` function). Must be unique within the namespace. |
| `namespace` | `String?` | The namespace in which the resource should be created. Defaults to `default`. |
| `generateName` | `String?` | A prefix for the name. Kubernetes generates a unique name by appending a suffix. |

### Methods

| Method | Description |
| :--- | :--- |
| `labels { label(key, value) }` | Adds labels for organization and selection. (Alternative: `addLabel`) |
| `annotations { annotation(key, value) }` | Adds non-identifying metadata usable by tools. (Alternative: `addAnnotation`) |
| `finalizers { finalizer(name) }` | Adds finalizers that control the deletion lifecycle of the resource. (Alternative: `addFinalizer`, `addFinalizers`) |
| `ownerReferences { ownerReference(...) { ... } }` | Defines dependencies on other resources (owner relationships). (Alternative: `addOwnerReference`) |

### Owner References (`ownerReference`)

The following fields can be set within `ownerReferences`:

| Property | Type | Description |
| :--- | :--- | :--- |
| `apiVersion` | `String` | The API version of the owning object. |
| `kind` | `String` | The kind of the owning object. |
| `name` | `String` | The name of the owning object. |
| `uid` | `UUID` | The unique ID (UID) of the owning object. |
| `controller` | `Boolean?` | Whether this reference points to a controller managing the object. |
| `blockOwnerDeletion` | `Boolean?` | Whether deletion of the owner should be blocked while this object exists. |

## About Metadata

Metadata is essential for Kubernetes because it:

1. **Identifies resources**: Via `name` and `namespace`.
2. **Groups resources**: Via `labels`, resources can be efficiently filtered and selected (e.g. by Services or NetworkPolicies).
3. **Enables extensibility**: Via `annotations`, additional information can be stored for controllers or external tools.
4. **Manages lifecycles**: Via `finalizers` and `ownerReferences`, resources are deleted in the correct order and cleanly.

In Kube KTS, metadata must be set in every template (`service`, `ingress`, etc.) to produce a valid Kubernetes resource.
