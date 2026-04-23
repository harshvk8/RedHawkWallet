package com.redhawk.wallet.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.redhawk.wallet.data.models.Event
import com.redhawk.wallet.data.models.Offer
import com.redhawk.wallet.data.repository.EventRepository
import com.redhawk.wallet.data.repository.OfferRepository
import kotlinx.coroutines.launch

class EventsOffersViewModel(
    private val eventRepository: EventRepository,
    private val offerRepository: OfferRepository
) : ViewModel() {

    var isLoading: Boolean = false
        private set

    var errorMessage: String? = null
        private set

    var events: List<Event> = emptyList()
        private set

    var offers: List<Offer> = emptyList()
        private set

    var selectedEvent: Event? = null
        private set

    var selectedOffer: Offer? = null
        private set

    fun loadAll() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                events = eventRepository.getEvents()
                offers = offerRepository.getOffers()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load events and offers."
            } finally {
                isLoading = false
            }
        }
    }

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                selectedEvent = eventRepository.getEvent(eventId)
                if (selectedEvent == null) {
                    errorMessage = "Event not found."
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load event."
            } finally {
                isLoading = false
            }
        }
    }

    fun loadOffer(offerId: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                selectedOffer = offerRepository.getOffer(offerId)
                if (selectedOffer == null) {
                    errorMessage = "Offer not found."
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load offer."
            } finally {
                isLoading = false
            }
        }
    }

    fun redeemOffer(offerId: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw IllegalStateException("User not logged in.")

                offerRepository.redeemOffer(offerId, uid)
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to redeem offer."
            } finally {
                isLoading = false
            }
        }
    }

    fun clearSelectedEvent() {
        selectedEvent = null
    }

    fun clearSelectedOffer() {
        selectedOffer = null
    }
}

class EventsOffersViewModelFactory(
    private val eventRepository: EventRepository,
    private val offerRepository: OfferRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EventsOffersViewModel::class.java)) {
            return EventsOffersViewModel(eventRepository, offerRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}