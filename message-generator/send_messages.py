import time
from data_generator import diamond_data_generator
from producer import send_diamond_event, close_producer


if __name__ == "__main__":
    print("Starting continuous generation and sending of diamond events to Kafka...")

    generator = diamond_data_generator()

    try:
        while True:
            event = next(generator)
            send_diamond_event(event)

            print(f"Sent event (ID: {event['id'][:8]}..., Carat: {event['carat']:.2f})")

            time.sleep(0.1)

    except KeyboardInterrupt:
        print("\nGenerator stopped by user.")

    finally:
        close_producer()
