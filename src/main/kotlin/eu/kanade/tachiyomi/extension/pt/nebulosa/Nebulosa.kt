package eu.kanade.tachiyomi.extension.pt.nebulosa

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class Nebulosa : ParsedHttpSource() {
    override val name = "Nebulosa"
    override val baseUrl = "https://nebulosascan.com"
    override val lang = "pt-br"
    override val supportsLatest = true

    override fun popularMangaRequest(page: Int): Request = GET("$baseUrl/manga/page/$page/", headers)
    override fun popularMangaSelector() = "div.manga-card, div.bs" 
    override fun popularMangaFromElement(element: Element) = SManga.create().apply {
        title = element.select("div.tt, h3").text()
        thumbnail_url = element.select("img").attr("abs:src")
        url = element.select("a").attr("href")
    }
    override fun popularMangaNextPageSelector() = "a.next"

    override fun latestUpdatesRequest(page: Int) = popularMangaRequest(page)
    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList) = GET("$baseUrl/?s=$query", headers)
    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element) = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()

    override fun mangaDetailsParse(document: Document) = SManga.create().apply {
        description = document.select("div.entry-content p").text()
    }

    override fun chapterListSelector() = "li.wp-manga-chapter"
    override fun chapterFromElement(element: Element) = SChapter.create().apply {
        val anchor = element.select("a")
        name = element.text()
        url = anchor.attr("href")
    }
    override fun pageListParse(document: Document): List<Page> = 
        document.select("div#readerarea img").mapIndexed { i, img -> 
            Page(i, "", img.attr("abs:src")) 
        }
    override fun imageUrlParse(document: Document) = ""
}
