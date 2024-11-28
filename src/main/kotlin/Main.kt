import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dataframe.Frame
import soup.BooksSoup
import java.text.DecimalFormat

@Composable
fun App() {
    var soup = remember { BooksSoup() }
    val money = remember { DecimalFormat(",##0.00 ₤") }
    val listState = rememberLazyListState()
    var graph by remember { mutableStateOf(Graph.None) }

    MaterialTheme {
        Box {
            Column {
                Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    val any by remember { derivedStateOf { soup.books.any { it.genre != null } } }
                    Text("#", Modifier.padding(horizontal = 8.dp).width(40.dp), textAlign = TextAlign.End)
                    Text("Book Title", Modifier.weight(1f))
                    if (any) {
                        Text("Genre", Modifier.width(140.dp).padding(start = 8.dp))
                        Text("In", Modifier.width(40.dp).padding(start = 8.dp))
                    }
                    Text("Book Rating",
                        Modifier.padding(horizontal = 8.dp).width(100.dp).clickable { graph = Graph.Rating },
                        color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                    Text("Cost",
                        Modifier.padding(end = 16.dp).width(60.dp).clickable { graph = Graph.Cost },
                        color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
                HorizontalDivider()

                Row(Modifier.weight(1f)) {
                    LazyColumn(Modifier.weight(1f), listState) {

                        items(soup.books) { book ->
                            Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    book.id.toString(),
                                    Modifier.padding(horizontal = 8.dp).width(40.dp), textAlign = TextAlign.End
                                )

                                Text(book.title, Modifier.weight(1f))

                                book.genre?.let {
                                    Text(it, Modifier.width(140.dp).padding(start = 8.dp))
                                }
                                book.stock?.let {
                                    Text(it.toString(), Modifier.width(40.dp).padding(start = 8.dp))
                                }

                                FiveStars(book.rating, Modifier.padding(horizontal = 8.dp).width(100.dp))

                                Text(money.format(book.price), Modifier.width(60.dp))
                            }
                            HorizontalDivider()
                        }
                    }
                    VerticalScrollbar(rememberScrollbarAdapter(listState), Modifier.padding(4.dp).fillMaxHeight())
                }
                if (soup.progress < 1f) LinearProgressIndicator(
                    { soup.progress },
                    Modifier.padding(8.dp).fillMaxWidth()
                )
            }

            if (graph != Graph.None) Box(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize().alpha(.35f).background(Color.LightGray).clickable { graph = Graph.None })
                ElevatedCard(Modifier.fillMaxSize().padding(32.dp)) {
                    Frame(soup.books, graph)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        soup.getAll()
    }
}

enum class Graph { None, Cost, Rating }

@Composable
fun FiveStars(stars: Int, modifier: Modifier = Modifier) = Row(modifier) {
    for (i in 1..5) {
        Icon(Icons.Default.Star, contentDescription = "", Modifier.size(20.dp),
            tint = if (i <= stars) Color.Blue else Color.Gray
        )
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Books to Scrape Demo") {
        App()
    }
}
