package uk.ac.aber.dcs.firestorepractise

sealed class Screen (val route: String){
    object Auth : Screen("auth")
    object Main : Screen("main")
}