package com.dataworks

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(SlackService)
class SlackServiceSpec extends Specification {

	def slackService = new SlackService()
	
    void "test channel list"() {
		when:
		def resp = slackService.apiCall('channels.list', '<token>')
		
		then:
		resp.ok
		resp.channels.name.size() > 0
    }
}
