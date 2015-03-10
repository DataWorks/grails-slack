package com.dataworks



class SlackChannelService {

	def slackService
	def slackTokenService
	
	static def HISTORY_METHOD_NAME_MAP = [
		channel: 'channels',
		im: 'im',
		group: 'groups'
	]
	
	private def getMethodName(channelType) {
		HISTORY_METHOD_NAME_MAP[channelType] ?: 'channels'
	}
	
    def listChannels() {
		slackService.apiCall('channels.list', slackTokenService.getCurrentUserToken())
    }
	
	def listFullChannelHistory(String channelType, String channel) {
		def messages = []
		def hasMore = true
		def latest = null
		
		while (hasMore) {
			def resp = slackService.apiCall("${getMethodName(channelType)}.history", slackTokenService.getCurrentUserToken(), 
				[channel: channel, count: 1000, latest: latest])
			
			if (resp.ok) {
				messages.addAll(resp.messages)
				hasMore = resp.has_more
				
				if (resp.messages) {
					latest = resp.messages.last().ts
				}
			} else {
				throw new Exception("Failed to retrieve channel history: ${resp.error}")
			}
		}
		
		messages.reverse()
	}
	
	def markChannel(String channelType, String channel, String timestamp) {
		slackService.apiCall("${getMethodName(channelType)}.mark", slackTokenService.getCurrentUserToken(), 
			[channel: channel, ts: timestamp])
	}
}
