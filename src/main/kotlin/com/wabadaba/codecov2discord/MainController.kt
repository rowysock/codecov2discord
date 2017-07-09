package com.wabadaba.codecov2discord

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import java.math.RoundingMode

@Suppress("unused")
@RestController
class MainController {

    @Autowired
    lateinit var mapper: ObjectMapper

    val logger = LoggerFactory.getLogger(this::class.java)

    private val iconUrl = "https://chocolatey.org/content/packageimages/codecov.1.0.1.png"

    @RequestMapping("/discord-webhook/{id}/{token}")
    fun test(
            @PathVariable id: String,
            @PathVariable token: String,
            @RequestBody bodyString: String
    ) {
        logger.info(bodyString)
        val body = mapper.readValue(bodyString, CodecovWebhook::class.java)
        val template = RestTemplate(HttpComponentsClientHttpRequestFactory())
        val author = DiscordWebhook.Embed.Author(body.head.author.username)
        val commitIdShort = body.head.commitid.substring(0, 6)
        val description = "[`$commitIdShort`](${body.head.service_url}) ${body.head.message}"

        val embed = DiscordWebhook.Embed(
                "[${body.repo.name}:${body.head.branch}]",
                description,
                body.repo.url,
                author,
                color = 0xF70557)
        val webhook = DiscordWebhook("""
                Coverage: **${body.head.totals.c.setScale(2, RoundingMode.HALF_UP)}%**
                [Change: **${body.compare.coverage.setScale(2, RoundingMode.HALF_UP)}%**](${body.compare.url})
                """.trimIndent(),
                "Codecov",
                iconUrl,
                listOf(embed))
        template.postForEntity("https://discordapp.com/api/webhooks/$id/$token", webhook, String::class.java)
    }
}

data class DiscordWebhook(
        val content: String,
        val username: String,
        val avatar_url: String,
        val embeds: List<Embed>) {

    data class Embed(
            val title: String,
            val description: String,
            val url: String,
            val author: Author,
            val fields: List<Field>? = null,
            val provider: Provider? = null,
            val thumbnail: Thumbnail? = null,
            val color: Int) {

        data class Author(
                val name: String
        )

        data class Field(
                val name: String,
                val value: String
        )

        data class Provider(
                val name: String
        )

        data class Thumbnail(
                val url: String,
                val height: Int,
                val width: Int
        )
    }
}


data class CodecovWebhook(
        val repo: Repo,
        val head: Head,
        val compare: Compare) {

    data class Head(
            val url: String,
            val message: String,
            val author: Author,
            val totals: Totals,
            val branch: String,
            val commitid: String,
            val service_url: String) {

        data class Totals(
                val c: BigDecimal
        )

        data class Author(
                val username: String,
                val name: String
        )

    }

    data class Repo(
            val name: String,
            val url: String
    )

    data class Compare(
            val coverage: BigDecimal,
            val url: String
    )

}
