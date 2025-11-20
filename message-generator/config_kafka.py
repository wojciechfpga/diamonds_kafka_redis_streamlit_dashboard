from kafka.admin import KafkaAdminClient, NewTopic
from kafka.errors import TopicAlreadyExistsError
from datetime import datetime

admin_client = KafkaAdminClient(
    bootstrap_servers = "broker-1:19092",
    client_id = "admin_config"
)

topic_source = NewTopic(
    name = "diamonds",
    num_partitions=1,
    replication_factor=1
)

topic_sink = NewTopic(
    name = "diamonds-output-full", 
    num_partitions=1,
    replication_factor=1
)

topic_list = [topic_source, topic_sink]

try:
    admin_client.create_topics(new_topics=topic_list, validate_only=False)
    now = datetime.now()
    print(f"Kafka Topics 'diamonds' and 'diamonds-output-full' created successfully. {now}")

except TopicAlreadyExistsError:
    now = datetime.now()
    print(f"Topics already exist. Skipping creation. {now}")

except Exception as e:
    print(f"An error occurred: {e}")

finally:
    admin_client.close()