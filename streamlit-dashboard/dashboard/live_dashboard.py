import time
import pandas as pd
import streamlit as st
import altair as alt

from data.stream_loader import load_stream


@st.fragment(run_every="2s")
def render_live_dashboard():
    """Main real-time dashboard fragment."""

    df = load_stream()

    if df.empty:
        st.warning("⚠️ Waiting for data stream from Flink...")
        return

    # ---- METRICS ----
    with st.container():
        col1, col2, col3 = st.columns(3)
        col1.metric("Avg Score", f"{df['calculated_score'].mean():.2f}")
        col2.metric("Avg Price", f"{df['price'].mean():.0f} USD")
        col3.metric("Buffer Size", len(df))

    # ---- CHART ----
    st.subheader("Live Market Trends")

    # Drop columns that shouldn't be plotted (e.g., IDs, raw timestamps)
    columns_to_drop = ['id', 'timestamp']
    df_for_chart = df.drop(columns=columns_to_drop, errors='ignore')

    chart_data = df_for_chart.melt(
        "datetime",
        var_name="Metric",
        value_name="Value",
    )

    chart = (
        alt.Chart(chart_data)
        .mark_line()
        .encode(
            x=alt.X("datetime", axis=alt.Axis(format="%H:%M:%S", title="Time")),
            y=alt.Y("Value", title="Value"),
            color="Metric",
        )
        .properties(height=400)
    )

    st.altair_chart(chart, use_container_width=True)

    st.caption(f"Last updated: {time.strftime('%H:%M:%S')}")