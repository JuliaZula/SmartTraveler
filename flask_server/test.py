import requests
import json

print("🛫 Getting flight info (mock)...")
# 假设你的 flight_fetcher 支持这个接口
# response = requests.post("http://localhost:5002/get_flights", json={...})

print("👕 Getting clothing advice...")
response = requests.post("http://localhost:5002/clothing_advice", json={
    "cities": ["Paris"],
    "start_date": "2025-07-20",
    "end_date": "2025-07-22"
})
print("✅ Result:")
print(json.dumps(response.json(), indent=2))

