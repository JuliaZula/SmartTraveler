# ✈️SmartTraveler
**SmartTraveler** is a smart travel assistant that combines an **Android App (Java)** and a **Flask backend (Python)** to help users find the most cost-effective travel itinerary.  
Simply enter your departure location and multiple destinations, and the app **automatically calculates the optimal travel order** while fetching the **cheapest flights**! 



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

### **Start the Flask server**
- cd flask_server
- python app.py

### Open the project in Android Studio
- cd ../SmartTraveler_v1
- **Requirements:** Android Studio **Giraffe (2023.3.1) or later**
- **Check `app/local.properties` configuration**
  API_BASE_URL=http://10.0.2.2:5002




