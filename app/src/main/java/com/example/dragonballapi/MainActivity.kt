package com.example.dragonballapi

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.dragonballapi.ui.theme.DragonBallAPITheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DragonBallAPITheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun CharacterCard(character: Character, planet: Planet) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(character.image),
                contentDescription = "Character Image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 10.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = character.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Raza: ${character.race}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Genero: ${character.gender}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Ki: ${character.ki}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "MaxKi: ${character.maxKi}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Afiliacion: ${character.affiliation}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = character.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
fun PlanetCard(planet: Planet) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium.copy(CornerSize(16.dp)),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(planet.image),
                contentDescription = "Planet Image",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 10.dp),
                contentScale = ContentScale.Fit
            )
            Text(
                text = planet.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Description: ${planet.description}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
fun CharactersList(characters: List<Character>, planets: List<Planet>) {
    val planetMap = planets.associateBy { it.id }

    val charactersWithPlanets = characters.map { character ->
        val originPlanetId = character.originPlanet?.id
        val planet = originPlanetId?.let { planetMap[it] } ?: Planet(id = -1, name = "Desconocido", description = "desconocido", image = "desconocido", isDestroyed = false)

        character to planet
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "The Dragon Ball API",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
        LazyColumn {
            item {
                Text(
                    text = "Personajes",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            items(charactersWithPlanets) { (character, planet) ->
                CharacterCard(character = character, planet = planet)
            }
            item {
                Text(
                    text = "Planetas",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            items(planets) { planet ->
                PlanetCard(planet = planet)
            }
        }
    }
}

@Composable
fun MainScreen() {
    var characters by remember { mutableStateOf<List<Character>>(emptyList()) }
    var character by remember { mutableStateOf(characters) }
    var planets by remember { mutableStateOf<List<Planet>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        fetchCharacters { result -> characters = result }
        fetchPlanets { result -> planets = result }
        fechtCharacter { result -> character = result }
    }

    LaunchedEffect(characters, planets) {
        if (characters.isNotEmpty() && planets.isNotEmpty()) {
            isLoading = false
        }
    }

    if (isLoading) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.primary)
    } else {
        CharactersList(characters = characters, planets = planets)
    }
}

fun fetchCharacters(onResult: (List<Character>) -> Unit) {
    RetrofitClient.instance.getCharacters().enqueue(object : Callback<CharacterResponse> {
        override fun onResponse(call: Call<CharacterResponse>, response: Response<CharacterResponse>) {
            if (response.isSuccessful) {
                val characters = response.body()?.items
                characters?.let {
                    onResult(it)
                } ?: run {
                    Log.e("MainScreen", "Null response")
                }
            } else {
                Log.e("MainScreen", "Wrong response: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<CharacterResponse>, t: Throwable) {
            Log.e("MainScreen", "Error calling the API: ${t.message}")
            t.printStackTrace()
        }
    })
}

fun fetchPlanets(onResult: (List<Planet>) -> Unit) {
    RetrofitClient.instance.getPlanets().enqueue(object : Callback<PlanetResponse> {
        override fun onResponse(call: Call<PlanetResponse>, response: Response<PlanetResponse>) {
            if (response.isSuccessful) {
                val planets = response.body()?.items
                if (!planets.isNullOrEmpty()) {
                    onResult(planets)
                } else {
                    Log.e("MainScreen", "Not found planets or the list is null")
                }
            } else {
                Log.e("MainScreen", "Wrong response: ${response.code()}")
            }
        }

        override fun onFailure(call: Call<PlanetResponse>, t: Throwable) {
            Log.e("MainScreen", "Error calling the API: ${t.message}")
            t.printStackTrace()
        }
    })
}

fun fechtCharacter(onResult: (List<Character>) -> Unit){
    val call = RetrofitClient.instance.getCharacterDetails(characterId = 1)
    call.enqueue(object : Callback<Character> {
        override fun onResponse(call: Call<Character>, response: Response<Character>) {
            if (response.isSuccessful) {
                val character = response.body()

                Log.d("Character Info", "Origin Planet: ${character?.originPlanet?.name}")
            } else {
                Log.e("API Error", "Error: ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Character>, t: Throwable) {
            Log.e("API Failure", t.message ?: "Unknown error")
        }
    })
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DragonBallAPITheme {
        MainScreen()
    }
}
