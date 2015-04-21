package com.dataworks

import grails.converters.JSON

class SlackChannelController {

	def slackChannelService
	
	def listChannelHistory() {
		def limit = params.limit ?: 50
		def result = slackChannelService.listChannelHistory(params.channelType, params.channel, params.latest, limit as int)
		render ([success: true, rows: result.messages, total: result.messages.size(), hasMore: result.hasMore] as JSON)
	}
	
	def markChannel() {
		def messages = slackChannelService.markChannel(params.channelType, params.channel, params.timestamp)
		render ([success: true] as JSON)
	}
}
