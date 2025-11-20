import pandas as pd
from data.redis_client import get_redis_connection, REDIS_KEY
from utils.parsing import parse_redis_entry


def load_stream(limit=500) -> pd.DataFrame:
    """Fetch latest N entries from Redis Stream."""
    r = get_redis_connection()

    try:
        entries = r.xrevrange(REDIS_KEY, count=limit)
    except Exception:
        return pd.DataFrame()

    rows = []

    for entry in entries:
        parsed = parse_redis_entry(entry)
        if parsed:
            rows.append(parsed)

    df = pd.DataFrame(rows)
    if df.empty:
        return df

    return df.sort_values("datetime")
