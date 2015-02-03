package com.dataworks

import grails.transaction.Transactional

@Transactional
class SlackIncomingMessageService {
	def brokerMessagingTemplate
	
	def processMessage(userName, message) {
		brokerMessagingTemplate.convertAndSendToUser(userName, "/topic/slack", message)
	}
	
	def processConnectionEstablished(userName) {
		brokerMessagingTemplate.convertAndSendToUser(userName, "/topic/slack", [text: 'Connection established to Slack'])
	}
}
