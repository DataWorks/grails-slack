package com.dataworks

import java.security.Principal

import org.springframework.messaging.handler.annotation.MessageMapping

class SlackRtmController {
	
	def slackOutgoingMessageService

    def index() {}

    @MessageMapping("/slack")
    protected String message(SlackMessage message, Principal principal) {
		slackOutgoingMessageService.sendMessage(principal?.principal?.username, message)
    }

}
