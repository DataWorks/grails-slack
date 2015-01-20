package com.dataworks

import grails.transaction.Transactional

@Transactional
class SlackOutgoingMessageService {

	def slackRtmService
	
    def sendMessage(message) {
		slackRtmService.sendMessage('slack token here', message)
    }
}
