package com.avis.app.ptalk.ui.screen.device

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.avis.app.ptalk.domain.model.DeviceChatLog
import com.avis.app.ptalk.domain.util.DateTimeUtil
import com.avis.app.ptalk.navigation.Route
import com.avis.app.ptalk.ui.component.appbar.BaseTopAppBar
import com.avis.app.ptalk.ui.viewmodel.VMDeviceChatLog
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import kotlin.collections.sortedDescending


@Composable
fun DeviceChatLogScreen(
    navController: NavController,
    vm: VMDeviceChatLog = viewModel()
) {
    val uiState = vm.uiState.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        vm.loadChatLog()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BaseTopAppBar(
            title = "Lịch sử trò chuyện",
            onBack = { navController.popBackStack() }
        )
        Spacer(modifier = Modifier.padding(8.dp))
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp)
        ) {
            // Search bar
            SearchFilterBar(
                query = "",
                onQueryChange = { },
                onFilterClick = {
                    // TODO: open date/month/year picker bottom sheet or dialog (UI-only for now)
                }
            )
            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val groupedMap = groupChatLogs(uiState.chatLogs)
                val months = groupedMap.keys
                if (months.isEmpty()) {
                    item {
                        Text(
                            "Không có dữ liệu",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    return@LazyColumn
                }

                months.forEach { yearMonth ->
                    val dates = groupedMap[yearMonth].orEmpty().sortedDescending()
                    stickyHeader {
                        Text(
                            text = "Tháng ${yearMonth.monthValue}/${yearMonth.year}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }

                    items(items = dates, key = { it.toString() }) { date ->
                        DateRow(
                            label = formatDate(date),
                            onClick = {
                                navController.navigate(Route.DEVICE_CHATDETAIL)
                            }
                        )
                    }
                }

            }
        }
    }
}

@Composable
private fun SearchFilterBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth(),
        placeholder = { Text("Tìm kiếm theo ngày, tháng, năm...") },
        singleLine = true,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
        trailingIcon = {
            IconButton(onClick = onFilterClick) {
                Icon(Icons.Filled.FilterList, contentDescription = "Filter")
            }
        },
        shape = RoundedCornerShape(20.dp),
    )
}

@Composable
private fun DateRow(
    label: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .height(48.dp)
            .fillMaxWidth(),
        onClick = { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Open")
        }
    }
}

private fun groupChatLogs(chatLogs: List<DeviceChatLog>): Map<YearMonth, List<LocalDate>> {
    if (chatLogs.isEmpty()) return emptyMap()

    val daysDesc = chatLogs.asSequence().map { log ->
            val ldt = DateTimeUtil.unixTimeToDateTime((log.getTimestamp()))
            ldt.toLocalDate()
        }
        .distinct()                 // 1 row per date
        .sortedDescending()         // newest date first
        .toList()

    return daysDesc.groupBy { YearMonth.from(it) }
        .mapValues { (_, dates) -> dates.sortedDescending() }
}

private fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return date.format(formatter)
}