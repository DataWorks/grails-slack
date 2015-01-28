package com.dataworks

import grails.transaction.Transactional

@Transactional
class SlackOutgoingMessageService {

	def slackRtmService
	def slackTokenService
	
    def sendMessage(message) {
		slackRtmService.sendMessage(slackTokenService.getCurrentUserToken(), message)
    }
}
