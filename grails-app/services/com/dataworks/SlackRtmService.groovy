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
		slackService.apiCall('rtm.start', token)
    }
	
	def startSession(String userName) {
		if (activeSockets[userName]) return
		
		def token = slackTokenService.getUserToken(userName)
		
		def resp = rtmStart(token)
		resp.type = 'connect'
		
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
			rtmInfo: resp
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
		
		if (!activeSocket || !activeSocket.handler.isOpen() || messageObj.type == 'initialInfo') {
			activeSocket = startNewSession(userName)
		}
		
		switch (messageObj.type) {
			case 'initialInfo':
				sendInitialInfo(userName)
				break
			case 'message':
				activeSocket.handler.sendMessage((messageObj as JSON).toString())
				break
		}
	}
	
	def sendInitialInfo(String userName) {
		def initialInfo = activeSockets[userName]?.rtmInfo
		
		if (initialInfo) {
			slackIncomingMessageService.processMessage(userName, initialInfo)
		}
	}
}
