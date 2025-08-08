package com.clinic.app.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.clinic.app.R
import com.clinic.app.presentation.appointments.AppointmentsScreen
import com.clinic.app.presentation.patients.PatientsScreen
import com.clinic.app.presentation.search.QuickSearchScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem(
            route = "search",
            icon = Icons.Default.Search,
            label = "البحث السريع"
        ),
        BottomNavItem(
            route = "patients",
            icon = Icons.Default.Person,
            label = stringResource(R.string.nav_patients)
        ),
        BottomNavItem(
            route = "appointments",
            icon = Icons.Default.DateRange,
            label = stringResource(R.string.nav_appointments)
        ),
        BottomNavItem(
            route = "chat",
            icon = Icons.Default.Chat,
            label = stringResource(R.string.nav_chat)
        )
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "search",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("search") {
                QuickSearchScreen(
                    onPatientSelected = { patient ->
                        // الانتقال إلى تفاصيل المريض
                        navController.navigate("patient_details/${patient.id}")
                    },
                    onAddNewPatient = { searchQuery ->
                        // الانتقال إلى شاشة إضافة مريض جديد
                        navController.navigate("add_patient?name=$searchQuery")
                    },
                    onAddQuickAppointment = { searchQuery ->
                        // الانتقال إلى شاشة إضافة موعد سريع
                        navController.navigate("quick_appointment?query=$searchQuery")
                    }
                )
            }

            composable("patients") {
                PatientsScreen(
                    onPatientClick = { patient ->
                        navController.navigate("patient_details/${patient.id}")
                    },
                    onAddPatient = {
                        navController.navigate("add_patient")
                    },
                    onAddAppointment = { patient ->
                        navController.navigate("add_appointment?patientId=${patient.id}")
                    }
                )
            }

            composable("appointments") {
                AppointmentsScreen(
                    onAppointmentClick = { appointment ->
                        navController.navigate("appointment_details/${appointment.appointment.id}")
                    },
                    onAddAppointment = {
                        navController.navigate("add_appointment")
                    }
                )
            }

            composable("chat") {
                // شاشة قائمة الدردشات
                ChatListScreen(
                    onChatClick = { chatRoomId, otherUserName ->
                        navController.navigate("chat_room/$chatRoomId?otherUserName=$otherUserName")
                    }
                )
            }

            // شاشات إضافية
            composable("patient_details/{patientId}") { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
                PatientDetailsScreen(
                    patientId = patientId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("add_patient?name={name}") { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: ""
                AddPatientScreen(
                    initialName = name,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable("chat_room/{chatRoomId}?otherUserName={otherUserName}") { backStackEntry ->
                val chatRoomId = backStackEntry.arguments?.getString("chatRoomId") ?: ""
                val otherUserName = backStackEntry.arguments?.getString("otherUserName") ?: ""
                ChatScreen(
                    chatRoomId = chatRoomId,
                    currentUserId = "current_user_id", // يجب الحصول عليه من AuthRepository
                    otherUserName = otherUserName
                )
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val label: String
)

// شاشات مؤقتة للتطوير
@Composable
fun ChatListScreen(onChatClick: (String, String) -> Unit) {
    // تطبيق مؤقت لقائمة الدردشات
    Text("قائمة الدردشات - قيد التطوير")
}

@Composable
fun PatientDetailsScreen(patientId: String, onNavigateBack: () -> Unit) {
    // تطبيق مؤقت لتفاصيل المريض
    Text("تفاصيل المريض: $patientId - قيد التطوير")
}

@Composable
fun AddPatientScreen(initialName: String, onNavigateBack: () -> Unit) {
    // تطبيق مؤقت لإضافة مريض
    Text("إضافة مريض: $initialName - قيد التطوير")
}

