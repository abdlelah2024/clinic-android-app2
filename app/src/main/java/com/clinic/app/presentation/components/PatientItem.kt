package com.clinic.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.clinic.domain.model.Patient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientItem(
    patient: Patient,
    onClick: () -> Unit,
    onAddAppointment: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // صورة المريض أو الأحرف الأولى
            if (patient.avatar.isNotEmpty()) {
                AsyncImage(
                    model = patient.avatar,
                    contentDescription = patient.name,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = patient.getInitials(),
                            color = MaterialTheme.colorScheme.onPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // معلومات المريض
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = patient.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (patient.phone.isNotEmpty()) {
                    Text(
                        text = patient.phone,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (patient.age > 0) {
                    Text(
                        text = "${patient.age} سنة",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (patient.lastVisit.isNotEmpty()) {
                    Text(
                        text = "آخر زيارة: ${patient.lastVisit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // زر إضافة موعد
            IconButton(
                onClick = onAddAppointment
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "إضافة موعد",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

