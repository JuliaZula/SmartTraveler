# SmartTraveler

SmartTraveler is atravel assistant that combines an Android App based on a Flask server to help users find the most cost-effective travel itinerary.  
Simply enter the departure location, multiple destinations and the dates, then it calculates the optimal travel order while fetching the cheapest flights by the server.




## Tech Stack

### Android (Java)
- `Retrofit` - Handles API requests
- `Room` - Local database storage
- `[jsprit](https://github.com/graphhopper/jsprit/tree/v1.4)` - Solving the Traveling Salesman Problem (TSP)
- `SQLite` - Storing country and airport data

### Flask (Python)
- `Flask` - Lightweight backend framework




## How to Run the Android App

### Clone the project
git clone https://github.com/JuliaZula/SmartTraveler.git

### Start the Flask server
- cd flask_server
- python app.py

### Open the project in Android Studio
- cd ../SmartTraveler_v1
  **Requirements:** Android Studio **Giraffe (2023.3.1) or later**
- Check `app/local.properties`            configuration
  API_BASE_URL=http://10.0.2.2:5002




## Documentation
- [UML](/docs/UML/)
- [Alorithme Report](/docs/algorithm_report.md)




