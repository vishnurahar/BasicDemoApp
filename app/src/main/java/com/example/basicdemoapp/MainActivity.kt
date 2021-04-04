package com.example.basicdemoapp

import android.content.Context
import android.media.AudioDeviceCallback
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_main.*
import us.zoom.sdk.*
import us.zoom.sdk.InMeetingChatController as InMeetingChatController

class MainActivity : AppCompatActivity() {
    private val authListener = object : ZoomSDKAuthenticationListener {
        /**
         * This callback is invoked when a result from the SDK's request to the auth server is
         * received.
         */
        override fun onZoomSDKLoginResult(result: Long) {
            if (result.toInt() == ZoomAuthenticationError.ZOOM_AUTH_ERROR_SUCCESS) {
                // Once we verify that the request was successful, we may start the meeting
                startMeeting(this@MainActivity)
            }
        }
        override fun onZoomIdentityExpired() = Unit
        override fun onZoomSDKLogoutResult(p0: Long) = Unit
        override fun onZoomAuthIdentityExpired() = Unit
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val zoomSDK = ZoomSDK.getInstance()

        initializeSdk(this)
        initViews()
    }

    /**
     * Initialize the SDK with your credentials. This is required before accessing any of the
     * SDK's meeting-related functionality.
     */
    private fun initializeSdk(context: Context) {
        val sdk = ZoomSDK.getInstance()

        val params = ZoomSDKInitParams().apply {
            appKey = "eh653FOACqT1qLKa5qYgf8PeHGYvcbQtqxa0"
            appSecret = "anGDxsEAnTD0Gq9eT7bTgQZ7jzGznT22Cwoi"
            domain = "zoom.us"
            enableLog = true // Optional: enable logging for debugging
        }

        val listener = object : ZoomSDKInitializeListener {
            /**
             * If the [errorCode] is [ZoomError.ZOOM_ERROR_SUCCESS], the SDK was initialized and can
             * now be used to join/start a meeting.
             */
            override fun onZoomSDKInitializeResult(errorCode: Int, internalErrorCode: Int) = Unit
            override fun onZoomAuthIdentityExpired() = Unit
        }
        sdk.initialize(context, listener, params)
    }

    private fun initViews() {
        join_button.setOnClickListener {
            createJoinMeetingDialog()
        }
        login_button.setOnClickListener {
            if (ZoomSDK.getInstance().isLoggedIn) {
                startMeeting(this)
            } else {
                createLoginDialog()
            }
        }
    }

    /**
     * Join a meeting without any login/authentication with the meeting's number & password
     */
    private fun joinMeeting(context: Context, meetingNumber: String, pw: String) {
        val meetingService = ZoomSDK.getInstance().meetingService
        val options = JoinMeetingOptions()
//        val chatOptions = ZoomSDK.getInstance().inMeetingService
//        val inMeetingChatController = chatOptions.inMeetingChatController
//        val privateChatDisabled = inMeetingChatController.isPrivateChatDisabled
//        Log.i("chat", "hello $privateChatDisabled")
        options.meeting_views_options = MeetingViewsOptions.NO_BUTTON_SHARE + MeetingViewsOptions.NO_BUTTON_VIDEO + MeetingViewsOptions.NO_BUTTON_SWITCH_CAMERA + MeetingViewsOptions.NO_BUTTON_AUDIO + MeetingViewsOptions.NO_BUTTON_PARTICIPANTS
        val params = JoinMeetingParams().apply {
            displayName = "Demo"
            meetingNo = "82940627403" /*meetingNumber*/
            password = "741Ck2" /* pw */
        }
        meetingService.joinMeetingWithParams(context, params, options)
    }

    /**
     * Log into a Zoom account through the SDK using your email and password. For more information,
     * see [ZoomSDKAuthenticationListener.onZoomSDKLoginResult] in the [authListener].
     */
    private fun login(username: String, password: String) {
        val result = ZoomSDK.getInstance().loginWithZoom(username, password)
        if (result == ZoomApiError.ZOOM_API_ERROR_SUCCESS) {
            // Request executed, listen for result to start meeting
            ZoomSDK.getInstance().addAuthenticationListener(authListener)
        }
    }

    /**
     * Start an instant meeting as a logged-in user. An instant meeting has a meeting number and
     * password generated when it is created.
     */
    private fun startMeeting(context: Context) {
        val zoomSdk = ZoomSDK.getInstance()
        if (zoomSdk.isLoggedIn) {
            val meetingService = zoomSdk.meetingService
            val options = StartMeetingOptions()
            meetingService.startInstantMeeting(context, options)
        }
    }

    /**
     * Prompt the user to input the meeting number and password and uses the Zoom SDK to join the
     * meeting.
     */
    private fun createJoinMeetingDialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.dialog_join_meeting)
            .setPositiveButton("Join") { dialog, _ ->
                dialog as AlertDialog
                val numberInput = dialog.findViewById<TextInputEditText>(R.id.meeting_no_input)
                val passwordInput = dialog.findViewById<TextInputEditText>(R.id.password_input)
                val meetingNumber = numberInput?.text?.toString()
                val password = passwordInput?.text?.toString()
                meetingNumber?.takeIf { it.isNotEmpty() }?.let { meetingNo ->
                    password?.let { pw ->
                        joinMeeting(this@MainActivity, meetingNo, pw)
                    }
                }
                dialog.dismiss()
            }
            .show()
    }

    /**
     * Prompts the user to input their account email and password and uses the Zoom SDK to login.
     * See [ZoomSDKAuthenticationListener.onZoomSDKLoginResult] in the [authListener] for more information.
     */
    private fun createLoginDialog() {
        AlertDialog.Builder(this)
            .setView(R.layout.dialog_login)
            .setPositiveButton("Log in") { dialog, _ ->
                dialog as AlertDialog
                val emailInput = dialog.findViewById<TextInputEditText>(R.id.email_input)
                val passwordInput = dialog.findViewById<TextInputEditText>(R.id.pw_input)
                val email = emailInput?.text?.toString()
                val password = passwordInput?.text?.toString()
                email?.takeIf { it.isNotEmpty() }?.let { emailAddress ->
                    password?.takeIf { it.isNotEmpty() }?.let { pw ->
                        login(emailAddress, pw)
                    }
                }
                dialog.dismiss()
            }.show()
    }

}
