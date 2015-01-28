package com.dataworks

class SlackChannelController {

	def slackChannelService
	
	def listChannelHistory() {
		slackChannelService.listFullChannelHistory(params.channel)
	}
}
