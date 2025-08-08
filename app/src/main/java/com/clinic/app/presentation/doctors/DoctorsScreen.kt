package com.clinic.app.presentation.doctors

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.clinic.app.R
import com.clinic.app.presentation.components.DoctorItem
import com.clinic.domain.model.Doctor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorsScreen(
    onDoctorClick: (Doctor) -> Unit,
    onAddDoctor: () -> Unit,
    onEditDoctor: (Doctor) -> Unit,
    viewModel: DoctorsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showInactiveOnly by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery, showInactiveOnly) {
        viewModel.searchDoctors(searchQuery, showInactiveOnly)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // العنوان والأزرار
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.nav_doctors),
                style = MaterialTheme.typography.headlineMedium
            )
            
            Row {
                // زر الفلترة
                FilterChip(
                    onClick = { showInactiveOnly = !showInactiveOnly },
                    label = { 
                        Text(if (showInactiveOnly) "غير نشط" else "نشط") 
                    },
                    selected = showInactiveOnly,
                    leadingIcon = {
                        Icon(
                            imageVector = if (showInactiveOnly) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                FloatingActionButton(
                    onClick = onAddDoctor,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add_doctor)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // شريط البحث
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("البحث في الأطباء...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null
                )
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // إحصائيات سريعة
        if (uiState.statistics != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatisticItem(
                        title = "إجمالي الأطباء",
                        value = uiState.statistics.totalDoctors.toString(),
                        icon = Icons.Default.Person
                    )
                    StatisticItem(
                        title = "نشط",
                        value = uiState.statistics.activeDoctors.toString(),
                        icon = Icons.Default.CheckCircle
                    )
                    StatisticItem(
                        title = "التخصصات",
                        value = uiState.statistics.specializations.toString(),
                        icon = Icons.Default.Category
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // قائمة الأطباء
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.doctors.isEmpty() && searchQuery.isNotBlank() -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لم يتم العثور على أطباء",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا يوجد أطباء يطابقون البحث \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            uiState.doctors.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا يوجد أطباء",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ابدأ بإضافة طبيب جديد",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onAddDoctor) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إضافة طبيب جديد")
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.doctors) { doctor ->
                        DoctorItem(
                            doctor = doctor,
                            onClick = { onDoctorClick(doctor) },
                            onEdit = { onEditDoctor(doctor) },
                            onToggleStatus = { 
                                if (doctor.isActive) {
                                    viewModel.deactivateDoctor(doctor.id)
                                } else {
                                    viewModel.activateDoctor(doctor.id)
                                }
                            }
                        )
                    }
                }
            }
        }

        // عرض الأخطاء
        uiState.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = error,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
private fun StatisticItem(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

