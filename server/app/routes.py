from datetime import datetime, timezone
from flask import Flask, request, make_response, jsonify
from flask_jwt_extended import (
    JWTManager, jwt_required, create_access_token, get_jwt, get_jwt_identity
)
from google.oauth2 import id_token
from google.auth.transport import requests as google_requests
from sqlalchemy.orm import Session
from app import db, app, model

jwt = JWTManager(app)

"""
==============
AUTHORIZATION
==============
"""

@jwt.token_in_blocklist_loader
def check_if_token_revoked(jwt_header, jwt_payload: dict) -> bool:
    """Check if a JWT token is revoked."""
    jti = jwt_payload["jti"]
    token = db.session.query(model.TokenBlocklist.id).filter_by(jti=jti).scalar()
    return token is not None


"""
==============
ERROR ROUTES
==============
"""


@app.errorhandler(404)
def page_not_found(e):
    """Handle 404 errors."""
    return make_response("Page Not Found", 404)


@app.errorhandler(405)
def wrong_method(e):
    """Handle 405 errors."""
    return make_response("Wrong REST-Method", 405)


@app.errorhandler(Exception)
def catch_all(e):
    """Catch all other errors."""
    return make_response("An Error Occurred", 500)


"""
==============
HOME ROUTE
==============
"""


@app.route("/", methods=["GET"])
def home():
    """Home route."""
    return "Home to THE SEARCH"


"""
==============
USER ROUTES
==============
"""


@app.route("/user/login", methods=["POST"])
def google_login():
    """Google Sign-In route to create or login a user."""
    req_json = request.get_json(force=True)

    if "id_token" not in req_json:
        app.logger.info("google_login failed, missing ID token")
        return make_response("Missing ID Token", 400)

    id_token_str = req_json["id_token"]

    try:
        id_info = id_token.verify_oauth2_token(
            id_token_str, google_requests.Request(), app.config["GOOGLE_CLIENT_ID"]
        )        
        
        user_id = id_info["sub"]
        email = id_info["email"]
        first_name = id_info.get("given_name", "")
        last_name = id_info.get("family_name", "")
        picture_url = id_info.get("picture", "")  # Extract the picture URL

        with Session(db.engine) as session:
            user = session.get(model.User, user_id)

            if user is None:
                user = model.User(id=user_id, first_name=first_name, last_name=last_name, email=email, picture_url=picture_url)
                session.add(user)
                session.commit()
                app.logger.info("No user found. Added new user with ID: %s", user_id)

            access_token = create_access_token(identity=user.id)

            ret = {
                "token": access_token,
                "user": {
                    "id": user.id,
                    "email": user.email,
                    "first_name": user.first_name,
                    "last_name": user.last_name,
                    "picture_url": user.picture_url
                }
            }
            
            return make_response(jsonify(ret), 200)

    except ValueError:
        app.logger.info("google_login failed, invalid ID token")
        return make_response("Invalid ID Token", 400)

    except Exception as e:
        app.logger.error("An error occurred: %s", str(e))
        return make_response("Internal Server Error", 500)
    

@app.route("/user/logout", methods=["POST"])
@jwt_required()
def logout():
    """Logout route."""
    jti = get_jwt()["jti"]
    now = datetime.now(timezone.utc)
    db.session.add(model.TokenBlocklist(jti=jti, created_at=now))
    db.session.commit()
    return make_response(jsonify({"msg": "Successfully logged out"}), 200)


@app.route("/user/<string:user_id>/spots", methods=["GET"])
@jwt_required()
def get_user_spots(user_id):
    """Get spots for a user."""
    app.logger.debug(f"get_user_spots() called with user_id: {user_id}")
    
    try:
        with Session(db.engine) as session:
            # Log that the session has been created
            app.logger.debug(f"Session created successfully for user_id: {user_id}")

            # Attempt to retrieve the user
            user = session.get(model.User, user_id)
            if user is None:
                app.logger.warning(f"No user found with user_id: {user_id}")
                return make_response("Faulty Or Missing Parameter", 400)

            # Log the retrieved user data
            app.logger.debug(f"Retrieved user: {user.json()}")

            # Retrieve and log the spots associated with the user
            spots = [spot.json() for spot in user.spots.all()]
            app.logger.debug(f"Retrieved spots for user {user_id}: {spots}")

    except Exception as e:
        # Log any exception that occurs
        app.logger.error(f"Error in get_user_spots for user_id {user_id}: {str(e)}")
        return make_response("Internal Server Error", 500)

    app.logger.info(f"get_user_spots: {spots}")
    return make_response(jsonify(spots), 200)



@app.route("/search_users", methods=["GET"])
@jwt_required()
def search_users():
    """Search for users by name or email."""
    query = request.args.get("query", "")

    if not query:
        return make_response("Query parameter is required", 400)

    with Session(db.engine) as session:
        users = session.query(model.User).filter(
            (model.User.first_name.ilike(f"%{query}%")) |
            (model.User.last_name.ilike(f"%{query}%")) |
            (model.User.email.ilike(f"%{query}%"))
        ).all()


        users_json = [user.json() for user in users]
        return make_response(jsonify(users_json), 200)


"""
==============
SPOT ROUTES
==============
"""


@app.route("/spots", methods=["POST"])
@jwt_required()
def create_spot():
    """Create a new spot."""
    req_json = request.get_json(force=True)

    if "spot" not in req_json:
        return make_response("Faulty Or Missing Parameter", 400)

    spot_json = req_json["spot"]

    required_keys = [
        "name", "description", "creator", "latitude", "longitude", "quality_rating", "difficulty_rating"
    ]

    if not all(k in spot_json for k in required_keys):
        return make_response("Faulty Or Missing Parameter", 400)

    with Session(db.engine) as session:
        user = session.get(model.User, spot_json["creator"])
        if user is None:
            return make_response("Faulty Or Missing User ID Parameter", 400)

        spot = model.Spot(
            name=spot_json["name"],
            description=spot_json["description"],
            latitude=spot_json["latitude"],
            longitude=spot_json["longitude"],
            creator=user.id,
            quality_rating=spot_json["quality_rating"],
            difficulty_rating=spot_json["difficulty_rating"],
        )
        session.add(spot)
        session.commit()

        return make_response(jsonify({"id": spot.id}), 200)


@app.route("/spots/<int:spot_id>", methods=["GET"])
@jwt_required()
def get_spot(spot_id):
    """Get a spot by ID."""
    with Session(db.engine) as session:
        spot = session.get(model.Spot, spot_id)
        if spot is None:
            return make_response("Resource not Found", 404)

        return make_response(jsonify(spot.json()), 200)
    

@app.route("/spots/<int:spot_id>", methods=["PUT"])
@jwt_required()
def update_spot(spot_id):
    """Update an existing spot."""
    req_json = request.get_json(force=True)
    app.logger.info("/spots/<int:spot_id> PUT: acquired JSON: %s", req_json)

    if "spot" not in req_json:
        return make_response("Faulty Or Missing Parameter", 400)

    spot_json = req_json["spot"]
    
    updatable_keys = [
        "name", "description", "latitude", "longitude", "quality_rating", "difficulty_rating"
    ]

    with Session(db.engine) as session:
        spot = session.get(model.Spot, spot_id)
        if spot is None:
            return make_response("Spot not found", 404)

        user_id = get_jwt_identity()
        if spot.creator != user_id:
            return make_response("Unauthorized to update this spot", 403)

        for key in updatable_keys:
            if key in spot_json:
                setattr(spot, key, spot_json[key])

        session.commit()

        return make_response("Spot updated successfully", 200)



@app.route("/spots/<int:spot_id>", methods=["DELETE"])
@jwt_required()
def remove_spot(spot_id):
    """Remove a spot by ID."""
    with Session(db.engine) as session:
        spot = session.get(model.Spot, spot_id)
        if spot is None:
            return make_response("Resource not Found", 404)

        session.delete(spot)
        session.commit()

    return make_response(jsonify({"msg": "Successfully removed entry"}), 200)


@app.route("/user/<string:user_id>/spots_and_followed_spots", methods=["GET"])
@jwt_required()
def get_user_and_followed_spots(user_id):
    """Get spots for a user and the spots they follow."""
    with Session(db.engine) as session:
        user = session.get(model.User, user_id)
        if user is None:
            return make_response("User not found", 404)

        user_spots = user.spots.all()

        followed_users_ids = session.query(model.Follow.target_id).filter_by(user_id=user_id).all()
        followed_users_ids = [id for (id,) in followed_users_ids]

        followed_users_spots = session.query(model.Spot).filter(model.Spot.creator.in_(followed_users_ids)).all()

    all_spots = user_spots + followed_users_spots

    spots_json = [spot.json() for spot in all_spots]

    return make_response(jsonify(spots_json), 200)



"""
==============
LIKE ROUTES
==============
"""


@app.route("/likes", methods=["POST"])
@jwt_required()
def like_spot():
    """Like a spot."""
    req_json = request.get_json(force=True)
    app.logger.info(f"like_spot recieved json: {req_json}")

    if not all(k in req_json for k in ("user_id", "spot_id")):
        return make_response("Faulty Or Missing Parameter", 400)

    user = db.session.get(model.User, req_json['user_id'])
    if user is None:
        return make_response("User does not exist", 400)

    user = db.session.get(model.Spot, req_json['spot_id'])
    if user is None:
        return make_response("User does not exist", 400)
    
    existing_like = db.session.get(model.Like, (req_json["spot_id"], req_json["user_id"]))
    if existing_like is not None:
        return make_response("Already liked", 400)
    
    like = model.Like(user_id=req_json["user_id"], spot_id=req_json["spot_id"])

    db.session.add(like)
    db.session.commit()

    payload = {
        "spot_id": like.spot_id,
        "user_id": like.user_id,
    }

    return make_response(jsonify(payload), 200)


@app.route("/likes", methods=["DELETE"])
@jwt_required()
def unlike_spot():
    """Unlike a spot."""
    req_json = request.get_json(force=True)

    if not all(k in req_json for k in ("user_id", "spot_id")):
        return make_response("Faulty Or Missing Parameter", 400)

    user_id = req_json["user_id"]
    spot_id = req_json["spot_id"]

    like = db.session.get(model.Like, (spot_id, user_id))

    if like is None:
        return make_response("Faulty Or Missing Parameter", 400)

    db.session.delete(like)
    db.session.commit()

    return make_response(jsonify({"msg": "Successfully removed entry"}), 200)


@app.route("/likes/<int:spot_id>", methods=["GET"])
@jwt_required()
def get_number_of_likes(spot_id):
    """Get the number of likes for a spot."""
    likes = db.session.query(model.Like).filter_by(spot_id=spot_id).all()

    count = len(likes)

    payload = {
        "id": spot_id,
        "count": count,
    }
    return make_response(jsonify(payload), 200)


"""
==============
COMMENT ROUTES
==============
"""


@app.route("/comments/<int:spot_id>", methods=["POST"])
@jwt_required()
def comment_spot(spot_id):
    """Comment on a spot."""
    req_json = request.get_json(force=True)

    if not all(k in req_json for k in ("text", "author_id")):
        return make_response("Faulty Or Missing Parameter", 400)

    app.logger.info(f"POST Comment json: {req_json}")

    text = req_json["text"]
    author_id = req_json["author_id"]

    with Session(db.engine) as session:
        author = session.get(model.User, author_id)
        if not author:
            return make_response("Faulty Or Missing Parameter", 400)

        spot = session.get(model.Spot, spot_id)
        if not spot:
            return make_response("Faulty Or Missing Parameter", 400)
        
        comment = model.Comment(author=author_id, spot=spot_id, text=text)        
        session.add(comment)                
        session.commit()
            
        payload = {
            "comment_id": comment.id,
        }
        
        return make_response(jsonify(payload), 200)


@app.route("/comments/<int:comment_id>", methods=["DELETE"])
@jwt_required()
def remove_comment(comment_id):
    """Remove a comment."""
    with Session(db.engine) as session:
        comment = session.get(model.Comment, comment_id)
        if comment is None:
            return make_response("Resource not Found", 404)

        session.delete(comment)
        session.commit()

    return make_response(jsonify({"msg": "Successfully removed entry"}), 200)


@app.route("/comments/<int:spot_id>", methods=["GET"])
@jwt_required()
def get_spot_comments(spot_id):
    """Get comments for a spot."""
    with Session(db.engine) as session:
        spot = session.get(model.Spot, spot_id)
        if spot is None:
            return make_response("Resource not Found", 404)

        comments = [comment.json() for comment in spot.comments]
        
    return make_response(jsonify(comments), 200)

    


"""
==============
FOLLOW ROUTES
==============
"""


@app.route("/follows", methods=["POST"])
@jwt_required()
def follow_user():
    """Follow a user."""
    req_json = request.get_json(force=True)
    user_id = req_json["user_id"]
    target_id = req_json["target_id"]

    app.logger.debug(f"follow_user() user_id: {user_id} target_id: {target_id}")
    
    existing_follow = db.session.query(model.Follow).filter_by(user_id=user_id, target_id=target_id).first()
    
    if existing_follow:
        return make_response(jsonify({"success": False, "message": "Already following"}), 400)

    follow = model.Follow(user_id=user_id, target_id=target_id)

    db.session.add(follow)
    db.session.commit()

    return make_response(jsonify({"success": True}), 200)
    

@app.route("/follows/<string:user_id>/<string:target_id>", methods=["DELETE"])
@jwt_required()
def unfollow_user(user_id, target_id):
    """Unfollow a user."""
    follow = db.session.query(model.Follow).filter_by(user_id=user_id, target_id=target_id).first()

    if follow:
        db.session.delete(follow)
        db.session.commit()
        return make_response(jsonify({"success": True}), 200)
    else:
        return make_response(jsonify({"success": False}), 400)


@app.route("/follows/check/<string:user_id>/<string:target_id>", methods=["GET"])
@jwt_required()
def check_if_following(user_id, target_id):
    """Check if the current user is following another user."""

    app.logger.debug(f"check_if_following() user_id: {user_id}, target_id: {target_id}")

    try:
        follow = db.session.query(model.Follow).filter_by(user_id=user_id, target_id=target_id).first()
        if follow:
            return make_response(jsonify({"following": True}), 200)
        else:
            return make_response(jsonify({"following": False}), 400)
    except Exception as e:
        app.logger.error(f"Error in check_if_following: {str(e)}")
        return make_response("Internal Server Error", 500)


@app.route("/user/<string:user_id>/followed_spots", methods=["GET"])
@jwt_required()
def get_followed_spots(user_id):
    """Get spots for users followed by the current user."""
    follows = db.session.query(model.Follow).filter_by(user_id=user_id).all()
    followed_user_ids = [follow.target_id for follow in follows]
    
    spots = db.session.query(model.Spot).filter(model.Spot.creator.in_(followed_user_ids)).all()
    spots_json = [spot.json() for spot in spots]
    
    app.logger.debug(f"spots: {spots_json}")
    return make_response(jsonify(spots_json), 200)



