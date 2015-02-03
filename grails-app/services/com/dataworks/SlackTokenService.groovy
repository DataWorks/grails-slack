package com.dataworks

import java.nio.file.FileSystems
import java.nio.file.Files


class SlackTokenService {

	static String TOKEN_FOLDER = "tokens"
	
	def springSecurityService
	
	private def getFile(userName) {
		if (!userName) {
			userName = springSecurityService.getCurrentUser()?.username
		}

		if (!userName) {
			throw new IllegalStateException('No user found!')
		}
		
		def path = FileSystems.getDefault().getPath(TOKEN_FOLDER)
		
		if (!Files.exists(path)) {
			Files.createDirectory(path)
		}
		
		path = FileSystems.getDefault().getPath("${TOKEN_FOLDER}/${userName}")
		
		if (!Files.exists(path)) {
			Files.createFile(path)
		}
		
		path.toFile()
	}
	
    def updateUserToken(String token) {
		getFile().text = token
    }
	
	def getUserToken(userName) {
		getFile(userName).text
	}
	
	def getCurrentUserToken() {
		getFile().text
	}
}
