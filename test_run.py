import requests
import json
import time

print("🛫 Waiting for server to start...")
time.sleep(3)  

try:
    print("👕 Getting clothing advice...")
    response = requests.post("http://localhost:5002/clothing_advice", json={
        "cities": ["Paris"],
        "start_date": "2025-07-20",
        "end_date": "2025-07-22"
    })

    response.raise_for_status()
    print("✅ Result:")
    print(json.dumps(response.json(), indent=2))
except Exception as e:
    print("❌ Test failed:", str(e))
    exit(1)

