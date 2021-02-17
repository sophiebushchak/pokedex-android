# PokéData - Android Pokédex.
PokéData is an Android Pokédex powered by a REST API specifically created for it.

## Features
* List of all current Pokemon up to Generation 8.
* Search Pokemon by name, with ability to find Pokemon with partial search entries such as just "chu".
* Detailed view of a Pokemon with the Pokedex entry and the evolution line, which can be used to navigate to a member of the evolution line.
* Pressing on a Pokemon's sprite in the detailed view displays the shiny sprite.
* Log-in and register with Firebase.
* Favourite Pokemon tracking with Firebase.
* Responses from the back-end are cached for speed and online usage. This allows the application to work offline as well, except without favourites.

## Screenshots
![Screenshot of login screen](screenshots/img%20(1).png) ![Screenshot of Pokedex main screen](screenshots/img%20(2).png)
![Screenshot of search](screenshots/img%20(3).png) ![Screenshot of Pokemon detail page](screenshots/img%20(4).png)
![Screenshot of Pokemon evolutions](screenshots/img%20(5).png) ![Screenshot of shiny Pokemon sprite](screenshots/img%20(6).png)


## Setup
### Back-end
To set up the project, [first the PokeData back-end must be set up locally or remotely.](https://github.com/sophiebushchak/pokedex-rest-server) Then, this project can be pulled.
Opening it in Android Studio should make Gradle download all dependencies.

Once the project has been opened in Android Studio, find PokeDataApiConfig in the "rest" package. Here, you can alter the host and base url of the back-end that the application will connect to. To run the application on a mobile phone, you will probably need to port-forward and use your external IP here.

### Firebase
Because the application uses Firebase for registering and login, a simple Firebase project must be set up for it to work. 

To create one, click on "Create a project". Then, once you are on the dashboard, you should be able to register an application. 
It is important that you enter the correct package name, which is "com.example.pokedata". Name it whatever you want.

A button should appear that will let you download the google.services.json file. This file needs to be dropped into the 
root of the project /app folder.

Now that the application is registered, you can create a Cloud Firestore to store data. Go to "Cloud Firestore" and 
click on "Create database". I would recommend starting in test mode for easy access.

Finally, Firebase Authentication must be set up. Go to the Authentication page and enable "Email/Password" for authentication.

With Firebase set up and the back-end running, the project is now set up for use.

### Compilation
To compile and run the application, simply press the run button in Android Studio. You can also compile a .apk file 
and transfer it to your phone.

## Missing Features
There were some features I would have liked to add, but did not because I did not have the time for it. In the future I may return
to finish these features.
* The display of Pokemon evolutions currently does not support multiple evolution branches. It only accounts for Pokemon that have a linear evolution line. It would be nice to somehow display this in a different way where the other evolutions of a Pokemon can be seen.
* Even though the back-end supports filtering on color, weight and height, the application currently does not support filtering using these parameters. The only thing needed to do this should be some UI with logic to send different API requests.
* It would be nice to view other users' profiles with favourites and have some kind of friend adding system as well as chat. This is not really a necessary feature, but it would be educational to implement this.
* Not every edge case is handled. A lot of bugs are handled gracefully, but some things are not. For example, there are no notifications about being offline or online. A list of issues could be compiled to fix this.
