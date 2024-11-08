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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                text = character.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Ki: ${character.ki}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Planeta: ${character.originPlanet?.name ?: "Desconocido"}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            character.transformations?.forEach { transformation ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Transformation: ${transformation.name}",
                    fontWeight = FontWeight.Bold
                )
                Image(
                    painter = rememberAsyncImagePainter(transformation.image),
                    contentDescription = "Transformation Image",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = "Ki: ${transformation.ki}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun CharactersList(characters: List<Character>, planets: List<Planet>) {

    characters.forEach { character ->
        Log.d("CharacterPlanetCheck", "Character: ${character.name}, Origin Planet ID: ${character.originPlanet?.id}")
    }
    planets.forEach { planet ->
        Log.d("PlanetMapCheck", "Planet ID: ${planet.id}, Name: ${planet.name}")
    }

    val planetMap = planets.associateBy { it.id }

    val charactersWithPlanets = characters.map { character ->
        val originPlanetId = character.originPlanet?.id
        val planet = if (originPlanetId != null) planetMap[originPlanetId] else null
        character to (planet ?: Planet(id = -1, name = "Planeta desconocido"))
    }

    LazyColumn {
        items(charactersWithPlanets) { (character, planet) ->
            CharacterCard(
                planet = planet,
                character = character
            )
        }
    }
}

@Composable
fun MainScreen() {
    var characters by remember { mutableStateOf<List<Character>>(emptyList()) }
    var planets by remember { mutableStateOf<List<Planet>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        fetchCharacters { result ->
            characters = result
        }
        fetchPlanets { result ->
            planets = result
        }
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DragonBallAPITheme {
        MainScreen()
    }
}
