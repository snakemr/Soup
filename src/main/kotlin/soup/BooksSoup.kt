package soup

import androidx.compose.runtime.mutableStateListOf
import org.jsoup.nodes.Document

class BooksSoup : Soup() {
    override val url = "https://books.toscrape.com/catalogue/"
    override val limits = 1..50
    override val pageUrl: String get() = url + "page-$page.html"

    private fun books(doc: Document) = doc.select("article[class=product_pod]").mapIndexed { index, element ->
        val a = element.select("h3").select("a")
        Book(
            id = page*20 + index - 19,
            title = a.attr("title"),
            rating = element.getElementsByClass("star-rating").attr("class").substringAfter(' ').toNumber(),
            price = element.select("p[class=price_color]").text().drop(1).toFloat(),
            url = a.attr("href")
        )
    }

    val books = mutableStateListOf<Book>()

    override fun onPageLoaded(page: Document) {
        books += books(page)
    }

    override suspend fun getAll() {
        super.getAll()
        for (index in books.indices) {
            BookSoup(url + books[index].url).run {
                getOne()
                books[index] = books[index].copy(genre = genre, stock = stock)
            }
            progress = (index+1).toFloat() / books.size
        }
    }

    private fun String.toNumber() = when (this) {
        "One" -> 1
        "Two" -> 2
        "Three" -> 3
        "Four" -> 4
        "Five" -> 5
        else -> 0
    }

    data class Book(
        val id: Int,
        val title: String,
        val rating: Int,
        val price: Float,
        val url: String,
        val genre: String? = null,
        val stock: Int? = null
    )

    private class BookSoup(bookUrl: String) : Soup() {
        override val url = bookUrl
        val genre: String? get() = soup.select("ul[class=breadcrumb]").select("li")[2].text()
        val stock: Int? get() = soup.select("p[class=instock availability]").text()
            .substringAfter('(').substringBefore(' ').toInt()
    }
}