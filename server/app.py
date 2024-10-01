from app import app

# az webapp up --name the-search resource-group the-search_group

if __name__ == '__main__':
    app.run(debug=True, port=8080, host='0.0.0.0')
