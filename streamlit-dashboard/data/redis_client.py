import os
import redis
import streamlit as st

REDIS_KEY = "diamonds-output-full"

@st.cache_resource
def get_redis_connection():
    """Cached Redis connection reused across reruns."""
    return redis.Redis(
        host=os.getenv("REDIS_HOST", "localhost"),
        port=int(os.getenv("REDIS_PORT", 6379)),
        decode_responses=True
    )
