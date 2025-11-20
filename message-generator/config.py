import os

KAFKA_BOOTSTRAP = 'broker-1:19092'
SCHEMA_REGISTRY_URL = 'http://schema-registry:8081'

BASE_DIR = os.path.dirname(__file__)

SCHEMA_PATH = os.path.join(BASE_DIR, "schemas/diamond.avsc")
CSV_PATH = os.path.join(BASE_DIR, "diamonds.csv")
