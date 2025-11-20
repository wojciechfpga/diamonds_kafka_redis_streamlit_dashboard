import pandas as pd
from typing import Optional, Dict, Any


def parse_redis_entry(entry) -> Optional[Dict[str, Any]]:
    """Convert a raw Redis entry into a Python dict."""

    try:
        if isinstance(entry, tuple):
            entry_id, fields = entry
        else:  # list variant
            entry_id, raw = entry
            it = iter(raw)
            fields = dict(zip(it, it))

        ts = float(entry_id.split("-")[0]) / 1000.0

        return {
            "timestamp": ts,
            "datetime": pd.to_datetime(ts, unit="s"),
            "id": fields.get("id"),
            "price": float(fields.get("price", 0)),
            "calculated_score": float(fields.get("calculated_score", 0)),
        }

    except Exception:
        return None
