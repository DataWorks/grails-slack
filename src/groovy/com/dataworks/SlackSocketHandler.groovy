package com.dataworks

import org.springframework.aop.aspectj.RuntimeTestWalker.ThisInstanceOfResidueTestVisitor;
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.AbstractWebSocketHandler

class SlackSocketHandler extends AbstractWebSocketHandler {
	private def session
	private def slackIncomingMessageHandler
	private String userName
	
	public SlackSocketHandler(String userNameIn, def slackIncomingMessageHandlerIn) {
		slackIncomingMessageHandler = slackIncomingMessageHandlerIn
		userName = userNameIn
	}
	
	def sendMessage(String message) {
		session.sendMessage(new TextMessage(message))
	}
	
	def isOpen() {
		session.isOpen()
	}

	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		slackIncomingMessageHandler.processMessage(userName, JSONUtils.getMap(message.payload))
	}

	@Override
	public void afterConnectionEstablished(WebSocketSession sessionIn) {
		session = sessionIn
		slackIncomingMessageHandler.processConnectionEstablished(userName)
	}
}
