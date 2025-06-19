FROM python:3.10-slim

WORKDIR /app

COPY flask_server/ /app

RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 5002

CMD ["python", "app.py"] 
