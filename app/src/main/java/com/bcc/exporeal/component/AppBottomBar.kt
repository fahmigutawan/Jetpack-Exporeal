package com.bcc.exporeal.component

import androidx.compose.animation.*
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bcc.exporeal.R
import com.bcc.exporeal.navigation.AppNavRoute
import com.bcc.exporeal.ui.style.AppColor

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppBottomBar(
    onItemClicked: (route: String) -> Unit,
    currentRoute: String
) {
    val itemWidth = LocalConfiguration.current.screenWidthDp / BottomBarItem.values().size
    BottomAppBar(
        backgroundColor = AppColor.Neutral10
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomBarItem.values().forEach {
                AnimatedContent(
                    targetState = currentRoute.equals(it.route),
                    transitionSpec = {
                        fadeIn(tween(200)) with fadeOut(tween(200))
                    }
                ) { onSameRoute ->
                    when {
                        onSameRoute -> {
                            Box(
                                modifier = Modifier
                                    .width(itemWidth.dp)
                                    .clickable { onItemClicked(it.route) },
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                // Menu Item
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    horizontalAlignment = CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        modifier = Modifier.size(24.dp), model = it.iconSelectedId,
                                        contentDescription = "Icon"
                                    )
                                    AppText(
                                        text = it.word,
                                        textType = TextType.BottomMenu,
                                        color = AppColor.Blue60
                                    )
                                }

                                // Underline
                                Box(
                                    modifier = Modifier
                                        .size(width = itemWidth.dp, height = 2.dp)
                                        .background(AppColor.Blue60)
                                )
                            }
                        }
                        else -> {
                            Box(
                                modifier = Modifier
                                    .width(itemWidth.dp)
                                    .clickable { onItemClicked(it.route) },
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                // Menu Item
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    horizontalAlignment = CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    AsyncImage(
                                        modifier = Modifier.size(24.dp),
                                        model = it.iconUnselectedId,
                                        contentDescription = "Icon"
                                    )
                                    AppText(
                                        text = it.word,
                                        textType = TextType.BottomMenu,
                                        color = AppColor.Neutral50
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

enum class BottomBarItem(
    val route: String, val iconSelectedId: Int, val iconUnselectedId: Int, val word: String
) {
    Home(
        route = AppNavRoute.HomeScreen.name,
        iconSelectedId = R.drawable.ic_botmenu_home_selected,
        iconUnselectedId = R.drawable.ic_botmenu_home_unselected,
        word = "Home"
    ),
    Market(
        route = AppNavRoute.MarketScreen.name,
        iconSelectedId = R.drawable.ic_botmenu_market_selected,
        iconUnselectedId = R.drawable.ic_botmenu_market_unselected,
        word = "Market"
    ),
    Pelatihan(
        route = AppNavRoute.PelatihanScreen.name,
        iconSelectedId = R.drawable.ic_botmenu_training_selected,
        iconUnselectedId = R.drawable.ic_botmenu_training_unselected,
        word = "Pelatihan"
    ),
    Profile(
        route = AppNavRoute.ProfileScreen.name,
        iconSelectedId = R.drawable.ic_botmenu_profile_selected,
        iconUnselectedId = R.drawable.ic_botmenu_profile_unselected,
        word = "Profile"
    )
}