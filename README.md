# Currency Converter for Android

## Project purpose
This is part of the Computer Science course "Mobile application development" at the Technical University of Ulm (https://www.thu.de).

## Architecture
There is a basic architecture in place. The app uses the European Central Bank (ECB) API in order to get the latest currency rates. On start, this API is called and the information is saved using a persistency layer. On further usage of the app, the API is only called once the persistance is deleted or the user wishes.
