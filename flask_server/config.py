GRAPHQL_ENDPOINT = "https://api.skypicker.com/umbrella/v2/graphql"

HEADERS = {
    "Content-Type": "application/json",
    "kw-skypicker-visitor-uniqid": "ca4531ae-574b-42f4-bb12-a4be7b6daacf",
    "kw-umbrella-token": "c6e08a10ae6abd0341f138462fb3578b6843063a88d5fce7b44d79ed3450496b",
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15",
    "Origin": "https://www.kiwi.com",
    "Referer": "https://www.kiwi.com/"
}

QUERY_TEMPLATE = """
query useQuickNavPricesQuery(
      $search: SearchPriceGraphInput
      $filter: ItinerariesFilterInput
      $options: ItinerariesOptionsInput
    ) {
      itineraryPriceGraph(search: $search, filter: $filter, options: $options) {
        __typename
        ... on ItineraryPriceGraph {
          prices {
            date
            ...QuickNavigationPrice
          }
        }
      }
    }

    fragment QuickNavigationPrice on PriceGraphItem {
      price {
        roundedFormattedValue
      }
      ratedPrice
    }
"""
