package soup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

open class Soup {
    protected open val url: String = "https://example.com/"
    protected open fun tune(connection: Connection) {}

    protected open fun get(url: String, tune: (Connection)->Unit): Document = Jsoup.connect(url).apply(tune).get()

    protected lateinit var soup: Document
        private set

    open suspend fun getOne() = withContext(Dispatchers.IO) {
        soup = get(url, ::tune)
    }

    open val text: String get() = soup.text()
    open val content: String get() = soup.toString()

    protected open var page: Int = 1
    protected open val limits: IntRange = 1..Int.MAX_VALUE

    protected open fun next(): Boolean {
        if (page >= limits.last) return false
        page++
        return true
    }

    protected open fun previous(): Boolean {
        if (page <= limits.first) return false
        page--
        return true
    }
    protected open val pageUrl: String get() = "$url$page"

    protected open val soups: MutableMap<Int, Document> = mutableMapOf()

    protected open fun getPage(): Document {
        if (page !in soups)
            soups[page] = get(pageUrl, ::tune)
        return soups[page]!!
    }

    open val pageText: String get() = getPage().text()
    open val pageContent: String get() = getPage().toString()

    var progress: Float by mutableStateOf(0f)
        protected set

    protected open fun onPageLoaded(page: Document) {}

    open suspend fun getAll() {
        page = limits.first
        while (true) {
            withContext(Dispatchers.IO) {
                onPageLoaded(getPage())
            }
            progress = page.toFloat() / limits.last
            if (!next()) break
        }
    }
}