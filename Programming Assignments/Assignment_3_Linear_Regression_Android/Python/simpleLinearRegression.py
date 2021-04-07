import tensorflow as tf
import numpy as np
from tensorflow.python.keras.models import Sequential
from tensorflow.python.keras.layers import Dense


tf.test.is_gpu_available()
#x1 = np.random.randint(50, 351, size=7)
#y1 = np.random.randint(1, 8, size=7)

x1 = np.array([50,100,150,200,250,300,350])
y1 = np.array([1,2,3,4,5,6,7])

model = Sequential()
model.add(Dense(10, input_shape=[1], activation='relu'))
model.add(Dense(1, activation='linear'))

model.compile(loss='mean_squared_error', optimizer='Adam', metrics=['mse'])

model.fit(x1, y1, epochs=1000)

model.predict([2400])

tf.keras.models.save_model(model, 'saved_model.pbtxt')

converter = tf.lite.TFLiteConverter.from_keras_model(model=model)

tfmodel = converter.convert()

open("degree.tflite", "wb").write(tfmodel)