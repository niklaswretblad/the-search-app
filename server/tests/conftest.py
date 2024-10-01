import os
import sys
import pytest
from flask_jwt_extended import create_access_token
from app import app, db
from app.model import User, Spot, Like, Follow, Comment, TokenBlocklist

# Ensure the app module is in the Python path
sys.path.insert(0, os.path.abspath(os.path.join(os.path.dirname(__file__), '..')))

@pytest.fixture(scope='module')
def test_client():
    flask_app = app

    # Configure the app for testing
    flask_app.config['TESTING'] = True
    flask_app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///:memory:'
    flask_app.config['JWT_SECRET_KEY'] = 'super-secret-key'  # Change this!

    # Create a test client using the Flask application configured for testing
    with flask_app.test_client() as testing_client:
        with flask_app.app_context():
            # Create the database and the database table
            db.create_all()

            yield testing_client  # this is where the testing happens!

            # Drop the database tables after the test runs
            db.drop_all()

@pytest.fixture(scope='module')
def new_user():
    user = User(id=123, first_name='Test', last_name='User', email='test@example.com')
    return user

@pytest.fixture(scope='module')
def init_database(test_client, new_user):
    # Create a new user in the database
    db.session.add(new_user)
    db.session.commit()

    yield db  # this is where the testing happens!

    db.session.remove()

@pytest.fixture(scope='module')
def access_token(new_user):
    with app.app_context():
        token = create_access_token(identity=new_user.id)
        return token

@pytest.fixture
def new_spot(init_database):
    spot = Spot(
        creator=123,
        latitude=10.0,
        longitude=20.0,
        name='Test Spot',
        description='Test Description',
        quality_rating=3,
        difficulty_rating=3
    )
    db.session.add(spot)
    db.session.commit()
    return spot


@pytest.fixture(scope='function')
def new_users_and_spots(init_database):
    user1 = User(id=131, first_name='Test1', last_name='User1', email='test1@example.com')
    user2 = User(id=132, first_name='Test2', last_name='User2', email='test2@example.com')
    db.session.add(user1)
    db.session.add(user2)
    db.session.commit()

    spot1 = Spot(
        creator=user1.id,
        latitude=10.0,
        longitude=20.0,
        name='Test Spot 1',
        description='Description 1',
        quality_rating=3,
        difficulty_rating=1
    )
    spot2 = Spot(
        creator=user2.id,
        latitude=15.0,
        longitude=25.0,
        name='Test Spot 2',
        description='Description 2',
        quality_rating=4,
        difficulty_rating=2
    )
    db.session.add(spot1)
    db.session.add(spot2)
    db.session.commit()

    follow = Follow(user_id=user1.id, target_id=user2.id)
    db.session.add(follow)
    db.session.commit()

    return user1, user2, spot1, spot2
