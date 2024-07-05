import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.ionspin.kotlin.bignum.integer.BigInteger
import idle_game.composeapp.generated.resources.Res
import idle_game.composeapp.generated.resources.image1
import idle_game.composeapp.generated.resources.image5
import idle_game.composeapp.generated.resources.image66
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
import util.gelds
import util.toHumanReadableString
import vw.GameViewModel

@Composable
@Preview
fun App() {
    MaterialTheme {
        Screen()
    }
}

@Composable
@Preview
fun Screen() {
    Scaffold(
        content = {
            val coroutineScope = rememberCoroutineScope()
            val viewModel by remember {
                mutableStateOf(
                    GameViewModel(
                        scope = coroutineScope,
                    )
                )
            }
            DisposableEffect(viewModel) {
                onDispose {
                    viewModel.clear()
                }
            }

            val gameState: GameState? by viewModel.gameState.collectAsState()
            val currentMoney: Gelds? by remember(gameState) {
                derivedStateOf { gameState?.stashedMoney }
            }
            var showDialog by remember { mutableStateOf(false) }
            var isBoxingGlovesRed by remember { mutableStateOf(true) }

            Image(
                painterResource(Res.drawable.image1),
                contentScale = ContentScale.Crop,
                contentDescription = "A square",
                modifier = Modifier.fillMaxWidth().fillMaxHeight()
            )


            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {

                Button(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        backgroundColor = Color.Magenta
                    )
                ) {
                    Text(
                        "Show Dialog"
                    )
                }
                if (showDialog) {
                    MinimalDialog {
                        showDialog = false
                    }
                }


                Text(
                    "Box-Until-Death",
                    style = MaterialTheme.typography.h1, color = Color.White,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.White,
                        backgroundColor = Color.Red
                    ),
                ) {
                    Text("Reset Game")
                }

                gameState?.let { state ->
                    Text(
                        "Konto: ${currentMoney?.toHumanReadableString()} Besiegte Gegner",
                        style = MaterialTheme.typography.h4, color = Color.White,
                    )
                    Button(
                        onClick = {
                            if (isBoxingGlovesRed) {
                                viewModel.clickMoney(state, 1.gelds) // Call the function
                            } else {
                                viewModel.clickMoney(state, 5.gelds) // Call the function

                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(153, 204, 0)),
                        modifier = Modifier.width(300.dp).height(200.dp)
                    ) {
                        if (isBoxingGlovesRed) {
                            Image(
                                painterResource(Res.drawable.image5),
                                contentDescription = "A square",
                                modifier = Modifier.fillMaxWidth().fillMaxHeight()
                            )
                        } else {
                            Image(
                                painterResource(Res.drawable.image66), // TODO: mit grünem Boxhandschuh ersetzen
                                contentDescription = "A square",
                                modifier = Modifier.fillMaxWidth().fillMaxHeight()
                            )
                        }
                    }

                    Button(
                        onClick = { isBoxingGlovesRed = false },
                        enabled = (currentMoney ?: 0) >= 1000,
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0, 204, 0)),
                        modifier = Modifier.padding(top = 10.dp).width(270.dp).height(100.dp),
                    ) {
                        Text("Buy a New Weapon: 1000$", color = Color.White)
                    }

                    state.availableJobs.forEach { availableJob ->
                        Generator(
                            gameJob = availableJob,
                            alreadyBought = state.workers.any { it.jobId == availableJob.id },
                            onBuy = { viewModel.addWorker(state, availableJob) },
                            onUpgrade = { viewModel.upgradeJob(state, availableJob) }
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun Generator(
    gameJob: GameJob,
    alreadyBought: Boolean,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
    onUpgrade: () -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .padding(8.dp)
            .background(Color(255, 140, 26), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(modifier = modifier.background(Color(255, 255, 0))) {

            Text("Automatic Box-Handschuh ${gameJob.id}")
            Text("Level: ${gameJob.level.level}")
            Text("Costs: ${gameJob.level.cost.toHumanReadableString()} Dollar")
            Text("Earns: ${gameJob.level.earn.toHumanReadableString()} Dollar")
            Text("Duration: ${gameJob.level.duration.inWholeSeconds} Seconds")
        }
        if (!alreadyBought) {
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.Black,
                    backgroundColor = Color(0, 204, 0)
                ),
            ) {
                Text("Buy")
            }
        } else {
            Text("Bought")
        }
        Button(
            onClick = onUpgrade, colors = ButtonDefaults.buttonColors(
                contentColor = Color.Black,
                backgroundColor = Color(0, 204, 255)
            )
        ) {
            Text("Upgrade", color = Color.Black)

        }
    }
}


@Composable
fun MinimalDialog(onDismissRequest: () -> Unit) {
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                text = " Info.\n" +
                        "  um Punkte zu sammeln klicken sie auf\n" +
                        "  die Boxhandschuhe im hell-Grünen\n" +
                        "   Kästchen. Sie werden punkte \n" +
                        "   verdinen und damit neue waffen kaufen oder upgraden \n" +
                        "\n",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                textAlign = TextAlign.Center,
            )
        }
    }
}