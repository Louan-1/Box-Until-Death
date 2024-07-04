import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import idle_game.composeapp.generated.resources.Res
import idle_game.composeapp.generated.resources.image1
import idle_game.composeapp.generated.resources.image5
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import util.Gelds
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





                Text(
                    "Box-Until-Death",
                    style = MaterialTheme.typography.h1, color = Color.White,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.reset() },
                            colors = ButtonDefaults.buttonColors(
                                contentColor = Color.White,
                                backgroundColor = Color.Red),
                ) {
                    Text("Reset Game")
                }

                gameState?.let { state ->
                    Text(
                        "Konto: ${currentMoney?.toHumanReadableString()} Besiegte Gegner",
                        style = MaterialTheme.typography.h4,color  = Color.White,
                    )
                    Button(
                        onClick = { viewModel.clickMoney(state) },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(153, 204, 0)),
                        modifier = Modifier.width(300.dp).height(200.dp)
                    ) {
                        Image(
                            painterResource(Res.drawable.image5,),
                            contentDescription = "A square",
                            modifier = Modifier.fillMaxWidth().fillMaxHeight())
                    }

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color(0, 204, 0)),
                        modifier = Modifier.padding(top=10.dp).width(270.dp).height(100.dp),
                     ) {
                        Text("Buy a New Weapon",color=Color.White)
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
        Column (modifier = modifier.background(Color(255, 140, 26))) {

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
                    backgroundColor = Color.Yellow),
                ) {
                Text("Buy")
            }
        } else {
            Text("Bought")
        }
        Button(onClick = onUpgrade, colors = ButtonDefaults.buttonColors(
            contentColor = Color.Black,
            backgroundColor = Color.Yellow)) {
            Text("Upgrade",color = Color.Black)

        }
    }
}