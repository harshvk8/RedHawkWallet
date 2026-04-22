package com.redhawk.wallet.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.redhawk.wallet.data.models.Event
import com.redhawk.wallet.data.models.Offer
import com.redhawk.wallet.events.EventsOffersViewModel
import com.redhawk.wallet.ui.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsOffersScreen(
    navController: NavController,
    viewModel: EventsOffersViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadAll()
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val red = Color(0xFFC8102E)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Events & Offers",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(selectedTabIndex = selectedTabIndex) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Events") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Offers") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                viewModel.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading...")
                    }
                }

                viewModel.errorMessage != null -> {
                    Text(
                        text = viewModel.errorMessage ?: "Something went wrong.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                selectedTabIndex == 0 -> {
                    if (viewModel.events.isEmpty()) {
                        Text("No events available right now.")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.events) { event ->
                                EventItemCard(
                                    event = event,
                                    onClick = {
                                        navController.navigate(
                                            Routes.eventDetailsRoute(event.id)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                else -> {
                    if (viewModel.offers.isEmpty()) {
                        Text("No offers available right now.")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(viewModel.offers) { offer ->
                                OfferItemCard(
                                    offer = offer,
                                    onClick = {
                                        navController.navigate(
                                            Routes.offerDetailsRoute(offer.id)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventItemCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = event.title.ifBlank { "Untitled Event" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Date: ${event.date.ifBlank { "TBD" }}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = event.description.ifBlank { "No description available." },
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun OfferItemCard(
    offer: Offer,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = offer.title.ifBlank { "Untitled Offer" },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Points Required: ${offer.pointsRequired}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Expires: ${offer.expiryDate.ifBlank { "TBD" }}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = offer.description.ifBlank { "No description available." },
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3
            )
        }
    }
}