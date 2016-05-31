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
		slackService.apiCall('rtm.start', token, ['mpim_aware': true])
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
			manager: manager
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
				sendInitialInfo(userName)
				break
			case 'message':
			case 'ping':
				activeSocket.handler.sendMessage((messageObj as JSON).toString())
				break
		}
	}
	
	def sendInitialInfo(String userName) {
		def initialInfo = rtmStart(slackTokenService.getUserToken(userName))
		
		if (initialInfo) {
			slackIncomingMessageService.processMessage(userName, initialInfo + [type: 'connect'])
		}
	}
}
