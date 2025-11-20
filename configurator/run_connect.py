import requests
import time

CONNECT_URL = "http://kafka-connect:8083/connectors"
CONNECTOR_NAME = "redis-diamonds-sink"

payload = {
  "name": CONNECTOR_NAME,
  "config": {
    "connector.class": "com.redis.kafka.connect.RedisSinkConnector",
    "topics": "diamonds-output-full",
    "redis.uri": "redis://redis:6379", 
    "redis.type": "JSON",           
    "redis.keyspace": "diament",    
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter.schema.registry.url": "http://schema-registry:8081",
    "value.converter.schemas.enable": "true",
    "transforms": "createKey,extractString", 
    "transforms.createKey.type": "org.apache.kafka.connect.transforms.ValueToKey",
    "transforms.createKey.fields": "id",
    "transforms.extractString.type": "org.apache.kafka.connect.transforms.ExtractField$Key",
    "transforms.extractString.field": "id"
  }
}

def run():
    headers = {'Content-Type': 'application/json'}
    
    print(f"--- Starting configuration: {CONNECTOR_NAME} ---", flush=True) # Zmieniono z: --- Start konfiguracji: ... ---

    while True:
        try:
            if requests.get(CONNECT_URL).status_code == 200: 
                print("Kafka Connect API available.", flush=True) # Zmieniono z: Kafka Connect API dostępne.
                break
        except: 
            pass
        print("Waiting for Kafka Connect...", flush=True) # Zmieniono z: Czekam na Kafka Connect...
        time.sleep(2)

    print("Deleting potential old connector...", flush=True) # Zmieniono z: Usuwanie ewentualnego starego konektora...
    requests.delete(f"{CONNECT_URL}/{CONNECTOR_NAME}")
    time.sleep(2)

    print("Sending configuration...", flush=True) # Zmieniono z: Wysyłanie konfiguracji...
    response = requests.post(CONNECT_URL, json=payload, headers=headers)
    
    print(f"Status Code: {response.status_code}")
    
    if response.status_code == 201:
        print("SUCCESS! Connector created.", flush=True) # Zmieniono z: SUKCES! Konektor utworzony.
        
        print("\n--- Verifying configuration in API ---", flush=True) # Zmieniono z: --- Weryfikacja konfiguracji w API ---
        verify = requests.get(f"{CONNECT_URL}/{CONNECTOR_NAME}/config")
        config_json = verify.json()
        
        r_type = config_json.get('redis.type')
        r_keyspace = config_json.get('redis.keyspace')
        print(f"redis.type = {r_type} (Expected: JSON)") # Zmieniono z: Oczekiwano: JSON
        print(f"redis.keyspace = {r_keyspace} (Expected: diament)") # Zmieniono z: Oczekiwano: diament
        
        if r_type == 'JSON' and r_keyspace == 'diament':
            print("\nConfiguration looks PERFECT. Check Redis.") # Zmieniono z: Konfiguracja wygląda IDEALNIE. Sprawdź Redis.
        else:
            print("\nWARNING: Configuration in API differs from sent payload!") # Zmieniono z: UWAGA: Konfiguracja w API różni się od wysłanej!
    else:
        print(f"Error creating connector: {response.text}", flush=True) # Zmieniono z: Błąd tworzenia: ...

if __name__ == "__main__":
    run()