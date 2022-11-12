package com.bcc.exporeal

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.SnackbarData
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bcc.exporeal.repository.AppRepository
import com.bcc.exporeal.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

lateinit var SnackbarListener: @Composable (word: String, state: MutableState<Boolean>) -> SnackbarData?
@SuppressLint("StaticFieldLeak")
lateinit var navController: NavHostController

@AndroidEntryPoint
open class ExporealActivity : ComponentActivity() {
    @Inject
    lateinit var repository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel by viewModels()
            navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            SnackbarListener = { word, state ->
                val snackbarHostState = scaffoldState.snackbarHostState

                if (state.value) {
                    LaunchedEffect(snackbarHostState) {
                        val result = snackbarHostState.showSnackbar(
                            word, duration = SnackbarDuration.Short, actionLabel = "Close"
                        )

                        when (result) {
                            SnackbarResult.Dismissed -> state.value = false
                            SnackbarResult.ActionPerformed -> state.value = false
                        }
                    }
                }

                snackbarHostState.currentSnackbarData
            }

            ExporealContent(
                navController = navController,
                mainViewModel = mainViewModel,
                scaffoldState = scaffoldState,
                repository = repository
            )
        }
    }
}