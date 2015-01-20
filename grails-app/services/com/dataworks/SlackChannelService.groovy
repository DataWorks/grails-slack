package com.dataworks



class SlackChannelService {

	def slackService
	
    def listChannels(String token) {
		slackService.apiCall('channels.list', token)
    }
}
