from config.settings import setup_page
from dashboard.layout import render_sidebar
from dashboard.live_dashboard import render_live_dashboard

setup_page()

render_sidebar()

render_live_dashboard()
