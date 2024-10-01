
import datetime
from app import db


class User(db.Model):
    # Fields
    id = db.Column(db.String(128), primary_key=True)
    first_name = db.Column(db.String(80), nullable=False)
    last_name = db.Column(db.String(80), nullable=False)
    email = db.Column(db.String(320), nullable=False)
    picture_url = db.Column(db.String(320))

    # Relationships
    spots = db.relationship('Spot', backref='user', lazy='dynamic')

    def __init__(self, id, first_name, last_name, email, picture_url):
        self.id = id
        self.first_name = first_name
        self.last_name = last_name
        self.email = email        
        self.picture_url = picture_url

    def json(self):
        return {
            'id': self.id,
            'first_name': self.first_name,
            'last_name': self.last_name,
            'email': self.email,
            'picture_url': self.picture_url,
            'spots': [spot.json() for spot in self.spots.all()]
        }
    

class Spot(db.Model):
    # Fields
    id = db.Column(db.Integer, primary_key=True)    
    creator = db.Column(db.String(128), db.ForeignKey('user.id'), nullable=False)
    latitude = db.Column(db.Numeric(8,6), nullable=False)
    longitude = db.Column(db.Numeric(9,6), nullable=False)
    name = db.Column(db.String(80), nullable=False)
    description = db.Column(db.String(255), nullable=False)
    quality_rating = db.Column(db.Integer, nullable=False)
    difficulty_rating = db.Column(db.Integer, nullable=False)

    # Relationships
    likes = db.relationship('Like', backref='spot', lazy='joined', cascade="all, delete-orphan")
    comments = db.relationship('Comment', backref='comment', lazy='joined', cascade="all, delete-orphan")

    def __init__(self, creator, latitude, longitude, name, description, quality_rating, difficulty_rating):
        self.creator = creator
        self.latitude = latitude
        self.longitude = longitude
        self.name = name
        self.description = description
        self.quality_rating = quality_rating
        self.difficulty_rating = difficulty_rating

    def json(self):
        return {
            'id': self.id,
            'creator': self.creator,
            'latitude': self.latitude,
            'longitude': self.longitude,
            'name': self.name,
            'description': self.description,
            'quality_rating': self.quality_rating,
            'difficulty_rating': self.difficulty_rating,
            'likes': [like.json() for like in self.likes],
            'comments': [comment.json() for comment in self.comments]
        }


class Like(db.Model):
    spot_id = db.Column(db.Integer, db.ForeignKey('spot.id'), primary_key=True)
    user_id = db.Column(db.String(128), db.ForeignKey('user.id'), primary_key=True)

    def json(self):
        return {
            'spot_id': self.spot_id,
            'user_id': self.user_id
        }


class Follow(db.Model):
    user_id = db.Column(db.String(128), db.ForeignKey('user.id'), primary_key=True)
    target_id = db.Column(db.String(128), db.ForeignKey('user.id'), primary_key=True)

    def json(self):
        return {
            'user_id': self.user_id,
            'target_id': self.target_id
        }
    

class Comment(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    author = db.Column(db.String(128), db.ForeignKey('user.id'), nullable=False)
    spot = db.Column(db.Integer, db.ForeignKey('spot.id'), nullable=False)
    text = db.Column(db.String(255), nullable=False)
    created_at = db.Column(db.DateTime, default=datetime.datetime.utcnow)

    def json(self):
        return {
            'id': self.id,
            'author_id': self.author,            
            'text': self.text,
            'spot_id': self.spot,
            'created_at': self.created_at.isoformat() + 'Z'
        }


class TokenBlocklist(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    jti = db.Column(db.String(36), nullable=False)
    created_at = db.Column(db.DateTime, nullable=False)

    def to_dict(self):
        return {
            'token_id': self.id,
            'jti':      self.jti,
            }

