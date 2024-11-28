package dataframe

import Graph
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toPainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.kotlinx.dataframe.api.toDataFrame
import org.jetbrains.kotlinx.dataframe.size
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.bars
import org.jetbrains.kotlinx.statistics.binning.BinsOption
import org.jetbrains.kotlinx.statistics.kandy.stattransform.statBin
import soup.BooksSoup
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB


@Composable
fun Frame(books: List<BooksSoup.Book>, graph: Graph) = BoxWithConstraints {
    val frame = books.toDataFrame()
    val empty = remember { BufferedImage(maxWidth.value.toInt(), maxHeight.value.toInt(), TYPE_INT_ARGB) }
    val x = remember(graph) {
        when(graph) {
            Graph.Cost -> BooksSoup.Book::price.name
            Graph.Rating -> BooksSoup.Book::rating.name
            else -> ""
        }
    }
    val bins = remember(graph) {
        BinsOption.byNumber(if (graph == Graph.Rating) 5 else 20)
    }
    val plot by produceState(empty, frame.size(), maxWidth, maxHeight) {
        withContext(Dispatchers.Default) {
            value = frame.plot {
                statBin(x, binsOption = bins) {
                    bars {
                        x(Stat.x)
                        y(Stat.count)
                    }
                }
                layout {
                    title = (if (graph == Graph.Rating) "Rating" else "Cost") + " of books in Books to Scrape Demo"
                    xAxisLabel = if (graph == Graph.Rating) "Book Rating, ★★★★★" else "Book Cost, ₤"
                    caption = "Just an example"
                    size = maxWidth.value.toInt() to maxHeight.value.toInt()
                }
            }.toBufferedImage()
        }
    }

    Image(plot.toPainter(), null, Modifier.fillMaxSize())
}