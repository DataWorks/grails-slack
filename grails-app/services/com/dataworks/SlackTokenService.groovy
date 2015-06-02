package com.dataworks

import java.nio.file.FileSystems
import java.nio.file.Files


class SlackTokenService {

	static String TOKEN_FOLDER = "tokens"
	static String TOKEN_TEAM_DELIM = "::"
	
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
	
    def updateUserToken(String token, String tokenTeam = null) {
		def allTokenInfo = getAllUserTokens(null)
		def tokenInfo = allTokenInfo.find { it.tokenTeam == tokenTeam }
		
		if (tokenInfo) {
			tokenInfo.token = token
		} else {
			allTokenInfo << [token: token, tokenTeam: tokenTeam]
		}
		
		def newFileData = allTokenInfo.collect {
			it.token + (it.tokenTeam ? "${TOKEN_TEAM_DELIM}${it.tokenTeam}" : '')
		}.join(System.lineSeparator())
		
		getFile().text = newFileData
    }
	
	def getAllUserTokens(userName) {
		def lines = getFile(userName).readLines()
		def tokenInfo = lines.collect { line ->
			def parts = line.split(TOKEN_TEAM_DELIM)
			def tokenTeam = parts.size() > 1 ? parts[1] : null
			[token: parts[0], tokenTeam: tokenTeam]
		}
		tokenInfo
	}
	
	def getUserToken(userName, tokenTeam = null) {
		getAllUserTokens(userName).find {
			it.tokenTeam == tokenTeam
		}?.token
	}
	
	def getCurrentUserTeams() {
		getAllUserTokens().collect { it.tokenTeam }
	}
	
	def getCurrentUserToken() {
		getUserToken(null)
	}
}
