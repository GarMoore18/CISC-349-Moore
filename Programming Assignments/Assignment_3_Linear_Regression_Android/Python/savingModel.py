import tensorflow
import simpleLinearRegression

tensorflow.keras.models.save_model(model, 'saved_model.pbtxt')

converter = tensorflow.lite.TFLiteConverter.from_keras_model(model=model)

tfmodel = converter.convert()

open("degree.tflite", "wb").write(tfmodel)