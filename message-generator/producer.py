from confluent_kafka.avro import AvroProducer, loads
from config import KAFKA_BOOTSTRAP, SCHEMA_REGISTRY_URL, SCHEMA_PATH


# Load Avro schema
with open(SCHEMA_PATH) as f:
    value_schema_str = f.read()

value_schema = loads(value_schema_str)

producer = AvroProducer(
    {
        'bootstrap.servers': KAFKA_BOOTSTRAP,
        'schema.registry.url': SCHEMA_REGISTRY_URL
    },
    default_value_schema=value_schema
)


def send_diamond_event(event: dict):
    """Send a diamond event to Kafka."""
    try:
        producer.produce(topic='diamonds', value=event)
        producer.poll(0)
    except Exception as e:
        print(f"Error while sending event: {e}")
        producer.flush()


def close_producer():
    """Flush and cleanly close the producer."""
    print("Flushing producer buffer...")
    producer.flush()
    print("Done.")
