import aiohttp
import asyncio
import json
from config import GRAPHQL_ENDPOINT, HEADERS, QUERY_TEMPLATE

async def fetch_graphql(session, departure, destination, start_date, end_date):
    variables = {
        "search": {
            "itinerary": {
                "source": {"ids": [departure]},
                "destination": {"ids": [destination]},
                "outboundDepartureDate": {"start": start_date, "end": end_date},
                "nightsCount": None
            },
            "passengers": {"adults": 1, "children": 0, "infants": 0},
            "cabinClass": {"cabinClass": "ECONOMY", "applyMixedClasses": False}
        },
        "filter": {
            "allowChangeInboundDestination": True,
            "allowChangeInboundSource": True,
            "allowDifferentStationConnection": True,
            "enableSelfTransfer": True,
            "enableThrowAwayTicketing": True,
            "enableTrueHiddenCity": True,
            "transportTypes": ["FLIGHT"],
            "contentProviders": ["KIWI"],
            "flightsApiLimit": 25
        },
        "options": {
            "sortBy": "PRICE",
            "mergePriceDiffRule": "INCREASED",
            "currency": "eur",
            "locale": "en",
            "market": "fr",
            "partner": "skypicker"
        }
    }

    payload = {"query": QUERY_TEMPLATE, "variables": variables}

    async with session.post(GRAPHQL_ENDPOINT, headers=HEADERS, json=payload) as response:
        response_data = await response.json()
        return {"destination": destination, "data": response_data} 

async def fetch_all(departure, destinations, start_date, end_date):
    async with aiohttp.ClientSession() as session:
        tasks = [fetch_graphql(session, departure, dest, start_date, end_date) for dest in destinations]
        return await asyncio.gather(*tasks)
