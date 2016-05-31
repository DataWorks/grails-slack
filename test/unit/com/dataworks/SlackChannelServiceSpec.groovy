package com.dataworks

import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(SlackChannelService)
class SlackChannelServiceSpec extends Specification {

	def channelService = new SlackChannelService()
	def slackService = new SlackService()
	
    def setup() {
		channelService.slackService = slackService
    }

    void "test channel list"() {
		when:
		def resp = channelService.listChannels('<token>')
		
		then:
		println resp
		resp.ok
		resp.channels.name.containsAll(['general', 'sports', 'training'])
    }
}
