package com.dataworks

import grails.converters.JSON

import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.client.standard.StandardWebSocketClient


class SlackRtmService {
	
	def slackService
	def slackTokenService
	def slackIncomingMessageService
	
	def activeSockets = [:]

    def rtmStart(String token) {
		slackService.apiCall('rtm.connect', token, ['mpim_aware': true])
    }
	
	def startSession(String userName) {
		if (activeSockets[userName]) return
		
		def token = slackTokenService.getUserToken(userName)
		
		def resp = rtmStart(token)
		
		if (!resp.ok) {
			throw new IllegalStateException("Could not connect to Slack: ${resp.error}")
		}
		
		def client = new StandardWebSocketClient()
		def handler = new SlackSocketHandler(userName, slackIncomingMessageService)
		def manager = new WebSocketConnectionManager(client, handler, resp.url)
		
		activeSockets[userName] = [
			client: client,
			handler: handler,
			manager: manager,
			self: resp.self
		]
		
		manager.start()
		
		activeSockets[userName]
	}
	
	def startNewSession(String userName)  {
		activeSockets[userName] = null
		startSession(userName)
	}
	
	def sendMessage(String userName, def messageObj) {
		def activeSocket = activeSockets[userName]
		
		if (!activeSocket || !activeSocket.handler.isOpen()) {
			activeSocket = startNewSession(userName)
		}
		
		switch (messageObj.type) {
			case 'initialInfo':
				sendInitialInfo(activeSocket, userName)
				break
			case 'message':
			case 'ping':
				activeSocket.handler.sendMessage((messageObj as JSON).toString())
				break
		}
	}
	
	def sendInitialInfo(def activeSocket, String userName) {
		def initialInfo = getInitialInfo(slackTokenService.getUserToken(userName))
		
		slackIncomingMessageService.processMessage(userName, initialInfo + [type: 'connect', self: activeSocket.self])
	}
	
	def getInitialInfo(String token) {
		[
			users: getUserInfo(token),
			channels: getChannelInfo(token), 
			groups: getGroupInfo(token),
			ims: getImInfo(token),
			bots: []
		]
	}
	
	def getUserInfo(String token) {
		def fullUserInfo = []
		def allUsersResp = slackService.apiCall('users.list', token, ['presence': true])
		
		if (!allUsersResp.ok) {
			throw new IllegalStateException("Could not retrieve users from Slack: ${allUsersResp.error}")
		}
		
		allUsersResp.members
	}
	
	def getChannelInfo(String token) {
		def fullChannelInfo = []
		def allChannelsResp = slackService.apiCall('channels.list', token, ['exclude_archived': true])
		
		if (!allChannelsResp.ok) {
			throw new IllegalStateException("Could not retrieve channels from Slack: ${allChannelsResp.error}")
		}
		
		allChannelsResp.channels.each { channel ->
			if (channel.is_member) {
				fullChannelInfo << slackService.apiCall('channels.info', token, ['channel': channel.id]).channel
			}
		}
		
		fullChannelInfo
	}
	
	def getGroupInfo(String token) {
		def fullGroupInfo = []
		def allGroupsResp = slackService.apiCall('groups.list', token, ['exclude_archived': true])
		
		if (!allGroupsResp.ok) {
			throw new IllegalStateException("Could not retrieve groups from Slack: ${allGroupsResp.error}")
		}
		
		allGroupsResp.groups.each { group ->
			fullGroupInfo << slackService.apiCall('groups.info', token, ['channel': group.id]).group
		}
		
		fullGroupInfo
	}
	
	def getImInfo(String token) {
		def fullImInfo = []
		def allImResp = slackService.apiCall('im.list', token)
		
		if (!allImResp.ok) {
			throw new IllegalStateException("Could not retrieve ims from Slack: ${allImResp.error}")
		}
		
		allImResp.ims.each { im ->
			fullImInfo << slackService.apiCall('im.info', token, ['channel': im.id]).im
		}
		
		fullImInfo
	}
}
