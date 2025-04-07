from flask import Flask, request, jsonify
import tensorflow as tf
from tensorflow.keras.models import load_model
from PIL import Image
import numpy as np
import io

app = Flask(__name__)
# CSRF protection is not needed for backend-to-backend API calls (no browser or session involved)
# This API is only accessed by a trusted backend servicepm

CLASS_PATH = "classes.txt"

model = load_model('best_model.keras')
class_names = [line.strip() for line in open(CLASS_PATH)]

def preprocess_image(image):
    image = image.resize((224, 224)) # resize the image
    image = np.array(image) # convert to numpy array
    image = image / 255.0 # scale pixel values to 0-1
    image = np.expand_dims(image, axis=0) # add batch dimension
    return image

@app.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return jsonify({'error': 'no image provided'}), 400
    image = request.files['image']
    try:
        image = Image.open(image)
        processed_image = preprocess_image(image)
        predictions = model.predict(processed_image)

        return_number = 5
        top_indices = np.argsort(predictions[0])[-return_number:][::-1]
        result = [
            {"dish_name" : class_names[idx], "probability" : float(predictions[0][idx])}
            for idx in top_indices
        ]
        
        return jsonify({"predictions":result})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    app.run(port=5000)