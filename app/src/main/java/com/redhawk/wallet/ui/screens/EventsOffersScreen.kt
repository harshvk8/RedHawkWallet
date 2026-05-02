package com.redhawk.wallet.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.redhawk.wallet.data.models.Event
import com.redhawk.wallet.data.models.Offer
import com.redhawk.wallet.events.EventsOffersViewModel
import com.redhawk.wallet.ui.navigation.Routes

// ── Brand colors ──────────────────────────────────────────────────────────────
private val HalalYellow  = Color(0xFFF5C518)
private val HalalBlack   = Color(0xFF111111)
private val AccentCrimson = Color(0xFFBE1E2D)
private val CardSurface  = Color(0xFF1C1917)
private val DividerColor = Color(0xFF2A2622)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsOffersScreen(
    navController: NavController,
    viewModel: EventsOffersViewModel
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()

    LaunchedEffect(Unit) { viewModel.loadAll() }
    LaunchedEffect(viewModel.isLoading) {
        if (!viewModel.isLoading) isRefreshing = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text  = "Events & Offers",
                        fontWeight    = FontWeight.Bold,
                        fontSize      = 20.sp,
                        letterSpacing = (-0.3).sp,
                        color         = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector        = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint               = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        PullToRefreshBox(
            state       = pullState,
            isRefreshing = isRefreshing,
            onRefresh   = {
                isRefreshing = true
                viewModel.loadAll()
                isRefreshing = false
            },
            modifier    = Modifier.padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // ── Custom tab bar ─────────────────────────────────────────────
                CustomTabBar(
                    tabs          = listOf("Events", "Offers"),
                    selectedIndex = selectedTabIndex,
                    onTabSelected = { selectedTabIndex = it }
                )

                // ── Content ────────────────────────────────────────────────────
                when {
                    viewModel.isLoading -> LoadingState()
                    viewModel.errorMessage != null -> ErrorState(viewModel.errorMessage!!)
                    selectedTabIndex == 0 -> EventsTab(viewModel.events, navController)
                    else -> OffersTab(viewModel.offers, navController, viewModel)
                }
            }
        } // end PullToRefreshBox
    }
}

// ── Custom pill tab bar ────────────────────────────────────────────────────────
@Composable
private fun CustomTabBar(
    tabs:          List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected)
                            Brush.linearGradient(
                                listOf(Color(0xFFD94455), Color(0xFF8C0E1A)),
                                start = Offset(0f, 0f),
                                end   = Offset(Float.POSITIVE_INFINITY, 0f)
                            )
                        else
                            Brush.linearGradient(
                                listOf(Color.Transparent, Color.Transparent)
                            )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = title,
                    color      = if (isSelected) Color.White
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize   = 14.sp
                )
            }
        }
    }
}

// ── Events tab ────────────────────────────────────────────────────────────────
@Composable
private fun EventsTab(events: List<Event>, navController: NavController) {
    if (events.isEmpty()) {
        EmptyState("No events right now.\nCheck back soon!")
    } else {
        LazyColumn(
            modifier            = Modifier.fillMaxSize(),
            contentPadding      = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 16.dp, vertical = 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(events) { i, event ->
                AnimatedVisibility(
                    visible = true,
                    enter   = fadeIn(tween(300, i * 60)) +
                            slideInVertically(tween(300, i * 60)) { it / 4 }
                ) {
                    EventItemCard(event) {
                        navController.navigate(Routes.eventDetailsRoute(event.id))
                    }
                }
            }
        }
    }
}

// ── Offers tab — with Halal Shack featured ad at top ─────────────────────────
@Composable
private fun OffersTab(
    offers:    List<Offer>,
    navController: NavController,
    viewModel: EventsOffersViewModel
) {
    var adDismissed by remember { mutableStateOf(false) }

    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp, vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Halal Shack featured ad ────────────────────────────────────────
        if (!adDismissed) {
            item {
                HalalShackAdBanner(
                    onDismiss = { adDismissed = true },
                    onRedeem  = { onSuccess ->
                        viewModel.redeemOffer("halal_shack_bowl_deal", onSuccess)
                    },
                    isLoading = viewModel.isLoading
                )
            }
        }

        if (offers.isEmpty()) {
            item { EmptyState("No offers available\nright now.") }
        } else {
            itemsIndexed(offers) { i, offer ->
                AnimatedVisibility(
                    visible = true,
                    enter   = fadeIn(tween(300, i * 60)) +
                            slideInVertically(tween(300, i * 60)) { it / 4 }
                ) {
                    OfferItemCard(offer) {
                        navController.navigate(Routes.offerDetailsRoute(offer.id))
                    }
                }
            }
        }
    }
}

// ── Halal Shack Ad Banner ─────────────────────────────────────────────────────
@Composable
private fun HalalShackAdBanner(
    onDismiss: () -> Unit,
    onRedeem:  (() -> Unit) -> Unit,
    isLoading: Boolean
) {
    var pressed           by remember { mutableStateOf(false) }
    var showConfirm       by remember { mutableStateOf(false) }
    var showSuccess       by remember { mutableStateOf(false) }
    var redemptionCode    by remember { mutableStateOf("") }

    val scale by animateFloatAsState(
        targetValue   = if (pressed) 0.97f else 1f,
        animationSpec = tween(120),
        label         = "adScale"
    )

    // ── Confirmation dialog ────────────────────────────────────────────────
    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = {
                Text("Confirm Redemption", fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("You are about to redeem:")
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(HalalBlack)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                "2 Large Rice Bowls",
                                color      = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize   = 15.sp
                            )
                            Text(
                                "The Halal Shack — Montclair Student Centre",
                                color    = Color(0xFF9E9590),
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "$20.99",
                                color      = HalalYellow,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "This will be charged to your Red Hawk account.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(HalalYellow)
                        .clickable {
                            showConfirm = false
                            redemptionCode = "HS-" + (100000..999999).random().toString()
                            onRedeem { showSuccess = true }
                        }
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier    = Modifier.size(16.dp),
                            color       = HalalBlack,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            "Confirm & Pay",
                            color      = HalalBlack,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 13.sp
                        )
                    }
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showConfirm = false }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        "Cancel",
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        )
    }

    // ── Success dialog ─────────────────────────────────────────────────────
    if (showSuccess) {
        AlertDialog(
            onDismissRequest = { showSuccess = false; onDismiss() },
            title = {
                Text("🎉 Offer Redeemed!", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Show this code to the cashier at The Halal Shack:",
                        style     = MaterialTheme.typography.bodyMedium,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(HalalBlack)
                            .border(2.dp, HalalYellow, RoundedCornerShape(14.dp))
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text       = redemptionCode,
                                color      = HalalYellow,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 32.sp,
                                letterSpacing = 4.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "2 Large Rice Bowls · $20.99",
                                color    = Color(0xFF9E9590),
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "✓ Payment deducted from your account",
                        color      = Color(0xFF4CAF76),
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(HalalYellow)
                        .clickable { showSuccess = false; onDismiss() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Done",
                        color      = HalalBlack,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(12.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(HalalBlack)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null
            ) { pressed = !pressed }
    ) {
        // Yellow accent stripe at top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(HalalYellow)
        )

        Column(modifier = Modifier.padding(top = 4.dp)) {
            // Header row
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Brand name block
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(HalalYellow, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text       = "THE HALAL SHACK",
                                color      = HalalBlack,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize   = 11.sp,
                                letterSpacing = 1.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF2A2622), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text      = "FEATURED",
                                color     = HalalYellow,
                                fontSize  = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text       = "🔥 Limited Time Deal",
                        color      = Color(0xFFE0D5C5),
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Dismiss X
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2A2622))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = Color(0xFF9E9590), fontSize = 11.sp)
                }
            }

            // Price hero section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E1A10), Color(0xFF2A2210)),
                            start = Offset(0f, 0f),
                            end   = Offset(Float.POSITIVE_INFINITY, 0f)
                        )
                    )
                    .border(1.dp, HalalYellow.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier              = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text       = "2 Large Rice Bowls",
                            color      = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize   = 18.sp,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text           = "$29.98",
                                color          = Color(0xFF6B6560),
                                fontSize       = 13.sp,
                                textDecoration = TextDecoration.LineThrough
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text       = "Save $8.99!",
                                color      = Color(0xFF4CAF76),
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Price badge
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text       = "ONLY",
                            color      = HalalYellow,
                            fontSize   = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text       = "$20.99",
                            color      = HalalYellow,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 28.sp,
                            letterSpacing = (-1).sp
                        )
                    }
                }
            }

            // Location line
            Row(
                modifier          = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF2A2622))
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍", fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text       = "Only Location: Montclair Student Centre",
                            color      = Color(0xFFCCC8C3),
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // CTA button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 18.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(HalalYellow)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { showConfirm = true }
                    .padding(vertical = 13.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "Redeem This Offer →",
                    color      = HalalBlack,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize   = 14.sp,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

// ── Event card ────────────────────────────────────────────────────────────────
@Composable
private fun EventItemCard(event: Event, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(80.dp)
                .align(Alignment.CenterStart)
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFD94455), Color(0xFF8C0E1A)),
                        start = Offset(0f, 0f),
                        end   = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                )
        )

        Column(modifier = Modifier.padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Text(
                    text       = event.title.ifBlank { "Untitled Event" },
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis,
                    modifier   = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector        = Icons.Filled.DateRange,
                            contentDescription = null,
                            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier           = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text     = event.date.ifBlank { "TBD" },
                            color    = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text     = event.description.ifBlank { "No description available." },
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// ── Offer card ────────────────────────────────────────────────────────────────
@Composable
private fun OfferItemCard(offer: Offer, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
                onClick           = onClick
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier              = Modifier.fillMaxWidth()
            ) {
                Text(
                    text       = offer.title.ifBlank { "Untitled Offer" },
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface,
                    modifier   = Modifier.weight(1f),
                    maxLines   = 1,
                    overflow   = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Points badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFFD94455), Color(0xFF8C0E1A)),
                                start = Offset(0f, 0f),
                                end   = Offset(Float.POSITIVE_INFINITY, 0f)
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector        = Icons.Filled.Star,
                            contentDescription = null,
                            tint               = Color.White,
                            modifier           = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text       = "${offer.pointsRequired} pts",
                            color      = Color.White,
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text     = offer.description.ifBlank { "No description available." },
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCFAD60))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text     = "Expires: ${offer.expiryDate.ifBlank { "TBD" }}",
                    color    = Color(0xFFCFAD60),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// ── Loading state ─────────────────────────────────────────────────────────────
@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color     = Color(0xFFD94455),
                modifier  = Modifier.size(40.dp),
                strokeWidth = 3.dp
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                "Loading...",
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

// ── Error state ───────────────────────────────────────────────────────────────
@Composable
private fun ErrorState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("⚠️", fontSize = 36.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text      = message,
                color     = MaterialTheme.colorScheme.error,
                style     = MaterialTheme.typography.bodyMedium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────
@Composable
private fun EmptyState(message: String) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text      = message,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            style     = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}