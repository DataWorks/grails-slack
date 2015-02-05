package com.dataworks



class SlackChannelService {

	def slackService
	def slackTokenService
	
	static def HISTORY_METHOD_NAME_MAP = [
		channel: 'channels',
		im: 'im',
		group: 'groups'
	]
	
    def listChannels() {
		slackService.apiCall('channels.list', slackTokenService.getCurrentUserToken())
    }
	
	def listFullChannelHistory(String channelType, String channel) {
		def messages = []
		def hasMore = true
		def latest = null
		
		def methodName = HISTORY_METHOD_NAME_MAP[channelType] ?: 'channels'
		
		while (hasMore) {
			def resp = slackService.apiCall("${methodName}.history", slackTokenService.getCurrentUserToken(), 
				[channel: channel, count: 1000, latest: latest])
			
			if (resp.ok) {
				messages.addAll(resp.messages)
				hasMore = resp.has_more
				latest = resp.messages.last().ts
			} else {
				throw new Exception("Failed to retrieve channel history: ${resp.error}")
			}
		}
		
		messages.reverse()
	}
}
