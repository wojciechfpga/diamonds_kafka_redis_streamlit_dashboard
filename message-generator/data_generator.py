import time
import uuid
import numpy as np
import pandas as pd
from config import CSV_PATH


def diamond_data_generator():
    """Generate diamond events indefinitely."""

    diamonds_df = pd.read_csv(CSV_PATH)
    diamonds_df = diamonds_df[diamonds_df["price"] > 2500]

    np.random.seed(42)
    diamonds_df_sampled = diamonds_df.sample(n=50000, replace=True, random_state=42)

    # Add "sold_copies"
    np.random.seed(42)
    diamonds_df_sampled['sold_copies'] = np.random.randint(
        0, 1001, size=len(diamonds_df_sampled)
    )

    columns = [
        "carat", "cut", "color", "clarity", "depth", "table",
        "price", "x", "y", "z", "sold_copies"
    ]

    events = diamonds_df_sampled[columns].to_dict('records')

    index = 0
    total_events = len(events)

    while True:
        raw = events[index % total_events]
        event = raw.copy()

        event["id"] = str(uuid.uuid4())
        event["timestamp"] = int(time.time() * 1000)

        # Type coercion
        for key in ["carat", "depth", "table", "price", "x", "y", "z"]:
            if key in event:
                event[key] = float(event[key])

        event["sold_copies"] = int(event["sold_copies"])

        yield event
        index += 1
