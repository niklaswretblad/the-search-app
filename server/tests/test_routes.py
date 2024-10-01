import json
from sqlalchemy.orm import Session
from app import db
from app.model import User, Spot, Like, Comment, Follow

def test_home_page(test_client):
    response = test_client.get('/')
    assert response.status_code == 200
    assert b'Home to THE SEARCH' in response.data

def test_create_user(init_database):
    with Session(db.engine) as session:
        user = session.get(User, 123)
        assert user is not None
        assert user.email == 'test@example.com'

def test_get_user_spots_empty(test_client, init_database, access_token):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    response = test_client.get('/user/123/spots', headers=headers)
    assert response.status_code == 200
    assert b'{}\n' in response.data  # Assuming no spots for the new user

def test_create_spot(test_client, init_database, access_token):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    response = test_client.post('/spots', json={
        'spot': {
            'name': 'Test Spot',
            'description': 'Test Description',
            'creator': 123,
            'latitude': 10.0,
            'longitude': 20.0,
            'quality_rating': 3,
            'difficulty_rating': 3
        }
    }, headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert 'id' in data

def test_get_spot(test_client, init_database, access_token, new_spot):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    spot_id = new_spot.id

    # Now get the spot
    response = test_client.get(f'/spots/{spot_id}', headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data['name'] == 'Test Spot'

def test_like_spot(test_client, init_database, access_token, new_spot):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    spot_id = new_spot.id

    # Like the spot
    response = test_client.post('/likes', json={
        'user_id': 123,
        'spot_id': spot_id
    }, headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data['spot_id'] == spot_id
    assert data['user_id'] == 123

def test_unlike_spot(test_client, init_database, access_token, new_spot):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    spot_id = new_spot.id

    # First like the spot
    like = Like(user_id=123, spot_id=spot_id)
    db.session.add(like)
    db.session.commit()

    # Unlike the spot
    response = test_client.delete('/likes', json={
        'user_id': 123,
        'spot_id': spot_id
    }, headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data['msg'] == 'Successfully removed entry'

def test_get_number_of_likes(test_client, init_database, access_token, new_spot):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    spot_id = new_spot.id

    # Add likes
    like1 = Like(user_id=123, spot_id=spot_id)
    like2 = Like(user_id=124, spot_id=spot_id)
    db.session.add(like1)
    db.session.add(like2)
    db.session.commit()

    # Get number of likes
    response = test_client.get(f'/likes/{spot_id}', headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data['count'] == 2

def test_comment_spot(test_client, init_database, access_token, new_spot):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    spot_id = new_spot.id

    # Comment on the spot
    response = test_client.post(f'/comments/{spot_id}', json={
        'text': 'Great spot!',
        'author_id': 123
    }, headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert 'comment_id' in data

def test_get_spot_comments(test_client, init_database, access_token, new_spot):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    spot_id = new_spot.id

    # Add a comment directly to the database
    comment = Comment(author=123, spot=spot_id, text='Great spot!')
    db.session.add(comment)
    db.session.commit()

    # Get comments for the spot
    response = test_client.get(f'/comments/{spot_id}', headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert len(data) == 1
    assert data[str(comment.id)]['text'] == 'Great spot!'

def test_remove_comment(test_client, init_database, access_token, new_spot):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    spot_id = new_spot.id

    # Add a comment directly to the database
    comment = Comment(author=123, spot=spot_id, text='Great spot!')
    db.session.add(comment)
    db.session.commit()

    # Remove the comment
    response = test_client.delete(f'/comments/{comment.id}', headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data['msg'] == 'Successfully removed entry'

def test_follow_user(test_client, init_database, access_token, new_user):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }

    # Create another user to follow
    target_user = User(id=124, first_name='Target', last_name='User', email='target@example.com')
    db.session.add(target_user)
    db.session.commit()

    # Follow the user
    response = test_client.post('/follows', json={
        'user_id': 123,
        'target_id': 124
    }, headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data['user_id'] == 123
    assert data['target_id'] == 124

def test_unfollow_user(test_client, init_database, access_token, new_user):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }

    # User and follow to unfollow already exists from previous test

    # Unfollow the user
    response = test_client.delete('/follows', json={
        'user_id': 123,
        'target_id': 124
    }, headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    assert data['msg'] == 'Successfully removed entry'


def test_get_user_and_followed_spots(test_client, init_database, access_token, new_users_and_spots):
    headers = {
        'Authorization': f'Bearer {access_token}'
    }
    user1, user2, spot1, spot2 = new_users_and_spots

    # Get spots for user1 and the spots they follow (user2)
    response = test_client.get(f'/user/{user1.id}/spots_and_followed_spots', headers=headers)
    assert response.status_code == 200
    data = json.loads(response.data)
    spot_ids = [spot['id'] for spot in data]

    assert spot1.id in spot_ids
    assert spot2.id in spot_ids

# def test_get_user_and_followed_spots_for_user_with_no_followings(test_client, init_database, access_token, new_users_and_spots):
#     headers = {
#         'Authorization': f'Bearer {access_token}'
#     }
#     user1, user2, spot1, spot2 = new_users_and_spots

#     # Get spots for user2 and the spots they follow (user2 follows no one)
#     response = test_client.get(f'/user/{user2.id}/spots_and_followed_spots', headers=headers)
#     assert response.status_code == 200
#     data = json.loads(response.data)
#     spot_ids = [spot['id'] for spot in data]

#     assert spot2.id in spot_ids
#     assert spot1.id not in spot_ids
