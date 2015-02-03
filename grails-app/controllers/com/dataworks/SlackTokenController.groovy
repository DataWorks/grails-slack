package com.dataworks

import grails.converters.JSON

class SlackTokenController {

	def slackTokenService
	
	def updateCurrentToken() {
		slackTokenService.updateUserToken(request.JSON.token)
		render ([success: true] as JSON)
	}
	
	def currentTokenStatus() {
		def hasToken = slackTokenService.getCurrentUserToken() ? true : false
		response.status = hasToken ? 200 : 404
		render ([success: hasToken] as JSON)
	}
}
