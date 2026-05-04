/*
 * Copyright (c) KleinerHacker alias pcsoft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.api.chart

import org.pcsoft.framework.kube.kts.api.chart.ChartSpec.Type
import org.pcsoft.framework.kube.kts.api.chart.types.DependencySpecBuilder
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersion
import org.pcsoft.framework.kube.kts.api.chart.types.KubeVersionBuilder
import org.pcsoft.framework.kube.kts.api.chart.types.MaintainerSpecBuilder
import java.net.URI

/**
 * A DSL builder for constructing [ChartSpec] objects.
 *
 * This class provides a fluent API for configuring Helm chart metadata, including
 * dependencies, maintainers, keywords, and Kubernetes version compatibility.
 * It is typically used via the top-level [chart] function.
 *
 * @param name The unique name of the Helm chart.
 * @param version The semantic version of the chart.
 */
class ChartSpecBuilder internal constructor(private val name: String, private val version: String) {
    private var keywords: MutableSet<String>? = null
    private var sources: MutableList<URI>? = null
    private var dependencies: MutableList<DependencySpecBuilder>? = null
    private var maintainers: MutableList<MaintainerSpecBuilder>? = null
    private var annotations: MutableMap<String, String>? = null
    private var kubeVersion: KubeVersionBuilder? = null

    /**
     * An optional brief description of the chart's purpose or usage.
     */
    var description: String? = null

    /**
     * The type of the Helm chart: [Type.Application] (default) or [Type.Library].
     *
     * - `Application`: A standard Helm chart for deploying applications.
     * - `Library`: Provides reusable templates or helpers for other charts.
     */
    var type: Type? = null

    /**
     * The URL of the chart's official homepage or project site.
     */
    var home: String? = null

    /**
     * The [URI] of the chart's icon (e.g., SVG or PNG).
     */
    var icon: URI? = null

    /**
     * The version of the underlying application being deployed.
     *
     * This is distinct from the [version] of the chart itself.
     */
    var appVersion: String? = null

    /**
     * Whether the chart is marked as deprecated.
     */
    var deprecated: Boolean? = null

    /**
     * Adds a keyword to the chart metadata.
     */
    fun addKeyword(keyword: String) {
        if (keywords == null) {
            keywords = mutableSetOf()
        }
        keywords!!.add(keyword)
    }

    /**
     * Adds multiple keywords to the chart metadata.
     */
    fun addKeywords(vararg keywords: String) {
        if (this.keywords == null) {
            this.keywords = mutableSetOf()
        }
        this.keywords!!.addAll(keywords.toSet())
    }

    /**
     * Configures the keywords using a DSL block.
     *
     * Example:
     * ```kotlin
     * keywords {
     *     add("kubernetes")
     *     add("helm")
     * }
     * ```
     */
    fun keywords(prepare: KeywordListBuilder.() -> Unit) =
        KeywordListBuilder().apply(prepare)

    /**
     * Adds a source URI to the chart metadata.
     *
     * @param source A [URI] representing the source repository of the chart.
     */
    fun addSource(source: URI) {
        if (sources == null) {
            sources = mutableListOf()
        }
        sources!!.add(source)
    }

    /**
     * Adds one or more source URIs to the chart metadata.
     *
     * @param sources A variable number of [URI]s representing the source repositories of the chart.
     */
    fun addSources(vararg sources: URI) {
        if (this.sources == null) {
            this.sources = mutableListOf()
        }
        this.sources!!.addAll(sources.toList())
    }

    /**
     * Configures the sources associated with the chart metadata using a DSL block.
     *
     * Example:
     * ```kotlin
     * sources {
     *     add(URI("https://github.com/example/repo"))
     *     add(URI("https://gitlab.com/example/project"))
     * }
     * ```
     *
     * @param prepare A lambda block for configuring the list of source URIs.
     */
    fun sources(prepare: SourceListBuilder.() -> Unit) =
        SourceListBuilder().apply(prepare)

    /**
     * Adds a sub-chart dependency.
     *
     * Example:
     * ```kotlin
     * addDependency("common", "1.0.0") {
     *     repository = URI("https://charts.example.com")
     *     condition = "common.enabled"
     * }
     * ```
     *
     * @param name The name of the dependency.
     * @param version The version constraint for the dependency.
     * @param prepare A DSL block for configuring the [DependencySpecBuilder].
     */
    fun addDependency(name: String, version: String, prepare: DependencySpecBuilder.() -> Unit) {
        if (dependencies == null) {
            dependencies = mutableListOf()
        }
        dependencies!!.add(DependencySpecBuilder(name, version).apply(prepare))
    }

    /**
     * Configures the chart dependencies using a DSL block.
     *
     * Example:
     * ```kotlin
     * dependencies {
     *     dependency("mariadb", "11.0.0") {
     *         repository = URI("https://charts.bitnami.com/bitnami")
     *     }
     * }
     * ```
     */
    fun dependencies(prepare: DependencyListBuilder.() -> Unit) {
        DependencyListBuilder().apply(prepare)
    }

    /**
     * Adds a maintainer to the chart metadata.
     *
     * Example:
     * ```kotlin
     * addMaintainer("John Doe") {
     *     email = "john.doe@example.com"
     *     url = "https://github.com/johndoe"
     * }
     * ```
     *
     * @param name The name of the maintainer.
     * @param prepare A DSL block for configuring the [MaintainerSpecBuilder] (email, url).
     */
    fun addMaintainer(name: String, prepare: MaintainerSpecBuilder.() -> Unit) {
        if (maintainers == null) {
            maintainers = mutableListOf()
        }
        maintainers!!.add(MaintainerSpecBuilder(name).apply(prepare))
    }

    /**
     * Configures the list of maintainers associated with the chart metadata using a DSL block.
     *
     * This method allows you to define multiple maintainers by utilizing the [MaintainerListBuilder].
     * Each maintainer can have attributes like name, email, and URL configured within the block.
     *
     * Example:
     * ```kotlin
     * maintainers {
     *     maintainer("John Doe") {
     *         email = "john.doe@example.com"
     *         url = "https://github.com/johndoe"
     *     }
     *     maintainer("Jane Smith") {
     *         email = "jane.smith@example.com"
     *     }
     * }
     * ```
     *
     * @param prepare A lambda block for configuring the list of maintainers.
     */
    fun maintainers(prepare: MaintainerListBuilder.() -> Unit) {
        MaintainerListBuilder().apply(prepare)
    }

    /**
     * Adds a key-value pair as an annotation to the chart metadata.
     *
     * Annotations can be used to attach custom metadata to the chart in the form of key-value pairs.
     *
     * @param key The key representing the name of the annotation. Must be unique within the context of annotations.
     * @param value The value associated with the given key for the annotation.
     */
    fun addAnnotation(key: String, value: String) {
        if (annotations == null) {
            annotations = mutableMapOf()
        }
        annotations!![key] = value
    }

    /**
     * Configures the annotations associated with the chart metadata using a DSL block.
     *
     * This method allows you to define multiple annotations in the form of key-value pairs.
     * Annotations can be used to store metadata or custom information related to the chart.
     *
     * Example:
     * ```kotlin
     * annotations {
     *     annotation("category", "database")
     *     annotation("license", "Apache-2.0")
     * }
     * ```
     *
     * @param prepare A lambda block for configuring the annotations using [AnnotationListBuilder].
     */
    fun annotations(prepare: AnnotationListBuilder.() -> Unit) {
        AnnotationListBuilder().apply(prepare)
    }

    /**
     * Sets the required Kubernetes version for the chart.
     *
     * @param version The Kubernetes version constraint as a string. This version will be added
     *                with an equality operator (==) to the chart's Kubernetes version requirements.
     */
    fun kubeVersion(version: String) {
        kubeVersion = KubeVersionBuilder().apply {
            add(version) {
                equality = KubeVersion.ItemEquality.EQUAL
            }
        }
    }

    /**
     * Configures the Kubernetes version requirements using a DSL block.
     *
     * Example:
     * ```kotlin
     * kubeVersion {
     *     minInclusive("1.20.0")
     *     maxExclusive("1.30.0")
     * }
     * ```
     */
    fun kubeVersion(prepare: KubeVersionBuilder.() -> Unit) {
        kubeVersion = KubeVersionBuilder().apply(prepare)
    }

    internal fun build(): ChartSpec =
        ChartSpec(
            apiVersion = ChartSpec.API_VERSION,
            name = name,
            version = version,
            description = description,
            keywords = keywords,
            sources = sources,
            dependencies = dependencies?.map { it.build() },
            maintainers = maintainers?.map { it.build() },
            kubeVersion = kubeVersion?.build(),
            type = type,
            home = home,
            icon = icon,
            appVersion = appVersion,
            deprecated = deprecated,
            annotations = annotations
        )

    /**
     * Builder for managing a list of chart dependencies within the chart metadata.
     *
     * This class is designed to be used within a DSL context to define and configure
     * a set of dependencies required by a chart. Each dependency is defined by its
     * name and version, and can include additional configuration through a [DependencySpecBuilder].
     */
    inner class DependencyListBuilder internal constructor() {
        /**
         * Defines a dependency within the context of a chart's metadata.
         *
         * This method allows you to specify a sub-chart dependency by providing its name and version.
         * Additional configuration can be applied using the [DependencySpecBuilder] DSL.
         *
         * Example:
         * ```kotlin
         * dependency("mariadb", "11.0.0") {
         *     repository = URI("https://charts.bitnami.com/bitnami")
         *     condition = "mariadb.enabled"
         * }
         * ```
         *
         * @param name The name of the dependency to add.
         * @param version The version constraint for the dependency to be used.
         * @param prepare A block of configuration applied to the [DependencySpecBuilder].
         */
        fun dependency(name: String, version: String, prepare: DependencySpecBuilder.() -> Unit) =
            addDependency(name, version, prepare)
    }

    /**
     * Builder for configuring a list of maintainers in chart metadata.
     *
     * This class allows defining multiple maintainers using a DSL block. Each maintainer can be
     * configured with attributes such as name, email, and URL.
     *
     * The configured maintainers are typically added to a chart's metadata and provide information
     * about individuals or teams responsible for the chart.
     */
    inner class MaintainerListBuilder internal constructor() {
        /**
         * Adds a maintainer to the chart metadata using the DSL configuration block.
         *
         * This function allows specifying details of a maintainer, such as their name, email address, 
         * and personal or project URL through a lambda configuration block for the [MaintainerSpecBuilder]. 
         * The information is used in Helm chart metadata to identify individuals or teams responsible for the chart.
         *
         * Example:
         * ```kotlin
         * maintainer("John Doe") {
         *     email = "john.doe@example.com"
         *     url = "https://github.com/johndoe"
         * }
         * ```
         *
         * @param name The name of the maintainer.
         * @param prepare A DSL block for configuring the maintainer details using [MaintainerSpecBuilder].
         */
        fun maintainer(name: String, prepare: MaintainerSpecBuilder.() -> Unit) =
            addMaintainer(name, prepare)
    }

    /**
     * Builder class for defining a list of annotations in a chart's metadata.
     *
     * This class provides a DSL for adding annotations, which are key-value pairs
     * used for attaching custom metadata or information to a chart.
     *
     * Instances of this builder are meant to be used within the enclosing `ChartSpecBuilder`
     * to configure annotations for the chart.
     */
    inner class AnnotationListBuilder internal constructor() {
        /**
         * Adds an annotation in the form of a key-value pair to the list of annotations.
         *
         * Annotations provide a mechanism to attach custom metadata or additional information
         * to a chart in a structured format.
         *
         * @param key The key representing the name of the annotation. This must be unique within the list of annotations.
         * @param value The value associated with the key for the annotation.
         */
        fun annotation(key: String, value: String) =
            addAnnotation(key, value)
    }

    /**
     * A builder class for managing keywords in the chart metadata using DSL syntax.
     *
     * This class allows you to define and add individual keywords to the chart metadata
     * through a fluent and concise API. Keywords are typically used to describe the nature
     * and purpose of a chart, aiding in discovery and categorization.
     *
     * This builder is not intended to be instantiated directly but is configured via the
     * containing class's DSL entry point.
     */
    inner class KeywordListBuilder internal constructor() {
        /**
         * Adds a keyword to the list of keywords for the chart metadata.
         *
         * @param keyword The keyword to be added. This is typically a descriptive term
         *                used to categorize or tag the chart for purposes such as
         *                discovery and classification.
         */
        fun keyword(keyword: String) =
            addKeyword(keyword)
    }

    /**
     * Builder for adding source URIs to the chart metadata.
     *
     * This builder provides a DSL for specifying source repositories associated with the chart.
     * Source URIs represent links to the chart's source code, typically hosted in platforms like
     * GitHub, GitLab, or a custom source control repository.
     *
     * This builder is intended to be used internally within the [ChartSpecBuilder].
     *
     * @constructor Creates a new instance of [SourceListBuilder]. This should not be instantiated
     * externally, as it is designed for internal use only.
     */
    inner class SourceListBuilder internal constructor() {
        /**
         * Adds a source URI to the list of sources for the chart metadata.
         *
         * This method allows specifying a URI that points to the source repository of the chart,
         * such as a GitHub or GitLab repository.
         *
         * @param source A [URI] representing the source repository of the chart to be added.
         */
        fun source(source: URI) =
            addSource(source)
    }
}

/**
 * Entry point for the Chart DSL.
 *
 * Configures the metadata and dependencies of the Helm chart.
 *
 * @param name The name of the chart.
 * @param version The version of the chart.
 * @param prepare DSL block for configuring the chart metadata.
 */
fun chart(name: String, version: String, prepare: ChartSpecBuilder.() -> Unit) =
    ChartSpecBuilder(name, version).apply(prepare).build()