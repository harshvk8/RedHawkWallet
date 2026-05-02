package com.redhawk.wallet.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.redhawk.wallet.data.models.AccountType
import com.redhawk.wallet.ui.navigation.Routes

private val SilverLight = Color(0xFFE8E8E8)
private val SilverMid   = Color(0xFFC0C0C0)
private val SilverDark  = Color(0xFF9A9A9A)
private val CardBlack1  = Color(0xFF080808)
private val CardBlack2  = Color(0xFF1A1A1A)
private val CardBlack3  = Color(0xFF111111)

private val accountTints = listOf(
    Color(0xFF1A1A2E),
    Color(0xFF1A2E1A),
    Color(0xFF2E1A1A),
    Color(0xFF1A1E2E),
)

private val PEEK_HEIGHT = 72.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    tapVm: TapToPayViewModel
) {
    val st by tapVm.state.collectAsState()
    val scrollState = rememberScrollState()
    var showTransactions by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { tapVm.loadDashboard() }
    LaunchedEffect(st.selectedAccount) { tapVm.loadDashboard() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { navController.navigate(Routes.QR_ID) }) {
                        Icon(
                            imageVector        = Icons.Filled.AccountCircle,
                            contentDescription = "Account",
                            tint               = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            WalletCardStack(
                accounts        = AccountType.values().toList(),
                selectedAccount = st.selectedAccount,
                balance         = st.balanceText,
                isLoading       = st.loading,
                isVerified      = st.isEmailVerified,
                onCardSelected  = { tapVm.selectAccount(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedContent(
                targetState   = st.selectedAccount.name,
                transitionSpec = { fadeIn(tween(300)).togetherWith(fadeOut(tween(300))) },
                label          = "nfcHint"
            ) { name ->
                Text(
                    text  = if (name == "MEAL_SWIPES") "Tap an NFC tag to use 1 meal swipe"
                    else "Tap an NFC tag to pay $5",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = st.error != null,
                enter   = fadeIn(tween(300)) + expandVertically(),
                exit    = fadeOut(tween(300)) + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(st.error ?: "", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showTransactions = !showTransactions }
                    .padding(vertical = 14.dp)
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedContent(
                        targetState   = showTransactions,
                        transitionSpec = { fadeIn(tween(200)).togetherWith(fadeOut(tween(200))) },
                        label          = "arrowIcon"
                    ) { expanded ->
                        Icon(
                            imageVector        = if (expanded) Icons.Filled.KeyboardArrowDown
                            else Icons.Filled.KeyboardArrowRight,
                            contentDescription = "Toggle Transactions",
                            tint               = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Text(
                        text     = "Transactions",
                        style    = MaterialTheme.typography.titleMedium,
                        color    = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 6.dp)
                    )
                }
            }

            AnimatedVisibility(
                visible = showTransactions,
                enter   = fadeIn(tween(300)) + expandVertically(tween(300)),
                exit    = fadeOut(tween(200)) + shrinkVertically(tween(200))
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    if (st.transactionsText.isBlank()) {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text(
                                "No Transactions Yet",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(st.transactionsText, color = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Apple Wallet card stack ────────────────────────────────────────────────────
@Composable
private fun WalletCardStack(
    accounts:        List<AccountType>,
    selectedAccount: AccountType,
    balance:         String,
    isLoading:       Boolean,
    isVerified:      Boolean,
    onCardSelected:  (AccountType) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        accounts.forEachIndexed { index, account ->
            val isSelected = account == selectedAccount

            val cardHeight by animateDpAsState(
                targetValue   = if (isSelected) 200.dp else PEEK_HEIGHT,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness    = Spring.StiffnessMediumLow
                ),
                label = "cardHeight_$index"
            )

            val scale by animateFloatAsState(
                targetValue   = if (isSelected) 1f else 0.97f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label         = "scale_$index"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
                    .shadow(if (isSelected) 14.dp else 5.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                CardBlack1,
                                accountTints.getOrElse(index) { CardBlack2 },
                                CardBlack3
                            ),
                            start = Offset(0f, 0f),
                            end   = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication        = null
                    ) { onCardSelected(account) }
            ) {
                // Sheen overlay
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.06f), Color.Transparent),
                                center = Offset(0f, 0f),
                                radius = 500f
                            )
                        )
                )

                // ── Card content ───────────────────────────────────────────────
                Column(modifier = Modifier.fillMaxSize()) {

                    // ── Always-visible header row ──────────────────────────────
                    Row(
                        modifier              = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: avatar + account name
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier         = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text       = account.displayName.firstOrNull()
                                        ?.uppercaseChar()?.toString() ?: "W",
                                    color      = SilverLight,
                                    fontWeight = FontWeight.Bold,
                                    fontSize   = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text          = account.displayName.uppercase(),
                                color         = SilverMid,
                                fontWeight    = FontWeight.SemiBold,
                                fontSize      = 12.sp,
                                letterSpacing = 1.sp,
                                maxLines      = 1,
                                overflow      = TextOverflow.Ellipsis
                            )
                        }


                    }

                    // ── Expanded body ──────────────────────────────────────────
                    AnimatedVisibility(
                        visible = isSelected,
                        enter   = fadeIn(tween(280, delayMillis = 100)) +
                                expandVertically(tween(280, delayMillis = 100)),
                        exit    = fadeOut(tween(150)) + shrinkVertically(tween(150))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                        ) {
                            Text(
                                "BALANCE",
                                color         = SilverDark,
                                fontSize      = 10.sp,
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))

                            // Balance value crossfades
                            AnimatedContent(
                                targetState   = if (isLoading) "••••"
                                else balance.replace("Balance:", "").trim(),
                                transitionSpec = {
                                    fadeIn(tween(300)).togetherWith(fadeOut(tween(200)))
                                },
                                label = "balance_$index"
                            ) { bal ->
                                Text(
                                    text          = bal,
                                    color         = SilverLight,
                                    fontSize      = 26.sp,
                                    fontWeight    = FontWeight.Bold,
                                    fontFamily    = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                            }

                            // Push status label to bottom-right
                            Spacer(modifier = Modifier.weight(1f))

                            // ── Active / Not Active status ─────────────────────
                            val isActive = isVerified
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text          = if (isActive) "ACTIVE" else "NOT ACTIVE",
                                    color         = if (isActive) Color(0xFF4CAF50)
                                    else Color(0xFFE57373),
                                    fontSize      = 10.sp,
                                    fontWeight    = FontWeight.SemiBold,
                                    letterSpacing = 1.5.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}