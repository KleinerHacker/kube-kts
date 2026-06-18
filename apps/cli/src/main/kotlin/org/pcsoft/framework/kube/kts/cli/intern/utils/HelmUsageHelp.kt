/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.framework.kube.kts.cli.intern.utils

import picocli.CommandLine
import picocli.CommandLine.Help
import picocli.CommandLine.Help.*
import picocli.CommandLine.Model.*

/** Width (including indent) of the dedicated Helm column inserted into the option/parameter table. */
private const val HELM_COLUMN_WIDTH = 10

/** picocli's default width of the long-option column (as used by its deprecated `forDefaultColumns`). */
private const val DEFAULT_LONG_OPTIONS_COLUMN_WIDTH = 24

/**
 * `CommandLine.IHelpFactory` that renders the option and parameter tables with an additional column
 * indicating whether an option is forwarded to Helm.
 *
 * Install it on the root command via `CommandLine.setHelpFactory`; picocli applies it recursively to
 * all subcommands.
 */
class HelmHelpFactory : CommandLine.IHelpFactory {
    override fun create(commandSpec: CommandSpec, colorScheme: ColorScheme): Help =
        HelmColumnHelp(commandSpec, colorScheme)
}

/**
 * A `Help` variant whose option/parameter list has three logical columns: the option itself (as
 * usual), a Helm hint column, and the description (as usual).
 *
 * The Helm hint is derived from the [HELM_MARKER] prefix embedded in an option's description: when
 * present, the marker is moved out of the description into the dedicated column.
 */
private class HelmColumnHelp(commandSpec: CommandSpec, colorScheme: ColorScheme) :
    Help(commandSpec, colorScheme) {

    override fun createDefaultLayout(): Layout {
        val width = commandSpec().usageMessage().width()

        // Start from the default 5 columns and splice in the Helm column right before the description.
        val base = TextTable.forDefaultColumns(colorScheme(), DEFAULT_LONG_OPTIONS_COLUMN_WIDTH, width).columns().toMutableList()
        val description = base.removeAt(base.size - 1)
        base.add(Column(HELM_COLUMN_WIDTH, 1, Column.Overflow.SPAN))
        base.add(Column(maxOf(description.width - HELM_COLUMN_WIDTH, 10), description.indent, description.overflow))

        val table = TextTable.forColumns(colorScheme(), *base.toTypedArray())
        return Layout(
            colorScheme(),
            table,
            HelmColumnOptionRenderer(createDefaultOptionRenderer()),
            HelmColumnParameterRenderer(createDefaultParameterRenderer()),
        )
    }
}

/** Markers that are moved out of the description into the dedicated hint column. */
private val HINT_MARKERS = listOf(HELM_MARKER, EXPERIMENTAL_MARKER, DANGER_MARKER)

/** Decorates the default option renderer, moving a leading hint marker into a dedicated column. */
private class HelmColumnOptionRenderer(private val delegate: IOptionRenderer) : IOptionRenderer {
    override fun render(option: OptionSpec, paramLabelRenderer: IParamLabelRenderer, scheme: ColorScheme): Array<Array<Ansi.Text>> {
        val marker = option.description().leadingMarker()
        val source = if (marker != null) option.toBuilder().description(*option.description().withoutMarker(marker)).build() else option
        return insertHintColumn(delegate.render(source, paramLabelRenderer, scheme), marker, scheme)
    }
}

/** Decorates the default parameter renderer so positional parameters keep the table column count. */
private class HelmColumnParameterRenderer(private val delegate: IParameterRenderer) : IParameterRenderer {
    override fun render(param: PositionalParamSpec, paramLabelRenderer: IParamLabelRenderer, scheme: ColorScheme): Array<Array<Ansi.Text>> {
        val marker = param.description().leadingMarker()
        val source = if (marker != null) param.toBuilder().description(*param.description().withoutMarker(marker)).build() else param
        return insertHintColumn(delegate.render(source, paramLabelRenderer, scheme), marker, scheme)
    }
}

/** The hint marker the first description line starts with, or `null` if none. */
private fun Array<String>.leadingMarker(): String? =
    firstOrNull()?.let { line -> HINT_MARKERS.firstOrNull { line.startsWith(it) } }

/** Returns the description with the given leading [marker] removed from the first line. */
private fun Array<String>.withoutMarker(marker: String): Array<String> = mapIndexed { index, line ->
    if (index == 0) line.removePrefix(marker).trimStart() else line
}.toTypedArray()

/**
 * Inserts the hint cell (the extracted [marker], if any) into every rendered row right before the
 * description column, so the resulting rows match the six-column table built in
 * [HelmColumnHelp.createDefaultLayout].
 */
private fun insertHintColumn(rows: Array<Array<Ansi.Text>>, marker: String?, scheme: ColorScheme): Array<Array<Ansi.Text>> {
    val empty = scheme.ansi().text("")
    val hint = marker?.let { scheme.ansi().text(it) } ?: empty
    return Array(rows.size) { rowIndex ->
        val row = rows[rowIndex]
        val descriptionIndex = row.size - 1
        val cell = if (rowIndex == 0) hint else empty
        Array(row.size + 1) { columnIndex ->
            when {
                columnIndex < descriptionIndex -> row[columnIndex]
                columnIndex == descriptionIndex -> cell
                else -> row[descriptionIndex]
            }
        }
    }
}
