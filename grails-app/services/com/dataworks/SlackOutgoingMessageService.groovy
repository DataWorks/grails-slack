package com.dataworks

import grails.transaction.Transactional

@Transactional
class SlackOutgoingMessageService {

	def slackRtmService
	
    def sendMessage(userName, message) {
		slackRtmService.sendMessage(userName, message)
    }
}
