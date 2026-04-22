package com.redhawk.wallet.ui.navigation

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val DASHBOARD = "dashboard"
    const val REGISTER = "register"
    const val EMAIL_VERIFICATION = "email_verification"
    const val QR_ID = "qr_id"
    const val QR_SCANNER = "qr_scanner"

    const val EVENTS_OFFERS = "events_offers"

    const val EVENT_ID_ARG = "eventId"
    const val OFFER_ID_ARG = "offerId"

    const val EVENT_DETAILS = "event_details/{$EVENT_ID_ARG}"
    const val OFFER_DETAILS = "offer_details/{$OFFER_ID_ARG}"

    fun eventDetailsRoute(eventId: String): String = "event_details/$eventId"
    fun offerDetailsRoute(offerId: String): String = "offer_details/$offerId"
}