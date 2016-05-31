package com.dataworks

import grails.converters.JSON
import grails.test.mixin.web.ControllerUnitTestMixin

import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.WebSocketConnectionManager
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.handler.AbstractWebSocketHandler

import spock.lang.Shared
import spock.lang.Specification

@TestFor(SlackRtmService)
@TestMixin(ControllerUnitTestMixin)
class SlackRtmServiceSpec extends Specification {

	def rtmService = new SlackRtmService()
	def slackService = new SlackService()

	def setup() {
		rtmService.slackService = slackService
	}

	def "test rtm hello"() {

		setup:
		def resp = rtmService.rtmStart('<token>')

		WebSocketClient client = new StandardWebSocketClient()
		TopoWebSocketHandler handler = new TopoWebSocketHandler()
		WebSocketConnectionManager manager = new WebSocketConnectionManager(client, handler, resp.url)
		manager.start()

		when:
		Thread.start {
			while (handler.response != 'done'){
				println 'waiting...'
				Thread.sleep 500
			}
		}.join()

		then:
		handler.response
	}

	public class TopoWebSocketHandler extends AbstractWebSocketHandler {
		private WebSocketSession session
		def response

		@Override
		public void handleTextMessage(WebSocketSession session,
				TextMessage message) {
			def json = JSON.parse(message.payload)
			
			if (json.text == 'Hello from spock test') {
				response = 'done'
			}
		}

		@Override
		public void afterConnectionEstablished(WebSocketSession session) {
			def message = [
				id: 650,
				type: 'message',
				channel: 'C03BXNG8N',
				text: 'Hello from spock test'
			]
			
			session.sendMessage(new TextMessage((message as JSON).toString()))
		}
	}
}
