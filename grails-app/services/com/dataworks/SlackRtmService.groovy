package com.dataworks

import grails.converters.JSON

import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.client.standard.StandardWebSocketClient


class SlackRtmService {
	
	def slackService
	def slackIncomingMessageService
	
	def activeSockets = [:]

    def rtmStart(String token) {
		slackService.apiCall('rtm.start', token)
    }
	
	def startSession(String token) {
		if (activeSockets[token]) return
		
		def resp = rtmStart(token)
		resp.type = 'connect'
		
		def client = new StandardWebSocketClient()
		def handler = new SlackSocketHandler(token, slackIncomingMessageService)
		def manager = new WebSocketConnectionManager(client, handler, resp.url)
		
		activeSockets[token] = [
			client: client,
			handler: handler,
			manager: manager,
			rtmInfo: resp
		]
		
		manager.start()
		
		activeSockets[token]
	}
	
	def sendMessage(String token, def messageObj) {
		def activeSocket = activeSockets[token]
		
		if (!activeSocket) {
			activeSocket = startSession(token)
		}
		
		switch (messageObj.type) {
			case 'initialInfo':
				sendInitialInfo(token)
				break
			case 'message':
				activeSocket.handler.sendMessage((messageObj as JSON).toString())
				break
		}
	}
	
	def sendInitialInfo(String token) {
		def initialInfo = activeSockets[token]?.rtmInfo
		
		if (initialInfo) {
			slackIncomingMessageService.processMessage(token, initialInfo)
		}
	}
}
