import requests
import os
from dotenv import load_dotenv
load_dotenv()


def get_historical_weather(city, date):
    API_KEY = os.getenv("VISUAL_CROSSING_API_KEY") 
    base_url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline"
    
    url = f"{base_url}/{city}/{date}?unitGroup=metric&key={API_KEY}&include=days"

    response = requests.get(url)
    if response.status_code != 200:
        return {"error": f"Failed to fetch weather: {response.status_code}"}

    data = response.json()
    if "days" not in data:
        return {"error": "No weather data found"}

    day = data["days"][0]
    return {
        "date": day["datetime"],
        "temp": day["temp"],
        "temp_max": day["tempmax"],
        "temp_min": day["tempmin"],
        "description": day["description"],
        "conditions": day["conditions"]
    }
