
with open('requirements.txt', 'r') as file:
    lines = file.readlines()

with open('requirements.txt', 'w') as file:
    for line in lines:
        package = line.split('==')[0]
        file.write(package + '\n')