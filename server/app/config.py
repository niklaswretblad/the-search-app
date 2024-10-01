import os
from app import app
from logging.handlers import RotatingFileHandler
import logging

base_dir = os.path.dirname(__file__)

secret = os.getenv('SECRET_KEY')
jwt_secret = os.getenv('JWT_SECRET_KEY')
google_client_secret = os.getenv('GOOGLE_CLIENT_ID')

#dbname=the-search-database host=the-search-server.postgres.database.azure.com port=5432 sslmode=require user=tvcilkakoj password=B5Jgf$YQr1QcVqT8

# pg_dump -U tvcilkakoj -d your_database -f backup.sql

if "AZURE_POSTGRESQL_CONNECTIONSTRING" in os.environ:
    conn = os.environ["AZURE_POSTGRESQL_CONNECTIONSTRING"]
    values = dict(x.split("=") for x in conn.split(' '))
    user = values['user']
    host = values['host']
    database = values['dbname']
    password = values['password']
    db_uri = f'postgresql+psycopg2://{user}:{password}@{host}/{database}'
    app.config['SQLALCHEMY_DATABASE_URI'] = db_uri 
    debug_flag = False
else: # when running locally: use sqlite in memory
    db_uri = 'sqlite:///:memory:'
    debug_flag = True

app.config.update(
    DEBUG=debug_flag,
    BASEDIR=base_dir,
    SQLALCHEMY_DATABASE_URI=db_uri,
    SECRET_KEY=secret,
    JWT_SECRET_KEY=jwt_secret,
    GOOGLE_CLIENT_ID=google_client_secret,
    SQLALCHEMY_TRACK_MODIFICATIONS=False
)

app.config['JWT_BLACKLIST_ENABLED'] = True
app.config['JWT_BLACKLIST_TOKEN_CHECKS'] = ['access']

# Logging setup

# Set up file handler for logging
log_file = os.path.join(base_dir, 'app.log')
handler = RotatingFileHandler(log_file, maxBytes=100000, backupCount=1)
handler.setLevel(logging.INFO)

# Set the logging format
formatter = logging.Formatter(
    '%(asctime)s %(levelname)s: %(message)s '
    '[in %(pathname)s:%(lineno)d]'
)
handler.setFormatter(formatter)

# Add the handler to the app's logger
app.logger.addHandler(handler)
app.logger.setLevel(logging.INFO)

app.logger.info('Logger initialized. App started.')
