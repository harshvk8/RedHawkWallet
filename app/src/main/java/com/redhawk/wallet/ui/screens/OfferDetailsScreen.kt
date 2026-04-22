package com.redhawk.wallet.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.redhawk.wallet.events.EventsOffersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfferDetailsScreen(
    navController: NavController,
    offerId: String,
    viewModel: EventsOffersViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(offerId) {
        viewModel.loadOffer(offerId)
    }

    val offer = viewModel.selectedOffer

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Offer Details",
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
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                viewModel.isLoading -> {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Loading offer...")
                    }
                }

                viewModel.errorMessage != null -> {
                    Text(
                        text = viewModel.errorMessage ?: "Failed to load offer.",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                offer == null -> {
                    Text("Offer not found.")
                }

                else -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color(0xFFE5E5E5)),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = offer.title.ifBlank { "Untitled Offer" },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "Points Required: ${offer.pointsRequired}",
                                style = MaterialTheme.typography.bodyLarge
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Expiry Date: ${offer.expiryDate.ifBlank { "TBD" }}",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = offer.description.ifBlank { "No description available." },
                                style = MaterialTheme.typography.bodyMedium
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    viewModel.redeemOffer(offerId) {
                                        Toast.makeText(
                                            context,
                                            "Offer redeemed successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Redeem Offer")
                            }
                        }
                    }
                }
            }
        }
    }
}