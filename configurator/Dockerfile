FROM python:3.9-slim

WORKDIR /app

RUN pip install requests

COPY run_connect.py .

CMD ["tail", "-f", "/dev/null"]