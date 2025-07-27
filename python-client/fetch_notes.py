import requests

TOKEN_URL = "http://localhost:8080/token"
NOTES_URL = "http://localhost:8080/notes"
VOWELS = "AEIOUaeiou"

try:
    login_request = {
        "login": "user1",
        "password": "pwd1"
    }
    token_response = requests.post(TOKEN_URL, json=login_request)
    token_response.raise_for_status()
    bearer_token = token_response.text

    if bearer_token:
        headers_with_token = {
            "Authorization": f"Bearer {bearer_token}",
            "Content-Type": "application/json"
        }
        notes_response = requests.get(NOTES_URL, headers=headers_with_token)
        notes_response.raise_for_status()
        notes_data = notes_response.json()
        total_notes = len(notes_data)
        print(f"Total notes: {total_notes}")
        if total_notes > 0:
            print("Titles starting with vowel:")
            for entry in notes_data:
                if entry["title"] and entry["title"][0] in VOWELS:
                    print(f" - {entry['title']}")

    else:
        print("Token not found in the response from the first endpoint.")

except requests.exceptions.RequestException as e:
    print(f"Error calling the API endpoint: {e}")
except Exception as e:
    print(f"An unexpected error occurred: {e}")
