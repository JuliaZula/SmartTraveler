import re

def parse_results(data):
    results = []
    for result in data:
        destination = result["destination"]
        prices = result["data"].get("data", {}).get("itineraryPriceGraph", {}).get("prices", [])
        
        total_price = sum(float(re.sub(r"[^\d.]", "", price.get("price", {}).get("roundedFormattedValue", "0"))) for price in prices if price.get("price"))
        avg_price = total_price / len(prices) if prices else None

        #results.append({"destination": destination, "price": avg_price})
        results.append(avg_price)

    return results
