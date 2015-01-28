package com.dataworks



class SlackChannelService {

	def slackService
	
    def listChannels(String token) {
		slackService.apiCall('channels.list', token)
    }
	
	def listFullChannelHistory(String token, String channel) {
		def messages = []
		def hasMore = true
		def latest = null
		
		while (hasMore) {
			def resp = slackService.apiCall('channels.list', token, [channel: channel, count: 1000, latest: latest])
			
			if (resp.ok) {
				messages.addAll(resp.messages)
				hasMore = resp.has_more
				latest = resp.messages.last().ts
			} else {
				throw new Exception("Failed to retrieve channel history: ${resp.error}")
			}
		}
		
		messages
	}
}
