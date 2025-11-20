import streamlit as st

def setup_page():
    """Configure Streamlit page settings."""
    st.set_page_config(
        page_title="Diamond Score Dashboard",
        layout="wide",
        initial_sidebar_state="expanded"
    )
