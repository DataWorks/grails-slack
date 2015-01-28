package com.dataworks

import org.apache.http.client.fluent.Request

import com.fasterxml.jackson.databind.ObjectMapper


class SlackService {

	def slackApiUrl = 'https://slack.com/api'
	
    def apiCall(String methodName, String token, Map extraParams = [:]) {
		def params = (extraParams + [token: token]).collect { key, value -> value ? "${key}=${value}" : ''}.join('&')
		def json = Request.Get("${slackApiUrl}/${methodName}?${params}").execute().returnContent().asString()
		JSONUtils.getMap(json)
    }
}
