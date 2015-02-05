package com.dataworks

import grails.converters.JSON

class SlackChannelController {

	def slackChannelService
	
	def listChannelHistory() {
		def messages = slackChannelService.listFullChannelHistory(params.channelType, params.channel)
		render ([success: true, rows: messages, total: messages.size()] as JSON)
	}
}
