# SmartTraveler

SmartTraveler is a travel assistant that combines an Android App based on a Flask server to help users find the most cost-effective travel itinerary.  
Simply enter the departure location, multiple destinations and the dates, then it calculates the optimal travel order while fetching the cheapest flights by the server.




## Features
- Input departure city, destinations, and travel dates
- Fetches cheapest flights in real-time (server side)
- Solves ATSP using jsprit to optimize visit order
- Displays total estimated cost and recommended route




## Tech Stack

### Android (Java)
- `Retrofit` - Handles API requests
- `Room` - Local database storage
- `jsprit` - Solving the Traveling Salesman Problem (TSP)
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
- [UML Diagrams](/docs/UML/) 
- [Algorithm Report](/docs/algorithm_report.md)




