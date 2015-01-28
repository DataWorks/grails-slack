package com.dataworks

class SlackTokenController {

	def slackTokenService
	
	def updateUserToken() {
		slackTokenService.updateUserToken(params.token)
	}
}
