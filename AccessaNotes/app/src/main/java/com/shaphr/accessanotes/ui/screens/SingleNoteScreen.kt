package com.shaphr.accessanotes.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.shaphr.accessanotes.data.models.UiNote
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.shaphr.accessanotes.R
import com.shaphr.accessanotes.data.database.Note
import com.shaphr.accessanotes.ui.viewmodels.NoteRepositoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleNoteScreen(
    noteID: Int,
    navController: NavHostController,
    viewModel: NoteRepositoryViewModel = hiltViewModel()
) {
    val note = viewModel.getNote(noteID).collectAsState(initial = Note()).value
    var ttsButtonText by remember { mutableStateOf("Read Summarized Notes") }
    val screenHeight = (LocalConfiguration.current.screenHeightDp).dp
    var transcriptHeight by remember { mutableStateOf(screenHeight * 0.35F) }
    var summaryHeight by remember { mutableStateOf(screenHeight * 0.35F) }

    val text = listOf(
        "The narrator introduces four groups of characters: Cinderella, who wishes to attend the king's festival; Jack who wishes his cow, Milky White, would give milk; a baker and his wife, who wish to have a child; and Little Red Ridinghood,[3] who wishes for bread to bring for her grandmother.",
        "The baker's neighbor, an ugly old witch, reveals the couple is infertile from a curse she cast on his father for stealing her vegetables, including magic beans, which prompted the Witch's own mother to punish her with the curse of age and ugliness. The witch took the baker's father's child, Rapunzel. She explains the curse will be lifted if she is brought four ingredients – \"the cow as white as milk, the cape as red as blood, the hair as yellow as corn, and the slipper as pure as gold\" – in three days' time. All begin the journey into the woods: Jack to sell his beloved cow; Cinderella to her mother's grave; Little Red to her grandmother's house; and the baker, refusing his wife's help, to find the ingredients (\"Prologue\").",
        "Cinderella receives a gown and golden slippers from her mother's spirit (\"Cinderella at the Grave\"). A mysterious man mocks Jack for valuing his cow more than a \"sack of beans\". Little Red meets a hungry wolf, who persuades her to take a longer path and admire the beauty of the woods, with his own ulterior motives in mind. (\"Hello, Little Girl\") The baker, followed by his wife, meets Jack. They convince Jack that the beans found in the baker's father's jacket are magic and trade them for the cow; Jack bids Milky White a tearful farewell (\"I Guess This Is Goodbye\"). The baker has qualms about their deceit, but his wife reassures him (\"Maybe They're Magic\").",
        "The witch has raised Rapunzel in a tall tower accessible only by climbing Rapunzel's long, golden hair (\"Our Little World\"); a prince spies Rapunzel. The baker, in pursuit of the red cape (\"Maybe They're Magic\" Reprise), slays the wolf and rescues Little Red and her grandmother. Little Red rewards him with her cape, and reflects on her experiences (\"I Know Things Now\"). Jack's mother tosses his beans aside, which grow into an enormous stalk. Cinderella flees the festival, pursued by another prince, and the baker's wife hides her; asked about the ball, Cinderella is unimpressed (\"A Very Nice Prince\"). Spotting Cinderella's gold slippers, the baker's wife chases her and loses Milky White. The characters recite morals as the day ends (\"First Midnight\").",
        "Jack describes his adventure climbing the beanstalk (\"Giants in the Sky\"). He gives the baker gold stolen from the giants to buy back his cow, and returns up the beanstalk to find more; the mysterious man steals the money. Cinderella's prince and Rapunzel's prince, who are brothers, compare their unobtainable amours (\"Agony\"). The baker's wife overhears their talk of a girl with golden hair. She fools Rapunzel and takes a piece of her hair. The mysterious man returns Milky White to the baker.",
        "The baker's wife again fails to seize Cinderella's slippers. The baker admits they must work together (\"It Takes Two\"). Jack arrives with a hen that lays golden eggs, but Milky White keels over dead as midnight chimes (\"Second Midnight\"). The Witch discovers the prince's visits and demands Rapunzel stay sheltered from the world (\"Stay with Me\"). Rapunzel refuses, and the witch cuts off Rapunzel's hair and banishes her. The mysterious man gives the baker money for another cow. Jack meets Little Red, now sporting a wolfskin cape and knife. She goads him into returning to the giant's home to retrieve the giant's harp.",
        "Cinderella, torn between staying with her prince or escaping, leaves him a slipper as a clue (\"On the Steps of the Palace\"), and trades shoes with the baker's wife. The baker arrives with another cow; they now have all four items. A great crash is heard, and Jack's mother reports a dead giant in her backyard. Jack returns with a magic harp. The witch discovers the new cow is useless, and resurrects Milky White, who is fed the ingredients but fails to give milk. The witch explains Rapunzel's hair will not work, and the mysterious man offers corn silk instead; Milky White produces the potion. The witch reveals the mysterious man is the baker's father, and she drinks the potion. The mysterious man falls dead, the curse is broken, and the witch regains her youth and beauty.",
        "Cinderella's prince seeks the girl who fits the slipper; Cinderella's desperate stepsisters mutilate their feet (\"Careful My Toe\"). Cinderella succeeds and becomes his bride. Rapunzel bears twins and is found by her prince. The witch finds her, and attempts to claim her back, but the witch's powers have been lost in exchange for her youth and beauty. At Cinderella's wedding, her stepsisters are blinded by birds, and the baker's wife, now very pregnant, thanks Cinderella for her help (\"So Happy\" Prelude). Congratulating themselves on living \"happily ever after,\" the characters fail to notice another beanstalk growing."
    )
    val bmp1 = Bitmap.createBitmap(1200, 400, Bitmap.Config.ARGB_8888)
    bmp1.eraseColor(-16711681)
    val bmp2 = Bitmap.createBitmap(1200, 400, Bitmap.Config.ARGB_8888)
    bmp2.eraseColor(-65281)
    val content =
        text.slice(0..1) + listOf(bmp1) + text.slice(2..4) + listOf(bmp2) + text.slice(5..6)

    LaunchedEffect(noteID) {
        viewModel.getNote(noteID).collect { value ->
            note.value = value
        }
    }

    Column(Modifier.fillMaxSize()) {
        // Header
        Box(
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
        ) {
            // Back button
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .clickable { navController.popBackStack() }
            )
            //Text
            Text(
                text = note.value?.title ?: "default",
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.Center)

            )
            // Share icon
//            Icon(
//                Icons.Default.Share,
//                contentDescription = "Share",
//                tint = Color.Black,
//                modifier = Modifier
//                    .align(alignment = Alignment.TopEnd)
//                    .padding(16.dp)
//            )
        }

        // Body
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.White)
        ) {
            item {
                Text(
                    text = "Transcribed Text",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 16.sp
                )

                LazyColumn(
                    modifier = Modifier
                        .height(transcriptHeight)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    content.forEach {
                        if (it is String) {
                            item {
                                BasicTextField(
                                    value = it,
                                    onValueChange = { },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 4.dp, end = 4.dp),
                                    textStyle = TextStyle.Default.copy(fontSize = 16.sp)
                                )
                            }
                        } else if (it is Bitmap) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "Image from notes",
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .fillMaxWidth(0.8F)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Divider(color = MaterialTheme.colorScheme.tertiary, thickness = 4.dp,
                    modifier = Modifier
                        .padding(4.dp)
                        .pointerInput(Unit) {
                            detectVerticalDragGestures { _, dragAmount ->
                                transcriptHeight = (transcriptHeight + dragAmount.dp).coerceIn(
                                    screenHeight * 0.1F,
                                    screenHeight * 0.6F
                                )
                                summaryHeight = (summaryHeight - dragAmount.dp).coerceIn(
                                    screenHeight * 0.1F,
                                    screenHeight * 0.6F
                                )
                            }
                        }
                )
            }

            item {
                Text(
                    text = "Summarized Notes",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 16.sp
                )

                LazyColumn(
                    modifier = Modifier
                        .height(summaryHeight)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    content.forEach {
                        if (it is String) {
                            item {
                                BasicTextField(
                                    value = it,
                                    onValueChange = { },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 4.dp, end = 4.dp),
                                    textStyle = TextStyle.Default.copy(fontSize = 16.sp)
                                )
                            }
                        } else if (it is Bitmap) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Image(
                                        bitmap = it.asImageBitmap(),
                                        contentDescription = "Image from notes",
                                        modifier = Modifier
                                            .padding(6.dp)
                                            .fillMaxWidth(0.8F)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                OutlinedButton(
                    modifier = Modifier
                        .width(235.dp)
                        .padding(4.dp),
                    onClick = {
                        viewModel.onTextToSpeech(note?.summarizeContent ?: "No content to read")
                        ttsButtonText = if (viewModel.isSpeaking) {
                            "Stop Reading Notes    "
                        } else {
                            "Read Summarized Notes"
                        }
                    }) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.read_text_icon),
                        contentDescription = "Voice Icon",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(
                        modifier = Modifier
                            .size(ButtonDefaults.IconSpacing)
                            .weight(1F)
                    )
                    Text(text = ttsButtonText)
                }
            }
        }
    }
}

@Preview
@Composable
fun SingleNoteScreenPreview() {
    SingleNoteScreen(1, navController = rememberNavController())
}