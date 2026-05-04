# Metadaten DSL

Die `metadata` DSL wird verwendet, um die Metadaten einer Kubernetes-Ressource zu definieren. Metadaten sind in fast allen Kubernetes-Ressourcen (wie Services, Ingresses, Deployments) enthalten und dienen der Identifizierung und Organisation.

## Grundlegende Verwendung

In Kube-KTS wird der `metadata` Block innerhalb einer Ressource (z. B. `service`, `ingress`) definiert. Der **Name** der Ressource wird als erstes Argument an die `metadata` Funktion übergeben.

### Minimale Ausprägung

Eine minimale Konfiguration benötigt lediglich den Namen der Ressource.

```kotlin
service {
    metadata("my-service") {
        // Keine weiteren Angaben nötig
    }
    // ... restliche service konfiguration
}
```

## Maximale Ausprägung

Unten findest du ein Beispiel, das alle verfügbaren Konfigurationsoptionen innerhalb des `metadata` Blocks zeigt.

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
            annotation("description", "Managed by Kube-KTS")
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
    // ... restliche ingress konfiguration
}
```

## Konfigurationsreferenz

### Eigenschaften

| Eigenschaft | Typ | Beschreibung |
| :--- | :--- | :--- |
| `name` | `String` | Der Name der Ressource (als erstes Argument der `metadata` Funktion übergeben). Muss im Namespace eindeutig sein. |
| `namespace` | `String?` | Der Namespace, in dem die Ressource erstellt werden soll. Standardmäßig `default`. |
| `generateName` | `String?` | Ein Präfix für den Namen. Kubernetes generiert daraus einen eindeutigen Namen durch Anhängen eines Suffixes. |

### Methoden

| Methode | Beschreibung |
| :--- | :--- |
| `labels { label(key, value) }` | Fügt Labels zur Organisation und Selektion hinzu. (Alternativ: `addLabel`) |
| `annotations { annotation(key, value) }` | Fügt nicht-identifizierende Metadaten hinzu, die von Tools genutzt werden können. (Alternativ: `addAnnotation`) |
| `finalizers { finalizer(name) }` | Fügt Finalizer hinzu, die den Löschvorgang der Ressource steuern. (Alternativ: `addFinalizer`, `addFinalizers`) |
| `ownerReferences { ownerReference(...) { ... } }` | Definiert Abhängigkeiten zu anderen Ressourcen (Besitzer-Beziehungen). (Alternativ: `addOwnerReference`) |

### Owner-Referenzen (`ownerReference`)

Innerhalb von `ownerReferences` können folgende Felder gesetzt werden:

| Eigenschaft | Typ | Beschreibung |
| :--- | :--- | :--- |
| `apiVersion` | `String` | Die API-Version des besitzenden Objekts. |
| `kind` | `String` | Die Art (Kind) des besitzenden Objekts. |
| `name` | `String` | Der Name des besitzenden Objekts. |
| `uid` | `UUID` | Die eindeutige ID (UID) des besitzenden Objekts. |
| `controller` | `Boolean?` | Ob diese Referenz auf einen Controller zeigt, der das Objekt verwaltet. |
| `blockOwnerDeletion` | `Boolean?` | Ob das Löschen des Besitzers blockiert werden soll, solange dieses Objekt existiert. |

## Allgemeines zu Metadaten

Metadaten sind essentiell für Kubernetes, da sie:
1. **Ressourcen identifizieren**: Über `name` und `namespace`.
2. **Ressourcen gruppieren**: Über `labels` können Ressourcen effizient gefiltert und ausgewählt werden (z.B. durch Services oder NetworkPolicies).
3. **Erweiterbarkeit ermöglichen**: Über `annotations` können zusätzliche Informationen für Controller oder externe Tools hinterlegt werden.
4. **Lebenszyklen verwalten**: Über `finalizers` und `ownerReferences` wird sichergestellt, dass Ressourcen in der richtigen Reihenfolge und sauber gelöscht werden.

In Kube-KTS müssen Metadaten in jedem Template (`service`, `ingress`, etc.) gesetzt werden, um eine gültige Kubernetes-Ressource zu erzeugen.

