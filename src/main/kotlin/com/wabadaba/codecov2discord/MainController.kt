package com.wabadaba.codecov2discord

import org.slf4j.LoggerFactory
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.awt.Color


@RestController
class MainController {

    val logger = LoggerFactory.getLogger(this::class.java)

    private val iconUrl = "https://chocolatey.org/content/packageimages/codecov.1.0.1.png"

    @RequestMapping("/discord-webhook/{id}/{token}")
    fun test(
            @PathVariable id: String,
            @PathVariable token: String,
            @RequestBody body: String
    ) {
        logger.info(body)
        val template = RestTemplate(HttpComponentsClientHttpRequestFactory())
        val mask: Int = 0xFFFFFF
        val color = Color.CYAN.rgb and mask
        val footer = WebhookEmbedFooter(
                "Footer",
                iconUrl
        )
        val embed = WebhookEmbed(
                "Embed",
                "Description",
                iconUrl,
                footer,
                color)
        val payload = WebhookPayload(
                "Content",
                "Username",
                iconUrl,
                listOf(embed))
        template.postForEntity("https://discordapp.com/api/webhooks/$id/$token", payload, String::class.java)
    }
}

data class WebhookPayload(
        val content: String,
        val username: String,
        val avatar_url: String,
        val embeds: List<WebhookEmbed>)

data class WebhookEmbed(
        val title: String,
        val description: String,
        val url: String,
        val footer: WebhookEmbedFooter,
        val color: Int)

data class WebhookEmbedFooter(
        val text: String,
        val icon_url: String)