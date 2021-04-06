import tensorflow as tf
import tensorflow_datasets as tfds

import matplotlib.pyplot as plt

#data set
fashion_mnist = tf.keras.datasets.fashion_mnist

#loading dataset into numpy arrays
(images_train, labels_train) , (images_test, labels_test) = fashion_mnist.load_data() 

#build the model
model = tf.keras.Sequential([
    tf.keras.layers.Flatten(input_shape=(28, 28)),
    tf.keras.layers.Dense(128, activation='relu'),
    tf.keras.layers.Dense(10)
])

#used when there are more than two labels
loss_use = tf.keras.losses.SparseCategoricalCrossentropy(from_logits=True)

model.compile(loss = loss_use, optimizer = 'Adam', metrics = ['accuracy'])

model.fit(images_train, labels_train, epochs=10)

#convert to tflite
tf.keras.models.save_model(model, 'saved_model.pbtxt')

converter = tf.lite.TFLiteConverter.from_keras_model(model=model)

tfmodel = converter.convert()

open("fashion.tflite", "wb").write(tfmodel)