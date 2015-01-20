package com.dataworks

import grails.converters.JSON

import org.springframework.messaging.handler.annotation.MessageMapping

class SlackIncomingMessageController {
	
	def slackOutgoingMessageService

    def index() {}

    @MessageMapping("/slack")
    protected String message(SlackMessage message) {
		slackOutgoingMessageService.sendMessage(message)
    }

}
