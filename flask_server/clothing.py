from flask import Blueprint, request, jsonify
import requests
from openai import OpenAI
from datetime import datetime, timedelta
from dateutil.parser import parse  

import os
from dotenv import load_dotenv

load_dotenv()
client = OpenAI()

clothing_bp = Blueprint('clothing_bp', __name__)
OPENWEATHER_API_KEY = os.getenv("OPENWEATHER_API_KEY")
VISUAL_CROSSING_API_KEY = os.getenv("VISUAL_CROSSING_API_KEY")


def get_historical_weather(city, date_str):
    """使用 Visual Crossing 获取历史天气（压缩格式）"""
    url = f"https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/{city}/{date_str}?unitGroup=metric&key={VISUAL_CROSSING_API_KEY}&include=days"
    response = requests.get(url)
    if response.status_code != 200:
        return f"{date_str}: failed to fetch data"
    
    data = response.json()
    if "days" not in data or not data["days"]:
        return f"{date_str}: no data available"

    day = data["days"][0]
    return f"{date_str}: {day['temp']}°C, {day['conditions'].lower()}"


@clothing_bp.route('/clothing_advice', methods=['POST'])
def clothing_advice():
    try:
        data = request.json
        cities = data['cities']
        if not cities:
            return jsonify({'error': 'No cities provided'}), 400

        city = cities[0]
        start_date = parse(data['start_date'])
        end_date = parse(data['end_date'])
        now = datetime.now()

        summary_lines = []

        if start_date > now:
            # 使用 Visual Crossing 获取去年同一时间的天气
            delta = (end_date - start_date).days
            for i in range(delta + 1):
                last_year_date = (start_date.replace(year=start_date.year - 1) + timedelta(days=i)).strftime("%Y-%m-%d")
                summary = get_historical_weather(city, last_year_date)
                summary_lines.append(summary)
        else:
            # 使用 OpenWeather 获取预测
            url = "https://api.openweathermap.org/data/2.5/forecast"
            params = {
                'q': city,
                'appid': OPENWEATHER_API_KEY,
                'units': 'metric',
                'lang': 'en'
            }
            weather_response = requests.get(url, params=params).json()
            if 'list' not in weather_response:
                return jsonify({'error': f'Failed to fetch forecast for {city}'}), 500

            for entry in weather_response['list']:
                dt = datetime.fromtimestamp(entry['dt'])
                if start_date <= dt <= end_date:
                    temp = entry['main']['temp']
                    description = entry['weather'][0]['description']
                    summary_lines.append(f"{dt.strftime('%m/%d %Hh')}: {temp:.1f}°C, {description.lower()}")

        weather_summary = "\n".join(summary_lines[:5]) or "No forecast found in date range"

        # 构造简洁的 GPT 提示
        prompt = f"""
I will travel to {city} from {data['start_date']} to {data['end_date']}.
The weather forecast is:
{weather_summary}

Based on this weather, what kind of clothes should I pack? Please give me a short, practical and friendly recommendation in English, preferably in bullet points.
"""

        model = os.getenv("OPENAI_MODEL", "gpt-3.5-turbo")
        gpt_response = client.chat.completions.create(
            model=model,
            messages=[{"role": "user", "content": prompt}]
        )
        advice = gpt_response.choices[0].message.content

        return jsonify({
            "weather_summary": {city: weather_summary},
            "clothing_advice": advice
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 500
