package com.clinic.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.clinic.app.presentation.theme.*
import com.clinic.domain.model.AppointmentStatus
import com.clinic.domain.model.EnrichedAppointment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentItem(
    appointment: EnrichedAppointment,
    onClick: () -> Unit,
    onStatusChange: (AppointmentStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var showStatusMenu by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // معلومات المريض والطبيب
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // صورة المريض
                if (appointment.patient.avatar.isNotEmpty()) {
                    AsyncImage(
                        model = appointment.patient.avatar,
                        contentDescription = appointment.patient.name,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = appointment.patient.getInitials(),
                                color = MaterialTheme.colorScheme.onPrimary,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = appointment.patient.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "د. ${appointment.doctor.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // حالة الموعد
                Box {
                    AssistChip(
                        onClick = { showStatusMenu = true },
                        label = { 
                            Text(
                                text = getStatusText(appointment.appointment.status),
                                style = MaterialTheme.typography.labelMedium
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = getStatusColor(appointment.appointment.status),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        AppointmentStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(getStatusText(status)) },
                                onClick = {
                                    onStatusChange(status)
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // تفاصيل الموعد
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${appointment.appointment.date} - ${appointment.appointment.startTime}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (appointment.appointment.cost > 0) {
                    Text(
                        text = "${appointment.appointment.cost} ر.س",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (appointment.appointment.reason.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = appointment.appointment.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun getStatusColor(status: AppointmentStatus): androidx.compose.ui.graphics.Color {
    return when (status) {
        AppointmentStatus.SCHEDULED -> StatusScheduled.copy(alpha = 0.2f)
        AppointmentStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.2f)
        AppointmentStatus.CANCELED -> StatusCanceled.copy(alpha = 0.2f)
        AppointmentStatus.WAITING -> StatusWaiting.copy(alpha = 0.2f)
        AppointmentStatus.RETURN -> StatusReturn.copy(alpha = 0.2f)
    }
}

private fun getStatusText(status: AppointmentStatus): String {
    return when (status) {
        AppointmentStatus.SCHEDULED -> "مجدول"
        AppointmentStatus.COMPLETED -> "مكتمل"
        AppointmentStatus.CANCELED -> "ملغي"
        AppointmentStatus.WAITING -> "منتظر"
        AppointmentStatus.RETURN -> "عودة"
    }
}

