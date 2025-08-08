package com.clinic.app.presentation.users

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
import com.clinic.app.presentation.components.UserItem
import com.clinic.domain.model.User
import com.clinic.domain.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersScreen(
    onUserClick: (User) -> Unit,
    onAddUser: () -> Unit,
    onEditUser: (User) -> Unit,
    onManagePermissions: (User) -> Unit,
    viewModel: UsersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf<UserRole?>(null) }
    var showInactiveOnly by remember { mutableStateOf(false) }

    LaunchedEffect(searchQuery, selectedRole, showInactiveOnly) {
        viewModel.searchUsers(searchQuery, selectedRole, showInactiveOnly)
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
                text = stringResource(R.string.nav_users),
                style = MaterialTheme.typography.headlineMedium
            )
            
            FloatingActionButton(
                onClick = onAddUser,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_user)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // شريط البحث
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("البحث في المستخدمين...") },
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

        // فلاتر
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
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
            }
            
            items(UserRole.values()) { role ->
                FilterChip(
                    onClick = { 
                        selectedRole = if (selectedRole == role) null else role
                    },
                    label = { Text(role.displayName) },
                    selected = selectedRole == role,
                    leadingIcon = {
                        Icon(
                            imageVector = getRoleIcon(role),
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // إحصائيات سريعة
        if (uiState.statistics != null) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "إحصائيات المستخدمين",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatisticItem(
                            title = "إجمالي",
                            value = uiState.statistics.totalUsers.toString(),
                            icon = Icons.Default.People
                        )
                        StatisticItem(
                            title = "نشط",
                            value = uiState.statistics.activeUsers.toString(),
                            icon = Icons.Default.CheckCircle
                        )
                        StatisticItem(
                            title = "متصل",
                            value = uiState.statistics.onlineUsers.toString(),
                            icon = Icons.Default.Circle
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }

        // قائمة المستخدمين
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.users.isEmpty() && searchQuery.isNotBlank() -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لم يتم العثور على مستخدمين",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لا يوجد مستخدمين يطابقون البحث \"$searchQuery\"",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            uiState.users.isEmpty() -> {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "لا يوجد مستخدمين",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "ابدأ بإضافة مستخدم جديد",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onAddUser) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إضافة مستخدم جديد")
                        }
                    }
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.users) { user ->
                        UserItem(
                            user = user,
                            onClick = { onUserClick(user) },
                            onEdit = { onEditUser(user) },
                            onManagePermissions = { onManagePermissions(user) },
                            onToggleStatus = { 
                                if (user.isActive) {
                                    viewModel.deactivateUser(user.id)
                                } else {
                                    viewModel.activateUser(user.id)
                                }
                            },
                            onResetPassword = { viewModel.resetUserPassword(user.id) }
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

private fun getRoleIcon(role: UserRole): androidx.compose.ui.graphics.vector.ImageVector {
    return when (role) {
        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
        UserRole.DOCTOR -> Icons.Default.LocalHospital
        UserRole.NURSE -> Icons.Default.MedicalServices
        UserRole.RECEPTIONIST -> Icons.Default.Person
        UserRole.ACCOUNTANT -> Icons.Default.AccountBalance
    }
}

