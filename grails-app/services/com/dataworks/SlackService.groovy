package com.dataworks

import org.apache.http.client.fluent.Request

import com.fasterxml.jackson.databind.ObjectMapper


class SlackService {

	def slackApiUrl = 'https://slack.com/api'
	
    def apiCall(String methodName, String token) {
		def json = Request.Get("${slackApiUrl}/${methodName}?token=${token}").execute().returnContent().asString()
		JSONUtils.getMap(json)
    }
}
