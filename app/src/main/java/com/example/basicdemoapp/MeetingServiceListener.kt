package com.example.basicdemoapp

import us.zoom.sdk.InMeetingChatMessage
import us.zoom.sdk.InMeetingServiceListener

interface MeetingServiceListener : InMeetingServiceListener {
    override fun onChatMessageReceived(p0: InMeetingChatMessage?) {
        TODO("Not yet implemented")

    }
}