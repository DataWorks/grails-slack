package com.dataworks

import grails.transaction.Transactional

@Transactional
class SlackIncomingMessageService {
	def brokerMessagingTemplate
	
	def processMessage(userToken, message) {
		brokerMessagingTemplate.convertAndSend("/topic/slack", message)
	}
	
	def processConnectionEstablished(userToken) {
		brokerMessagingTemplate.convertAndSend("/topic/slack", [text: 'Connection established to Slack'])
	}
}
