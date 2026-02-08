package com.kgzn.gamecenter.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlin.math.min

interface CellItemSpan {
    val widthCells: Int
    val heightCells: Int
}

fun CellItemSpan(widthCells: Int, heightCells: Int) = object : CellItemSpan {
    override val widthCells: Int = widthCells
    override val heightCells: Int = heightCells
}

@Composable
fun <T : CellItemSpan> LazyHorizontalCell(
    maxLineSpan: Int = 1,
    cellSize: DpSize,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalSpacing: Dp = 0.dp,
    horizontalSpacing: Dp = 0.dp,
    spans: List<T>,
    content: @Composable BoxScope.(index: Int, T) -> Unit
) {

    val cellHeight = cellSize.height
    val cellWidth = cellSize.width

    LazyHorizontalGrid(
        contentPadding = contentPadding,
        rows = GridCells.Fixed(maxLineSpan),
        modifier = Modifier.height(cellHeight * maxLineSpan + verticalSpacing * (maxLineSpan - 1)),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
    ) {
        itemsIndexed(spans, span = { index, span ->
            GridItemSpan(min(span.heightCells, maxLineSpan))
        }) { index, span ->
            Box(modifier = Modifier.width(cellWidth * span.widthCells + horizontalSpacing * (span.widthCells - 1))) {
                content(index, span)
            }
        }
    }
}

@Composable
fun LazyHorizontalCell(
    maxLineSpan: Int = 1,
    cellSize: DpSize,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalSpacing: Dp = 0.dp,
    horizontalSpacing: Dp = 0.dp,
    count: Int,
    span: (index: Int) -> CellItemSpan,
    content: @Composable BoxScope.(index: Int) -> Unit
) {

    val cellHeight = cellSize.height
    val cellWidth = cellSize.width

    val spans = List(count) { span(it) }

    LazyHorizontalGrid(
        contentPadding = contentPadding,
        rows = GridCells.Fixed(maxLineSpan),
        modifier = Modifier.height(cellHeight * maxLineSpan + verticalSpacing * (maxLineSpan - 1)),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
    ) {
        itemsIndexed(spans, span = { index, span ->
            GridItemSpan(min(span.heightCells, maxLineSpan))
        }) { index, span ->
            Box(modifier = Modifier.width(cellWidth * span.widthCells + horizontalSpacing * (span.widthCells - 1))) {
                content(index)
            }
        }
    }
}

@Composable
fun <T> LazyHorizontalCell(
    maxLineSpan: Int = 1,
    cellSize: DpSize,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalSpacing: Dp = 0.dp,
    horizontalSpacing: Dp = 0.dp,
    items: List<T>,
    span: (index: T) -> CellItemSpan,
    content: @Composable BoxScope.(index: T) -> Unit
) {

    val cellHeight = cellSize.height
    val cellWidth = cellSize.width

    val spans = items.map { span(it) }

    LazyHorizontalGrid(
        contentPadding = contentPadding,
        rows = GridCells.Fixed(maxLineSpan),
        modifier = Modifier.height(cellHeight * maxLineSpan + verticalSpacing * (maxLineSpan - 1)),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing),
        horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
    ) {
        itemsIndexed(spans, span = { index, span ->
            GridItemSpan(min(span.heightCells, maxLineSpan))
        }) { index, span ->
            Box(modifier = Modifier.width(cellWidth * span.widthCells + horizontalSpacing * (span.widthCells - 1))) {
                content(items[index])
            }
        }
    }
}
