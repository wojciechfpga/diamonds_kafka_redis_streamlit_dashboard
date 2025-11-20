import streamlit as st

def render_sidebar():
    """Static sidebar elements (not affected by fragment updates)."""
    with st.sidebar:
        st.header("System Controls")
        st.success("System Online")
        st.markdown("---")
        st.info(
            "This dashboard uses `st.fragment` for partial updates, ensuring "
            "sidebar state is preserved."
        )
