package com.v2ray.ang.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.v2ray.ang.R
import com.v2ray.ang.compose.AppDivider
import com.v2ray.ang.compose.ReorderableGridItem
import com.v2ray.ang.compose.ReorderableListItem
import com.v2ray.ang.compose.colorConfigType
import com.v2ray.ang.compose.colorPing
import com.v2ray.ang.compose.colorPingRed
import com.v2ray.ang.compose.verticalScrollbar
import com.v2ray.ang.dto.entities.ProfileItem
import com.v2ray.ang.dto.entities.ServersCache
import com.v2ray.ang.extension.isComplexType
import com.v2ray.ang.extension.nullIfBlank
import com.v2ray.ang.extension.toTrafficString
import com.v2ray.ang.handler.AngConfigManager
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.viewmodel.MainViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyGridState
import sh.calvin.reorderable.rememberReorderableLazyListState
import kotlin.math.abs

@Composable
fun GroupPagerPage(
    groupId: String,
    mainViewModel: MainViewModel,
    selectedGuid: String?,
    doubleColumnDisplay: Boolean,
    confirmRemove: Boolean,
    searchQuery: String,
    lazyListStates: MutableMap<String, LazyListState>,
    lazyGridStates: MutableMap<String, LazyGridState>,
    onSelectServer: (String) -> Unit,
    onEditServer: (String, ProfileItem) -> Unit,
    onShareServer: (String, ProfileItem) -> Unit,
    onMoreServer: (String, ProfileItem) -> Unit,
    onRemoveServer: (String) -> Unit,
    contentPadding: PaddingValues
) {
    val serverFlow = remember(groupId) {
        mainViewModel.serversForGroup(groupId)
    }
    val servers by serverFlow.collectAsStateWithLifecycle()
    val canReorder = groupId.isNotEmpty() && searchQuery.isEmpty()
    ServerListPage(
        servers = servers,
        selectedGuid = selectedGuid,
        canReorder = canReorder,
        doubleColumnDisplay = doubleColumnDisplay,
        subscriptionId = groupId,
        confirmRemove = confirmRemove,
        groupId = groupId,
        lazyListStates = lazyListStates,
        lazyGridStates = lazyGridStates,
        onSelectServer = onSelectServer,
        onEditServer = onEditServer,
        onShareServer = onShareServer,
        onMoreServer = onMoreServer,
        onRemoveServer = onRemoveServer,
        onSwapServer = mainViewModel::swapServer,
        contentPadding = contentPadding
    )
}

@Composable
private fun ServerListPage(
    servers: List<ServersCache>,
    selectedGuid: String?,
    canReorder: Boolean,
    doubleColumnDisplay: Boolean,
    subscriptionId: String,
    confirmRemove: Boolean,
    groupId: String,
    lazyListStates: MutableMap<String, LazyListState>,
    lazyGridStates: MutableMap<String, LazyGridState>,
    onSelectServer: (String) -> Unit,
    onEditServer: (String, ProfileItem) -> Unit,
    onShareServer: (String, ProfileItem) -> Unit,
    onMoreServer: (String, ProfileItem) -> Unit,
    onRemoveServer: (String) -> Unit,
    onSwapServer: (Int, Int) -> Unit,
    contentPadding: PaddingValues
) {
    if (doubleColumnDisplay) {
        val gridState = remember(groupId) {
            lazyGridStates.getOrPut(groupId) { LazyGridState() }
        }
        val reorderableGridState = if (canReorder) {
            rememberReorderableLazyGridState(gridState) { from, to ->
                onSwapServer(from.index, to.index)
            }
        } else null

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollbar(gridState),
            contentPadding = contentPadding
        ) {
            itemsIndexed(items = servers, key = { _, item -> item.guid }) { _, serverCache ->
                val content: @Composable () -> Unit = {
                    ServerItemColumn(
                        serverCache = serverCache,
                        selectedGuid = selectedGuid,
                        subscriptionId = subscriptionId,
                        doubleColumnDisplay = true,
                        onSelectServer = onSelectServer,
                        onEditServer = onEditServer,
                        onShareServer = onShareServer,
                        onMoreServer = onMoreServer,
                        onRemoveServer = onRemoveServer
                    )
                }
                if (canReorder && reorderableGridState != null) {
                    ReorderableItem(
                        reorderableGridState,
                        key = serverCache.guid
                    ) { isDragging ->
                        ReorderableGridItem(
                            scope = this,
                            isDragging = isDragging
                        ) { content() }
                    }
                } else {
                    content()
                }
            }
        }
    } else {
        val listState = remember(groupId) {
            lazyListStates.getOrPut(groupId) { LazyListState() }
        }
        val reorderableState = if (canReorder) {
            rememberReorderableLazyListState(listState) { from, to ->
                onSwapServer(from.index, to.index)
            }
        } else null

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .verticalScrollbar(listState),
            contentPadding = contentPadding
        ) {
            itemsIndexed(items = servers, key = { _, item -> item.guid }) { _, serverCache ->
                if (canReorder && reorderableState != null) {
                    ReorderableItem(
                        reorderableState,
                        key = serverCache.guid
                    ) { isDragging ->
                        ReorderableListItem(
                            scope = this,
                            isDragging = isDragging
                        ) {
                            ServerItemRow(
                                serverCache = serverCache,
                                selectedGuid = selectedGuid,
                                subscriptionId = subscriptionId,
                                onSelectServer = onSelectServer,
                                onEditServer = onEditServer,
                                onShareServer = onShareServer,
                                onMoreServer = onMoreServer,
                                onRemoveServer = onRemoveServer
                            )
                        }
                    }
                } else {
                    ServerItemRow(
                        serverCache = serverCache,
                        selectedGuid = selectedGuid,
                        subscriptionId = subscriptionId,
                        onSelectServer = onSelectServer,
                        onEditServer = onEditServer,
                        onShareServer = onShareServer,
                        onMoreServer = onMoreServer,
                        onRemoveServer = onRemoveServer
                    )
                }
            }
        }
    }
}

@Composable
private fun ServerItemRow(
    serverCache: ServersCache,
    selectedGuid: String?,
    subscriptionId: String,
    onSelectServer: (String) -> Unit,
    onEditServer: (String, ProfileItem) -> Unit,
    onShareServer: (String, ProfileItem) -> Unit,
    onMoreServer: (String, ProfileItem) -> Unit,
    onRemoveServer: (String) -> Unit
) {
    val profile = serverCache.profile
    val subRemarks = if (subscriptionId.isEmpty()) {
        MmkvManager.decodeSubscription(profile.subscriptionId)?.remarks?.firstOrNull()
            ?.toString() ?: ""
    } else ""

    ServerListItem(
        remarks = profile.remarks,
        statistics = profile.description.nullIfBlank()
            ?: AngConfigManager.generateDescription(profile),
        typeDescription = getProtocolDescription(profile),
        testResult = serverCache.testDelayString,
        testDelayMillis = serverCache.testDelayMillis,
        trafficUploadBytes = serverCache.trafficUploadBytes,
        trafficDownloadBytes = serverCache.trafficDownloadBytes,
        isSelected = serverCache.guid == selectedGuid,
        subscriptionRemarks = subRemarks,
        doubleColumnDisplay = false,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
        onClick = { onSelectServer(serverCache.guid) },
        onShare = { onShareServer(serverCache.guid, profile) },
        onEdit = { onEditServer(serverCache.guid, profile) },
        onRemove = { onRemoveServer(serverCache.guid) },
        onMore = { onMoreServer(serverCache.guid, profile) }
    )
}

@Composable
private fun ServerItemColumn(
    serverCache: ServersCache,
    selectedGuid: String?,
    subscriptionId: String,
    doubleColumnDisplay: Boolean,
    onSelectServer: (String) -> Unit,
    onEditServer: (String, ProfileItem) -> Unit,
    onShareServer: (String, ProfileItem) -> Unit,
    onMoreServer: (String, ProfileItem) -> Unit,
    onRemoveServer: (String) -> Unit
) {
    val profile = serverCache.profile
    val subRemarks = if (subscriptionId.isEmpty()) {
        MmkvManager.decodeSubscription(profile.subscriptionId)?.remarks?.firstOrNull()?.toString() ?: ""
    } else ""
    Column {
        ServerListItem(
            remarks = profile.remarks,
            statistics = profile.description.nullIfBlank() ?: AngConfigManager.generateDescription(profile),
            typeDescription = getProtocolDescription(profile),
            testResult = serverCache.testDelayString,
            testDelayMillis = serverCache.testDelayMillis,
            trafficUploadBytes = serverCache.trafficUploadBytes,
            trafficDownloadBytes = serverCache.trafficDownloadBytes,
            isSelected = serverCache.guid == selectedGuid,
            subscriptionRemarks = subRemarks,
            doubleColumnDisplay = doubleColumnDisplay,
            modifier = Modifier.padding(6.dp),
            onClick = { onSelectServer(serverCache.guid) },
            onEdit = { onEditServer(serverCache.guid, profile) },
            onShare = { onShareServer(serverCache.guid, profile) },
            onRemove = { onRemoveServer(serverCache.guid) },
            onMore = { onMoreServer(serverCache.guid, profile) }
        )
    }
}

@Composable
fun ServerListItem(
    remarks: String,
    statistics: String,
    typeDescription: String,
    testResult: String,
    testDelayMillis: Long,
    isSelected: Boolean,
    subscriptionRemarks: String,
    doubleColumnDisplay: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onRemove: () -> Unit,
    onMore: () -> Unit,
    modifier: Modifier = Modifier,
    dragModifier: Modifier = Modifier,
    trafficUploadBytes: Long = 0L,
    trafficDownloadBytes: Long = 0L,
) {
    val accent = MaterialTheme.colorScheme.primary
    val cardShape = RoundedCornerShape(20.dp)
    val cardBackground = MaterialTheme.colorScheme.surfaceContainer
    val cardBorder = if (isSelected) accent else Color.Transparent

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .then(dragModifier)
            .shadow(
                elevation = 1.dp,
                shape = cardShape,
                clip = false
            )
            .border(width = 1.dp, color = cardBorder, shape = cardShape)
            .clip(cardShape)
            .clickable(onClick = onClick),
        color = cardBackground,
        shape = cardShape,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Colorful rounded protocol badge, echoing the app's accent color.
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(accent, accent.copy(alpha = 0.65f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    typeDescription.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(remarks, Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge.copy(lineBreak = LineBreak.Paragraph, fontWeight = FontWeight.SemiBold), maxLines = 2, overflow = TextOverflow.Ellipsis)
                    if (doubleColumnDisplay) {
                        IconButton(onClick = onMore, Modifier.size(32.dp)) {
                            Icon(painterResource(R.drawable.ic_more_vert_24dp), null, Modifier.size(20.dp))
                        }
                    } else {
                        IconButton(onClick = onShare, Modifier.size(32.dp)) { Icon(painterResource(R.drawable.ic_share_24dp), null, Modifier.size(20.dp)) }
                        IconButton(onClick = onEdit, Modifier.size(32.dp)) { Icon(painterResource(R.drawable.ic_edit_24dp), null, Modifier.size(20.dp)) }
                        IconButton(onClick = onRemove, Modifier.size(32.dp)) { Icon(painterResource(R.drawable.ic_delete_24dp), null, Modifier.size(20.dp)) }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    if (subscriptionRemarks.isNotBlank()) {
                        Box(Modifier.size(20.dp).clip(CircleShape).background(accent.copy(alpha = 0.18f)), Alignment.Center) {
                            Text(subscriptionRemarks.take(1).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = accent)
                        }
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(statistics, Modifier.weight(1f), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = colorConfigType().copy(alpha = 0.14f)
                    ) {
                        Text(
                            typeDescription,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = colorConfigType(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Text(testResult, style = MaterialTheme.typography.labelSmall, color = if (testDelayMillis < 0L) colorPingRed else colorPing(), maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                if (trafficUploadBytes > 0L || trafficDownloadBytes > 0L) {
                    Text(
                        "${trafficUploadBytes.toTrafficString()}↑ ${trafficDownloadBytes.toTrafficString()}↓",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private fun getProtocolDescription(profile: ProfileItem): String {
    if (profile.configType.isComplexType()) return profile.configType.name
    val parts = mutableListOf(profile.configType.name)
    profile.network?.let { net ->
        if (net.isNotBlank() && !net.equals("tcp", ignoreCase = true)) parts.add(net)
    }
    profile.security?.let { sec ->
        if (sec.isNotBlank()) {
            if (profile.insecure == true && sec.equals("tls", ignoreCase = true)) {
                parts.add("$sec insecure")
            } else {
                parts.add(sec)
            }
        }
    }
    return parts.joinToString(" / ")
}

internal suspend fun PagerState.navigateToPageOptimized(
    targetPage: Int,
    animateAdjacentPage: Boolean = true
) {
    if (pageCount <= 0) return
    val target = targetPage.coerceIn(0, pageCount - 1)
    val current = settledPage.coerceIn(0, pageCount - 1)
    if (target == current) return

    if (abs(target - current) == 1 && animateAdjacentPage) {
        animateScrollToPage(target)
    } else {
        scrollToPage(target)
    }
}
