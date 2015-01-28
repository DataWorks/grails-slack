package com.dataworks


class SlackTokenService {

	static String TOKEN_FILE = "tokens.dat"
	
    def updateUserToken(String token) {
		new File(TOKEN_FILE).text = token
    }
	
	def getCurrentUserToken() {
		new File(TOKEN_FILE).text
	}
}
