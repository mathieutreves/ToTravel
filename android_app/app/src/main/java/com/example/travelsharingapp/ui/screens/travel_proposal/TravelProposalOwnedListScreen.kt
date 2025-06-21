package com.example.travelsharingapp.ui.screens.travel_proposal

import android.content.res.Configuration
import androidx.compose.foundation.MarqueeAnimationMode
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.travelsharingapp.R
import com.example.travelsharingapp.data.model.ProposalStatus
import com.example.travelsharingapp.data.model.TravelProposal
import com.example.travelsharingapp.ui.screens.chat.ChatViewModel
import com.example.travelsharingapp.ui.screens.main.AppRoutes
import com.example.travelsharingapp.ui.screens.main.TopBarViewModel
import com.example.travelsharingapp.utils.shouldUseTabletLayout
import com.example.travelsharingapp.utils.toProposalStatusOrNull
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelProposalOwnedListScreen(
    modifier: Modifier,
    viewModel: TravelProposalViewModel,
    topBarViewModel: TopBarViewModel,
    chatViewModel: ChatViewModel,
    userId: String,
    navController: NavController
) {
    val unreadCounts by chatViewModel.unreadMessagesCount.collectAsState()
    val configuration = LocalConfiguration.current
    val isTablet = shouldUseTabletLayout()

    val numColumns = remember(configuration.orientation) {
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (isTablet) 3 else 2
        } else {
            if (isTablet) 2 else 1
        }
    }

    val ownedProposals by viewModel.ownedProposals.collectAsState()
    val openProposals by viewModel.openProposals.collectAsState()
    val concludedProposals by viewModel.concludedProposals.collectAsState()
    val statusFilter by viewModel.statusFilter.collectAsState()

    LaunchedEffect(userId) {
        viewModel.startListeningOwnedProposals(userId)
    }

    LaunchedEffect(Unit) {
        topBarViewModel.setConfig(
            title = "My Travels",
            navigationIcon = { /* nothing */ },
            actions = {
                IconButton(onClick = { navController.navigate(AppRoutes.CHAT_LIST) }) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "Chat")
                }
            }
        )
    }

    LaunchedEffect(ownedProposals) {
        val allChatIds = ownedProposals.map { it.proposalId }
        chatViewModel.listenForUnreadCounts(allChatIds, userId)
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        StatusFilterChips(
            selectedStatuses = statusFilter,
            onStatusChanged = { newStatuses ->
                viewModel.updateStatusFilter(newStatuses)
            }
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(numColumns),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Open",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.height(12.dp),
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 1.dp
                    )
                }
            }

            if (openProposals.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No new travels " + if (!(statusFilter.contains(ProposalStatus.Published.name) || statusFilter.contains(ProposalStatus.Full.name))) "match selection." else "yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            items(
                count = openProposals.size,
                key = { index -> openProposals[index].proposalId },
                contentType = { "OwnedTravelProposalCard" },
                itemContent = { index ->
                    val ownedProposal = openProposals[index]
                    val count = unreadCounts[ownedProposal.proposalId] ?: 0
                    OwnedTravelProposalCard(
                        ownedProposal = ownedProposal,
                        isOwner = true,
                        modifier = Modifier.fillMaxWidth(),
                        unreadCount = count,
                        onClick = {
                            navController.navigate(AppRoutes.travelProposalInfo(ownedProposal.proposalId))
                        },
                        onPendingApplicationsClick = {
                            navController.navigate(AppRoutes.manageApplications(ownedProposal.proposalId))
                        },
                        onViewReviewsClick = {
                            navController.navigate(AppRoutes.reviewViewAllScreen(ownedProposal.proposalId))
                        },
                        onNavigateToChatRoom = {
                            navController.navigate(AppRoutes.chatRoom(ownedProposal.proposalId))
                        }
                    )
                }
            )

            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp)) {
                    Text(
                        text = "Concluded",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier.height(12.dp),
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 1.dp
                    )
                }
            }

            if (concludedProposals.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.EventBusy,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "No concluded travels " + if (!(statusFilter.contains(ProposalStatus.Concluded.name))) "match selection." else "yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            items(
                count = concludedProposals.size,
                key = { index -> concludedProposals[index].proposalId },
                contentType = { "OwnedTravelProposalCard" },
                itemContent = { index ->
                    val ownedProposal = concludedProposals[index]
                    val count = unreadCounts[ownedProposal.proposalId] ?: 0
                    OwnedTravelProposalCard(
                        ownedProposal = ownedProposal,
                        isOwner = true,
                        modifier = Modifier.fillMaxWidth(),
                        unreadCount = count,
                        onClick = {
                            navController.navigate(AppRoutes.travelProposalInfo(ownedProposal.proposalId))
                        },
                        onPendingApplicationsClick = {
                            navController.navigate(AppRoutes.manageApplications(ownedProposal.proposalId))
                        },
                        onViewReviewsClick = {
                            navController.navigate(AppRoutes.reviewViewAllScreen(ownedProposal.proposalId))
                        },
                        onNavigateToChatRoom = {
                            navController.navigate(AppRoutes.chatRoom(ownedProposal.proposalId))
                        }
                    )
                }
            )
        }
    }
}

@Composable
fun OwnedTravelProposalCard(
    ownedProposal: TravelProposal,
    isOwner: Boolean,
    modifier: Modifier = Modifier,
    unreadCount: Int,
    onClick: () -> Unit,
    onPendingApplicationsClick: () -> Unit,
    onViewReviewsClick: () -> Unit,
    onNavigateToChatRoom: () -> Unit
) {
    val statusColor = when (ownedProposal.statusEnum) {
        ProposalStatus.Published -> Color(0xFF2E7D32)
        ProposalStatus.Full -> Color(0xFFFFA000)
        ProposalStatus.Concluded -> Color(0xFF1565C0)
        else -> Color.Gray
    }

    Card(
        modifier = modifier.height(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            val imageList = if (ownedProposal.thumbnails.isNotEmpty()) {
                ownedProposal.thumbnails
            } else {
                ownedProposal.images
            }

            if (imageList.isNotEmpty()) {
                val banners = imageList.mapIndexed { index, item ->
                    BannerModel(
                        imageUrl = item,
                        contentDescription = "Image ${index + 1}"
                    )
                }

                BannerCarouselWidget(
                    banners = banners,
                    modifier = Modifier
                        .width(140.dp)
                        .fillMaxHeight(),
                    pageSpacing = 0.dp,
                    contentPadding = PaddingValues(0.dp)
                )
            } else {
                AsyncImage(
                    model = R.drawable.placeholder_error,
                    contentDescription = "Destination image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(140.dp)
                        .fillMaxHeight()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 4.dp, top = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Text(
                        text = ownedProposal.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth(fraction = 0.6f)
                            .basicMarquee(animationMode = MarqueeAnimationMode.Immediately)
                    )

                    BoxWithConstraints {
                        val showText = this.maxWidth > 150.dp
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = statusColor,
                            tonalElevation = 2.dp,
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            if (showText) {
                                Text(
                                    text =  ownedProposal.statusEnum?.name ?: ownedProposal.status,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            } else {
                                Icon(
                                    getIconForTravelStatus(ownedProposal.status.toProposalStatusOrNull() ?: ProposalStatus.Full),
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = ownedProposal.startDate?.toDate()?.let {
                        DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(it.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate())
                    } ?: "No date",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${ownedProposal.participantsCount}/${ownedProposal.maxParticipants} joined",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (isOwner) {
                        if (ownedProposal.pendingApplicationsCount > 0) {
                            AssistChip(
                                onClick = onPendingApplicationsClick,
                                label = {
                                    Text(
                                        text = "${ownedProposal.pendingApplicationsCount} pending",
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.PersonAdd, contentDescription = "Pending applications")
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFFFEBEE),
                                    labelColor = Color(0xFFD32F2F),
                                    leadingIconContentColor = Color(0xFFD32F2F)
                                )
                            )
                        }

                        if (unreadCount > 0) {
                            AssistChip(
                                onClick = onNavigateToChatRoom,
                                label = {
                                    Text(
                                        text = "$unreadCount message" + (if (unreadCount > 1) "s" else ""),
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.MarkEmailUnread,
                                        contentDescription = "New messages"
                                    )
                                },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color(0xFFE3F2FD),
                                    labelColor = Color(0xFF1565C0),
                                    leadingIconContentColor = Color(0xFF1565C0)
                                )
                            )
                        }
                    }
                }

                if (ownedProposal.statusEnum == ProposalStatus.Concluded) {
                    AssistChip(
                        onClick = onViewReviewsClick,
                        label = {
                            Text(
                                text = "Reviews"
                            ) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            labelColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFDBA809)
                            )
                        },
                        border = null,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun StatusFilterChips(
    selectedStatuses: List<String>,
    onStatusChanged: (List<String>) -> Unit
) {
    val allStatuses = ProposalStatus.entries.map { it.name }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(
            count = allStatuses.size,
            key = { index -> allStatuses[index] }
        ) { index ->
            val status = allStatuses[index]
            FilterChip(
                selected = selectedStatuses.contains(status),
                onClick = {
                    val newSelection = selectedStatuses.toMutableList()
                    if (newSelection.contains(status)) {
                        newSelection.remove(status)
                    } else {
                        newSelection.add(status)
                    }
                    onStatusChanged(newSelection)
                },
                label = { Text(status) },
                leadingIcon = if (selectedStatuses.contains(status)) {
                    {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null
            )
        }
    }
}