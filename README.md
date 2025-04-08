# recipe_find
Recipe Find is a Web Application built based on Spring Boot and React. This application amis to bridge the gap between real-world food and digital recipes.
Helping user with reduce food wast and inspiring culinary creativity.

## Features
- Search for recipe by dish image recognition
- Search for recipe by list of ingredients
- Detailed recipe information from Spoonacular API
- Self-trained MobileNetV3 model for dish image recognition
- Integrated with Google Cloud Vision API for ingredient extraction

## Tech Stack
- Frontend: React, Axios, Material UI, Ant Design
- Backend: Spring Boot, Java, Maven
- Database: PostgreSQL
- Model: TensorFlow, Flask

## Getting Started

### Prerequisites
- Java 21
- Node.js
- Maven
- PostgreSQL
- Python 3.12

### Backend Setup
1. Clone the repository
2. Navigate to the backend directory.
```
cd backend
```
3. Install the dependencies.
```
mvn clean install
```
4. Start the Spring Boot application.
5. Create a PostgreSQL database named `recipe_find` and run `recipe_find.sql` to create tables required.
6. Set up Environment Variables for database and external APIs connection.
   - `POSTGRES_USER`: database username.
   - `POSTGRES_PASSWORD`: database password.
   - `GOOGLE_VISION_API_KEY`: Your Google Vision API key.
   - `SPOONACULAR_API_KEY`: Your Spoonacular API key.
   - `OPENAI_API_KEY`: Your OpenAI API key.
   - `SECRET_KEY`: A secret key for JWT token generation.
7. Run the application.
### Frontend Setup
1. Navigate to the frontend directory.
```
cd frontend
```
2. Install the dependencies.
```
npm install
```
3. Start the React application.
```
npm start
```


