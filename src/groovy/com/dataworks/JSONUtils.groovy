package com.dataworks

import com.fasterxml.jackson.databind.ObjectMapper

class JSONUtils {

	static def getMap(String json) {
		new ObjectMapper().readValue(json, HashMap.class)
	}
}
