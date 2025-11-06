package edu.farmingdale.datastoredemo.ui

import android.util.Log
import android.widget.Switch
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.farmingdale.datastoredemo.R

import edu.farmingdale.datastoredemo.data.local.LocalEmojiData

/*
 * Screen level composable
 */

//Storage for emojis and their corresponding descriptions
var Emojis=HashMap<String, String>()

@Composable
fun EmojiReleaseApp(
    emojiViewModel: EmojiScreenViewModel = viewModel(
        factory = EmojiScreenViewModel.Factory
    )
) {
    addEmojis()
    EmojiScreen(
        vm=emojiViewModel,
        uiState = emojiViewModel.uiState.collectAsState().value,
        themeState = emojiViewModel.themeState.collectAsState().value,
        selectLayout = emojiViewModel::selectLayout,
        selectTheme=emojiViewModel::selectTheme,
    )
}
//Add data to the HashMap
fun addEmojis(){
        var i=0
        for (item in LocalEmojiData.EmojiList) {
            Emojis.put(item, LocalEmojiData.EmojiTitleList.get(i))
            i++
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmojiScreen(
    vm: EmojiScreenViewModel,
    uiState: EmojiReleaseUiState,
    themeState: EmojiReleaseThemeState,
    selectLayout: (Boolean) -> Unit,
    selectTheme: (Boolean) -> Unit
) {
    val isLinearLayout = uiState.isLinearLayout
    val isDarkTheme=themeState.isDarkTheme
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.top_bar_name)) },
                actions = {
                    IconButton(
                        onClick = {
                            selectLayout(!isLinearLayout)
                        }
                    ) {
                        Icon(
                            painter = painterResource(uiState.toggleIcon),
                            contentDescription = stringResource(uiState.toggleContentDescription),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    //This add a switch that changes to dark mode when clicked on and
                    //turns back to light mode when turned off
                    Switch(checked = isDarkTheme, onCheckedChange = {selectTheme(!isDarkTheme)})
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    //Changes app bar background color to dark theme
                    containerColor = if(isDarkTheme) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.inversePrimary
                     //containerColor = vm.appBarColor
                )
            )
        }
        //Changes background color to dark theme
    , containerColor = themeState.backgroundColor) { innerPadding ->
        val modifier = Modifier
            .padding(
                top = dimensionResource(R.dimen.padding_medium),
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium),
            )
        if (isLinearLayout) {
            EmojiReleaseLinearLayout(
                modifier = modifier.fillMaxWidth(),
                contentPadding = innerPadding,
                isDarkTheme=isDarkTheme
            )
        } else {
            EmojiReleaseGridLayout(
                modifier = modifier,
                contentPadding = innerPadding,
                isDarkTheme=isDarkTheme
            )
        }
    }
}

@Composable
fun EmojiReleaseLinearLayout(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    isDarkTheme:Boolean
) {
    val cntxt = LocalContext.current
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
    ) {
        items(
            items = LocalEmojiData.EmojiList,
            key = { e -> e }
        ) { e ->
            Card(
                colors = CardDefaults.cardColors(
                    //Changes emoji background color to dark theme
                    containerColor = if(isDarkTheme) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.medium,
                //This shows a description of the emoji when it is clicked on
                modifier=Modifier.clickable { Toast.makeText(cntxt, Emojis.get(e), Toast.LENGTH_SHORT).show()}
            ) {
                    Text(
                        text = e, fontSize = 50.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(dimensionResource(R.dimen.padding_medium)),
                        textAlign = TextAlign.Center
                    )


            }
        }
    }
}

@Composable
fun EmojiReleaseGridLayout(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    isDarkTheme: Boolean
) {
    val cntxt = LocalContext.current
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(3),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
        horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
    ) {
        items(
            items = LocalEmojiData.EmojiList,
            key = { e -> e }
        ) { e ->
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if(isDarkTheme) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.height(110.dp).clickable { Toast.makeText(cntxt, Emojis.get(e), Toast.LENGTH_SHORT).show()},
                shape = MaterialTheme.shapes.medium,
                //This shows a description of the emoji when it is clicked on
            ) {
                Text(
                    text = e, fontSize = 50.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight(Alignment.CenterVertically)
                        .padding(dimensionResource(R.dimen.padding_small))
                        .align(Alignment.CenterHorizontally),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
