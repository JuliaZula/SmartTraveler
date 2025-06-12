from flask import Flask, jsonify, request, Response

import os
import openai
import requests
from datetime import datetime
from dotenv import load_dotenv

import json
import asyncio
from fetcher import fetch_all
from parser import parse_results
from clothing import clothing_bp



load_dotenv()
OPENWEATHER_API_KEY = os.getenv("OPENWEATHER_API_KEY")
openai.api_key = os.getenv("OPENAI_API_KEY")
print("OPENWEATHER_API_KEY =", OPENWEATHER_API_KEY)


app = Flask(__name__)

app.register_blueprint(clothing_bp)

@app.route('/get_flights', methods=['GET'])
def get_flights():
    print("recived the request")
    try:
        departure = request.args.get("departure")
        if not departure:
            return jsonify({"error": "Missing required parameter: departure"}), 400

        destinations_param = request.args.get("destinations")
        if not destinations_param:
            return jsonify({"error": "Missing required parameter: destinations"}), 400
        destinations = destinations_param.split(",")

        start_date = request.args.get("start_date")
        end_date = request.args.get("end_date")

        print(f"parametres: departure={departure}, destinations={destinations}, time={start_date} - {end_date}")

        loop = asyncio.new_event_loop()
        asyncio.set_event_loop(loop)
        data = loop.run_until_complete(fetch_all(departure, destinations, start_date, end_date))

        parsed_data = parse_results(data)
        print("check formal: ",parsed_data)

        #return jsonify(parsed_data) 
        json_data = json.dumps(parsed_data, ensure_ascii=False) 

        response = Response(json_data, status=200, mimetype="application/json")
        response.headers["Content-Length"] = str(len(json_data))
        response.headers["Connection"] = "close" 
        return response

    except Exception as e:
        print(f"error:{e}")
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    import logging
    logging.basicConfig(level=logging.DEBUG)
    app.run(host='0.0.0.0', port=5002, debug=False)

